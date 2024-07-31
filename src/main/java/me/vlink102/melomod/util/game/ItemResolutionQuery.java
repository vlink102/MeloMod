package me.vlink102.melomod.util.game;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.IteratorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemResolutionQuery {

    private static final Pattern ENCHANTED_BOOK_NAME_PATTERN = Pattern.compile("^((?:§.)+)([^§]+) ([IVXL]+)$");
    private static final String EXTRA_ATTRIBUTES = "ExtraAttributes";
    private static final List<String> PET_RARITIES = Arrays.asList(
            "COMMON",
            "UNCOMMON",
            "RARE",
            "EPIC",
            "LEGENDARY",
            "MYTHIC"
    );
    private NBTTagCompound compound;
    private Item itemType;
    private int stackSize = -1;
    private Gui guiContext;
    private String knownInternalName;

    public ItemResolutionQuery withItemNBT(NBTTagCompound compound) {
        this.compound = compound;
        return this;
    }

    public ItemResolutionQuery withItemStack(ItemStack stack) {
        if (stack == null) return this;
        this.itemType = stack.getItem();
        this.compound = stack.getTagCompound();
        this.stackSize = stack.stackSize;
        return this;
    }

    public ItemResolutionQuery withGuiContext(Gui gui) {
        this.guiContext = gui;
        return this;
    }

    public ItemResolutionQuery withCurrentGuiContext() {
        this.guiContext = Minecraft.getMinecraft().currentScreen;
        return this;
    }

    public ItemResolutionQuery withKnownInternalName(String knownInternalName) {
        this.knownInternalName = knownInternalName;
        return this;
    }

    private String resolveContextualName() {
        if (!(guiContext instanceof GuiChest)) {
            return null;
        }
        GuiChest chest = (GuiChest) guiContext;
        ContainerChest inventorySlots = (ContainerChest) chest.inventorySlots;
        String guiName = inventorySlots.getLowerChestInventory().getDisplayName().getUnformattedText();
        boolean isOnBazaar = isBazaar(inventorySlots.getLowerChestInventory());
        String displayName = ItemUtils.getDisplayName(compound);
        if (displayName == null) return null;
        if (itemType == Items.enchanted_book && isOnBazaar && compound != null) {
            return resolveEnchantmentByName(displayName);
        }
        if (displayName.endsWith("Enchanted Book") && guiName.startsWith("Superpairs")) {
            for (String loreLine : ItemUtils.getLore(compound)) {
                String enchantmentIdCandidate = resolveEnchantmentByName(loreLine);
                if (enchantmentIdCandidate != null) return enchantmentIdCandidate;
            }
            return null;
        }
        /*
        if (guiName.equals("Catacombs RNG Meter")) {
            return resolveItemInCatacombsRngMeter();
        }

         */
        return null;
    }

    @Nullable
    public String resolveInternalName() {
        if (knownInternalName != null) {
            return knownInternalName;
        }
        String resolvedName = resolveFromSkyblock();
        if (resolvedName == null) {
            resolvedName = resolveContextualName();
        } else {
            switch (resolvedName.intern()) {
                case "PET":
                    resolvedName = resolvePetName();
                    break;
                case "RUNE":
                    resolvedName = resolveRuneName();
                    break;
                case "ENCHANTED_BOOK":
                    resolvedName = resolveEnchantedBookNameFromNBT();
                    break;
                case "PARTY_HAT_CRAB":
                case "PARTY_HAT_CRAB_ANIMATED":
                    resolvedName = resolveCrabHatName();
                    break;
            }
        }

        return resolvedName;
    }


    // <editor-fold desc="Resolution Helpers">
    private boolean isBazaar(IInventory chest) {
        if (chest.getDisplayName().getFormattedText().startsWith("Bazaar ➜ ")) {
            return true;
        }
        int bazaarSlot = chest.getSizeInventory() - 5;
        if (bazaarSlot < 0) return false;
        ItemStack stackInSlot = chest.getStackInSlot(bazaarSlot);
        if (stackInSlot == null || stackInSlot.stackSize == 0) return false;
        // NBT lore, we do not care about rendered lore
        List<String> lore = ItemUtils.getLore(stackInSlot);
        return lore.contains("§7To Bazaar");
    }




    private String resolveEnchantmentByName(String name) {
        Matcher matcher = ENCHANTED_BOOK_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) return null;
        String format = matcher.group(1).toLowerCase(Locale.ROOT);
        String enchantmentName = matcher.group(2).trim();
        String romanLevel = matcher.group(3);
        boolean ultimate = (format.contains("§l"));

        return (ultimate ? "ULTIMATE_" : "")
                + enchantmentName.replace(" ", "_").toUpperCase(Locale.ROOT)
                + ";" + Utils.parseRomanNumeral(romanLevel);
    }

    private String resolveCrabHatName() {
        int crabHatYear = getExtraAttributes().getInteger("party_hat_year");
        String color = getExtraAttributes().getString("party_hat_color");
        return "PARTY_HAT_CRAB_" + color.toUpperCase(Locale.ROOT) + (crabHatYear == 2022 ? "_ANIMATED" : "");
    }

    private String resolveEnchantedBookNameFromNBT() {
        NBTTagCompound enchantments = getExtraAttributes().getCompoundTag("enchantments");
        String enchantName = IteratorUtils.getOnlyElement(enchantments.getKeySet(), null);
        if (enchantName == null || enchantName.isEmpty()) return null;
        return enchantName.toUpperCase(Locale.ROOT) + ";" + enchantments.getInteger(enchantName);
    }

    private String resolveRuneName() {
        NBTTagCompound runes = getExtraAttributes().getCompoundTag("runes");
        String runeName = IteratorUtils.getOnlyElement(runes.getKeySet(), null);
        if (runeName == null || runeName.isEmpty()) return null;
        return runeName.toUpperCase(Locale.ROOT) + "_RUNE;" + runes.getInteger(runeName);
    }

    private String resolvePetName() {
        String petInfo = getExtraAttributes().getString("petInfo");
        if (petInfo == null || petInfo.isEmpty()) return null;
        try {
            JsonObject petInfoObject = MeloMod.gson.fromJson(petInfo, JsonObject.class);
            String petId = petInfoObject.get("type").getAsString();
            String petTier = petInfoObject.get("tier").getAsString();
            int rarityIndex = PET_RARITIES.indexOf(petTier);
            return petId.toUpperCase(Locale.ROOT) + ";" + rarityIndex;
        } catch (JsonParseException | ClassCastException ex) {
			//This happens if Hypixel changed the pet json format;
				// I still log this exception, since this case *is* exceptional and cannot easily be recovered from
            ex.printStackTrace();
            return null;
        }
    }

    private NBTTagCompound getExtraAttributes() {
        if (compound == null) return new NBTTagCompound();
        return compound.getCompoundTag(EXTRA_ATTRIBUTES);
    }

    private String resolveFromSkyblock() {
        String internalName = getExtraAttributes().getString("id");
        if (internalName == null || internalName.isEmpty()) return null;
        return internalName.toUpperCase(Locale.ROOT).replace(':', '-');
    }

    // </editor-fold>
}