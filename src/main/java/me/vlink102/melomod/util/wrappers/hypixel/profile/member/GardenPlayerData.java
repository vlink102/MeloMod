package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class GardenPlayerData {
    private final Integer copper;
    private final Integer larvaConsumed;

    public GardenPlayerData(JsonObject object) {
        this.copper = SkyblockUtil.getAsInteger("copper", object);
        this.larvaConsumed = SkyblockUtil.getAsInteger("larva_consumed", object);
    }

    public int getCopper() {
        return copper;
    }

    public int getLarvaConsumed() {
        return larvaConsumed;
    }
}
