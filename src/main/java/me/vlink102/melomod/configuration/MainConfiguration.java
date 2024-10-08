package me.vlink102.melomod.configuration;

import cc.polyfrost.oneconfig.config.annotations.*;
import me.vlink102.melomod.MeloMod;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.vlink102.melomod.command.client.BanMe;
import me.vlink102.melomod.util.enums.skyblock.Gamemode;
import me.vlink102.melomod.util.translation.DataUtils;
import me.vlink102.melomod.util.translation.Language;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class MainConfiguration extends Config {

    @Text(
            subcategory = "API Keys",
            name = "Hypixel API Key",
            description = "API Key obtained from https://developer.hypixel.net/ (Required to use some chat commands)",
            secure = true
    )
    public static String apiKey = "";

    @Dropdown(
            subcategory = "Language",
            name = "Language",
            description = "Mod Language",
            options = {
                    "English (en_US)",
                    "Afrikaans (af_ZA)",
                    "Arabic (ar_SA)",
                    "Catalan (ca_ES)",
                    "Chinese Simplified (zh_CN)",
                    "Chinese Traditional (zh_HK)",
                    "Czech (cs_CZ)",
                    "Danish (da_DK)",
                    "Dutch (nl_NL)",
                    "Finnish (fi_FI)",
                    "French (fr_FR)",
                    "German (de_DE)",
                    "Greek (el_GR)",
                    "Hebrew (he_IL)",
                    "Hungarian (hu_HU)",
                    "Italian (it_IT)",
                    "Japanese (ja_JP)",
                    "Korean (ko_KR)",
                    "Norwegian (no_NO)",
                    "Polish (pl_PL)",
                    "Portuguese (pt_PT)",
                    "Portuguese Brazil (pt_BR)",
                    "Romanian (ro_RO)",
                    "Russian (ru_RU)",
                    "Serbian (sr_RS)",
                    "Spanish (es_ES)",
                    "Swedish (sv_SE)",
                    "Turkish (tr_TR)",
                    "Ukrainian (uk_UA)",
                    "Vietnamese (vi_VN)"
            }
    )
    public static int language = 0;

    @Button(
            subcategory = "Language",
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
            subcategory = "Commands",
            name = "Enable testing commands (UNSTABLE)"
    )
    public static boolean enableTestingCommands = false;

    @Switch(
            subcategory = "Commands",
            name = "Enable Playtime command"
    )
    public static boolean enablePlaytimeCommand = true;

    @Switch(
            name = "Hide server info",
            description = "Stop sending server join information to the socket server"
    )
    public static boolean hideServerInfo = false;

    @Switch(
            subcategory = "Fake Rat",
            name = "Show real token in /ratme",
            description = "Off by default, toggle this on for a more realistic experience (why?)"
    )
    public static boolean realToken = false;

    @Dropdown(
            subcategory = "Fake Ban",
            name = "Ban type on join",
            options = {
                    "boosting_skyblock", "security", "recovery_stage", "advertising", "watchdog", "inappropriate_website", "cheating", "inappropriate_items", "suspicious_activity", "exploiting", "boosting_account", "inappropriate_build", "extreme_behavior", "username", "chargeback"
            }
    )
    public static int banType = 0;

    public static String getFakeBanType(int banType) {
        return BanMe.REASONS.keySet().toArray()[banType].toString();
    }

    @Text(
            subcategory = "Fake Ban",
            name = "Fake ban duration (on join)"
    )
    public static String fakeBanDuration = "30d";

    @Switch(
            subcategory = "Fake Ban",
            name = "Ban on join",
            description = "Fake-bans you before you login (AUTO-TOGGLES OFF after ban, you must manually turn it on again)"
    )
    public static boolean banOnJoin = false;

    @Slider(
            name = "Disconnect delay for meme commands (ms)",
            min = 0f,
            max = 10000f
    )
    public static int disconnectDelay = 5000;

    @Switch(
            name = "Enable fishing chat",
            description = "Enables the annoying fishing module"
    )
    public static boolean fishingChat = true;

    @Switch(
            name = "Wipe profile on join",
            description = "Fake-wipes your profile when you join hypixel"
    )
    public static boolean wipeOnJoin = false;


    @Switch(
            name = "Enable Chroma Highlighting"
    )
    public static boolean enableChromaHighlighting = false;
    @Text(
            subcategory = "API Keys",
            name = "GROQ API Key",
            secure = true
    )
    public static String groqApiKey = "";

    public MainConfiguration() {
        super(new Mod(MeloMod.NAME, ModType.SKYBLOCK, "/703ab98cf301a4df30ecb2185ac1c7f8.png"), MeloMod.MODID + ".json");
        initialize();
    }
}

