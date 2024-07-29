package me.vlink102.melomod.util;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.ChatConfig;
import me.vlink102.melomod.config.MeloConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

public class ApiUtil {
    private static final Gson gson = new Gson();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final String USER_AGENT = MeloMod.MODID + "/" + MeloMod.VERSION;


    public interface Endpoint {
        String getEndpoint();
    }
    public static class Request {

        private final List<NameValuePair> queryArguments = new ArrayList<>();
        private String baseUrl = null;
        private boolean shouldGunzip = false;
        private String method;

        public Request method(String method) {
            this.method = method;
            return this;
        }

        public Request url(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Request queryArgument(String key, String value) {
            queryArguments.add(new BasicNameValuePair(key, value));
            return this;
        }

        public Request queryArguments(Collection<NameValuePair> queryArguments) {
            this.queryArguments.addAll(queryArguments);
            return this;
        }

        public Request gunzip() {
            shouldGunzip = true;
            return this;
        }

        private CompletableFuture<URL> buildUrl() {
            CompletableFuture<URL> fut = new CompletableFuture<>();
            try {
                fut.complete(new URIBuilder(baseUrl)
                        .addParameters(queryArguments)
                        .build()
                        .toURL());
            } catch (URISyntaxException |
                     MalformedURLException |
                     NullPointerException e) { // Using CompletableFuture as an exception monad, isn't that exiting?
                fut.completeExceptionally(e);
            }
            return fut;
        }

        public CompletableFuture<String> requestString() {
            return buildUrl().thenApplyAsync(url -> {
                try {
                    InputStream inputStream = null;
                    URLConnection conn = null;
                    try {
                        conn = url.openConnection();
                        if (conn instanceof HttpURLConnection) {
                            ((HttpURLConnection) conn).setRequestMethod(method);
                        }
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);
                        conn.setRequestProperty("User-Agent", USER_AGENT);
                        conn.setRequestProperty("API-Key", MeloConfiguration.apiKey);

                        if (conn instanceof HttpsURLConnection) {
                            int response = ((HttpsURLConnection) conn).getResponseCode();
                            if (response != 200) {
                                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("FATAL: " + StatusCodes.getFromCode(response)));
                                ((HttpsURLConnection) conn).disconnect();
                                return null;
                            }
                        }
                        inputStream = conn.getInputStream();

                        if (shouldGunzip) {
                            inputStream = new GZIPInputStream(inputStream);
                        }

                        // While the assumption of UTF8 isn't always true; it *should* always be true.
                        // Not in the sense that this will hold in most cases (although that as well),
                        // but in the sense that any violation of this better have a good reason.
                        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } finally {
                            if (conn instanceof HttpURLConnection) {
                                ((HttpURLConnection) conn).disconnect();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // We can rethrow, since supplyAsync catches exceptions.
                }
            }, executorService);
        }

        public CompletableFuture<JsonObject> requestJson() {
            return requestJson(JsonObject.class);
        }

        public <T> CompletableFuture<T> requestJson(Class<? extends T> clazz) {
            return requestString().thenApply(str -> gson.fromJson(str, clazz));
        }
        public CompletableFuture<JsonObject> requestJsonAnon(JsonObject body) {
            return requestJsonAnon(JsonObject.class, body);
        }
        public <T> CompletableFuture<T> requestJsonAnon(Class<? extends T> clazz, JsonObject body) {
            return requestStringAnon(body).thenApply(str -> gson.fromJson(str, clazz));
        }
        public CompletableFuture<String> requestStringAnon(JsonObject body) {
            return buildUrl().thenApplyAsync(url -> {
                try {
                    InputStream inputStream = null;
                    URLConnection conn = null;
                    try {
                        conn = url.openConnection();
                        if (conn instanceof HttpURLConnection) {
                            ((HttpURLConnection) conn).setRequestMethod(method);
                        }
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);
                        conn.setRequestProperty("Content-Type", "application/json");

                        conn.setDoOutput(true);
                        String jsonInputString = gson.toJson(body);
                        try(OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        if (conn instanceof HttpsURLConnection) {
                            int response = ((HttpsURLConnection) conn).getResponseCode();
                            if (response != 200) {
                                System.out.println("FATAL: " + StatusCodes.getFromCode(response));
                            }
                        }
                        inputStream = conn.getInputStream();

                        if (shouldGunzip) {
                            inputStream = new GZIPInputStream(inputStream);
                        }

                        // While the assumption of UTF8 isn't always true; it *should* always be true.
                        // Not in the sense that this will hold in most cases (although that as well),
                        // but in the sense that any violation of this better have a good reason.
                        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } finally {
                            if (conn instanceof HttpURLConnection) {
                                ((HttpURLConnection) conn).disconnect();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // We can rethrow, since supplyAsync catches exceptions.
                }
            }, executorService);
        }

        public CompletableFuture<String> requestAIStringAnon(JsonObject body) {
            return buildUrl().thenApplyAsync(url -> {
                try {
                    InputStream inputStream = null;
                    URLConnection conn = null;
                    try {
                        conn = url.openConnection();
                        if (conn instanceof HttpURLConnection) {
                            ((HttpURLConnection) conn).setRequestMethod(method);
                        }
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);
                        conn.setRequestProperty("User-Agent", USER_AGENT);
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "Bearer " + ChatConfig.groqApiKey);


                        conn.setDoOutput(true);
                        String jsonInputString = body.toString();
                        try(OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        if (conn instanceof HttpsURLConnection) {
                            int response = ((HttpsURLConnection) conn).getResponseCode();
                            if (response != 200) {
                                System.out.println("FATAL: " + StatusCodes.getFromCode(response));
                            }
                        }
                        inputStream = conn.getInputStream();

                        if (shouldGunzip) {
                            inputStream = new GZIPInputStream(inputStream);
                        }

                        // While the assumption of UTF8 isn't always true; it *should* always be true.
                        // Not in the sense that this will hold in most cases (although that as well),
                        // but in the sense that any violation of this better have a good reason.
                        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } finally {
                            if (conn instanceof HttpURLConnection) {
                                ((HttpURLConnection) conn).disconnect();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // We can rethrow, since supplyAsync catches exceptions.
                }
            }, executorService);
        }


        public CompletableFuture<JsonObject> requestAIAnon(JsonObject body) {
            return requestAIAnon(JsonObject.class, body);
        }
        public <T> CompletableFuture<T> requestAIAnon(Class<? extends T> clazz, JsonObject body) {
            return requestAIStringAnon(body).thenApply(str -> gson.fromJson(str, clazz));
        }


    }



    public synchronized void requestAI(String url, JsonObject body, Consumer<? super JsonObject> action) {
        ApiUtil.Request request = new Request()
                .url(url).method("POST");
        CompletableFuture<JsonObject> object = request.requestAIAnon(body);
        object.thenAcceptAsync(action, executorService);
    }

    public Request request() {
        return new Request();
    }

    protected Request newApiRequest(Endpoint apiPath) {
        return newAnonymousApiRequest(apiPath)
                .queryArgument("key", MeloConfiguration.apiKey);
    }




    public synchronized void requestSkyCrypt(SkyCryptEndpoint endpoint, String playerName, String profileName, Consumer<? super JsonObject> action) {
        ApiUtil.Request request = newApiRequest(new SkyCryptEndpointClass(endpoint, playerName, profileName)).method("GET");

        CompletableFuture<JsonObject> object = request.requestJson();
        object.thenAcceptAsync(action, executorService);
    }

    public synchronized void requestAPI(HypixelEndpoint endpoint, Consumer<? super JsonObject> action, HypixelEndpoint.FilledEndpointArgument... arguments) {
        ApiUtil.Request request = newApiRequest(endpoint).method("GET");
        for (ApiUtil.HypixelEndpoint.FilledEndpointArgument argument : arguments) {
            request.queryArgument(argument.getTag(), argument.getValue());

        }
        CompletableFuture<JsonObject> object = request.requestJson();
        object.thenAcceptAsync(action, executorService);
    }

    public synchronized void requestServer(String url, Consumer<? super JsonObject> action) {
        ApiUtil.Request request = new Request()
                .url(url).method("GET");
        CompletableFuture<JsonObject> object = request.requestJson();
        object.thenAcceptAsync(action, executorService);
    }

    public synchronized void requestServer(String url, JsonObject body, Consumer<? super JsonObject> action, HypixelEndpoint.FilledEndpointArgument... arguments) {
        ApiUtil.Request request = new Request()
                .url(url).method("GET");
        for (HypixelEndpoint.FilledEndpointArgument argument : arguments) {
            request.queryArgument(argument.getTag(), argument.getValue());
        }

        CompletableFuture<JsonObject> object = request.requestJsonAnon(body);
        object.handle((jsonObject, ex) -> {
            if (jsonObject != null && jsonObject.has("text")) {
                return object.thenAcceptAsync(action, executorService);
            }
            return null;
        });
    }




    public enum SkyCryptEndpoint {
        PROFILE("profile/player_name"),
        TALISMANS("talismans/player_name"),
        TALISMANS_PROFILE("talismans/player_name/profile_name"),
        SLAYERS("slayers/player_name"),
        SLAYERS_PROFILE("slayers/player_name/profile_name"),
        COINS("coins/player_name"),
        COINS_PROFILE("coins/player_name/profile_name"),
        BAZAAR("bazaar"),
        DUNGEONS("dungeons/player_name"),
        DUNGEON_PROFILE("dungeons/player_name/profile_name");

        private final String endPoint;

        SkyCryptEndpoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public String getReplacedEndPoint(String playerName, String playerProfile) {
            String newEndpoint = endPoint;
            if (playerName != null) {
                newEndpoint = newEndpoint.replaceAll("player_name", playerName);
            }
            if (playerProfile != null) {
                newEndpoint = newEndpoint.replaceAll("profile_name", playerProfile);
            }
            return newEndpoint;
        }


    }



    public enum HypixelEndpoint implements Endpoint {
        PLAYER("player", RequiredEndPoint.uuid()),
        RECENT_GAMES("recentgames", RequiredEndPoint.uuid()),
        STATUS("status", RequiredEndPoint.uuid()),
        GUILD("guild", RequiredEndPoint.required("id"), RequiredEndPoint.required("player"), RequiredEndPoint.required("name")),
        RESOURCES_GAMES("resources/games"),
        RESOURCES_ACHIEVEMENTS("resources/achievements"),
        RESOURCES_CHALLENGES("resources/challenges"),
        RESOURCES_QUESTS("resources/quests"),
        RESOURCES_GUILDS_ACHIEVEMENTS("resources/guilds/achievements"),
        RESOURCES_VANITY_PETS("resources/vanity/pets"),
        RESOURCES_VANITY_COMPANIONS("resources/vanity/companions"),
        RESOURCES_SKYBLOCK_COLLECTIONS("resources/skyblock/collections"),
        RESOURCES_SKYBLOCK_SKILLS("resources/skyblock/skills"),
        RESOURCES_SKYBLOCK_ITEMS("resources/skyblock/items"),
        RESOURCES_SKYBLOCK_ELECTION("resources/skyblock/election"),
        RESOURCES_SKYBLOCK_BINGO("resources/skyblock/bingo"),
        SKYBLOCK_NEWS("skyblock/news"),
        SKYBLOCK_AUCTION("skyblock/auction", RequiredEndPoint.uuid(), RequiredEndPoint.required("player"), RequiredEndPoint.required("profile")),
        SKYBLOCK_AUCTIONS("skyblock/auctions", RequiredEndPoint.required("page")),
        SKYBLOCK_AUCTIONS_ENDED("skyblock/auctions_ended"),
        SKYBLOCK_BAZAAR("skyblock/bazaar"),
        SKYBLOCK_PROFILE("skyblock/profile", RequiredEndPoint.required("profile")),
        SKYBLOCK_PROFILES("skyblock/profiles", RequiredEndPoint.uuid()),
        SKYBLOCK_MUSEUM("skyblock/museum", RequiredEndPoint.required("profile")),
        SKYBLOCK_GARDEN("skyblock/garden", RequiredEndPoint.required("profile")),
        SKYBLOCK_BINGO("skyblock/bingo", RequiredEndPoint.uuid()),
        SKYBLOCK_FIRESALES("skyblock/firesales"),
        HOUSING_ACTIVE("housing/active"),
        HOUSING_HOUSE("housing/house", RequiredEndPoint.required("house")),
        HOUSING_HOUSES("housing/houses", RequiredEndPoint.required("player")),
        BOOSTERS("boosters"),
        COUNTS("counts"),
        LEADERBOARDS("leaderboards"),
        PUNISHMENT_STATS("punishmentstats");

        public static final String MAIN = "https://api.hypixel.net/v2/";
        private final String endPoint;

        private final List<EndPointArgument> endpointArguments;

        HypixelEndpoint(String endPoint, EndPointArgument... endpoints) {
            this.endPoint = endPoint;
            this.endpointArguments = Arrays.asList(endpoints);
        }

        @Override
        public String getEndpoint() {
            return MAIN + endPoint;
        }

        public List<EndPointArgument> getEndpointArguments() {
            return endpointArguments;
        }

        public static class FilledEndpointArgument {
            private final EndPointArgument argument;
            private final String value;

            public FilledEndpointArgument(EndPointArgument argument, String value) {
                this.argument = argument;
                this.value = value;
            }

            public String getTag() {
                return argument.getArgumentTag();
            }

            public boolean isRequired() {
                return argument.isRequired();
            }

            public String getValue() {
                return value;
            }

            public static FilledEndpointArgument from(String tag, String value) {
                return new FilledEndpointArgument(new EndPointArgument(tag, true), value);
            }

            public static FilledEndpointArgument uuid() {
                return new FilledEndpointArgument(RequiredEndPoint.uuid(), MeloMod.playerUUID.toString());
            }
            public static FilledEndpointArgument uuid(UUID uuid) {
                return new FilledEndpointArgument(RequiredEndPoint.uuid(), uuid.toString());
            }
        }

        public static class EndPointArgument {
            private final String argumentTag;
            private final boolean required;

            public EndPointArgument(String argumentTag, Boolean required) {
                this.argumentTag = argumentTag;
                this.required = required;
            }

            public String getArgumentTag() {
                return argumentTag;
            }

            public boolean isRequired() {
                return required;
            }
        }

        public static class RequiredEndPoint extends EndPointArgument {
            public RequiredEndPoint(String argumentTag) {
                super(argumentTag, true);
            }

            public static EndPointArgument required(String tag) {
                return new RequiredEndPoint(tag);
            }
            public static EndPointArgument uuid() {
                return new RequiredEndPoint("uuid");
            }
        }

    }

    public static class SkyCryptEndpointClass implements Endpoint {
        private final String endPoint;
        public static final String MAIN = "https://sky.shiiyu.moe/api/v2/";

        public SkyCryptEndpointClass(SkyCryptEndpoint endpoint, String playerName, String profileName) {
            this.endPoint = MAIN + endpoint.getReplacedEndPoint(playerName, profileName);
        }

        @Override
        public String getEndpoint() {
            return endPoint;
        }
    }

    protected Request newAnonymousApiRequest(Endpoint endpoint) {
        return new Request().url(endpoint.getEndpoint());
    }

}