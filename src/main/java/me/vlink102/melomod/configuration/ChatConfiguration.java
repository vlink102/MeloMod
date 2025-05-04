package me.vlink102.melomod.configuration;

import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.elements.SubConfig;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;

public class ChatConfiguration extends SubConfig {
    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable gay command"
    )
    public static boolean gay = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable racist command"
    )
    public static boolean racist = true;

    @Switch(
            name = "Enable other player mode",
            description = "If other mode is enabled, the (player) parameter on most commands will be available."
    )
    public static boolean runOthers = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable dice roll"
    )
    public static boolean diceRoll = true;

    @Switch(
            name = "Enable multiple dice rolls",
            description = "If this is enabled, the (count) parameter will be available."
    )
    public static boolean multiDiceRoll = true;


    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable pray command"
    )
    public static boolean pray = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable chess engine"
    )
    public static boolean chessEngine = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable math evaluation"
    )
    public static boolean mathEvaluation = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable guild command"
    )
    public static boolean guild = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable locate command"
    )
    public static boolean locate = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable coinflip command"
    )
    public static boolean coinFlip = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable stalk command"
    )
    public static boolean stalk = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable social media commands"
    )
    public static boolean socialMedia = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable secret command"
    )
    public static boolean secret = true;

    @Getter
    @Switch(
            subcategory = "Incomplete",
            name = "Enable whole party secret command"
    )
    public static boolean wholePartySecret = false;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "N-Word pass"
    )
    public static boolean nWordPass = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable femboy command"
    )
    public static boolean femboy = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable networth command"
    )
    public static boolean networth = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable username history"
    )
    public static boolean userNameHistory = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable last online info"
    )
    public static boolean lastOnlineInfo = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable FPS command"
    )
    public static boolean fps = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable Ping command"
    )
    public static boolean ping = true;

   /* @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable CPU temp command"
    )
    public static boolean temp = true;*/

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable CPU command"
    )
    public static boolean cpu = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable Internet command"
    )
    public static boolean internet = true;

    @Getter
    @Switch(
            subcategory = "Commands",
            name = "Enable Power command"
    )
    public static boolean power = true;

    @Switch(
            name = "Enable Chat Caching",
            description = "Responses by the mod will be accessible through the up/down arrows in the chat bar."
    )
    public static boolean chatCaching = true;

    @Getter
    @Switch(
            name = "Enable AI"
    )
    public static boolean ai = true;

    @Text(
            name = "Prefix"
    )
    public static String chatPrefix = "?";

    @Getter
    @Dropdown(
            name = "AI Model",
            options = {
                    "gemma2-9b-it",
                    "gemma-7b-it",
                    "llama-3.1-70b-versatile",
                    "llama-3.1-8b-instant",
                    "llama3-70b-8192",
                    "llama3-8b-8192",
                    "llama3-groq-70b-8192-tool-use-preview",
                    "llama3-groq-8b-8192-tool-use-preview",
                    "mixtral-8x7b-32768"
            }
    )
    public static int aiModel = 3;


    @Switch(
            name = "Enable ALL chat"
    )
    public static boolean allChat = true;

    @Switch(
            name = "Enable PARTY chat"
    )
    public static boolean partyChat = true;

    @Switch(
            name = "Enable GUILD chat"
    )
    public static boolean guildChat = true;

    @Switch(
            name = "Enable " + MeloMod.MODID + " chat"
    )
    public static boolean modChat = true;

    @Switch(
            name = "Enable COOP chat"
    )
    public static boolean coopChat = true;

    @Switch(
            name = "Enable OFFICER chat"
    )
    public static boolean officerChat = true;

    public ChatConfiguration() {
        super("Chat", MeloMod.MODID + "/" + "chat" + ".json");
        initialize();
    }
}
