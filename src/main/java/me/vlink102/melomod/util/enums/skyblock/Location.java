package me.vlink102.melomod.util.enums.skyblock;

public enum Location {
    PRIVATE_ISLAND("dynamic"),
    GARDEN("garden"),
    HUB("hub"),
    BARN("farming_1"),
    PARK("foraging_1"),
    SPIDER_DEN("combat_1"),
    END("combat_3"),
    CRIMSON_ISLE("crimson_isle"),
    GOLD_MINE("mining_1"),
    DEEP_CAVERNS("mining_2"),
    DWARVEN_MINES("mining_3"),
    CRYSTAL_HOLLOWS("crystal_hollows"),
    WINTER(""), // TODO
    DUNGEON_HUB("dungeon_hub"),
    RIFT("rift"),
    DARK_AUCTION(""); // TODO

    private final String internal;

    Location(String internal) {
        this.internal = internal;
    }

    public static Location parseFromLocraw(String locrawGamemode) {
        if (locrawGamemode == null) return null;
        for (Location value : Location.values()) {
            if (locrawGamemode.equalsIgnoreCase(value.getInternal())) {
                return value;
            }
        }
        return null;
    }

    public String getInternal() {
        return internal;
    }
}
