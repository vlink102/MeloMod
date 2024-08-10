package me.vlink102.melomod.util.enums.skyblock;

import me.vlink102.melomod.util.game.SkyblockUtil;
import net.minecraft.item.ItemStack;

import java.util.List;

public enum ItemType {
    BELT,
    BRACELET,
    CLOAK,
    GLOVES,
    NECKLACE,
    SWORD,
    LONGSWORD,
    BOW,
    SHORTBOW,
    FISHING_ROD,
    FISHING_WEAPON,
    PICKAXE,
    DRILL,
    AXE,
    GAUNTLET,
    SHOVEL,
    HOE,
    WAND,
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    ACCESSORY,
    HATCESSORY,
    POWER_STONE,
    REFORGE_STONE,
    VACUUM,
    DEPLOYABLE;

    ItemType() {

    }

    public static ItemType parseFromItemStack(ItemStack stack) {
        if (stack == null) return null;
        List<String> lore = SkyblockUtil.getLore(stack);
        for (String s : lore) {
            if (s.matches(".*?(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|SPECIAL|VERY SPECIAL|ADMIN)\\s(BELT|BRACELET|CLOAK|GLOVES|NECKLACE|SWORD|LONGSWORD|BOW|SHORTBOW|FISHING ROD|FISHING WEAPON|PICKAXE|DRILL|AXE|GAUNTLET|SHOVEL|HOE|WAND|HELMET|CHESTPLATE|LEGGINGS|BOOTS|ACCESSORY|HATCESSORY|POWER STONE|REFORGE STONE|VACUUM|DEPLOYABLE).*?")) {
                for (ItemType value : ItemType.values()) {
                    if (s.contains(value.toString().replaceAll("_", " "))) {
                        return value;
                    }
                }
                return null;
            }
        }

        return null;
    }
}
