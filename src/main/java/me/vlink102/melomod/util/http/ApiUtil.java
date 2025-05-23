package me.vlink102.melomod.util.http;


import com.google.gson.*;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.configuration.ChatConfiguration;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.events.LocrawHandler;
import me.vlink102.melomod.util.BitMapFont;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.enums.http.StatusCodes;
import me.vlink102.melomod.util.enums.skyblock.Location;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.packets.PacketPlayOutChat;
import me.vlink102.melomod.util.wrappers.hypixel.Guild;
import me.vlink102.melomod.util.wrappers.hypixel.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.JsonException;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static me.vlink102.melomod.util.StringUtils.paginate;

public class ApiUtil {
    public static final List<String> models = new ArrayList<String>() {{
        this.add("gemma2-9b-it");
        this.add("gemma-7b-it");
        this.add("llama-3.1-70b-versatile");
        this.add("llama-3.1-8b-instant");
        this.add("llama3-70b-8192");
        this.add("llama3-8b-8192");
        this.add("llama3-groq-70b-8192-tool-use-preview");
        this.add("llama3-groq-8b-8192-tool-use-preview");
        this.add("mixtral-8x7b-32768");
    }};
    private static final Gson gson = new Gson();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final String USER_AGENT = MeloMod.MODID + "/" + MeloMod.VERSION;

    public static void sendCommandLater(String command) {
        if (!command.startsWith("/")) command = "/" + command;
        TickHandler.addToQueue(command);
    }

    public static void sendLater(String message, ChatChannel chatChannel) {
        if (chatChannel == null) return;
        String prefixCommand = "";
        switch (chatChannel) {
            case PARTY:
                prefixCommand = "/pc";
                break;
            case ALL:
                prefixCommand = "/ac";
                break;
            case COOP:
                prefixCommand = "/cc";
                break;
            case GUILD:
                prefixCommand = "/gc";
                break;
            case OFFICER:
                prefixCommand = "/oc";
                break;
            case CUSTOM:
                if (message.contains("<item>")) {
                    MeloMod.runAsync(() -> {

                    //String data = ItemSerializer.INSTANCE.serialize(Minecraft.getMinecraft().thePlayer.getHeldItem());
                    String image = ApiUtil.imgToBase64String(BitMapFont.getTooltipBackground(Minecraft.getMinecraft().thePlayer.getHeldItem()), "png");
                    String data = ItemSerializer.INSTANCE.serialize(Minecraft.getMinecraft().thePlayer.getHeldItem());

                    MeloMod.addDebug(data);
                    CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, null, data, image));
                            });
                } else {

                    CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, null, null, null));
                }
                return;
        }
        String finalCommand = prefixCommand + " " + message;
        TickHandler.addToQueue(finalCommand);
    }

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (final OutputStream b64os = Base64.getEncoder().wrap(os)) {
            ImageIO.write(img, formatName, b64os);
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return os.toString();
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static Gson getGson() {
        return ApiUtil.gson;
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
                .queryArgument("key", MainConfiguration.apiKey);
    }


    public synchronized CompletableFuture<UUID> fromName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            ApiUtil.Request request = new Request()
                    .url("https://api.mojang.com/users/profiles/minecraft/" + name).method("GET");
            CompletableFuture<JsonObject> object = request.requestJson();
            try {
                return SkyblockUtil.fixMalformed(object.get().get("id").getAsString());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

    }

    public synchronized void sayPlayerNetworth(String player, ChatChannel chatChannel) {
        MeloMod.runAsync(() -> {
            try {
                UUID uuid = fromName(player).get();

                String endPoint = new SkyCryptEndpointClass(SkycryptV2Endpoint.PROFILE_ANON, player, "").getEndpoint();
                //System.out.println(endPoint);
                CookieStore cookieStore = new BasicCookieStore();

                try (org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
                    org.apache.http.client.methods.HttpGet request = new org.apache.http.client.methods.HttpGet(endPoint);


                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
                        int responseCode = response.getStatusLine().getStatusCode();
                        // System.out.println("HTTP response for " + username + ": " + responseCode);

                        if (responseCode == 200) {
                            Pattern pattern = Pattern.compile("networth:([\\d.]*?),");
                            Matcher matcher = pattern.matcher(responseBody);
                            if (matcher.find()) {
                                String networthString = matcher.group(1);
                                Double networth = Double.parseDouble(networthString);
                                if (uuid.equals(MeloMod.playerUUID)) {
                                    sendLater("⛀⛁ Networth: $" + String.format("%,.0f", networth) + " ⛃⛂", chatChannel);
                                } else {
                                    sendLater("⛀⛁ " + player + "'s Networth: $" + String.format("%,.0f", networth) + " ⛃⛂", chatChannel);
                                }
                            }
                            /*String rawData = extractJsonArray(responseBody);
                            System.out.println(rawData);
                            JsonArray array = new JsonParser().parse(rawData).getAsJsonArray();
                            JsonObject dataWrapper = array.get(1).getAsJsonObject();
                            JsonObject user = dataWrapper.get("data").getAsJsonObject().get("user").getAsJsonObject();
                            JsonObject stats = user.get("stats").getAsJsonObject();
                            JsonObject networth = stats.get("networth").getAsJsonObject();
                            double value = networth.get("networth").getAsDouble();*/


                        } else {
                            //System.out.println("Non-200 response for " + username + ": " + responseBody);
                        }
                    } catch (JsonException e) {
                        //System.out.println("Failed to parse response for " + username);
                        e.printStackTrace();
                    }
                } catch (java.io.IOException e) {
                    // System.out.println("IOException for " + username);
                    e.printStackTrace();
                }
                /*requestHtml(
                        endPoint,

                        s -> {

                        }
                );*/
                /*requestSkyCrypt(
                        *//*ApiUtil.SkyCryptEndpoint.PROFILE*//*SkycryptV2Endpoint.PROFILE,
                        player, null,
                        object -> {
                            JsonObject profiles = SkyblockUtil.getAsJsonObject("profiles", object);
                            for (Map.Entry<String, JsonElement> entry : profiles.entrySet()) {
                                String string = entry.getKey();
                                JsonObject profile = SkyblockUtil.getAsJsonObject(string, profiles);
                                if (SkyblockUtil.getAsBoolean("current", profile)) {
                                    JsonObject data = SkyblockUtil.getAsJsonObject("data", profile);
                                    JsonObject networth = SkyblockUtil.getAsJsonObject("networth", data);
                                    Double networthDouble = SkyblockUtil.getAsDouble("networth", networth);

                                }
                            }
                        }
                );*/
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public synchronized void getPlayerLastLogin(String playerName, ChatChannel chatChannel) {

        MeloMod.runAsync(() -> {
            try {
                UUID uuid = fromName(playerName).get();
                requestAPI(
                        ApiUtil.HypixelEndpoint.PLAYER,
                        object -> {
                            PlayerUtil.Player player = new PlayerUtil.Player(SkyblockUtil.getAsJsonObject("player", object));
                            Long lastLogin = player.getLastLogin();
                            Long lastLogout = player.getLastLogout();
                            String lastLoginTime = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - lastLogin, true, true);
                            String lastLogoutTime = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - lastLogout, true, true);

                            sendLater("❣ «" + playerName + "» Last Logout: " + lastLogoutTime + " ◆ (Last Login: " + lastLoginTime + ") ❣", chatChannel);
                        },
                        ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid(uuid)
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public synchronized void sayPlayerSecrets(String player, ChatChannel chatChannel) {
        MeloMod.runAsync(() -> {
            try {
                UUID uuid = fromName(player).get();
                requestAPI(
                        ApiUtil.HypixelEndpoint.SKYBLOCK_PROFILES,
                        object -> {
                            JsonArray profiles = object.get("profiles").getAsJsonArray();
                            for (JsonElement profile : profiles) {
                                JsonObject profileObject = profile.getAsJsonObject();
                                if (profileObject.get("selected").getAsBoolean()) {
                                    SkyblockUtil.SkyblockProfile sbProfile = new SkyblockUtil.SkyblockProfile(profileObject);
                                    Integer secrets = sbProfile.getMembers().get(uuid.toString().replaceAll("-", "")).getDungeons().getSecrets();
                                    sendLater("☠ " + player + "'s secrets: " + secrets + " ☠", chatChannel);
                                }
                            }
                        },
                        ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid(uuid)
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public synchronized void sayPlayerStatus(String player, ChatChannel chatChannel) {
        MeloMod.runAsync(() -> {
            try {
                UUID uuid = fromName(player).get();
                requestAPI(
                        ApiUtil.HypixelEndpoint.STATUS,
                        object -> {
                            if (uuid.equals(MeloMod.playerUUID)) {
                                Location info = LocrawHandler.getLocation();
                                sendLater("◇ Server: " + LocrawHandler.getServerID() + " ⚑ Island: " + WordUtils.capitalizeFully(info.toString().replaceAll("_", " ")) + " ◇", chatChannel);
                            } else {
                                JsonObject session = SkyblockUtil.getAsJsonObject("session", object);
                                if (session != null) {
                                    if (!session.get("online").getAsBoolean()) {
                                        sendLater("◇ " + player + " is not currently online! ◇", chatChannel);
                                    } else {
                                        boolean onCurrent = false;
                                        for (EntityPlayer playerEntity : Minecraft.getMinecraft().theWorld.playerEntities) {
                                            if (playerEntity.getName().equals(player)) {
                                                onCurrent = true;
                                                break;
                                            }
                                        }

                                        sendLater("◇ «" + player + "» Server: " + (onCurrent ? LocrawHandler.getServerID() : "Unknown") + " ◇ Game: " + WordUtils.capitalizeFully(SkyblockUtil.getAsString("gameType", session).replaceAll("_", " ")) + " ⚑ Mode: " + WordUtils.capitalizeFully(SkyblockUtil.getAsString("mode", session).replaceAll("_", " ")) + " ◇", chatChannel);
                                    }
                                }


                            }
                        },
                        ApiUtil.HypixelEndpoint.FilledEndpointArgument.uuid(uuid)
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public synchronized void sayGuildInformation(String player, ChatChannel chatChannel) {

        MeloMod.runAsync(() -> {
            try {
                UUID uuid = fromName(player).get();
                requestAPI(
                        ApiUtil.HypixelEndpoint.GUILD,
                        object -> {
                            if (object.has("guild") && object.get("guild").isJsonObject()) {
                                Guild guild = new Guild(object.get("guild").getAsJsonObject());

                                if (uuid.equals(MeloMod.playerUUID)) {
                                    sendLater("✿ Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿", chatChannel);
                                } else {
                                    sendLater("✿ «" + player + "» Guild: [" + guild.getTag() + "] " + guild.getName() + " (" + guild.getGuildID() + ") ✿", chatChannel);
                                }
                            }
                        },
                        ApiUtil.HypixelEndpoint.FilledEndpointArgument.from("player", "" + uuid)
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public synchronized void getAI(String prompt, ChatChannel chatChannel) {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt + "(WARN: Keep your response to LESS THAN 200 characters)");
        array.add(message);
        object.add("messages", array);
        String model = models.get(ChatConfiguration.aiModel);
        MeloMod.addDebug("&eUsing AI model: &7" + model + " &8(" + ChatConfiguration.aiModel + ")");
        object.addProperty("model", model);
        requestAI(
                "https://api.groq.com/openai/v1/chat/completions",
                object,
                o -> {
                    JsonArray choices = SkyblockUtil.getAsJsonArray("choices", o);
                    for (JsonElement choice : choices) {
                        JsonObject choiceObject = choice.getAsJsonObject();
                        JsonObject messageObject = SkyblockUtil.getAsJsonObject("message", choiceObject);
                        sendLater("✉ AI: '" + messageObject.get("content").getAsString() + "'", chatChannel);
                    }
                }
        );
    }

    public static String extractBetween(String input, String startDelimiter, String endDelimiter) {
        // Escape regex special characters in delimiters
        String regex = Pattern.quote(startDelimiter) + "(.*?)" + Pattern.quote(endDelimiter);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);  // Group 1 is the content between the delimiters
        }
        return null; // No match found
    }
    public static String extractJsonArray(String input) {
        String regex = "data:\\s*(\\[\\{.*\\}\\])";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL); // DOTALL makes '.' match newlines
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1); // the JSON array: [{...}]
        }
        return null;
    }

    public synchronized void lastLogin(String player, ChatChannel chatChannel) {
        MeloMod.runAsync(() -> {

            sendLater("ℹ «" + player + "» Last Area: " + "Unknown" + " (Updated: Unknown) ℹ", chatChannel);
            /*requestSkyCrypt(
                    ApiUtil.SkycryptV2Endpoint.PROFILE,
                    player,
                    null,
                    object -> {
                        if (object.has("profiles")) {
                            JsonObject profiles = object.get("profiles").getAsJsonObject();
                            for (Map.Entry<String, JsonElement> entry : profiles.entrySet()) {
                                String string = entry.getKey();
                                JsonObject profile = SkyblockUtil.getAsJsonObject(string, profiles);
                                if (SkyblockUtil.getAsBoolean("current", profile)) {
                                    JsonObject currentArea = SkyblockUtil.getAsJsonObject("current_area", SkyblockUtil.getAsJsonObject("user_data", SkyblockUtil.getAsJsonObject("data", profile)));
                                    String currentAreaString = SkyblockUtil.getAsString("current_area", currentArea);
                                    if (currentAreaString == null) {
                                        currentAreaString = "Unknown";
                                    }
                                    Boolean currentAreaUpdated = SkyblockUtil.getAsBoolean("current_area_updated", currentArea);
                                    sendLater("ℹ «" + player + "» Last Area: " + currentAreaString + " (Updated: " + currentAreaUpdated + ") ℹ", chatChannel);
                                }
                            }
                        }
                    }
            );*/
        });

    }

    public synchronized void playerSocials(String player, String request, ChatChannel chatChannel) {

        MeloMod.runAsync(() -> {
            try {
                UUID uuid = fromName(player).get();
                requestAPI(
                        ApiUtil.HypixelEndpoint.PLAYER,
                        object -> {
                            if (object.has("player") && object.get("player").isJsonObject()) {
                                PlayerUtil.Player playerProfile = new PlayerUtil.Player(SkyblockUtil.getAsJsonObject("player", object));
                                switch (request) {
                                    case "dc":
                                        sendLater("»»» " + player + "'s DC: " + playerProfile.getDiscord() + " «««", chatChannel);
                                        break;
                                    case "twitch":
                                        sendLater("»»» " + player + "'s Twitch: " + playerProfile.getTwitch() + " «««", chatChannel);
                                        break;
                                    case "twitter":
                                        sendLater("»»» " + player + "'s Twitter: " + playerProfile.getTwitter() + " «««", chatChannel);
                                        break;
                                    case "instagram":
                                        sendLater("»»» " + player + "'s Instagram: " + playerProfile.getInstagram() + " «««", chatChannel);
                                        break;
                                    case "youtube":
                                        sendLater("»»» " + player + "'s YouTube: " + playerProfile.getYoutube() + " «««", chatChannel);
                                        break;
                                    case "forums":
                                        sendLater("»»» " + player + "'s Forum Profile: " + playerProfile.getForums() + " «««", chatChannel);
                                        break;
                                    case "tiktok":
                                        sendLater("»»» " + player + "'s TikTok: " + playerProfile.getTiktok() + " «««", chatChannel);
                                        break;
                                }
                            }

                        },
                        ApiUtil.HypixelEndpoint.FilledEndpointArgument.from("uuid", "" + uuid)
                );
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });


    }

    public synchronized JsonArray getNameChanges(String username) {
        String uuid = username; // Assuming username is already a UUID or valid identifier
        String url = "https://laby.net/api/v3/user/" + uuid + "/profile";
        CookieStore cookieStore = new BasicCookieStore();

        try (org.apache.http.impl.client.CloseableHttpClient httpClient = org.apache.http.impl.client.HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
            org.apache.http.client.methods.HttpGet request = new org.apache.http.client.methods.HttpGet(url);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
                int responseCode = response.getStatusLine().getStatusCode();
               // System.out.println("HTTP response for " + username + ": " + responseCode);

                if (responseCode == 200) {
                    JsonObject object = new JsonParser().parse(responseBody).getAsJsonObject();
                    JsonArray history = object.getAsJsonArray("name_history");
                    return history;
                } else {
                    //System.out.println("Non-200 response for " + username + ": " + responseBody);
                }
            } catch (JsonException e) {
                //System.out.println("Failed to parse response for " + username);
                e.printStackTrace();
            }
        } catch (java.io.IOException e) {
           // System.out.println("IOException for " + username);
            e.printStackTrace();
        }
        return new JsonArray();
    }

    /**
     * <a href="https://laby.net/api/v3/user/1871dfae-0a6a-4486-8366-6bc0131d370e/profile">LabyMod Endpoint (92/94)</a>
     * <a href="https://api.crafty.gg/api/v2/players/swageater34">Crafty Endpoint (67/94)</a>
     */
    public synchronized void getPlayerPastNames(String player, int page, ChatChannel chatChannel) {
        MeloMod.runAsync(() -> {
            /*UUID uuid = fromName(player).get();*/
                /*requestServer(
                        "https://laby.net/api/v3/user/" + player + "/profile",
                *//*
                object -> {
                    System.out.println(player);
                    JsonObject data = SkyblockUtil.getAsJsonObject("data", object);
                    JsonArray usernames = SkyblockUtil.getAsJsonArray("usernames", data);
                    System.out.println(usernames);
                    List<String> strings = new ArrayList<>();
                    for (JsonElement usernameElement : usernames) {
                        JsonObject username = usernameElement.getAsJsonObject();
                        String oldUser = SkyblockUtil.getAsString("username",username);
                        strings.add(oldUser);
                    }
                    List<String> usernameList = paginate("❄ " + player + "'s Username History (Page #): ", strings);

                    sendLaterParty(usernameList.get(page - 1) + " ❄");
                }
                 *//*
                        object -> {

*/
            JsonArray userNameHistory = getNameChanges(player);
            List<String> names = new ArrayList<>();
            for (JsonElement jsonElement : userNameHistory) {
                JsonObject nameChange = jsonElement.getAsJsonObject();
                names.add(SkyblockUtil.getAsString("name", nameChange));
            }
            Collections.reverse(names);
            List<String> usernameList = paginate("❄ " + player + "'s Username History (Page #): ", names);

            sendLater(usernameList.get(page - 1) + " ❄", chatChannel);
                       /* }
                );*/
        });


    }

    public synchronized void requestSkyCrypt(SkycryptV2Endpoint endpoint, String playerName, String profileName, Consumer<? super JsonObject> action) {
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

    public synchronized void requestHtml(String url, Consumer<? super String> action) {
        ApiUtil.Request request = new Request().url(url).method("GET");
        CompletableFuture<String> string = request.requestString();

        string.thenAcceptAsync(action, executorService);
    }

    public synchronized void requestServer(String url, Consumer<? super JsonObject> action) {
        ApiUtil.Request request = new Request()
                .url(url).method("GET");
        CompletableFuture<JsonObject> object = request.requestJson();
        object.thenAcceptAsync(action, executorService);
    }

    public synchronized void requestServer(String url, JsonObject body, Consumer<? super JsonElement> action, HypixelEndpoint.FilledEndpointArgument... arguments) {
        ApiUtil.Request request = new Request()
                .url(url).method("GET");
        for (HypixelEndpoint.FilledEndpointArgument argument : arguments) {
            request.queryArgument(argument.getTag(), argument.getValue());
        }

        CompletableFuture<JsonElement> object = request.requestJsonAnon(body);
        object.handle((jsonObject, ex) -> {
            if (jsonObject.isJsonObject()) {
                JsonObject jsonObject1 = jsonObject.getAsJsonObject();
                if (jsonObject1 != null && jsonObject1.has("text")) {
                    return object.thenAcceptAsync(action, executorService);
                }
            }

            return null;
        });
    }

    protected Request newAnonymousApiRequest(Endpoint endpoint) {
        return new Request().url(endpoint.getEndpoint());
    }

    public enum ChatChannel {
        ALL, // TODO
        GUILD, // TODO
        OFFICER, // TODO
        PARTY,
        CUSTOM,
        COOP; // TODO

        public static ChatChannel fromString(String channel) {
            for (ChatChannel value : ChatChannel.values()) {
                if (value.toString().equals(channel)) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum SkycryptV2Endpoint {
        PROFILE("stats/player_name/profile_name"),
        PROFILE_ANON("stats/player_name");


        private final String endPoint;

        SkycryptV2Endpoint(String endPoint) {
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

        @Getter
        private final List<EndPointArgument> endpointArguments;

        HypixelEndpoint(String endPoint, EndPointArgument... endpoints) {
            this.endPoint = endPoint;
            this.endpointArguments = Arrays.asList(endpoints);
        }

        @Override
        public String getEndpoint() {
            return MAIN + endPoint;
        }

        public static class FilledEndpointArgument {
            private final EndPointArgument argument;
            @Getter
            private final String value;

            public FilledEndpointArgument(EndPointArgument argument, String value) {
                this.argument = argument;
                this.value = value;
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

            public String getTag() {
                return argument.getArgumentTag();
            }

            public boolean isRequired() {
                return argument.isRequired();
            }

        }

        @Getter
        public static class EndPointArgument {
            private final String argumentTag;
            private final boolean required;

            public EndPointArgument(String argumentTag, Boolean required) {
                this.argumentTag = argumentTag;
                this.required = required;
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
                        conn.setRequestProperty("API-Key", MainConfiguration.apiKey);

                        if (conn instanceof HttpsURLConnection) {
                            int response = ((HttpsURLConnection) conn).getResponseCode();
                            if (response != 200) {
                                MeloMod.addMessage("FATAL: " + StatusCodes.getFromCode(response));
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
                    System.out.println("error: " + url.toString());
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

        public CompletableFuture<JsonElement> requestJsonAnon(JsonElement body) {
            return requestJsonAnon(JsonElement.class, body);
        }

        public <T> CompletableFuture<T> requestJsonAnon(Class<? extends T> clazz, JsonElement body) {
            return requestStringAnon(body).thenApply(str -> gson.fromJson(str, clazz));
        }

        public CompletableFuture<String> requestStringAnon(JsonElement body) {
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
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        if (conn instanceof HttpURLConnection) {
                            int response = ((HttpURLConnection) conn).getResponseCode();
                            if (response != 200) {
                                StatusCodes code = StatusCodes.getStatusCode(response);
                                assert code != null;
                                MeloMod.addError("&cConnection failed: &6" + code);
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
                        conn.setRequestProperty("Authorization", "Bearer " + MainConfiguration.groqApiKey);


                        conn.setDoOutput(true);
                        String jsonInputString = body.toString();
                        try (OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        if (conn instanceof HttpURLConnection) {
                            int response = ((HttpURLConnection) conn).getResponseCode();
                            if (response != 200) {
                                StatusCodes code = StatusCodes.getStatusCode(response);
                                assert code != null;
                                MeloMod.addError("&cConnection failed: &6" + code);
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

    public static class SkyCryptEndpointClass implements Endpoint {
        public static final String MAIN = "https://sky.shiiyu.moe/";
        private final String endPoint;

        public SkyCryptEndpointClass(SkycryptV2Endpoint endpoint, String playerName, String profileName) {
            this.endPoint = MAIN + endpoint.getReplacedEndPoint(playerName, profileName);
        }

        @Override
        public String getEndpoint() {
            return endPoint;
        }
    }

}