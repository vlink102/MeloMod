package me.vlink102.melomod.util.wrappers.hypixel.profile.member.playerdata;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class TempStatBuff {
    private final Integer stat;
    private final String key;
    private final Integer amount;
    private final Long expireAt;

    public TempStatBuff(JsonObject object) {
        this.stat = SkyblockUtil.getAsInteger("stat", object);
        this.key = SkyblockUtil.getAsString("key", object);
        this.amount = SkyblockUtil.getAsInteger("amount", object);
        this.expireAt = SkyblockUtil.getAsLong("expire_at", object);
    }

    public int getAmount() {
        return amount;
    }

    public int getStat() {
        return stat;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public String getKey() {
        return key;
    }
}
