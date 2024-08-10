package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class ItemData {
    private final Integer soulflow;
    private final Integer favoriteArrow;

    public ItemData(JsonObject object) {
        this.soulflow = SkyblockUtil.getAsInteger("soulflow", object);
        this.favoriteArrow = SkyblockUtil.getAsInteger("favorite_arrow", object);
    }

    public int getFavoriteArrow() {
        return favoriteArrow;
    }

    public int getSoulflow() {
        return soulflow;
    }
}
