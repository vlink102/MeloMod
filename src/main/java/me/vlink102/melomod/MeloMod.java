package me.vlink102.melomod;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.gson.Gson;
import me.vlink102.melomod.command.*;
import me.vlink102.melomod.config.MeloConfiguration;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import me.vlink102.melomod.events.ChatEvent;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.events.PlayerConnection;
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
import me.vlink102.melomod.world.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.*;

import static me.vlink102.melomod.util.StringUtils.cc;

/**
 * The entrypoint of the Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = MeloMod.MODID, name = MeloMod.NAME, version = MeloMod.VERSION)
public class MeloMod {

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

        AbstractColor(char color, String ansi) {
            this.color = color;
            this.ansi = ansi;
        }
        private final String ansi;
        private final char color;

        public String getAnsi() {
            return ansi;
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

        public String getColor() {
            return "§" + color;
        }
    }



    public enum MessageScheme {
        CHAT(AbstractColor.DARK_PURPLE, AbstractColor.LIGHT_PURPLE, "CHAT"),
        PRIVATE_CHAT(AbstractColor.DARK_PURPLE, AbstractColor.DARK_AQUA, "DM"),
        NOTIFICATION(AbstractColor.DARK_GREEN, AbstractColor.GREEN, "SYSTEM"),
        DEBUG(AbstractColor.BLUE, AbstractColor.DARK_AQUA, "DEBUG"),
        ERROR(AbstractColor.DARK_RED, AbstractColor.RED, "ERROR"),
        WARN(AbstractColor.RED, AbstractColor.GOLD, "WARNING"),
        RAW(null, null, null),
        RAW_SIGNED(AbstractColor.DARK_AQUA, AbstractColor.AQUA, null);

        private final AbstractColor bracketColor;
        private final AbstractColor prefixColor;
        private final String tag;

        MessageScheme(AbstractColor bracketColor, AbstractColor prefixColor, String tag) {
            this.bracketColor = bracketColor;
            this.prefixColor = prefixColor;
            this.tag = tag;
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
                    "MM" +
                    this.bracketColor.generate(system) + "]" + (system ? Ansi.RESET : "");
        }

        public String generateTag(boolean system) {
            if (tag != null && !tag.isEmpty()) {
                return this.bracketColor.generate(system) + "[" + this.prefixColor.generate(system) +
                        this.tag + this.bracketColor.generate(system) + "]" + (system ? Ansi.RESET : "");
            }
            return null;
        }

        public static String generatePrefixHover() {
            StringJoiner joiner = new StringJoiner("\n");
            joiner.add("&3&lMeloMod");
            joiner.add("");
            joiner.add("&8Authors:");
            joiner.add(" &8→ &eMelo &8(__MeloMio)");
            joiner.add(" &8→ &evlink102 &8(ZenmosM)");
            joiner.add("");
            Version.VersionCompatibility stability = MeloMod.versionCompatibility;
            joiner.add("&8Version: " + stability.getColor().getColor() + MeloMod.VERSION + " " + stability.getIcon());
            joiner.add("&8Status: " + stability.getPretty());
            joiner.add("");
            joiner.add("&8discord.gg/NVPUTYSk3u");
            joiner.add("&eLeft-click to join!");
            return cc(joiner.toString());
        }

        // move to vchatcomponent

        public String getTag() {
            return tag;
        }

        public AbstractColor getBracketColor() {
            return bracketColor;
        }

        public AbstractColor getPrefixColor() {
            return prefixColor;
        }
    }

    public static boolean isObfuscated;
    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    public static Version VERSION_NEW;

    public static Version.VersionCompatibility versionCompatibility = Version.VersionCompatibility.INCOMPATIBLE; //updated on runtime and version packet
    public static Version serverVersion = null;

    @Mod.Instance(MODID)
    public static MeloMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static MeloConfiguration config;
    public static Gson gson;

    public static InternalLocraw internalLocraw = null;
    public static ChatEvent chatEvent = null;
    public static LocrawUtil locrawUtil;

    public static UUID playerUUID;
    public static String playerName;

    private SkyblockUtil.SkyblockProfile skyblockProfile = null;
    public SkyblockUtil skyblockUtil;
    public ApiUtil apiUtil;

    public void setPlayerProfile(SkyblockUtil.SkyblockProfile skyblockProfile) {
        this.skyblockProfile = skyblockProfile;
    }


    public SkyblockUtil.SkyblockProfile getPlayerProfile() {
        return skyblockProfile;
    }

    public SkyblockUtil getSkyblockUtil() {
        return skyblockUtil;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        isObfuscated = isObfuscated();
    }

    public CommunicationHandler handler;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        playerUUID = Minecraft.getMinecraft().getSession().getProfile().getId();
        playerName = Minecraft.getMinecraft().getSession().getUsername();

        VERSION_NEW = Version.parse(VERSION);

        config = new MeloConfiguration();
        gson = new Gson();
        MinecraftForge.EVENT_BUS.register(new ServerTracker());
        skyblockUtil = new SkyblockUtil(this);
        apiUtil = new ApiUtil();
        CommandManager.INSTANCE.registerCommand(new MeloCommand(this));
        CommandManager.INSTANCE.registerCommand(new MeloMsg(this));
        CommandManager.INSTANCE.registerCommand(new MeloOnline());
        CommandManager.INSTANCE.registerCommand(new MeloTest());
        CommandManager.INSTANCE.registerCommand(new PrivateMessage());
        PlayerObjectUtil objectUtil = new PlayerObjectUtil(this);
        locrawUtil = new LocrawUtil();
        internalLocraw = new InternalLocraw(this);
        chatEvent = new ChatEvent(this);
        EventManager.INSTANCE.register(new TickHandler());
        Render render = new Render();
        MinecraftForge.EVENT_BUS.register(render);
        //MinecraftForge.EVENT_BUS.register(internalLocraw);
        new PlayerConnection();

        handler = new CommunicationHandler();
        handler.beginKeepAlive(playerUUID, playerName);
    }

    public static boolean isOnline() {
        return Minecraft.getMinecraft().thePlayer != null;
    }

    public static List<VChatComponent> queue = new ArrayList<>();

    public static boolean addCenteredMessage(String message) {
        return addMessage(StringUtils.getCentredMessage(message));
    }

    public static boolean addError(String message, Exception... exception) {
        boolean result = addMessage(new VChatComponent(MessageScheme.ERROR)
                .add(
                        message,
                        "&cPlease report this error to the discord. &e(Click)",
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
                        "&cPlease report serious warnings to the discord. &6(Click)",
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
        if (MeloConfiguration.debugMessages) {
            System.out.println("Debug: " + message);
            return addMessage(new VChatComponent(MessageScheme.DEBUG)
                    .add(
                            message,
                           "&eClick to disable debug mode",
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
        if (isOnline() && Minecraft.getMinecraft().ingameGUI != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent.build());
            return true;
        } else {
            queue.add(chatComponent);
        }
        return false;
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
                                new ChatComponentText(cc("&eClick to reply!"))
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
                        "&eClick to reply!",
                        new ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/melopriv " + recipient + " "
                        ),
                        StringUtils.VComponentSettings.INHERIT_NONE
                )
        );
    }

    private static boolean isObfuscated()
    {
        try
        {
            Minecraft.class.getDeclaredField("logger");
            return false;
        }
        catch (NoSuchFieldException e1)
        {
            return true;
        }
    }
}
