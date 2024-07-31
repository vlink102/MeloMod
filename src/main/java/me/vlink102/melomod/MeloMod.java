package me.vlink102.melomod;

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
import me.vlink102.melomod.util.game.PlayerObjectUtil;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.Version;
import me.vlink102.melomod.world.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The entrypoint of the Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = MeloMod.MODID, name = MeloMod.NAME, version = MeloMod.VERSION)
public class MeloMod {

    public enum MinecraftColors {
        BLACK('0'),
        DARK_BLUE('1'),
        DARK_GREEN('2'),
        DARK_AQUA('3'),
        DARK_RED('4'),
        DARK_PURPLE('5'),
        GOLD('6'),
        GRAY('7'),
        DARK_GRAY('8'),
        BLUE('9'),
        GREEN('a'),
        AQUA('b'),
        RED('c'),
        LIGHT_PURPLE('d'),
        YELLOW('e'),
        WHITE('f'),
        OBFUSCATED('k'),
        BOLD('l'),
        ITALIC('m'),
        STRIKETHROUGH('n'),
        UNDERLINE('o'),
        RESET('r');

        MinecraftColors(char color) {
            this.color = color;
        }
        private final char color;

        public String getColor() {
            return "§" + color;
        }
    }

    public static boolean isObfuscated;
    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    public static Version VERSION_NEW;

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
        MinecraftForge.EVENT_BUS.register(new TickHandler());
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

    public static boolean addMessage(String message) {
        try {
            if (isOnline() && Minecraft.getMinecraft().ingameGUI != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
                return true;
            } else {
                queue.add(message);
                System.out.println("Queued: " + message);
            }
        } catch (NullPointerException e) {
            System.out.println("Failed to add message: " + e.getMessage());
            queue.add(message);
        }
        return false;
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
