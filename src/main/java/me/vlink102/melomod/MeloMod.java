package me.vlink102.melomod;

import cc.polyfrost.oneconfig.libs.checker.units.qual.A;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import me.vlink102.melomod.config.MeloConfiguration;
import me.vlink102.melomod.command.MeloCommand;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import me.vlink102.melomod.events.ChatEvent;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.events.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.events.chatcooldownmanager.TickHandler;
import me.vlink102.melomod.mixin.PlayerObjectUtil;
import me.vlink102.melomod.mixin.SkyblockUtil;
import me.vlink102.melomod.util.ApiUtil;
import me.vlink102.melomod.world.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
    @Mod.Instance(MODID)
    public static MeloMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static MeloConfiguration config;
    public static Gson gson;

    public static InternalLocraw internalLocraw = null;
    public static ChatEvent chatEvent = null;
    public static LocrawUtil locrawUtil;

    public static UUID playerUUID;

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

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        playerUUID = Minecraft.getMinecraft().getSession().getProfile().getId();
        config = new MeloConfiguration();
        gson = new Gson();
        MinecraftForge.EVENT_BUS.register(new ServerTracker());
        MinecraftForge.EVENT_BUS.register(new TickHandler());
        skyblockUtil = new SkyblockUtil(this);
        apiUtil = new ApiUtil();
        CommandManager.INSTANCE.registerCommand(new MeloCommand(this));
        PlayerObjectUtil objectUtil = new PlayerObjectUtil(this);
        locrawUtil = new LocrawUtil();
        internalLocraw = new InternalLocraw(this);
        chatEvent = new ChatEvent(this);
        Render render = new Render();
        MinecraftForge.EVENT_BUS.register(render);
        //MinecraftForge.EVENT_BUS.register(internalLocraw);
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
