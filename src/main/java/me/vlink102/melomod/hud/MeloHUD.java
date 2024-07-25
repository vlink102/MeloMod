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

    final String BLACK = "§0";
    final String DARK_BLUE = "§1";
    final String DARK_GREEN = "§2";
    final String DARK_AQUA = "§3";
    final String DARK_RED = "§4";
    final String DARK_PURPLE = "§5";
    final String GOLD = "§6";
    final String GRAY = "§7";
    final String DARK_GRAY = "§8";
    final String BLUE = "§9";
    final String GREEN = "§a";
    final String AQUA = "§b";
    final String RED = "§c";
    final String LIGHT_PURPLE = "§d";
    final String YELLOW = "§e";
    final String WHITE = "§f";
    final String OBFUSCATED = "§k";
    final String BOLD = "§l";
    final String ITALIC = "§m";
    final String STRIKETHROUGH = "§n";
    final String UNDERLINE = "§o";
    final String RESET = "§r";

    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (example) {
            lines.add(GOLD + "Mining Highlights:");
            lines.add(GRAY + "  X: " + DARK_AQUA + "???");
            lines.add(GRAY + "  Y: " + DARK_AQUA + "???");
            lines.add(GRAY + "  Z: " + DARK_AQUA + "???");
            lines.add(GRAY + "  Drill: " + RED + "None");
        } else {
            if (!HypixelUtils.INSTANCE.isHypixel() || Minecraft.getMinecraft().thePlayer == null) {
                return;
            }
            lines.add(GOLD + "Mining Highlights:");
            if (MeloConfiguration.enableCoords) {
                BlockPos pos = MeloMod.internalLocraw.getCoords();

                lines.add(GRAY + "  X: " + DARK_AQUA + pos.getX());
                lines.add(GRAY + "  Y: " + DARK_AQUA + pos.getY());
                lines.add(GRAY + "  Z: " + DARK_AQUA + pos.getZ());
            }

            ItemStack heldItem = PlayerObjectUtil.getHeldItem();
            if (heldItem != null) {
                SkyblockUtil.ItemType type = SkyblockUtil.ItemType.parseFromItemStack(heldItem);
                if (type == SkyblockUtil.ItemType.DRILL || type == SkyblockUtil.ItemType.GAUNTLET || type == SkyblockUtil.ItemType.PICKAXE) {
                    lines.add(GRAY + "  Drill: " + RED + PlayerObjectUtil.getHeldItemName());
                } else {
                    lines.add(GRAY + "  Drill: " + RED + "None");
                }
            } else {
                lines.add(GRAY + "  Drill: " + RED + "None");
            }

            if (MeloConfiguration.miningHighlightType == 2) {
                lines.add(GRAY + "  Type: " + YELLOW + "Automatic");
                switch (SkyblockUtil.getPlayerLocation()) {
                    case DWARVEN_MINES:
                        lines.add(GRAY + "  Detected: " + DARK_GREEN + "Mithril");
                        break;
                    case CRYSTAL_HOLLOWS:
                        lines.add(GRAY + "  Detected: " + DARK_PURPLE + "Gemstone");
                        break;
                    default:
                        lines.add(GRAY + "  Detected: " + RED + "None");
                        break;
                }
            } else if (MeloConfiguration.miningHighlightType == 0) {
                lines.add(GRAY + "  Type: " + DARK_GREEN + "Mithril");
            } else if (MeloConfiguration.miningHighlightType == 1) {
                lines.add(GRAY + "  Type: " + DARK_PURPLE + "Gemstone");
            }

        }
    }
}
