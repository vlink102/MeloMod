package me.vlink102.melomod.util.game;

import me.vlink102.melomod.MeloMod;
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
        if (heldItem == null || !heldItem.hasDisplayName()) return MeloMod.MinecraftColors.RED.getColor() + "None";
        return getHeldItem().getDisplayName();
    }
}
