package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Levelling {
    private final Integer experience;
    @Getter
    private final HashMap<String, Integer> completions;
    @Getter
    private final List<String> completed;
    private final Boolean migratedCompletions;
    @Getter
    private final List<String> completedTasks;
    @Getter
    private final String highestPetScore;
    private final Integer miningFiestaOresMined;
    private final Integer fishingFestivalSharksKilled;
    private final Boolean migrated;
    private final Boolean migratedCompletions2;
    @Getter
    private final List<String> lastViewedTasks;
    private final Boolean claimedTalisman;
    @Getter
    private final String bopBonus;
    private final Boolean categoryExpanded;
    @Getter
    private final List<String> emblemUnlocks;
    @Getter
    private final String taskSort;

    public Levelling(JsonObject object) {
        this.experience = SkyblockUtil.getAsInteger("experience", object);
        this.completions = new HashMap<>();
        JsonObject completionsObject = SkyblockUtil.getAsJsonObject("completions", object);
        for (Map.Entry<String, JsonElement> entry : completionsObject.entrySet()) {
            String string = entry.getKey();
            completions.put(string, SkyblockUtil.getAsInteger(string, completionsObject));
        }
        this.completed = new ArrayList<>();
        JsonArray completedArray = SkyblockUtil.getAsJsonArray("completed", object);
        for (JsonElement jsonElement : completedArray) {
            completed.add(jsonElement.getAsString());
        }
        this.migratedCompletions = SkyblockUtil.getAsBoolean("migrated_completions", object);
        this.completedTasks = new ArrayList<>();
        JsonArray completedTasksArray = SkyblockUtil.getAsJsonArray("completed_tasks", object);
        for (JsonElement jsonElement : completedTasksArray) {
            completedTasks.add(jsonElement.getAsString());
        }
        this.highestPetScore = SkyblockUtil.getAsString("highest_pet_score", object);
        this.miningFiestaOresMined = SkyblockUtil.getAsInteger("mining_fiesta_ores_mined", object);
        this.fishingFestivalSharksKilled = SkyblockUtil.getAsInteger("fishing_festival_sharks_killed", object);
        this.migrated = SkyblockUtil.getAsBoolean("migrated", object);
        this.migratedCompletions2 = SkyblockUtil.getAsBoolean("migrated_completions2", object);
        this.lastViewedTasks = new ArrayList<>();
        JsonArray lastViewedTasksArray = SkyblockUtil.getAsJsonArray("last_viewed_tasks", object);
        for (JsonElement jsonElement : lastViewedTasksArray) {
            lastViewedTasks.add(jsonElement.getAsString());
        }
        this.claimedTalisman = SkyblockUtil.getAsBoolean("claimed_talisman", object);
        this.bopBonus = SkyblockUtil.getAsString("bop_bonus", object);
        this.categoryExpanded = SkyblockUtil.getAsBoolean("category_expanded", object);
        this.emblemUnlocks = new ArrayList<>();
        JsonArray emblemUnlocksArray = SkyblockUtil.getAsJsonArray("emblem_unlocks", object);
        for (JsonElement jsonElement : emblemUnlocksArray) {
            emblemUnlocks.add(jsonElement.getAsString());
        }
        this.taskSort = SkyblockUtil.getAsString("task_sort", object);
    }

    public int getExperience() {
        return experience;
    }

    public int getFishingFestivalSharksKilled() {
        return fishingFestivalSharksKilled;
    }

    public int getMiningFiestaOresMined() {
        return miningFiestaOresMined;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public boolean isClaimedTalisman() {
        return claimedTalisman;
    }

    public boolean isCategoryExpanded() {
        return categoryExpanded;
    }

    public boolean isMigratedCompletions() {
        return migratedCompletions;
    }

    public boolean isMigratedCompletions2() {
        return migratedCompletions2;
    }
}
