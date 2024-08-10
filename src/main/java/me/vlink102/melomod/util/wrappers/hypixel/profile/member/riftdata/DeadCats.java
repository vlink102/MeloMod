package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.enums.skyblock.ItemRarity;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeadCats {
    private final Boolean talkedToJacquelle;
    private final Boolean pickedUpDetector;
    @Getter
    private final List<String> foundCats;
    private final Boolean unlockedPet;
    @Getter
    private final Montezuma montezuma;

    public DeadCats(JsonObject object) {
        this.talkedToJacquelle = SkyblockUtil.getAsBoolean("talked_to_jacquelle", object);
        this.pickedUpDetector = SkyblockUtil.getAsBoolean("picked_up_detector", object);
        this.foundCats = new ArrayList<>();
        JsonArray foundCatsArray = SkyblockUtil.getAsJsonArray("found_cats", object);
        for (JsonElement foundCat : foundCatsArray) {
            foundCats.add(foundCat.getAsString());
        }
        this.unlockedPet = SkyblockUtil.getAsBoolean("unlocked_pet", object);
        this.montezuma = new Montezuma(SkyblockUtil.getAsJsonObject("montezuma", object));
    }

    public boolean isPickedUpDetector() {
        return pickedUpDetector;
    }

    public boolean isTalkedToJacquelle() {
        return talkedToJacquelle;
    }

    public boolean isUnlockedPet() {
        return unlockedPet;
    }

    public static class Montezuma {
        //private final UUID uuid; // ??
        @Getter
        private final UUID uniqueID;
        @Getter
        private final String type;
        @Getter
        private final Double exp;
        private final Boolean active;
        @Getter
        private final ItemRarity rarity;
        //private final ItemStack heldItem;
        private final Integer candyUsed;
        //private final Skin skin;

        public Montezuma(JsonObject object) {
            //this.uuid = fromString(getAsString("uuid", object));
            this.type = SkyblockUtil.getAsString("type", object);
            this.exp = SkyblockUtil.getAsDouble("exp", object);
            this.active = SkyblockUtil.getAsBoolean("active", object);
            this.candyUsed = SkyblockUtil.getAsInteger("candy_used", object);
            this.uniqueID = SkyblockUtil.fromString(SkyblockUtil.getAsString("uniqueId", object));
            this.rarity = ItemRarity.parseRarity(SkyblockUtil.getAsString("tier", object));
        }

        public int getCandyUsed() {
            return candyUsed;
        }


        public boolean isActive() {
            return active;
        }
    }
}
