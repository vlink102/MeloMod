package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessoryBagStorage {
    private final Tuning tuning;
    private final String selectedPower;
    private final List<String> unlockedPowers;
    private final Integer bagUpgradesPurchased;
    private final Integer highestMagicalPower;

    public AccessoryBagStorage(JsonObject object) {
        this.tuning = new Tuning(SkyblockUtil.getAsJsonObject("tuning", object));
        this.selectedPower = SkyblockUtil.getAsString("selected_power", object);
        this.bagUpgradesPurchased = SkyblockUtil.getAsInteger("bag_upgrades_purchased", object);
        this.unlockedPowers = new ArrayList<>();
        JsonArray unlockedPowersArray = SkyblockUtil.getAsJsonArray("unlocked_powers", object);
        for (JsonElement jsonElement : unlockedPowersArray) {
            unlockedPowers.add(jsonElement.getAsString());
        }
        this.highestMagicalPower = SkyblockUtil.getAsInteger("highest_magical_power", object);
    }

    public int getBagUpgradesPurchased() {
        return bagUpgradesPurchased;
    }

    public int getHighestMagicalPower() {
        return highestMagicalPower;
    }

    public List<String> getUnlockedPowers() {
        return unlockedPowers;
    }

    public String getSelectedPower() {
        return selectedPower;
    }

    public Tuning getTuning() {
        return tuning;
    }

    public static class Tuning {
        private final HashMap<String, Integer> slot0;

        public Tuning(JsonObject object) {
            this.slot0 = new HashMap<>();
            JsonObject slot0Object = SkyblockUtil.getAsJsonObject("slot_0", object);
            for (Map.Entry<String, JsonElement> entry : slot0Object.entrySet()) {
                String string = entry.getKey();
                slot0.put(string, SkyblockUtil.getAsInteger(string, slot0Object));
            }
        }

        public HashMap<String, Integer> getSlot0() {
            return slot0;
        }
    }

}
