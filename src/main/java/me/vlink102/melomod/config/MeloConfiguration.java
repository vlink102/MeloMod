package me.vlink102.melomod.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class MeloConfiguration extends Config {

    @Text(
            name = "API Key",
            description = "API Key obtained from https://developer.hypixel.net/ (Required to use some chat commands)"
    )
    public static String apiKey = "";

    @SubConfig
    public static MiningHelper miningHelper = new MiningHelper();

    @SubConfig
    public static ChatConfig chatConfig = new ChatConfig();

    @Switch(
            name = "Debug messages",
            description = "Debug messages will print in chat (developer)"
    )
    public static boolean debugMessages = false;

    @Text(
            name = "Mod chat prefix"
    )
    public static String modChatPrefix = "MM";
     // The default value for the boolean Switch.

    /*
    @Slider(
            name = "Example Slider",
            min = 0f, max = 100f, // Minimum and maximum values for the slider.
            step = 10 // The amount of steps that the slider should have.
    )
    public static float exampleSlider = 50f; // The default value for the float Slider.

     */

     // Default option (in this case "Option 2")

    @Switch(
            name = "Enable Chroma Highlighting"
    )
    public static boolean enableChromaHighlighting = false;

    public MeloConfiguration() {
        super(new Mod(MeloMod.NAME, ModType.SKYBLOCK, "/703ab98cf301a4df30ecb2185ac1c7f8.png"), MeloMod.MODID + ".json");
        initialize();
    }
}

