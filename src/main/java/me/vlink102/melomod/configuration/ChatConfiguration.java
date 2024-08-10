package me.vlink102.melomod.configuration;

import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.elements.SubConfig;
import me.vlink102.melomod.MeloMod;

public class ChatConfiguration extends SubConfig {
    @Switch(
            name = "Enable gay command"
    )
    public static boolean gay = true;

    public static boolean isGay() {
        return gay;
    }

    @Switch(
            name = "Enable racist command"
    )
    public static boolean racist = true;

    public static boolean isRacist() {
        return racist;
    }

    @Switch(
            name = "Enable other mode"
    )
    public static boolean runOthers = true;

    @Switch(
            name = "Enable dice roll"
    )
    public static boolean diceRoll = true;

    public static boolean isDiceRoll() {
        return diceRoll;
    }

    @Switch(
            name = "Enable multiple dice rolls"
    )
    public static boolean multiDiceRoll = true;


    @Switch(
            name = "Enable pray command"
    )
    public static boolean pray = true;

    public static boolean isPray() {
        return pray;
    }

    @Switch(
            name = "Enable chess engine"
    )
    public static boolean chessEngine = true;

    public static boolean isChessEngine() {
        return chessEngine;
    }

    @Switch(
            name = "Enable math evaluation"
    )
    public static boolean mathEvaluation = true;

    public static boolean isMathEvaluation() {
        return mathEvaluation;
    }

    @Switch(
            name = "Enable guild command"
    )
    public static boolean guild = true;

    public static boolean isGuild() {
        return guild;
    }

    @Switch(
            name = "Enable locate command"
    )
    public static boolean locate = true;

    public static boolean isLocate() {
        return locate;
    }

    @Switch(
            name = "Enable coinflip command"
    )
    public static boolean coinFlip = true;

    public static boolean isCoinFlip() {
        return coinFlip;
    }

    @Switch(
            name = "Enable stalk command"
    )
    public static boolean stalk = true;

    public static boolean isStalk() {
        return stalk;
    }

    @Switch(
            name = "Enable social media commands"
    )
    public static boolean socialMedia = true;

    public static boolean isSocialMedia() {
        return socialMedia;
    }

    @Switch(
            name = "Enable secret command"
    )
    public static boolean secret = true;

    public static boolean isSecret() {
        return secret;
    }

    @Switch(
            name = "Enable whole party secret command"
    )
    public static boolean wholePartySecret = true;
    public static boolean isWholePartySecret() {
        return wholePartySecret;
    }

    @Switch(
            name = "N-Word pass"
    )
    public static boolean nWordPass = true;
    public static boolean isnWordPass() {
        return nWordPass;
    }

    @Switch(
            name = "Enable femboy command"
    )
    public static boolean femboy = true;

    public static boolean isFemboy() {
        return femboy;
    }

    @Switch(
            name = "Enable networth command"
    )
    public static boolean networth = true;

    public static boolean isNetworth() {
        return networth;
    }

    @Switch(
            name = "Enable username history"
    )
    public static boolean userNameHistory = true;

    public static boolean isUserNameHistory() {
        return userNameHistory;
    }

    @Switch(
            name = "Enable last online info"
    )
    public static boolean lastOnlineInfo = true;

    public static boolean isLastOnlineInfo() {
        return lastOnlineInfo;
    }

    @Switch(
            name = "Enable fishing chat"
    )
    public static boolean fishingChat = true;

    @Switch(
            name = "Enable Chat Caching",
            description = "Responses by the mod will be accessible through the up/down arrows in the chat bar."
    )
    public static boolean chatCaching = true;

    @Switch(
            name = "Enable AI"
    )
    public static boolean ai = true;

    public static boolean isAi() {
        return ai;
    }

    @Text(
            name = "Prefix"
    )
    public static String chatPrefix = "?";

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


    public static int getAiModel() {
        return aiModel;
    }

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
