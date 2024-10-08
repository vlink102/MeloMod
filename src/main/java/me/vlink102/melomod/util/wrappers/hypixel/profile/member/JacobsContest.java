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

public class JacobsContest {
    @Getter
    private final HashMap<String, Integer> medalsInv;
    @Getter
    private final HashMap<String, Integer> perks;
    @Getter
    private final List<JacobsContestData> jacobsContestDataList;
    private final Boolean talked;
    @Getter
    private final HashMap<String, List<String>> uniqueBrackets;
    private final Boolean migration;
    @Getter
    private final HashMap<String, Integer> personalBests;

    public JacobsContest(JsonObject object) {
        this.medalsInv = new HashMap<>();
        JsonObject medalsInvObject = SkyblockUtil.getAsJsonObject("medals_inv", object);
        for (Map.Entry<String, JsonElement> entry : medalsInvObject.entrySet()) {
            String string = entry.getKey();
            medalsInv.put(string, SkyblockUtil.getAsInteger(string, medalsInvObject));
        }
        this.perks = new HashMap<>();
        JsonObject perksObject = SkyblockUtil.getAsJsonObject("perks", object);
        for (Map.Entry<String, JsonElement> entry : perksObject.entrySet()) {
            String string = entry.getKey();
            perks.put(string, SkyblockUtil.getAsInteger(string, perksObject));
        }
        this.jacobsContestDataList = new ArrayList<>();
        JsonArray jacobsContestDataListArray = SkyblockUtil.getAsJsonArray("contests", object);
        for (JsonElement jsonElement : jacobsContestDataListArray) {
            JsonObject contestObject = jsonElement.getAsJsonObject();
        }
        this.talked = SkyblockUtil.getAsBoolean("talked", object);
        this.uniqueBrackets = new HashMap<>();
        JsonObject uniqueBracketsObject = SkyblockUtil.getAsJsonObject("unique_brackets", object);
        for (Map.Entry<String, JsonElement> entry : uniqueBracketsObject.entrySet()) {
            String string = entry.getKey();
            uniqueBrackets.put(string, new ArrayList<>());
            JsonArray stringArray = uniqueBracketsObject.getAsJsonArray(string);
            for (JsonElement jsonElement : stringArray) {
                uniqueBrackets.get(string).add(jsonElement.getAsString());
            }
        }
        this.migration = SkyblockUtil.getAsBoolean("migration", object);
        this.personalBests = new HashMap<>();
        JsonObject personalBestsObject = SkyblockUtil.getAsJsonObject("personal_bests", object);
        for (Map.Entry<String, JsonElement> entry : personalBestsObject.entrySet()) {
            String string = entry.getKey();
            personalBests.put(string, SkyblockUtil.getAsInteger(string, personalBestsObject));
        }
    }

    public boolean isTalked() {
        return talked;
    }

    public boolean isMigration() {
        return migration;
    }

    public static class JacobsContestData {
        private final Integer collected;
        @Getter
        private final Boolean claimedRewards;
        private final Integer claimedPosition;
        @Getter
        private final String claimedMedal;
        private final Integer claimedParticipants;

        public JacobsContestData(JsonObject object) {
            this.collected = SkyblockUtil.getAsInteger("collected", object);
            this.claimedParticipants = SkyblockUtil.getAsInteger("claimed_participants", object);
            this.claimedMedal = SkyblockUtil.getAsString("claimed_medal", object);
            this.claimedPosition = SkyblockUtil.getAsInteger("claimed_position", object);
            this.claimedRewards = SkyblockUtil.getAsBoolean("claimed_rewards", object);
        }

        public int getClaimedParticipants() {
            return claimedParticipants;
        }

        public int getClaimedPosition() {
            return claimedPosition;
        }

        public int getCollected() {
            return collected;
        }
    }
}
