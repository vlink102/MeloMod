package me.vlink102.melomod.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.elements.SubConfig;
import cc.polyfrost.oneconfig.hud.TextHud;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.util.game.PlayerObjectUtil;
import me.vlink102.melomod.util.game.SkyblockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import java.util.List;

public class MiningHelper extends SubConfig {
    @HUD(
            name = "Mining Helper"
    )
    public static MiningHud hud = new MiningHud();

    @Switch(
            name = "Enable HUD"
    )
    public static boolean enableHUD = true;

    @Switch(
            name = "Enable Highlights"
    )
    public static boolean enableHighlights = true;

    @Switch(
            name = "Enable Coordinates"
    )
    public static boolean enableCoords = true;
    @Dropdown(
            name = "Mining Highlight Type", // Name of the Dropdown
            options = {"Mithril", "Gemstones", "Auto"} // Options available.
    )
    public static int miningHighlightType = 2;

    @Color(
            name = "Mining Highlights Color"
    )
    public static OneColor miningHighlightColor = new OneColor(0, 255, 0, 50, 255);


    @Dropdown(
            name = "Default Render Type",
            options = {
                    "Outline",
                    "Fill"
            }
    )
    public static int defaultRenderType = 1;

    @Color(
            name = "Titanium Highlight"
    )
    public static OneColor titaniumHighlightColor = new OneColor(255, 255, 255, 50);


    @Dropdown(
            name = "Titanium Render Type",
            options = {
                    "Outline",
                    "Fill"
            }
    )
    public static int titaniumRenderType = 0;

    @Slider(
            name = "Render Amount",
            min = 1,
            max = 256,
            step = 255
    )
    public static int renderAmount = 128;

    @Slider(
            name = "Block Reach",
            min = 1f,
            max = 4.5f,
            step = 35
    )
    public static float blockReach = 4.5f;

    @Switch(
            name = "GL State Depth",
            description = "When enabled, you will not be able to see blocks through other blocks."
    )
    public static boolean renderDepth = false;

    public MiningHelper() {
        super("Mining Helper", MeloMod.MODID + "/" + "mining-helper" + ".json");
        initialize();
    }

    public static class MiningHud extends TextHud {

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

        public MiningHud() {
            super(true);
        }

        @Override
        protected void getLines(List<String> lines, boolean example) {
            if (example) {
                lines.add(GOLD + "Mining Highlights:");
                lines.add(GRAY + "  X: " + DARK_AQUA + "???");
                lines.add(GRAY + "  Y: " + DARK_AQUA + "???");
                lines.add(GRAY + "  Z: " + DARK_AQUA + "???");
                lines.add(GRAY + "  Drill: " + RED + "None");
            } else {
                if (!enableHUD) return;
                if (!HypixelUtils.INSTANCE.isHypixel() || Minecraft.getMinecraft().thePlayer == null) {
                    return;
                }
                SkyblockUtil.Location location = InternalLocraw.getLocation();
                if (location != SkyblockUtil.Location.DWARVEN_MINES && location != SkyblockUtil.Location.CRYSTAL_HOLLOWS) {
                    return;
                }
                lines.add(GOLD + "Mining Highlights:");
                if (MiningHelper.enableCoords) {
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

                if (MiningHelper.miningHighlightType == 2) {
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
                } else if (MiningHelper.miningHighlightType == 0) {
                    lines.add(GRAY + "  Type: " + DARK_GREEN + "Mithril");
                } else if (MiningHelper.miningHighlightType == 1) {
                    lines.add(GRAY + "  Type: " + DARK_PURPLE + "Gemstone");
                }

            }
        }
    }
}
