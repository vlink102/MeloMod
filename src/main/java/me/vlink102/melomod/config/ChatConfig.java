package me.vlink102.melomod.config;

import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.elements.SubConfig;
import me.vlink102.melomod.MeloMod;

import java.util.function.Supplier;

public class ChatConfig extends SubConfig {
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
            name = "Enable fishing chat"
    )
    public static boolean fishingChat = true;

    @Switch(
            name = "Enable Chat Caching",
            description = "Responses by the mod will be accessible through the up/down arrows in the chat bar."
    )
    public static boolean chatCaching = true;

    @Text(
            name = "Prefix"
    )
    public static String chatPrefix = "?";

    public ChatConfig() {
        super("Chat", MeloMod.MODID + "/" + "chat" + ".json");
        initialize();
    }
}
