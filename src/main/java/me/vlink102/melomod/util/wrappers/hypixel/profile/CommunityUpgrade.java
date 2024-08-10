package me.vlink102.melomod.util.wrappers.hypixel.profile;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.UUID;

public class CommunityUpgrade {
    private final String upgradeName;
    private final Integer tier;
    private final Long startedMS;
    private final Long claimedMS;
    private final UUID startedBy;
    private final UUID claimedBy;
    private final Boolean fastTracked;

    @Deprecated
    public CommunityUpgrade(String upgradeName, int tier, long startedMS, long claimedMS, UUID startedBy, UUID claimedBy, boolean fastTracked) {
        this.upgradeName = upgradeName;
        this.tier = tier;
        this.startedMS = startedMS;
        this.claimedMS = claimedMS;
        this.startedBy = startedBy;
        this.claimedBy = claimedBy;
        this.fastTracked = fastTracked;
    }

    public CommunityUpgrade(JsonObject object) {
        this.upgradeName = SkyblockUtil.getAsString("upgrade", object);
        this.tier = SkyblockUtil.getAsInteger("tier", object);
        this.startedMS = SkyblockUtil.getAsLong("started_ms", object);
        this.claimedMS = SkyblockUtil.getAsLong("claimed_ms", object);
        this.claimedBy = SkyblockUtil.fromString(SkyblockUtil.getAsString("claimed_by", object));
        this.fastTracked = SkyblockUtil.getAsBoolean("fast_tracked", object);
        this.startedBy = SkyblockUtil.fromString(SkyblockUtil.getAsString("started_by", object));
    }

    public int getTier() {
        return tier;
    }

    public long getClaimedMS() {
        return claimedMS;
    }

    public long getStartedMS() {
        return startedMS;
    }

    public String getUpgradeName() {
        return upgradeName;
    }

    public UUID getClaimedBy() {
        return claimedBy;
    }

    public UUID getStartedBy() {
        return startedBy;
    }

    public boolean isFastTracked() {
        return fastTracked;
    }
}
