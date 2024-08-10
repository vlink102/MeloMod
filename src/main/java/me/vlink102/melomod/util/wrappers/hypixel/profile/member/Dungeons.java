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

@Getter
public class Dungeons {
    private final Catacombs catacombs;
    private final Catacombs masterCatacombs;
    private final HashMap<String, Double> playerClasses;
    private final List<String> unlockedJournals;
    private final List<String> dungeonsBlahBlah;
    private final Integer secrets;

    private final DailyRuns dailyRuns;

    public Dungeons(JsonObject object) {
        JsonObject typeObject = SkyblockUtil.getAsJsonObject("dungeon_types", object);
        this.catacombs = new Catacombs(SkyblockUtil.getAsJsonObject("catacombs", typeObject), false);
        this.masterCatacombs = new Catacombs(SkyblockUtil.getAsJsonObject("master_catacombs", typeObject), true);
        this.playerClasses = new HashMap<>();
        JsonObject playerClassesObject = SkyblockUtil.getAsJsonObject("player_classes", object);
        for (Map.Entry<String, JsonElement> entry : playerClassesObject.entrySet()) {
            String string = entry.getKey();
            playerClasses.put(string, SkyblockUtil.getAsDouble(string, playerClassesObject));
        }
        this.unlockedJournals = new ArrayList<>();
        this.dungeonsBlahBlah = new ArrayList<>();
        this.dailyRuns = new DailyRuns(SkyblockUtil.getAsJsonObject("daily_runs", object));
        JsonArray unlockedJournalsArray = SkyblockUtil.getAsJsonArray("unlocked_journals", SkyblockUtil.getAsJsonObject("dungeon_journal", object));
        for (JsonElement jsonElement : unlockedJournalsArray) {
            unlockedJournals.add(jsonElement.getAsString());
        }
        JsonArray dungeonsBlahBlahArray = SkyblockUtil.getAsJsonArray("dungeons_blah_blah", object);
        for (JsonElement jsonElement : dungeonsBlahBlahArray) {
            dungeonsBlahBlah.add(jsonElement.getAsString());
        }
        // todo
        this.secrets = SkyblockUtil.getAsInteger("secrets", object);
    }

    // TODO line 8991

    public static class DailyRuns {
        private final Integer currentDayStamp;
        private final Integer completedRunsCount;

        public DailyRuns(JsonObject object) {
            this.currentDayStamp = SkyblockUtil.getAsInteger("current_day_stamp", object);
            this.completedRunsCount = SkyblockUtil.getAsInteger("completed_runs_count", object);
        }

        public int getCompletedRunsCount() {
            return completedRunsCount;
        }

        public int getCurrentDayStamp() {
            return currentDayStamp;
        }

    }

    public static class Catacombs {
        @Getter
        private final HashMap<String, Float> mobsKilled;
        @Getter
        private final HashMap<String, Float> fastestTimeS;
        @Getter
        private final HashMap<String, Float> mostDamageTank;
        @Getter
        private final HashMap<String, Float> fastestTime;
        @Getter
        private final HashMap<String, Float> mostDamageMage;
        @Getter
        private final HashMap<String, Float> tierCompletions;
        @Getter
        private final HashMap<String, Float> mostDamageHealer;
        @Getter
        private final HashMap<String, Float> mostDamageArcher;
        @Getter
        private final HashMap<String, Float> watcherKills;
        @Getter
        private final HashMap<String, Float> mostHealing;
        @Getter
        private final HashMap<String, Float> bestScore;
        @Getter
        private final HashMap<String, Float> mostDamageBerserk;
        @Getter
        private final HashMap<String, Float> fastestTimeSPlus;
        @Getter
        private final HashMap<String, Float> mostMobsKilled;
        @Getter
        private final HashMap<String, Float> timesPlayed;
        @Getter
        private final HashMap<String, Float> milestoneCompletions;
        @Getter
        private final Double experience;
        @Getter
        private final HashMap<String, List<Catacombs.BestRun>> bestRuns;
        private final Boolean master;

        public Catacombs(JsonObject object, boolean master) {
            this.mobsKilled = new HashMap<>();
            JsonObject mobsKilledObject = SkyblockUtil.getAsJsonObject("mobs_killed", object);
            for (Map.Entry<String, JsonElement> entry : mobsKilledObject.entrySet()) {
                String string = entry.getKey();
                mobsKilled.put(string, SkyblockUtil.getAsFloat(string, mobsKilledObject));
            }
            this.fastestTimeS = new HashMap<>();
            JsonObject fastestTimeSObject = SkyblockUtil.getAsJsonObject("fastest_time_s", object);
            for (Map.Entry<String, JsonElement> entry : fastestTimeSObject.entrySet()) {
                String string = entry.getKey();
                fastestTimeS.put(string, SkyblockUtil.getAsFloat(string, fastestTimeSObject));
            }
            this.mostDamageTank = new HashMap<>();
            JsonObject mostDamageTankObject = SkyblockUtil.getAsJsonObject("most_damage_tank", object);
            for (Map.Entry<String, JsonElement> entry : mostDamageTankObject.entrySet()) {
                String string = entry.getKey();
                mostDamageTank.put(string, SkyblockUtil.getAsFloat(string, mostDamageTankObject));
            }
            this.fastestTime = new HashMap<>();
            JsonObject fastestTimeObject = SkyblockUtil.getAsJsonObject("fastest_time", object);
            for (Map.Entry<String, JsonElement> entry : fastestTimeObject.entrySet()) {
                String string = entry.getKey();
                fastestTime.put(string, SkyblockUtil.getAsFloat(string, fastestTimeObject));
            }
            this.mostDamageMage = new HashMap<>();
            JsonObject mostDamageMageObject = SkyblockUtil.getAsJsonObject("most_damage_mage", object);
            for (Map.Entry<String, JsonElement> entry : mostDamageMageObject.entrySet()) {
                String string = entry.getKey();
                mostDamageMage.put(string, SkyblockUtil.getAsFloat(string, mostDamageMageObject));
            }
            this.tierCompletions = new HashMap<>();
            JsonObject tierCompletionsObject = SkyblockUtil.getAsJsonObject("tier_completions", object);
            for (Map.Entry<String, JsonElement> entry : tierCompletionsObject.entrySet()) {
                String string = entry.getKey();
                tierCompletions.put(string, SkyblockUtil.getAsFloat(string, tierCompletionsObject));
            }
            this.mostDamageHealer = new HashMap<>();
            JsonObject mostDamageHealerObject = SkyblockUtil.getAsJsonObject("most_damage_healer", object);
            for (Map.Entry<String, JsonElement> entry : mostDamageHealerObject.entrySet()) {
                String string = entry.getKey();
                mostDamageHealer.put(string, SkyblockUtil.getAsFloat(string, mostDamageHealerObject));
            }
            this.mostDamageArcher = new HashMap<>();
            JsonObject mostDamageArcherObject = SkyblockUtil.getAsJsonObject("most_damage_archer", object);
            for (Map.Entry<String, JsonElement> entry : mostDamageArcherObject.entrySet()) {
                String string = entry.getKey();
                mostDamageArcher.put(string, SkyblockUtil.getAsFloat(string, mostDamageArcherObject));
            }
            this.watcherKills = new HashMap<>();
            JsonObject watcherKillsObject = SkyblockUtil.getAsJsonObject("watcher_kills", object);
            for (Map.Entry<String, JsonElement> entry : watcherKillsObject.entrySet()) {
                String string = entry.getKey();
                watcherKills.put(string, SkyblockUtil.getAsFloat(string, watcherKillsObject));
            }
            this.mostHealing = new HashMap<>();
            JsonObject mostHealingObject = SkyblockUtil.getAsJsonObject("most_healing", object);
            for (Map.Entry<String, JsonElement> entry : mostHealingObject.entrySet()) {
                String string = entry.getKey();
                mostHealing.put(string, SkyblockUtil.getAsFloat(string, mostHealingObject));
            }
            this.bestScore = new HashMap<>();
            JsonObject bestScoreObject = SkyblockUtil.getAsJsonObject("best_score", object);
            for (Map.Entry<String, JsonElement> entry : bestScoreObject.entrySet()) {
                String string = entry.getKey();
                bestScore.put(string, SkyblockUtil.getAsFloat(string, bestScoreObject));
            }
            this.mostDamageBerserk = new HashMap<>();
            JsonObject mostDamageBerserkObject = SkyblockUtil.getAsJsonObject("most_damage_berserk", object);
            for (Map.Entry<String, JsonElement> entry : mostDamageBerserkObject.entrySet()) {
                String string = entry.getKey();
                mostDamageBerserk.put(string, SkyblockUtil.getAsFloat(string, mostDamageBerserkObject));
            }
            this.fastestTimeSPlus = new HashMap<>();
            JsonObject fastestTimeSPlusObject = SkyblockUtil.getAsJsonObject("fastest_time_s_plus", object);
            for (Map.Entry<String, JsonElement> entry : fastestTimeSPlusObject.entrySet()) {
                String string = entry.getKey();
                fastestTimeSPlus.put(string, SkyblockUtil.getAsFloat(string, fastestTimeSPlusObject));
            }
            this.mostMobsKilled = new HashMap<>();
            JsonObject mostMobsKilledObject = SkyblockUtil.getAsJsonObject("most_mobs_killed", object);
            for (Map.Entry<String, JsonElement> entry : mostMobsKilledObject.entrySet()) {
                String string = entry.getKey();
                mostMobsKilled.put(string, SkyblockUtil.getAsFloat(string, mostMobsKilledObject));
            }
            this.timesPlayed = new HashMap<>();
            JsonObject timesPlayedObject = SkyblockUtil.getAsJsonObject("times_played", object);
            for (Map.Entry<String, JsonElement> entry : timesPlayedObject.entrySet()) {
                String string = entry.getKey();
                timesPlayed.put(string, SkyblockUtil.getAsFloat(string, timesPlayedObject));
            }
            this.milestoneCompletions = new HashMap<>();
            JsonObject milestoneCompletionsObject = SkyblockUtil.getAsJsonObject("milestone_completions", object);
            for (Map.Entry<String, JsonElement> entry : milestoneCompletionsObject.entrySet()) {
                String string = entry.getKey();
                milestoneCompletions.put(string, SkyblockUtil.getAsFloat(string, milestoneCompletionsObject));
            }
            this.experience = SkyblockUtil.getAsDouble("experience", object);
            this.bestRuns = new HashMap<>();
            JsonObject bestRunsObject = SkyblockUtil.getAsJsonObject("best_runs", object);
            for (Map.Entry<String, JsonElement> entry : bestRunsObject.entrySet()) {
                String string = entry.getKey();
                bestRuns.put(string, new ArrayList<>());
                JsonArray bestRunsArray = bestRunsObject.getAsJsonArray(string);
                for (JsonElement jsonElement : bestRunsArray) {
                    bestRuns.get(string).add(new Catacombs.BestRun(jsonElement.getAsJsonObject()));
                }
            }
            this.master = master;
        }

        public boolean isMaster() {
            return master;
        }

        public static class BestRun {
            private final Long timeStamp;
            private final Integer scoreExploration;
            private final Integer scoreSpeed;
            private final Integer scoreSkill;
            private final Integer scoreBonus;
            @Getter
            private final String dungeonClass;
            @Getter
            private final List<String> teammates;
            private final Integer elapsedTime;
            private final Double damageDealt;
            private final Integer deaths;
            private final Integer mobsKilled;
            private final Integer secretsFound;
            private final Integer damageMitigated;
            private final Integer allyHealing;

            public BestRun(JsonObject object) {
                this.timeStamp = SkyblockUtil.getAsLong("time_stamp", object);
                this.scoreExploration = SkyblockUtil.getAsInteger("score_exploration", object);
                this.scoreSpeed = SkyblockUtil.getAsInteger("score_speed", object);
                this.scoreSkill = SkyblockUtil.getAsInteger("score_skill", object);
                this.scoreBonus = SkyblockUtil.getAsInteger("score_bonus", object);
                this.dungeonClass = SkyblockUtil.getAsString("dungeon_class", object);
                this.teammates = new ArrayList<>();
                JsonArray teammatesArray = SkyblockUtil.getAsJsonArray("teammates", object);
                for (JsonElement jsonElement : teammatesArray) {
                    teammates.add(jsonElement.getAsString());
                }
                this.elapsedTime = SkyblockUtil.getAsInteger("elapsed_time", object);
                this.damageDealt = SkyblockUtil.getAsDouble("damage_dealt", object);
                this.deaths = SkyblockUtil.getAsInteger("deaths", object);
                this.mobsKilled = SkyblockUtil.getAsInteger("mobs_killed", object);
                this.secretsFound = SkyblockUtil.getAsInteger("secrets_found", object);
                this.damageMitigated = SkyblockUtil.getAsInteger("damage_mitigated", object);
                this.allyHealing = SkyblockUtil.getAsInteger("ally_healing", object);
            }

            public double getDamageDealt() {
                return damageDealt;
            }

            public int getAllyHealing() {
                return allyHealing;
            }

            public int getDamageMitigated() {
                return damageMitigated;
            }

            public int getDeaths() {
                return deaths;
            }

            public int getElapsedTime() {
                return elapsedTime;
            }

            public int getMobsKilled() {
                return mobsKilled;
            }

            public int getScoreExploration() {
                return scoreExploration;
            }

            public int getScoreSkill() {
                return scoreSkill;
            }

            public int getScoreBonus() {
                return scoreBonus;
            }

            public int getScoreSpeed() {
                return scoreSpeed;
            }

            public int getSecretsFound() {
                return secretsFound;
            }

            public long getTimeStamp() {
                return timeStamp;
            }

        }
    }

}
