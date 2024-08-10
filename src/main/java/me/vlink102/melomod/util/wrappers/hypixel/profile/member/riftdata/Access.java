package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class Access {
    private final Long lastFree;
    private final Boolean consumedPrism;

    public Access(JsonObject object) {
        this.lastFree = SkyblockUtil.getAsLong("last_free", object);
        this.consumedPrism = SkyblockUtil.getAsBoolean("consumed_prism", object);
    }

    public long getLastFree() {
        return lastFree;
    }

    public boolean isConsumedPrism() {
        return consumedPrism;
    }

}
