package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class Castle {
    private final Boolean unlockedPathwaySkip;
    private final Integer fairyStep;
    private final Integer grubberStacks;

    public Castle(JsonObject object) {
        this.unlockedPathwaySkip = SkyblockUtil.getAsBoolean("unlocked_pathway_skip", object);
        this.fairyStep = SkyblockUtil.getAsInteger("fairy_step", object);
        this.grubberStacks = SkyblockUtil.getAsInteger("grubber_stacks", object);
    }

    public int getFairyStep() {
        return fairyStep;
    }

    public int getGrubberStacks() {
        return grubberStacks;
    }

    public boolean isUnlockedPathwaySkip() {
        return unlockedPathwaySkip;
    }
}
