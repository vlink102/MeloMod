package me.vlink102.melomod.util.game;

import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.translation.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class PlayerObjectUtil {
    private final MeloMod meloMod;

    public PlayerObjectUtil(MeloMod meloMod) {
        this.meloMod = meloMod;
    }

    public static ItemStack getHeldItem() {
        return Minecraft.getMinecraft().thePlayer.getHeldItem();
    }

    public static String getHeldItemName() {
        ItemStack heldItem = getHeldItem();
        if (heldItem == null || !heldItem.hasDisplayName()) return MeloMod.AbstractColor.RED.getColor() + Feature.GENERIC_NONE.toString();
        return getHeldItem().getDisplayName();
    }
}
