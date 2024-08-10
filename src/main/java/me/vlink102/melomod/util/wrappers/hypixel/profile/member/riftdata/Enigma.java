package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class Enigma {
    private final Boolean boughtCloak;
    @Getter
    private final List<String> foundSouls;
    private final Integer claimedBonusIndex;

    public Enigma(JsonObject object) {
        this.boughtCloak = SkyblockUtil.getAsBoolean("bought_cloak", object);
        this.claimedBonusIndex = SkyblockUtil.getAsInteger("claimed_bonus_index", object);
        this.foundSouls = new ArrayList<>();
        JsonArray foundSoulsArray = SkyblockUtil.getAsJsonArray("found_souls", object);
        for (JsonElement foundSoul : foundSoulsArray) {
            foundSouls.add(foundSoul.getAsString());
        }
    }

    public boolean isBoughtCloak() {
        return boughtCloak;
    }

    public int getClaimedBonusIndex() {
        return claimedBonusIndex;
    }
}
