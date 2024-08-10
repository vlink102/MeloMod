package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final EnderChestContents enderChestContents;
    private final InventoryContents inventoryContents;
    private final InventoryArmor inventoryArmor;
    private final EquipmentContents equipmentContents;
    private final List<String> enderChestPageIcons;

    public Inventory(JsonObject object) {
        this.enderChestPageIcons = new ArrayList<>();
        JsonArray enderChestPageIconsArray = SkyblockUtil.getAsJsonArray("ender_chest_page_icons", object);
        for (JsonElement jsonElement : enderChestPageIconsArray) {
            if (jsonElement.isJsonNull()) {
                enderChestPageIcons.add(null);
            } else {
                if (jsonElement.isJsonPrimitive()) {
                    if (jsonElement.getAsJsonPrimitive().isString()) {
                        enderChestPageIcons.add(jsonElement.getAsString());
                    }
                }
            }
        }
        this.enderChestContents = new EnderChestContents(SkyblockUtil.getAsJsonObject("ender_chest_contents", object));
        this.inventoryContents = new InventoryContents(SkyblockUtil.getAsJsonObject("inv_contents", object));
        this.inventoryArmor = new InventoryArmor(SkyblockUtil.getAsJsonObject("inv_armor", object));
        this.equipmentContents = new EquipmentContents(SkyblockUtil.getAsJsonObject("equipment_contents", object));
    }

    public EnderChestContents getEnderChestContents() {
        return enderChestContents;
    }

    public EquipmentContents getEquipmentContents() {
        return equipmentContents;
    }

    public InventoryArmor getInventoryArmor() {
        return inventoryArmor;
    }

    public InventoryContents getInventoryContents() {
        return inventoryContents;
    }

    public List<String> getEnderChestPageIcons() {
        return enderChestPageIcons;
    }

    public static class EnderChestContents {
        private final Integer type;
        private final String data;

        public EnderChestContents(JsonObject object) {
            this.type = SkyblockUtil.getAsInteger("type", object);
            this.data = SkyblockUtil.getAsString("data", object); // TODO
        }

        public int getType() {
            return type;
        }

        public String getData() {
            return data;
        }
    }

    public static class InventoryContents {
        private final Integer type;
        private final String data;

        public InventoryContents(JsonObject object) {
            this.type = SkyblockUtil.getAsInteger("type", object);
            this.data = SkyblockUtil.getAsString("data", object);// TODO
        }

        public String getData() {
            return data;
        }

        public int getType() {
            return type;
        }
    }

    public static class InventoryArmor {
        private final Integer type;
        private final String data;

        public InventoryArmor(JsonObject object) {
            this.type = SkyblockUtil.getAsInteger("type", object);
            this.data = SkyblockUtil.getAsString("data", object); // TODO
        }

        public int getType() {
            return type;
        }

        public String getData() {
            return data;
        }
    }

    public static class EquipmentContents {
        private final Integer type;
        private final String data;

        public EquipmentContents(JsonObject object) {
            this.type = SkyblockUtil.getAsInteger("type", object);
            this.data = SkyblockUtil.getAsString("data", object); // TODO
        }

        public int getType() {
            return type;
        }

        public String getData() {
            return data;
        }
    }
}
