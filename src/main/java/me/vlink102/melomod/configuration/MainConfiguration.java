package me.vlink102.melomod.configuration;

import cc.polyfrost.oneconfig.config.annotations.*;
import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.vlink102.melomod.util.enums.skyblock.Gamemode;
import me.vlink102.melomod.util.translation.DataUtils;
import me.vlink102.melomod.util.translation.Language;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class MainConfiguration extends Config {

    @Text(
            name = "Hypixel API Key",
            description = "API Key obtained from https://developer.hypixel.net/ (Required to use some chat commands)",
            secure = true
    )
    public static String apiKey = "";

    @Dropdown(
            name = "Language",
            description = "Mod Language",
            options = {
                    "eng",
                    "afr",
                    "ara",
                    "cat",
                    "chi",
                    "zho",
                    "ces",
                    "dan",
                    "nld",
                    "fin",
                    "fra",
                    "deu",
                    "ell",
                    "heb",
                    "hun",
                    "ita",
                    "jpn",
                    "kor",
                    "nor",
                    "pol",
                    "por",
                    "ptb",
                    "ron",
                    "rus",
                    "srp",
                    "spa",
                    "swe",
                    "tur",
                    "ukr",
                    "vie"
            }
    )
    public int language = 0;

    @Button(
            name = "Refresh languages",
            description = "Reloads the language files",
            text = "Reload languages"
    )
    Runnable reloadLanguages = () -> DataUtils.loadLocalizedStrings(Objects.requireNonNull(Language.getById(language)));

    @SubConfig
    public static MiningHelperConfiguration miningHelperConfiguration = new MiningHelperConfiguration();

    @SubConfig
    public static ChatConfiguration chatConfiguration = new ChatConfiguration();

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
    @Text(
            name = "GROQ API Key",
            secure = true
    )
    public static String groqApiKey = "";

    public MainConfiguration() {
        super(new Mod(MeloMod.NAME, ModType.SKYBLOCK, "/703ab98cf301a4df30ecb2185ac1c7f8.png"), MeloMod.MODID + ".json");
        initialize();
    }
}

