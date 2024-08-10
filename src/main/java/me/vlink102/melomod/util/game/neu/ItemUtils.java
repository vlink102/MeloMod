package me.vlink102.melomod.util.game.neu;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public class ItemUtils {

    public static ItemStack getCoinItemStack(long coinAmount) {
        String uuid = "2070f6cb-f5db-367a-acd0-64d39a7e5d1b";
        String texture =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4MDcxNzIxY2M1YjRjZDQwNmNlNDMxYTEzZjg2MDgzYTg5NzNlMTA2NGQyZjg4OTc4Njk5MzBlZTZlNTIzNyJ9fX0=";
        if (coinAmount >= 100000) {
            uuid = "94fa2455-2881-31fe-bb4e-e3e24d58dbe3";
            texture =
                    "eyJ0aW1lc3RhbXAiOjE2MzU5NTczOTM4MDMsInByb2ZpbGVJZCI6ImJiN2NjYTcxMDQzNDQ0MTI4ZDMwODllMTNiZGZhYjU5IiwicHJvZmlsZU5hbWUiOiJsYXVyZW5jaW8zMDMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M5Yjc3OTk5ZmVkM2EyNzU4YmZlYWYwNzkzZTUyMjgzODE3YmVhNjQwNDRiZjQzZWYyOTQzM2Y5NTRiYjUyZjYiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQo=";
        }
        if (coinAmount >= 10000000) {
            uuid = "0af8df1f-098c-3b72-ac6b-65d65fd0b668";
            texture =
                    "ewogICJ0aW1lc3RhbXAiIDogMTYzNTk1NzQ4ODQxNywKICAicHJvZmlsZUlkIiA6ICJmNThkZWJkNTlmNTA0MjIyOGY2MDIyMjExZDRjMTQwYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ1bnZlbnRpdmV0YWxlbnQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I5NTFmZWQ2YTdiMmNiYzIwMzY5MTZkZWM3YTQ2YzRhNTY0ODE1NjRkMTRmOTQ1YjZlYmMwMzM4Mjc2NmQzYiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
        }
        ItemStack skull = Utils.createSkull(
                "\u00A7r\u00A76" + NumberFormat.getInstance(Locale.US).format(coinAmount) + " Coins",
                uuid,
                texture
        );
        NBTTagCompound extraAttributes = skull.getTagCompound().getCompoundTag("ExtraAttributes");
        extraAttributes.setString("id", "SKYBLOCK_COIN");
        skull.getTagCompound().setTag("ExtraAttributes", extraAttributes);
        return skull;
    }

    public static ItemStack createQuestionMarkSkull(String label) {
        return Utils.createSkull(
                label,
                "00000000-0000-0000-0000-000000000000",
                "bc8ea1f51f253ff5142ca11ae45193a4ad8c3ab5e9c6eec8ba7a4fcb7bac40"
        );
    }

    public static NBTTagCompound getOrCreateTag(ItemStack is) {
        if (is.hasTagCompound()) return is.getTagCompound();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        is.setTagCompound(nbtTagCompound);
        return nbtTagCompound;
    }

    public static void appendLore(ItemStack is, List<String> moreLore) {
        NBTTagCompound tagCompound = is.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagCompound display = tagCompound.getCompoundTag("display");
        NBTTagList lore = display.getTagList("Lore", 8);
        for (String s : moreLore) {
            lore.appendTag(new NBTTagString(s));
        }
        display.setTag("Lore", lore);
        tagCompound.setTag("display", display);
        is.setTagCompound(tagCompound);
    }

    public static List<String> getLore(ItemStack is) {
        return getLore(is.getTagCompound());
    }

    public static List<String> getLore(NBTTagCompound tagCompound) {
        if (tagCompound == null) {
            return Collections.emptyList();
        }
        NBTTagList tagList = tagCompound.getCompoundTag("display").getTagList("Lore", 8);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            list.add(tagList.getStringTagAt(i));
        }
        return list;
    }

    public static String getDisplayName(NBTTagCompound compound) {
        if (compound == null) return null;
        String string = compound.getCompoundTag("display").getString("Name");
        if (string == null || string.isEmpty())
            return null;
        return string;
    }

    /**
     * Mutates baseValues
     */
    public static <T> void modifyReplacement(
            Map<String, String> baseValues,
            Map<String, T> modifiers,
            BiFunction<String, T, String> mapper
    ) {
        if (modifiers == null || baseValues == null) return;
        for (Map.Entry<String, T> modifier : modifiers.entrySet()) {
            String baseValue = baseValues.get(modifier.getKey());
            if (baseValue == null) continue;
            try {
                baseValues.put(modifier.getKey(), mapper.apply(baseValue, modifier.getValue()));
            } catch (Exception e) {
                System.out.println("Exception during replacement mapping: ");
                e.printStackTrace();
            }
        }
    }

    public static String applyReplacements(Map<String, String> replacements, String text) {
        for (Map.Entry<String, String> replacement : replacements.entrySet()) {
            String search = "{" + replacement.getKey() + "}";
            text = text.replace(search, replacement.getValue());
        }
        return text;
    }

    private static final DecimalFormat decimalFormatter = new DecimalFormat("#,###,###.###");


    public static boolean isSoulbound(ItemStack item) {
        return ItemUtils.getLore(item).stream()
                .anyMatch(line -> line.equals("§8§l* §8Co-op Soulbound §8§l*") ||
                        line.equals("§8§l* Soulbound §8§l*"));
    }

}