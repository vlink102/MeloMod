package me.vlink102.melomod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static me.vlink102.melomod.util.StringUtils.cc;

@SuppressWarnings("unused")
public class ItemSerializer {
    public static final ItemSerializer INSTANCE = new ItemSerializer();

    public String serialize(ItemStack stack) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(stack.serializeNBT().toString().getBytes(StandardCharsets.UTF_8));
    }

    public String deserializeFromBase64(String stack) {
        Base64.Decoder decoder = Base64.getDecoder();
        return cc(new String(decoder.decode(stack), StandardCharsets.UTF_8));
    }

    public ItemStack deserializeFromNBT(String nbt) {
        try {
            NBTTagCompound tagCompound = JsonToNBT.getTagFromJson(nbt);
            return ItemStack.loadItemStackFromNBT(tagCompound);
        } catch (NBTException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack deserialize(String stack) {
        String newStack = deserializeFromBase64(stack);
        return deserializeFromNBT(newStack);
    }
}
