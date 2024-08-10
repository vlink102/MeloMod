package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class WyldWoods {
    @Getter
    private final List<String> talkedThreeBrothers;
    private final Boolean siriusStartedQA;
    private final Boolean siriusQAChainDone;
    private final Boolean siriusCompletedQA;
    private final Boolean siriusClaimedDoubloon;
    private final Integer bughunter_step;

    public WyldWoods(JsonObject object) {
        this.talkedThreeBrothers = new ArrayList<>();
        JsonArray talkedThreeBrothersArray = SkyblockUtil.getAsJsonArray("talked_threebrothers", object);
        for (JsonElement jsonElement : talkedThreeBrothersArray) {
            talkedThreeBrothers.add(jsonElement.getAsString());
        }
        this.siriusStartedQA = SkyblockUtil.getAsBoolean("sirius_started_q_a", object);
        this.siriusQAChainDone = SkyblockUtil.getAsBoolean("sirius_q_a_chain_done", object);
        this.siriusCompletedQA = SkyblockUtil.getAsBoolean("sirius_completed_q_a", object);
        this.siriusClaimedDoubloon = SkyblockUtil.getAsBoolean("sirius_claimed_doubloon", object);
        this.bughunter_step = SkyblockUtil.getAsInteger("bighunter_step", object);
    }

    public int getBughunter_step() {
        return bughunter_step;
    }

    public boolean isSiriusClaimedDoubloon() {
        return siriusClaimedDoubloon;
    }

    public boolean isSiriusCompletedQA() {
        return siriusCompletedQA;
    }

    public boolean isSiriusQAChainDone() {
        return siriusQAChainDone;
    }

    public boolean isSiriusStartedQA() {
        return siriusStartedQA;
    }

}
