package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class BlackLagoon {
    private final Boolean talkedToEdwin;
    private final Boolean receivedSciencePaper;
    private final Integer completedStep;
    private final Boolean deliveredSciencePaper;

    public BlackLagoon(JsonObject object) {
        this.talkedToEdwin = SkyblockUtil.getAsBoolean("talked_to_edwin", object);
        this.receivedSciencePaper = SkyblockUtil.getAsBoolean("received_science_paper", object);
        this.completedStep = SkyblockUtil.getAsInteger("completed_step", object);
        this.deliveredSciencePaper = SkyblockUtil.getAsBoolean("delivered_science_paper", object);
    }

    public int getCompletedStep() {
        return completedStep;
    }

    public boolean isDeliveredSciencePaper() {
        return deliveredSciencePaper;
    }

    public boolean isReceivedSciencePaper() {
        return receivedSciencePaper;
    }

    public boolean isTalkedToEdwin() {
        return talkedToEdwin;
    }
}
