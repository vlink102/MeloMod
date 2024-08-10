package me.vlink102.melomod.util.enums.skyblock;

import me.vlink102.melomod.util.game.SkyblockUtil;
import net.minecraft.item.ItemStack;

import java.util.List;

public enum ItemRarity {
    UNCOMMON('a'), // swapped for matching
    COMMON('f'),
    RARE('9'),
    EPIC('5'),
    LEGENDARY('6'),
    MYTHIC('d'),
    DIVINE('b'),
    VERY_SPECIAL('c'), // swapped for matching
    SPECIAL('c'),
    SUPREME('4'),
    ADMIN('4');

    private final char color;

    ItemRarity(char color) {
        this.color = color;
    }

    public static ItemRarity parseRarity(String rarity) {
        if (rarity == null) {
            return null;
        }
        return ItemRarity.valueOf(rarity.toUpperCase());
    }

    public static ItemRarity parseFromItemStack(ItemStack stack) {
        List<String> lore = SkyblockUtil.getLore(stack);
        String entry = lore.get(lore.size() - 1);
        for (ItemRarity value : ItemRarity.values()) {
            if (entry.contains(value.toString().replaceAll("_", " "))) {
                return value;
            }
        }
        return null;
    }

    public String getColor() {
        return "§" + color;
    }
}
