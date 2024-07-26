package me.vlink102.melomod.config;

import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.elements.SubConfig;
import me.vlink102.melomod.MeloMod;

public class ChatConfig extends SubConfig {
    @Switch(
            name = "Enable gay command"
    )
    public static boolean gay = true;

    @Switch(
            name = "Enable racist command"
    )
    public static boolean racist = true;

    @Switch(
            name = "Enable other mode"
    )
    public static boolean runOthers = true;

    @Switch(
            name = "Enable dice roll"
    )
    public static boolean diceRoll = true;

    @Switch(
            name = "Enable double dice roll"
    )
    public static boolean doubleDiceRoll = true;

    @Switch(
            name = "Enable pray command"
    )
    public static boolean pray = true;

    @Switch(
            name = "Enable chess engine"
    )
    public static boolean chessEngine = true;

    @Switch(
            name = "Enable math evaluation"
    )
    public static boolean mathEvaluation = true;

    @Switch(
            name = "Enable guild command"
    )
    public static boolean guild = true;

    @Switch(
            name = "Enable locate command"
    )
    public static boolean locate = true;

    @Switch(
            name = "Enable fishing chat"
    )
    public static boolean fishingChat = true;

    public ChatConfig() {
        super("Chat", MeloMod.MODID + "/" + "chat" + ".json");
        initialize();
    }
}
