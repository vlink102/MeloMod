package me.vlink102.melomod.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.config.MeloConfiguration;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.mixin.PlayerObjectUtil;
import me.vlink102.melomod.mixin.SkyblockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import java.util.List;

import static com.mojang.realmsclient.gui.ChatFormatting.DARK_AQUA;
import static me.vlink102.melomod.MeloMod.MinecraftColors.*;

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

    }
}
