package me.vlink102.melomod.config;

import me.vlink102.melomod.hud.MeloHUD;
import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class MeloConfiguration extends Config {
    @HUD(
            name = "MeloMod HUD"
    )
    public MeloHUD hud = new MeloHUD();

    @Switch(
            name = "Enable Coordinates",
            size = OptionSize.SINGLE // Optional
    )
    public static boolean enableCoords = true; // The default value for the boolean Switch.

    /*
    @Slider(
            name = "Example Slider",
            min = 0f, max = 100f, // Minimum and maximum values for the slider.
            step = 10 // The amount of steps that the slider should have.
    )
    public static float exampleSlider = 50f; // The default value for the float Slider.

     */

    @Dropdown(
            name = "Mining Highlight Type", // Name of the Dropdown
            options = {"Mithril", "Gemstones", "Auto"} // Options available.
    )
    public static int miningHighlightType = 2; // Default option (in this case "Option 2")

    public MeloConfiguration() {
        super(new Mod(MeloMod.NAME, ModType.SKYBLOCK), MeloMod.MODID + ".json");
        initialize();
    }
}

