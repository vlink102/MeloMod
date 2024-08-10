package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.wrappers.hypixel.profile.member.playerdata.TempStatBuff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PlayerData {
    private final List<String> visitedZones;
    private final Long lastDeath;
    private final HashMap<String, Integer> perks;
    //private final List<Effect> activeEffects; // TODO
    //private final List<Effect> pausedEffects; // TODO
    private final Integer reaperPeppersEaten;
    private final List<TempStatBuff> buffs;
    private final Integer deathCount;
    //private final List<Effect> disabledPotionEffects; // TODO
    private final List<String> achievementSpawnedIslandTypes;
    private final List<String> visitedModes;
    private final List<String> unlockedCollTiers;
    private final List<String> craftedGenerators;
    private final Integer fishingTreasureCaught;
    private final HashMap<String, Double> experience;


    public PlayerData(JsonObject object) {
        this.visitedZones = new ArrayList<>();
        JsonArray visitedZonesArray = SkyblockUtil.getAsJsonArray("visited_zones", object);
        for (JsonElement jsonElement : visitedZonesArray) {
            visitedZones.add(jsonElement.getAsString());
        }
        this.lastDeath = SkyblockUtil.getAsLong("last_death", object);
        this.perks = new HashMap<>();
        JsonObject perksObject = SkyblockUtil.getAsJsonObject("perks", object);
        for (Map.Entry<String, JsonElement> entry : perksObject.entrySet()) {
            String string = entry.getKey();
            perks.put(string, SkyblockUtil.getAsInteger(string, perksObject));
        }
        this.reaperPeppersEaten = SkyblockUtil.getAsInteger("reaper_peppers_eaten", object);
        this.buffs = new ArrayList<>();
        JsonArray buffsArray = SkyblockUtil.getAsJsonArray("temp_stat_buffs", object);
        for (JsonElement jsonElement : buffsArray) {
            buffs.add(new TempStatBuff(jsonElement.getAsJsonObject()));
        }
        this.deathCount = SkyblockUtil.getAsInteger("death_count", object);
        this.achievementSpawnedIslandTypes = new ArrayList<>();
        JsonArray achievementSpawnedIslandTypesArray = SkyblockUtil.getAsJsonArray("achievement_spawned_island_types", object);
        for (JsonElement jsonElement : achievementSpawnedIslandTypesArray) {
            achievementSpawnedIslandTypes.add(jsonElement.getAsString());
        }
        this.visitedModes = new ArrayList<>();
        JsonArray visitedModesArray = SkyblockUtil.getAsJsonArray("visited_modes", object);
        for (JsonElement jsonElement : visitedModesArray) {
            visitedModes.add(jsonElement.getAsString());
        }
        this.unlockedCollTiers = new ArrayList<>();
        JsonArray unlockedCollTiersArray = SkyblockUtil.getAsJsonArray("unlocked_coll_tiers", object);
        for (JsonElement jsonElement : unlockedCollTiersArray) {
            unlockedCollTiers.add(jsonElement.getAsString());
        }
        this.craftedGenerators = new ArrayList<>();
        JsonArray craftedGeneratorsArray = SkyblockUtil.getAsJsonArray("crafted_generators", object);
        for (JsonElement jsonElement : craftedGeneratorsArray) {
            craftedGenerators.add(jsonElement.getAsString());
        }
        this.fishingTreasureCaught = SkyblockUtil.getAsInteger("fishing_treasure_caught", object);
        this.experience = new HashMap<>();
        JsonObject experienceObject = SkyblockUtil.getAsJsonObject("experience", object);
        for (Map.Entry<String, JsonElement> entry : experienceObject.entrySet()) {
            String string = entry.getKey();
            experience.put(string, SkyblockUtil.getAsDouble(string, experienceObject));
        }

    }


}
