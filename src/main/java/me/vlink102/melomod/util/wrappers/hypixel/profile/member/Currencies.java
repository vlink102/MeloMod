package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.HashMap;
import java.util.Map;

public class Currencies {
    private final Double coinPurse;
    private final Double motesPurse;
    private final HashMap<String, Integer> essence;

    public Currencies(JsonObject object) {
        this.coinPurse = SkyblockUtil.getAsDouble("coin_purse", object);
        this.motesPurse = SkyblockUtil.getAsDouble("motes_purse", object);
        this.essence = new HashMap<>();
        JsonObject essenceObject = SkyblockUtil.getAsJsonObject("essence", object);
        for (Map.Entry<String, JsonElement> entry : essenceObject.entrySet()) {
            String string = entry.getKey();
            essence.put(string, SkyblockUtil.getAsInteger("current", SkyblockUtil.getAsJsonObject(string, essenceObject)));
        }
    }

    public double getCoinPurse() {
        return coinPurse;
    }

    public double getMotesPurse() {
        return motesPurse;
    }

    public HashMap<String, Integer> getEssence() {
        return essence;
    }
}
