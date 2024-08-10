package me.vlink102.melomod.util.enums.skyblock;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;

import java.util.Map;

public enum Gemstone {
    AMBER(EnumDyeColor.ORANGE),
    TOPAZ(EnumDyeColor.YELLOW),
    SAPPHIRE(EnumDyeColor.LIGHT_BLUE),
    AMETHYST(EnumDyeColor.PURPLE),
    JASPER(EnumDyeColor.MAGENTA),
    RUBY(EnumDyeColor.RED),
    JADE(EnumDyeColor.LIME),
    OPAL(EnumDyeColor.WHITE),
    AQUAMARINE(EnumDyeColor.BLUE),
    CITRINE(EnumDyeColor.BROWN),
    ONYX(EnumDyeColor.BLACK),
    PERIDOT(EnumDyeColor.GREEN);
    private final EnumDyeColor blockType;


    Gemstone(EnumDyeColor blockType) {
        this.blockType = blockType;
    }

    public static Gemstone getFromBlock(BlockPos block) {
        IBlockState state = Minecraft.getMinecraft().theWorld.getBlockState(block);
        ImmutableMap<IProperty, Comparable> map = state.getProperties();
        for (Map.Entry<IProperty, Comparable> iPropertyComparableEntry : map.entrySet()) {
            for (Gemstone value : Gemstone.values()) {
                if (iPropertyComparableEntry.getValue().toString().equalsIgnoreCase(value.blockType.getName())) {
                    return value;
                }
            }
        }
        return null;
    }

    public EnumDyeColor getBlockType() {
        return blockType;
    }

}
