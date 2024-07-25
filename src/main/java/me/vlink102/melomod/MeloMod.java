package me.vlink102.melomod;

import me.vlink102.melomod.config.MeloConfiguration;
import me.vlink102.melomod.command.MeloCommand;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.mixin.PlayerObjectUtil;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.UUID;

/**
 * The entrypoint of the Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = MeloMod.MODID, name = MeloMod.NAME, version = MeloMod.VERSION)
public class MeloMod {

    public static final String API_KEY = "ab720b01-ee6c-4a2b-9a88-d3314fe73151";

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

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static MeloMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static MeloConfiguration config;

    public static InternalLocraw internalLocraw = null;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new MeloConfiguration();
        CommandManager.INSTANCE.registerCommand(new MeloCommand());
        PlayerObjectUtil objectUtil = new PlayerObjectUtil(this);
        internalLocraw = new InternalLocraw(this);
    }
}
