package me.vlink102.melomod.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.MeloConfiguration;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.mixin.PlayerObjectUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

import java.util.List;

/**
 * An example OneConfig HUD that is started in the config and displays text.
 *
 * @see MeloConfiguration#hud
 */
public class MeloHUD extends TextHud {
    public MeloHUD() {
        super(true);
    }

    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (example) {
            lines.add(MeloMod.MinecraftColors.GOLD.getColor() + "Mining Highlights:");
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  X: " + MeloMod.MinecraftColors.DARK_AQUA.getColor() + "???");
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  Y: " + MeloMod.MinecraftColors.DARK_AQUA.getColor() + "???");
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  Z: " + MeloMod.MinecraftColors.DARK_AQUA.getColor() + "???");
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  Drill: " + MeloMod.MinecraftColors.RED.getColor() + "None");
        } else {
            lines.add(MeloMod.MinecraftColors.GOLD.getColor() + "Mining Highlights:");
            BlockPos pos = MeloMod.internalLocraw.getCoords();
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  X: " + MeloMod.MinecraftColors.DARK_AQUA.getColor() + pos.getX());
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  Y: " + MeloMod.MinecraftColors.DARK_AQUA.getColor() + pos.getY());
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  Z: " + MeloMod.MinecraftColors.DARK_AQUA.getColor() + pos.getZ());
            lines.add(MeloMod.MinecraftColors.GRAY.getColor() + "  Drill: " + MeloMod.MinecraftColors.RED.getColor() + PlayerObjectUtil.getHeldItemName());
        }
    }
}
