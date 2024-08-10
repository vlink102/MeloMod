package me.vlink102.melomod.util.enums.skyblock;

public enum Gamemode {
    NORMAL,
    IRONMAN,
    STRANDED,
    BINGO;

    public static Gamemode parseFromJSON(String gamemode) {
        if (gamemode == null) return null;
        if (gamemode.equalsIgnoreCase("ironman")) return IRONMAN;
        if (gamemode.equalsIgnoreCase("island")) return STRANDED;
        if (gamemode.equalsIgnoreCase("bingo")) return BINGO;
        return NORMAL;
    }
}
