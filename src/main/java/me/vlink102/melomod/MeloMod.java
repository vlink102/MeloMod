package me.vlink102.melomod;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.command.client.InternalTesting;
import me.vlink102.melomod.command.client.MainCommand;
import me.vlink102.melomod.command.client.RatMe;
import me.vlink102.melomod.command.server.SocketMessage;
import me.vlink102.melomod.command.server.SocketOnline;
import me.vlink102.melomod.command.server.SocketPrivateMessage;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.events.*;
import me.vlink102.melomod.util.*;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.mutable.MutableObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static me.vlink102.melomod.util.StringUtils.cc;

/**
 * The entrypoint of the Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = MeloMod.MODID, name = MeloMod.NAME, version = MeloMod.VERSION)
public class MeloMod {

    @Getter
    private File configurationFile;

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    private static final ThreadPoolExecutor THREAD_EXECUTOR = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat(MeloMod.MODID + " - #%d").build());

    @Getter
    private NewScheduler newScheduler;
    public static boolean isObfuscated;
    public static Version VERSION_NEW;
    public static Version.Compatibility compatibility = Version.Compatibility.INCOMPATIBLE; //updated on runtime and version packet
    public static Version serverVersion = null;
    @Mod.Instance(MODID)
    public static MeloMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static MainConfiguration config;
    public static Gson gson;
    public static LocrawHandler locrawHandler = null;
    public static ChatEventHandler chatEventHandler = null;
    public static LocrawUtil locrawUtil;
    public static UUID playerUUID;
    public static String playerName;
    public static List<VChatComponent> queue = new ArrayList<>();
    private final MutableObject<Language> language = new MutableObject<>(Language.ENGLISH);
    @Getter
    public SkyblockUtil skyblockUtil;
    public ApiUtil apiUtil;
    public CommunicationHandler handler;
    @Getter
    @Setter
    private JsonObject languageConfig = new JsonObject();
    private SkyblockUtil.SkyblockProfile skyblockProfile = null;

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
        // ??
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

    public Language getLanguage() {
        return language.getValue();
    }

    public void setLanguage(Language language) {
        this.language.setValue(language);
    }

    public SkyblockUtil.SkyblockProfile getPlayerProfile() {
        return skyblockProfile;
    }

    public void setPlayerProfile(SkyblockUtil.SkyblockProfile skyblockProfile) {
        this.skyblockProfile = skyblockProfile;
    }

    public static File createNewRandomUUID(String extension) {
        return new File(MeloMod.INSTANCE.getConfigurationFile(), UUID.randomUUID() + "." + extension);
    }

    public static Font custom;
    public static FontMetrics metrics;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        isObfuscated = isObfuscated();
        configurationFile = new File(e.getModConfigurationDirectory(), MeloMod.MODID + "/");
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();

        try {
            custom = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/Minecraft-Seven_v2.ttf")).deriveFont(Font.PLAIN, 16f);
            ge.registerFont(custom);
        } catch (FontFormatException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
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
        DataUtils.loadLocalizedStrings(Objects.requireNonNull(Language.getById(config.language)));
        CommandManager.INSTANCE.registerCommand(new MainCommand(this));
        CommandManager.INSTANCE.registerCommand(new SocketMessage(this));
        CommandManager.INSTANCE.registerCommand(new SocketOnline());
        CommandManager.INSTANCE.registerCommand(new InternalTesting());
        CommandManager.INSTANCE.registerCommand(new RatMe());
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
        handler.beginKeepAlive(playerUUID, playerName);
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
