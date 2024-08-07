package me.vlink102.melomod.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.loohp.interactivechatdiscordsrvaddon.main.BlockModelRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ItemSerializer {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    public String serialize(ItemStack stack) {
        return new String(stack.serializeNBT().toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    public ItemStack deserialize(String stack) {
        try {
            NBTTagCompound tagCompound = JsonToNBT.getTagFromJson(stack);
            return ItemStack.loadItemStackFromNBT(tagCompound);
        } catch (NBTException e) {
            throw new RuntimeException(e);
        }
    }
}
