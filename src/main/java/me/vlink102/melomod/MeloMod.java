package me.vlink102.melomod;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.gson.Gson;
import me.vlink102.melomod.command.MeloMsg;
import me.vlink102.melomod.command.MeloOnline;
import me.vlink102.melomod.command.PrivateMessage;
import me.vlink102.melomod.config.MeloConfiguration;
import me.vlink102.melomod.command.MeloCommand;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import me.vlink102.melomod.events.ChatEvent;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.events.PlayerConnection;
import me.vlink102.melomod.util.StringUtils;
import me.vlink102.melomod.util.game.PlayerObjectUtil;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.Version;
import me.vlink102.melomod.util.jcolor.Ansi;
import me.vlink102.melomod.util.jcolor.AnsiFormat;
import me.vlink102.melomod.util.jcolor.Attribute;
import me.vlink102.melomod.util.jcolor.Command;
import me.vlink102.melomod.world.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

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
            String toReturn = string.replaceAll("&", "§");
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
        RAW(AbstractColor.RESET, AbstractColor.RESET, null),
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
            Version.VersionStability stability = MeloMod.versionStability;
            joiner.add("&8Version: " + stability.getColor().getColor() + MeloMod.VERSION + " " + stability.getIcon());
            joiner.add("&8Status: " + stability.getPretty());
            joiner.add("");
            joiner.add("&8discord.gg/NVPUTYSk3u");
            joiner.add("&eLeft-click to join!");
            return joiner.toString().replaceAll("&", "§");
        }

        public IChatComponent generateComponent(IChatComponent message) {
            String prefix = generatePrefix(false);
            String tag = generateTag(false);
            IChatComponent prefixComponent = new ChatComponentText(prefix);
            ChatStyle prefixStyle = prefixComponent.getChatStyle();
            prefixStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/NVPUTYSk3u"));
            prefixStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(generatePrefixHover())));
            prefixComponent.setChatStyle(prefixStyle);
            if (tag == null) {
                return prefixComponent.appendText("§r ").appendSibling(message);
            }
            IChatComponent tagComponent = new ChatComponentText(tag);
            return prefixComponent.appendText(" ").appendSibling(tagComponent).appendText("§r ").appendSibling(message);
        }

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

    public static Version.VersionStability versionStability = Version.VersionStability.INCOMPATIBLE; //updated on runtime and version packet
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
        CommandManager.INSTANCE.registerCommand(new PrivateMessage());
        PlayerObjectUtil objectUtil = new PlayerObjectUtil(this);
        locrawUtil = new LocrawUtil();
        internalLocraw = new InternalLocraw(this);
        chatEvent = new ChatEvent(this);
        EventManager.INSTANCE.register(new TickHandler());
        Render render = new Render();
        MinecraftForge.EVENT_BUS.register(render);
        new PlayerConnection();
        //MinecraftForge.EVENT_BUS.register(internalLocraw);
        handler = new CommunicationHandler();
        handler.beginKeepAlive(playerUUID, playerName);
    }

    public static boolean isOnline() {
        return Minecraft.getMinecraft().thePlayer != null;
    }

    public static List<String> queue = new ArrayList<>();

    public static boolean addCenteredMessage(String message) {
        return addMessage(StringUtils.getCentredMessage(message));
    }

    public static boolean addError(String message, Exception... exception) {
        boolean result = addMessage(message,
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§cPlease report this error to the discord. §e(Click)")),
                new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/NVPUTYSk3u"),
                MessageScheme.ERROR
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
        boolean result = addMessage(message,
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§cPlease report serious warnings to the discord. §6(Click)")),
                new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/NVPUTYSk3u"),
                MessageScheme.WARN
        );

        if (!result) {
            System.err.println(message);
        }
        return result;
    }

    public static boolean addDebug(String message) {
        if (MeloConfiguration.debugMessages) {
            System.out.println("Debug: " + message);
            return addMessage(
                    message,
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§eClick to disable debug mode")),
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/melomod debug"),
                    MessageScheme.DEBUG
            );
        }
        return false;
    }

    private static String justify(String s, int limit) {
        StringBuilder justifiedText = new StringBuilder();
        StringBuilder justifiedLine = new StringBuilder();
        String[] words = s.split(" ");
        for (int i = 0; i < words.length; i++) {
            justifiedLine.append(words[i]).append(" ");
            if (i+1 == words.length || justifiedLine.length() + words[i+1].length() > limit) {
                justifiedLine.deleteCharAt(justifiedLine.length() - 1);
                justifiedText.append(justifiedLine).append(System.lineSeparator());
                justifiedLine = new StringBuilder();
            }
        }
        return justifiedText.toString();
    }

    public static IChatComponent box(IChatComponent tooLong) {
        if (tooLong.getFormattedText().length() >= 256) {
            boolean shouldReformat = tooLong.getChatStyle().getChatHoverEvent() == null;
            if (shouldReformat) {
                MeloMod.addWarn("§eDetected IChatComponent too large: Converting without formatting...");

            } else {
                MeloMod.addWarn("§eDetected IChatComponent too large: Converting...");

            }
            String trimmed = StringUtils.cleanColour(tooLong.getUnformattedText()).substring(0, 255);
            IChatComponent newComponent = new ChatComponentText(trimmed);
            ChatStyle newComponentStyle = newComponent.getChatStyle();
            newComponentStyle.setChatHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ChatComponentText(justify(tooLong.getUnformattedText(), 64))
            ));
            newComponent.setChatStyle(newComponentStyle);
            return newComponent;
        }
        return tooLong;
    }

    public static boolean addMessage(String message, HoverEvent hoverMessage, ClickEvent execute, MessageScheme scheme) {
        message = message.replaceAll("&", "§");
        try {
            if (isOnline() && Minecraft.getMinecraft().ingameGUI != null) {
                IChatComponent chatComponent = new ChatComponentText(message);
                ChatStyle style = chatComponent.getChatStyle();
                if (hoverMessage != null) style.setChatHoverEvent(hoverMessage);
                if (execute != null) style.setChatClickEvent(execute);
                chatComponent.setChatStyle(style);
                if (scheme == MessageScheme.RAW) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(box(chatComponent));
                } else {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(scheme.generateComponent(box(chatComponent)));
                }
                return true;
            } else {
                queue.add(message);
            }
        } catch (NullPointerException e) {
            System.err.println("Failed to add message: " + e.getMessage());
            queue.add(message);
        }
        return false;
    }

    public static boolean addMessage(String message) {
        return addMessage(message, null, null, MessageScheme.RAW_SIGNED);
    }

    public static boolean addRaw(String message) {
        return addMessage(message, null, null, MessageScheme.RAW);
    }

    public static boolean addSystemNotification(String message) {
        return addMessage(message, null, null, MessageScheme.NOTIFICATION);
    }

    public static boolean addChat(String message) {
        return addMessage(message, null, null, MessageScheme.CHAT);
    }

    public static boolean addPrivateChat(String message, String recipient) {
        return addMessage(message,
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§eClick to reply!")
                ),
                new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/melopriv " + recipient + " "
                ),
                MessageScheme.PRIVATE_CHAT
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
