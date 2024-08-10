package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Bestiary {
    private final Boolean migratedStats;
    private final Boolean migration;
    private final HashMap<String, Integer> kills;
    // todo complete

    public Bestiary(JsonObject object) {
        this.migratedStats = SkyblockUtil.getAsBoolean("migrated_stats", object);
        this.migration = SkyblockUtil.getAsBoolean("migration", object);
        this.kills = new HashMap<>();
        JsonObject killsObject = SkyblockUtil.getAsJsonObject("kills", object);
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : killsObject.entrySet()) {
            String string = stringJsonElementEntry.getKey();
            if (Objects.equals(string, "last_killed_mob")) {
                continue;
            }
            kills.put(string, killsObject.get(string).getAsInt());
        }
    }

    public HashMap<String, Integer> getKills() {
        return kills;
    }

    public boolean isMigration() {
        return migration;
    }

    public boolean isMigratedStats() {
        return migratedStats;
    }
}
