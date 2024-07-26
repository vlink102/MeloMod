package me.vlink102.melomod.mixin;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.events.InternalLocraw;
import me.vlink102.melomod.world.ItemResolutionQuery;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialTransparent;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.util.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class SkyblockUtil {
    private final MeloMod meloMod;

    public SkyblockUtil(MeloMod meloMod) {
        this.meloMod = meloMod;
    }

    public enum ItemType {
        BELT,
        BRACELET,
        CLOAK,
        GLOVES,
        NECKLACE,
        SWORD,
        LONGSWORD,
        BOW,
        SHORTBOW,
        FISHING_ROD,
        FISHING_WEAPON,
        PICKAXE,
        DRILL,
        AXE,
        GAUNTLET,
        SHOVEL,
        HOE,
        WAND,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        ACCESSORY,
        HATCESSORY,
        POWER_STONE,
        REFORGE_STONE,
        VACUUM,
        DEPLOYABLE;

        ItemType() {

        }

        public static ItemType parseFromItemStack(ItemStack stack) {
            if (stack == null) return null;
            List<String> lore = getLore(stack);
            System.out.println(lore);
            for (String s : lore) {
                if (s.matches(".*?(COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|SPECIAL|VERY SPECIAL|ADMIN)\\s(BELT|BRACELET|CLOAK|GLOVES|NECKLACE|SWORD|LONGSWORD|BOW|SHORTBOW|FISHING ROD|FISHING WEAPON|PICKAXE|DRILL|AXE|GAUNTLET|SHOVEL|HOE|WAND|HELMET|CHESTPLATE|LEGGINGS|BOOTS|ACCESSORY|HATCESSORY|POWER STONE|REFORGE STONE|VACUUM|DEPLOYABLE).*?")) {
                    for (ItemType value : ItemType.values()) {
                        if (s.contains(value.toString().replaceAll("_", " "))) {
                            return value;
                        }
                    }
                    return null;
                }
            }

            return null;
        }
    }

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

        public EnumDyeColor getBlockType() {
            return blockType;
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

    }

    public static Location getPlayerLocation() {
        return Location.parseFromLocraw(HypixelUtils.INSTANCE.getLocrawInfo().getGameMode());
    }

    public enum Location {
        PRIVATE_ISLAND("dynamic"),
        GARDEN("garden"),
        HUB("hub"),
        BARN("farming_1"),
        PARK("foraging_1"),
        SPIDER_DEN("combat_1"),
        END("combat_3"),
        CRIMSON_ISLE("crimson_isle"),
        GOLD_MINE("mining_1"),
        DEEP_CAVERNS("mining_2"),
        DWARVEN_MINES("mining_3"),
        CRYSTAL_HOLLOWS("crystal_hollows"),
        WINTER(""), // TODO
        DUNGEON_HUB("dungeon_hub"),
        RIFT("rift"),
        DARK_AUCTION(""); // TODO

        private final String internal;

        Location(String internal) {
            this.internal = internal;
        }

        public String getInternal() {
            return internal;
        }

        public static Location parseFromLocraw(String locrawGamemode) {
            for (Location value : Location.values()) {
                if (locrawGamemode.equalsIgnoreCase(value.getInternal())) {
                    return value;
                }
            }
            return null;
        }
    }

    public JsonArray getPlayerProfiles(UUID player) {
        try {
            URL url = new URL("https://api.hypixel.net/v2/skyblock/profiles?uuid=" + player);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("API-Key", MeloMod.API_KEY);

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            int status = con.getResponseCode();

            if (status == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream())
                );
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                String contentString = content.toString();
                JsonObject toReturn = new JsonParser().parse(contentString).getAsJsonObject();
                if (toReturn.get("success").getAsBoolean()) {
                    return toReturn.getAsJsonArray("profiles");
                }
            }


            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public JsonObject getActiveProfile(UUID player) {
        JsonArray profiles = getPlayerProfiles(player);
        for (JsonElement profile : profiles) {
            JsonObject profileObj = profile.getAsJsonObject();
            if (profileObj.get("selected").getAsBoolean()) {
                return profileObj;
            }
        }
        return null;
    }

    public class SkyblockProfile {
        private final UUID profileID;
        private final Long createdAt;
        private final Boolean selected;
        private final List<ProfileMember> members;
        private final String cuteName;
        private final List<CommunityUpgrade> communityUpgrades;
        private final Gamemode gamemode;

        @Deprecated
        public SkyblockProfile(UUID profileID, Long createdAt, boolean selected, List<ProfileMember> members, Gamemode gamemode) {
            this.profileID = profileID;
            this.selected = selected;
            this.createdAt = createdAt;
            this.members = members;
            this.gamemode = gamemode;
            this.cuteName = null;
            this.communityUpgrades = null;
        }

        public SkyblockProfile(JsonObject profileObject) {
            this.profileID = UUID.fromString(profileObject.get("profile_id").getAsString());
            JsonObject communityUpgrades = profileObject.get("community_upgrades").getAsJsonObject();
            JsonArray upgradeStates = communityUpgrades.get("upgrade_states").getAsJsonArray();
            JsonArray currentlyUpgrading = communityUpgrades.get("currently_upgrading").getAsJsonArray();
            List<CommunityUpgrade> upgrades = new ArrayList<>();
            for (JsonElement upgradeState : upgradeStates) {
                upgrades.add(new CommunityUpgrade(upgradeState.getAsJsonObject()));
            }
            for (JsonElement jsonElement : currentlyUpgrading) {
                upgrades.add(new CommunityUpgrade(jsonElement.getAsJsonObject()));
            }
            this.communityUpgrades = upgrades;
            this.createdAt = profileObject.get("created_at").getAsLong();
            this.members = new ArrayList<>();
            JsonObject membersObject = profileObject.get("members").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : membersObject.entrySet()) {
                String string = entry.getKey();
                members.add(new ProfileMember(UUID.fromString(string), membersObject.get(string).getAsJsonObject()));
            }
            this.selected = profileObject.get("selected").getAsBoolean();
            this.cuteName = profileObject.get("cute_name").getAsString();
            this.gamemode = Gamemode.parseFromJSON(profileObject.get("game_mode").getAsString());
        }

        public Boolean getSelected() {
            return selected;
        }

        public Gamemode getGamemode() {
            return gamemode;
        }

        public List<CommunityUpgrade> getCommunityUpgrades() {
            return communityUpgrades;
        }

        public List<ProfileMember> getMembers() {
            return members;
        }

        public Long getCreatedAt() {
            return createdAt;
        }

        public String getCuteName() {
            return cuteName;
        }

        public UUID getProfileID() {
            return profileID;
        }
    }

    public enum Gamemode {
        NORMAL,
        IRONMAN,
        STRANDED,
        BINGO;

        public static Gamemode parseFromJSON(String gamemode) {
            if (gamemode.equalsIgnoreCase("ironman")) return IRONMAN;
            if (gamemode.equalsIgnoreCase("island")) return STRANDED;
            if (gamemode.equalsIgnoreCase("bingo")) return BINGO;
            return NORMAL;
        }
    }

    public static class CommunityUpgrade {
        private final String upgradeName;
        private final int tier;
        private final long startedMS;
        private final long claimedMS;
        private final UUID startedBy;
        private final UUID claimedBy;
        private final boolean fastTracked;

        @Deprecated
        public CommunityUpgrade(String upgradeName, int tier, long startedMS, long claimedMS, UUID startedBy, UUID claimedBy, boolean fastTracked) {
            this.upgradeName = upgradeName;
            this.tier = tier;
            this.startedMS = startedMS;
            this.claimedMS = claimedMS;
            this.startedBy = startedBy;
            this.claimedBy = claimedBy;
            this.fastTracked = fastTracked;
        }

        public CommunityUpgrade(JsonObject object) {
            this.upgradeName = object.get("upgrade").getAsString();
            this.tier = object.get("tier").getAsInt();
            this.startedMS = object.get("started_ms").getAsLong();
            this.claimedMS = object.get("claimed_ms").getAsLong();
            this.claimedBy = UUID.fromString(object.get("claimed_by").getAsString());
            this.fastTracked = object.get("fast_tracked").getAsBoolean();
            this.startedBy = UUID.fromString(object.get("started_by").getAsString());
        }

        public int getTier() {
            return tier;
        }

        public long getClaimedMS() {
            return claimedMS;
        }

        public long getStartedMS() {
            return startedMS;
        }

        public String getUpgradeName() {
            return upgradeName;
        }

        public UUID getClaimedBy() {
            return claimedBy;
        }

        public UUID getStartedBy() {
            return startedBy;
        }

        public boolean isFastTracked() {
            return fastTracked;
        }
    }

    public static class Banking {
        private final double balance;
        private final List<Transaction> transactions;

        @Deprecated
        public Banking(double balance, List<Transaction> transactions) {
            this.balance = balance;
            this.transactions = transactions;
        }

        public Banking(JsonObject object) {
            this.balance = object.get("balance").getAsDouble();
            JsonArray transactionArray = object.getAsJsonArray("transactions");
            List<Transaction> transactions = new ArrayList<>();
            for (JsonElement jsonElement : transactionArray) {
                JsonObject transaction = jsonElement.getAsJsonObject();
                transactions.add(new Transaction(transaction));
            }
            this.transactions = transactions;
        }

        public double getBalance() {
            return balance;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public static class Transaction {
            private final long timestamp;
            private final Action action;
            private final String initiatorName;
            private final double amount;

            public enum Action {
                DEPOSIT,
                WITHDRAW;

                public static Action parseFromJSON(String action) {
                    if (action.equalsIgnoreCase("DEPOSIT")) return DEPOSIT;
                    if (action.equalsIgnoreCase("WITHDRAW")) return WITHDRAW;
                    return null;
                }
            }

            @Deprecated
            public Transaction(int timestamp, Action action, String initiatorName, double amount) {
                this.timestamp = timestamp;
                this.action = action;
                this.initiatorName = initiatorName;
                this.amount = amount;
            }

            public Transaction(JsonObject object) {
                this.amount = object.get("amount").getAsDouble();
                this.timestamp = object.get("timestamp").getAsLong();
                this.action = Action.parseFromJSON(object.get("action").getAsString());
                this.initiatorName = object.get("initiator_name").getAsString();
            }

            public long getTimestamp() {
                return timestamp;
            }

            public Action getAction() {
                return action;
            }

            public double getAmount() {
                return amount;
            }

            public String getInitiatorName() {
                return initiatorName;
            }
        }
    }

    public class ProfileMember {
        private final UUID playerID;
        private final RiftData riftData;
        private final PlayerData playerData;
        private final GlacitePlayerData glacitePlayerData;
        private final Events events;
        private final GardenPlayerData gardenPlayerData;
        private final PetsData petsData;
        private final AccessoryBagStorage accessoryBagStorage;
        private final Levelling levelling;
        private final ItemData itemData;
        private final JacobsContest jacobsContest;
        private final Currencies currencies;
        private final Dungeons dungeons;

        public ProfileMember(UUID playerID, JsonObject object) {
            this.playerID = playerID;
            this.riftData = new RiftData(object.getAsJsonObject("rift"));
            this.playerData = new PlayerData(object.get("player_data").getAsJsonObject());
            this.glacitePlayerData = new GlacitePlayerData(object.get("glacite_player_data").getAsJsonObject());
            this.events = new Events(object.get("events").getAsJsonObject());
            this.gardenPlayerData = new GardenPlayerData(object.get("garden_player_data").getAsJsonObject());
            this.petsData = new PetsData(object.get("pets_data").getAsJsonObject());
            this.accessoryBagStorage = new AccessoryBagStorage(object.get("accessory_bag").getAsJsonObject());
            this.levelling = new Levelling(object.get("levelling").getAsJsonObject());
            this.itemData = new ItemData(object.get("item_data").getAsJsonObject());
            this.jacobsContest = new JacobsContest(object.get("jacobs_contest").getAsJsonObject());
            this.currencies = new Currencies(object.get("currencies").getAsJsonObject());
            this.dungeons = new Dungeons(object.get("dungeons").getAsJsonObject());
        }

        public AccessoryBagStorage getAccessoryBagStorage() {
            return accessoryBagStorage;
        }

        public Currencies getCurrencies() {
            return currencies;
        }

        public Dungeons getDungeons() {
            return dungeons;
        }

        public Events getEvents() {
            return events;
        }

        public GardenPlayerData getGardenPlayerData() {
            return gardenPlayerData;
        }

        public GlacitePlayerData getGlacitePlayerData() {
            return glacitePlayerData;
        }

        public ItemData getItemData() {
            return itemData;
        }

        public JacobsContest getJacobsContest() {
            return jacobsContest;
        }

        public Levelling getLevelling() {
            return levelling;
        }

        public PetsData getPetsData() {
            return petsData;
        }

        public PlayerData getPlayerData() {
            return playerData;
        }

        public RiftData getRiftData() {
            return riftData;
        }

        public class DeletionNotice {
            private final int timestamp;

            public DeletionNotice(int timestamp) {
                this.timestamp = timestamp;
            }

            public int getTimestamp() {
                return timestamp;
            }
        }

        public UUID getPlayerID() {
            return playerID;
        }
    }

    public enum ItemRarity {
        UNCOMMON('a'), // swapped for matching
        COMMON('f'),
        RARE('9'),
        EPIC('5'),
        LEGENDARY('6'),
        MYTHIC('d'),
        DIVINE('b'),
        VERY_SPECIAL('c'), // swapped for matching
        SPECIAL('c'),
        SUPREME('4'),
        ADMIN('4');

        private final char color;

        ItemRarity(char color) {
            this.color = color;
        }

        public String getColor() {
            return "§" + color;
        }

        public static ItemRarity parseRarity(String rarity) {
            return ItemRarity.valueOf(rarity.toUpperCase());
        }

        public static ItemRarity parseFromItemStack(ItemStack stack) {
            List<String> lore = getLore(stack);
            String entry = lore.get(lore.size() - 1);
            for (ItemRarity value : ItemRarity.values()) {
                if (entry.contains(value.toString().replaceAll("_", " "))) {
                    return value;
                }
            }
            return null;
        }
    }

    public static List<String> getLore(ItemStack stack) {
        if (stack == null) return null;
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey("display")) {
            return null;
        }
        NBTTagCompound display = tag.getCompoundTag("display");
        if (!display.hasKey("Lore")) {
            return null;
        }
        NBTTagList lore = display.getTagList("Lore", Constants.NBT.TAG_STRING);
        List<String> loreList = new ArrayList<>();
        for (int i = 0; i < lore.tagCount(); i++) {
            loreList.add(lore.get(i).toString());
        }
        return loreList;
    }

    public class RiftData {
        private final List<String> lifetimePurchasedBoundaries;
        private final VillagePlaza villagePlaza;
        private final WitherCage witherCage;
        private final BlackLagoon blackLagoon;
        private final DeadCats deadCats;
        private final WizardTower wizardTower;
        private final Enigma enigma;
        private final Gallery gallery;
        private final WestVillage westVillage;
        private final WyldWoods wyldWoods;
        private final Castle castle;
        private final Access access;
        private final Dreadfarm dreadfarm;
        private final Inventory inventory;

        public RiftData(JsonObject object) {
            this.lifetimePurchasedBoundaries = new ArrayList<>();
            JsonArray lifetimePurchasedBoundariesArray = object.getAsJsonArray("lifetime_purchased_boundaries");
            for (JsonElement jsonElement : lifetimePurchasedBoundariesArray) {
                lifetimePurchasedBoundaries.add(jsonElement.getAsString());
            }
            this.villagePlaza = new VillagePlaza(object.getAsJsonObject("village_plaza"));
            this.witherCage = new WitherCage(object.getAsJsonObject("wither_cage"));
            this.blackLagoon = new BlackLagoon(object.getAsJsonObject("black_lagoon"));
            this.deadCats = new DeadCats(object.getAsJsonObject("dead_cats"));
            this.wizardTower = new WizardTower(object.getAsJsonObject("wizard_tower"));
            this.enigma = new Enigma(object.getAsJsonObject("enigma"));
            this.gallery = new Gallery(object.getAsJsonObject("gallery"));
            this.westVillage = new WestVillage(object.getAsJsonObject("west_village"));
            this.wyldWoods = new WyldWoods(object.getAsJsonObject("wyld_woods"));
            this.castle = new Castle(object.getAsJsonObject("castle"));
            this.access = new Access(object.getAsJsonObject("access"));
            this.dreadfarm = new Dreadfarm(object.getAsJsonObject("dreadfarm"));
            this.inventory = new Inventory(object.getAsJsonObject("inventory"));
        }

        public class VillagePlaza {
            private final boolean gotScammed;
            private final Murder murder;
            private final BarryCenter barryCenter;
            private final Cowboy cowboy;
            private final Lonely lonely;
            private final Seraphine seraphine;

            public VillagePlaza(JsonObject object) {
                this.gotScammed = object.get("got_scammed").getAsBoolean();
                this.murder = new Murder(object.getAsJsonObject("murder"));
                this.barryCenter = new BarryCenter(object.getAsJsonObject("barry_center"));
                this.cowboy = new Cowboy(object.getAsJsonObject("cowboy"));
                this.lonely = new Lonely(object.getAsJsonObject("lonely"));
                this.seraphine = new Seraphine(object.getAsJsonObject("seraphine"));
            }

            public class Murder {
                private final int stepIndex;
                private final List<String> roomClues;

                public Murder(JsonObject object) {
                    this.stepIndex = object.get("step_index").getAsInt();
                    this.roomClues = new ArrayList<>();
                    JsonArray roomCluesArray = object.getAsJsonArray("room_clues");
                    for (JsonElement jsonElement : roomCluesArray) {
                        roomClues.add(jsonElement.getAsString());
                    }
                }

                public List<String> getRoomClues() {
                    return roomClues;
                }

                public int getStepIndex() {
                    return stepIndex;
                }
            }
            public class BarryCenter {
                private final boolean firstTalkToBarry;
                private final List<String> convinced;
                private final boolean receivedReward;

                public BarryCenter(JsonObject object) {
                    this.firstTalkToBarry = object.get("first_talk_to_barry").getAsBoolean();
                    this.convinced = new ArrayList<>();
                    JsonArray convincedArray = object.getAsJsonArray("convinced");
                    for (JsonElement jsonElement : convincedArray) {
                        convinced.add(jsonElement.getAsString());
                    }
                    this.receivedReward = object.get("received_reward").getAsBoolean();
                }

                public boolean isFirstTalkToBarry() {
                    return firstTalkToBarry;
                }

                public List<String> getConvinced() {
                    return convinced;
                }

                public boolean isReceivedReward() {
                    return receivedReward;
                }
            }
            public class Cowboy {
                private final int stage;
                private final int hayEaten;
                private final String rabbitName;
                private final int exportedCarrots;

                public Cowboy(JsonObject object) {
                    this.stage = object.get("stage").getAsInt();
                    this.hayEaten = object.get("hay_eaten").getAsInt();
                    this.rabbitName = object.get("rabbit_name").getAsString();
                    this.exportedCarrots = object.get("exported_carrots").getAsInt();
                }

                public int getExportedCarrots() {
                    return exportedCarrots;
                }

                public int getHayEaten() {
                    return hayEaten;
                }

                public int getStage() {
                    return stage;
                }

                public String getRabbitName() {
                    return rabbitName;
                }
            }
            public class Lonely {
                private final int secondsSitting;

                public Lonely(JsonObject object) {
                    this.secondsSitting = object.get("seconds_sitting").getAsInt();
                }

                public int getSecondsSitting() {
                    return secondsSitting;
                }
            }
            public class Seraphine {
                private final int stepIndex;

                public Seraphine(JsonObject object) {
                    this.stepIndex = object.get("step_index").getAsInt();
                }
                public int getStepIndex() {
                    return stepIndex;
                }
            }

            public boolean isGotScammed() {
                return gotScammed;
            }

            public BarryCenter getBarryCenter() {
                return barryCenter;
            }

            public Cowboy getCowboy() {
                return cowboy;
            }

            public Lonely getLonely() {
                return lonely;
            }

            public Murder getMurder() {
                return murder;
            }

            public Seraphine getSeraphine() {
                return seraphine;
            }
        }
        public class WitherCage {
            private final List<String> eyesKilled;

            public WitherCage(JsonObject object) {
                this.eyesKilled = new ArrayList<>();
                JsonArray killedEyes = object.getAsJsonArray("killed_eyes");
                for (JsonElement killedEye : killedEyes) {
                    eyesKilled.add(killedEye.getAsString());
                }
            }

            public List<String> getEyesKilled() {
                return eyesKilled;
            }
        }
        public class BlackLagoon {
            private final boolean talkedToEdwin;
            private final boolean receivedSciencePaper;
            private final int completedStep;
            private final boolean deliveredSciencePaper;

            public BlackLagoon(JsonObject object) {
                this.talkedToEdwin = object.get("talked_to_edwin").getAsBoolean();
                this.receivedSciencePaper = object.get("received_science_paper").getAsBoolean();
                this.completedStep = object.get("completed_step").getAsInt();
                this.deliveredSciencePaper = object.get("delivered_science_paper").getAsBoolean();
            }

            public int getCompletedStep() {
                return completedStep;
            }

            public boolean isDeliveredSciencePaper() {
                return deliveredSciencePaper;
            }

            public boolean isReceivedSciencePaper() {
                return receivedSciencePaper;
            }

            public boolean isTalkedToEdwin() {
                return talkedToEdwin;
            }
        }
        public class DeadCats {
            private final boolean talkedToJacquelle;
            private final boolean pickedUpDetector;
            private final List<String> foundCats;
            private final boolean unlockedPet;
            private final Montezuma montezuma;

            public DeadCats(JsonObject object) {
                this.talkedToJacquelle = object.get("talked_to_jacquelle").getAsBoolean();
                this.pickedUpDetector = object.get("picked_up_detector").getAsBoolean();
                this.foundCats = new ArrayList<>();
                JsonArray foundCatsArray = object.getAsJsonArray("found_cats");
                for (JsonElement foundCat : foundCatsArray) {
                    foundCats.add(foundCat.getAsString());
                }
                this.unlockedPet = object.get("unlocked_pet").getAsBoolean();
                this.montezuma = new Montezuma(object.getAsJsonObject("montezuma"));
            }

            public List<String> getFoundCats() {
                return foundCats;
            }

            public boolean isPickedUpDetector() {
                return pickedUpDetector;
            }

            public boolean isTalkedToJacquelle() {
                return talkedToJacquelle;
            }

            public boolean isUnlockedPet() {
                return unlockedPet;
            }

            public Montezuma getMontezuma() {
                return montezuma;
            }

            public class Montezuma {
                private final UUID uuid; // ??
                private final UUID uniqueID;
                private final String type;
                private final BigInteger exp;
                private final boolean active;
                private final ItemRarity rarity;
                //private final ItemStack heldItem;
                private final int candyUsed;
                //private final Skin skin;

                public Montezuma(JsonObject object) {
                    this.uuid = UUID.fromString(object.get("uuid").getAsString());
                    this.type = object.get("type").getAsString();
                    this.exp = object.get("exp").getAsBigInteger();
                    this.active = object.get("active").getAsBoolean();
                    this.candyUsed = object.get("candy_used").getAsInt();
                    this.uniqueID = UUID.fromString(object.get("uniqueId").getAsString());
                    this.rarity = ItemRarity.parseRarity(object.get("tier").getAsString());
                }

                public BigInteger getExp() {
                    return exp;
                }

                public UUID getUniqueID() {
                    return uniqueID;
                }

                public int getCandyUsed() {
                    return candyUsed;
                }

                public String getType() {
                    return type;
                }

                @Deprecated
                public UUID getUuid() {
                    return uuid;
                }

                public ItemRarity getRarity() {
                    return rarity;
                }

                public boolean isActive() {
                    return active;
                }
            }
        }
        public class WizardTower {
            private final int wizardQuestStep;
            private final int crumbsLaidOut;

            public WizardTower(JsonObject object) {
                this.wizardQuestStep = object.get("wizard_quest_step").getAsInt();
                this.crumbsLaidOut = object.get("crumbs_laid_out").getAsInt();
            }

            public int getCrumbsLaidOut() {
                return crumbsLaidOut;
            }

            public int getWizardQuestStep() {
                return wizardQuestStep;
            }
        }
        public class Enigma {
            private final boolean boughtCloak;
            private final List<String> foundSouls;
            private final int claimedBonusIndex;

            public Enigma(JsonObject object) {
                this.boughtCloak = object.get("bought_cloak").getAsBoolean();
                this.claimedBonusIndex = object.get("claimed_bonus_index").getAsInt();
                this.foundSouls = new ArrayList<>();
                JsonArray foundSoulsArray = object.getAsJsonArray("found_souls");
                for (JsonElement foundSoul : foundSoulsArray) {
                    foundSouls.add(foundSoul.getAsString());
                }
            }
            public boolean isBoughtCloak() {
                return boughtCloak;
            }

            public List<String> getFoundSouls() {
                return foundSouls;
            }

            public int getClaimedBonusIndex() {
                return claimedBonusIndex;
            }
        }
        public class Gallery {
            private final int eliseStep;
            private final List<SecuredTrophy> securedTrophies;
            private final List<String> sentTrophyDialogues;

            public Gallery(JsonObject object) {
                this.eliseStep = object.get("elise_step").getAsInt();
                this.securedTrophies = new ArrayList<>();
                JsonArray securedTrophiesArray = object.getAsJsonArray("secured_trophies");
                for (JsonElement securedTrophy : securedTrophiesArray) {
                    securedTrophies.add(new SecuredTrophy(securedTrophy.getAsJsonObject()));
                }
                this.sentTrophyDialogues = new ArrayList<>();
                JsonArray sentTrophyDialoguesArray = object.getAsJsonArray("sent_trophy_dialogues");
                for (JsonElement jsonElement : sentTrophyDialoguesArray) {
                    sentTrophyDialogues.add(jsonElement.getAsString());
                }
            }

            public int getEliseStep() {
                return eliseStep;
            }

            public List<SecuredTrophy> getSecuredTrophies() {
                return securedTrophies;
            }

            public List<String> getSentTrophyDialogues() {
                return sentTrophyDialogues;
            }

            public class SecuredTrophy {
                private final String type;
                private final long timestamp;
                private final int visits;

                public SecuredTrophy(JsonObject object) {
                    this.type = object.get("type").getAsString();
                    this.timestamp = object.get("timestamp").getAsLong();
                    this.visits = object.get("visits").getAsInt();
                }

                public String getType() {
                    return type;
                }

                public int getVisits() {
                    return visits;
                }

                public long getTimestamp() {
                    return timestamp;
                }
            }
        }
        public class WestVillage {
            private final MirrorVerse mirrorVerse;
            private final CrazyKloon crazyKloon;
            private final KatHouse katHouse;
            private final Glyphs glyphs;

            public WestVillage(JsonObject object) {
                this.mirrorVerse = new MirrorVerse(object.getAsJsonObject("mirrorverse"));
                this.crazyKloon = new CrazyKloon(object.getAsJsonObject("crazy_kloon"));
                this.katHouse = new KatHouse(object.getAsJsonObject("kat_house"));
                this.glyphs = new Glyphs(object.getAsJsonObject("glyphs"));
            }

            public CrazyKloon getCrazyKloon() {
                return crazyKloon;
            }

            public Glyphs getGlyphs() {
                return glyphs;
            }

            public KatHouse getKatHouse() {
                return katHouse;
            }

            public MirrorVerse getMirrorVerse() {
                return mirrorVerse;
            }

            public class MirrorVerse {
                private final List<String> roomsVisited;
                private final boolean upsideDownHard;
                private final List<String> claimedChestItems;
                private final boolean claimedReward;

                public MirrorVerse(JsonObject object) {
                    this.roomsVisited = new ArrayList<>();
                    this.upsideDownHard = object.get("upside_down_hard").getAsBoolean();
                    this.claimedChestItems = new ArrayList<>();
                    this.claimedReward = object.get("claimed_reward").getAsBoolean();
                    JsonArray visitedRoomsArray = object.getAsJsonArray("visited_rooms");
                    for (JsonElement visitedRoom : visitedRoomsArray) {
                        roomsVisited.add(visitedRoom.getAsString());
                    }
                    JsonArray claimedChestItemsArray = object.getAsJsonArray("claimed_chest_items");
                    for (JsonElement claimedChestItem : claimedChestItemsArray) {
                        claimedChestItems.add(claimedChestItem.getAsString());
                    }
                }

                public List<String> getClaimedChestItems() {
                    return claimedChestItems;
                }

                public List<String> getRoomsVisited() {
                    return roomsVisited;
                }

                public boolean isClaimedReward() {
                    return claimedReward;
                }

                public boolean isUpsideDownHard() {
                    return upsideDownHard;
                }
            }
            public class CrazyKloon {
                private final HashMap<String, String> selectedColors;
                private final boolean talked;
                private final List<String> hackedTerminals;
                private final boolean questComplete;

                public CrazyKloon(JsonObject object) {
                    this.selectedColors = new HashMap<>();
                    JsonObject selectedColorsMap = object.getAsJsonObject("selected_colors");
                    for (Map.Entry<String, JsonElement> entry : selectedColorsMap.entrySet()) {
                        String string = entry.getKey();
                        selectedColors.put(string, selectedColorsMap.get(string).getAsString());
                    }
                    this.talked = object.get("talked").getAsBoolean();
                    this.hackedTerminals = new ArrayList<>();
                    JsonArray hackedTerminalsArray = object.getAsJsonArray("hacked_terminals");
                    for (JsonElement hackedTerminal : hackedTerminalsArray) {
                        hackedTerminals.add(hackedTerminal.getAsString());
                    }
                    this.questComplete = object.get("quest_complete").getAsBoolean();
                }

                public HashMap<String, String> getSelectedColors() {
                    return selectedColors;
                }

                public List<String> getHackedTerminals() {
                    return hackedTerminals;
                }

                public boolean isQuestComplete() {
                    return questComplete;
                }

                public boolean isTalked() {
                    return talked;
                }
            }
            public class KatHouse {
                private final int binCollectedMosquito;
                private final int binCollectedSilverfish;
                private final int binCollectedSpider;

                public KatHouse(JsonObject object) {
                    this.binCollectedMosquito = object.get("bin_collected_mosquito").getAsInt();
                    this.binCollectedSilverfish = object.get("bin_collected_silverfish").getAsInt();
                    this.binCollectedSpider = object.get("bin_collected_spider").getAsInt();
                }

                public int getBinCollectedMosquito() {
                    return binCollectedMosquito;
                }

                public int getBinCollectedSilverfish() {
                    return binCollectedSilverfish;
                }

                public int getBinCollectedSpider() {
                    return binCollectedSpider;
                }
            }
            public class Glyphs {
                private final boolean claimedWand;
                private final boolean currentGlyphDelivered;
                private final boolean currentGlyphCompleted;
                private final int currentGlyph;
                private final boolean completed;
                private final boolean claimedBracelet;

                public Glyphs(JsonObject object) {
                    this.claimedWand = object.get("claimed_wand").getAsBoolean();
                    this.currentGlyphDelivered = object.get("current_glyph_delivered").getAsBoolean();
                    this.currentGlyphCompleted = object.get("current_glyph_completed").getAsBoolean();
                    this.currentGlyph = object.get("current_glyph").getAsInt();
                    this.completed = object.get("completed").getAsBoolean();
                    this.claimedBracelet = object.get("claimed_bracelet").getAsBoolean();
                }

                public boolean isClaimedBracelet() {
                    return claimedBracelet;
                }

                public boolean isClaimedWand() {
                    return claimedWand;
                }

                public boolean isCompleted() {
                    return completed;
                }

                public boolean isCurrentGlyphCompleted() {
                    return currentGlyphCompleted;
                }

                public boolean isCurrentGlyphDelivered() {
                    return currentGlyphDelivered;
                }

                public int getCurrentGlyph() {
                    return currentGlyph;
                }
            }
        }
        public class WyldWoods {
            private final List<String> talkedThreeBrothers;
            private final boolean siriusStartedQA;
            private final boolean siriusQAChainDone;
            private final boolean siriusCompletedQA;
            private final boolean siriusClaimedDoubloon;
            private final int bughunter_step;

            public WyldWoods(JsonObject object) {
                this.talkedThreeBrothers = new ArrayList<>();
                JsonArray talkedThreeBrothersArray = object.getAsJsonArray("talked_threebrothers");
                for (JsonElement jsonElement : talkedThreeBrothersArray) {
                    talkedThreeBrothers.add(jsonElement.getAsString());
                }
                this.siriusStartedQA = object.get("sirius_started_q_a").getAsBoolean();
                this.siriusQAChainDone = object.get("sirius_q_a_chain_done").getAsBoolean();
                this.siriusCompletedQA = object.get("sirius_completed_q_a").getAsBoolean();
                this.siriusClaimedDoubloon = object.get("sirius_claimed_doubloon").getAsBoolean();
                this.bughunter_step = object.get("bighunter_step").getAsInt();
            }

            public int getBughunter_step() {
                return bughunter_step;
            }

            public boolean isSiriusClaimedDoubloon() {
                return siriusClaimedDoubloon;
            }

            public boolean isSiriusCompletedQA() {
                return siriusCompletedQA;
            }

            public boolean isSiriusQAChainDone() {
                return siriusQAChainDone;
            }

            public boolean isSiriusStartedQA() {
                return siriusStartedQA;
            }

            public List<String> getTalkedThreeBrothers() {
                return talkedThreeBrothers;
            }
        }
        public class Castle {
            private final boolean unlockedPathwaySkip;
            private final int fairyStep;
            private final int grubberStacks;

            public Castle(JsonObject object) {
                this.unlockedPathwaySkip = object.get("unlocked_pathway_skip").getAsBoolean();
                this.fairyStep = object.get("fairy_step").getAsInt();
                this.grubberStacks = object.get("grubber_stacks").getAsInt();
            }

            public int getFairyStep() {
                return fairyStep;
            }

            public int getGrubberStacks() {
                return grubberStacks;
            }

            public boolean isUnlockedPathwaySkip() {
                return unlockedPathwaySkip;
            }
        }
        public class Access {
            private final long lastFree;
            private final boolean consumedPrism;

            public Access(JsonObject object) {
                this.lastFree = object.get("last_free").getAsLong();
                this.consumedPrism = object.get("consumed_prism").getAsBoolean();
            }

            public long getLastFree() {
                return lastFree;
            }

            public boolean isConsumedPrism() {
                return consumedPrism;
            }

        }
        public class Dreadfarm {
            private final int shaniaStage;
            private final List<Long> caducousFeederUses;

            public Dreadfarm(JsonObject object) {
                this.shaniaStage = object.get("shania_stage").getAsInt();
                this.caducousFeederUses = new ArrayList<>();
                JsonArray caducousFeederUsesArray = object.getAsJsonArray("caducous_feeder_uses");
                for (JsonElement jsonElement : caducousFeederUsesArray) {
                    caducousFeederUses.add(jsonElement.getAsLong());
                }
            }

            public int getShaniaStage() {
                return shaniaStage;
            }

            public List<Long> getCaducousFeederUses() {
                return caducousFeederUses;
            }
        }

        public Access getAccess() {
            return access;
        }

        public BlackLagoon getBlackLagoon() {
            return blackLagoon;
        }

        public Castle getCastle() {
            return castle;
        }

        public DeadCats getDeadCats() {
            return deadCats;
        }

        public Dreadfarm getDreadfarm() {
            return dreadfarm;
        }

        public Enigma getEnigma() {
            return enigma;
        }

        public Gallery getGallery() {
            return gallery;
        }

        public Inventory getInventory() {
            return inventory;
        }

        public List<String> getLifetimePurchasedBoundaries() {
            return lifetimePurchasedBoundaries;
        }

        public VillagePlaza getVillagePlaza() {
            return villagePlaza;
        }

        public WestVillage getWestVillage() {
            return westVillage;
        }

        public WitherCage getWitherCage() {
            return witherCage;
        }

        public WizardTower getWizardTower() {
            return wizardTower;
        }

        public WyldWoods getWyldWoods() {
            return wyldWoods;
        }
    }

    public class Inventory {
        private final EnderChestContents enderChestContents;
        private final InventoryContents inventoryContents;
        private final InventoryArmor inventoryArmor;
        private final EquipmentContents equipmentContents;

        public class EnderChestContents {
            private final int type;
            private final String data;

            public EnderChestContents(JsonObject object) {
                this.type = object.get("type").getAsInt();
                this.data = object.get("data").getAsString(); // TODO
            }

            public int getType() {
                return type;
            }

            public String getData() {
                return data;
            }
        }
        private final List<String> enderChestPageIcons;
        public class InventoryContents {
            private final int type;
            private final String data;

            public InventoryContents(JsonObject object) {
                this.type = object.get("type").getAsInt();
                this.data = object.get("data").getAsString();// TODO
            }

            public String getData() {
                return data;
            }
            public int getType() {
                return type;
            }
        }
        public class InventoryArmor {
            private final int type;
            private final String data;
            public InventoryArmor(JsonObject object) {
                this.type = object.get("type").getAsInt();
                this.data = object.get("data").getAsString(); // TODO
            }
            public int getType() {
                return type;
            }
            public String getData() {
                return data;
            }
        }
        public class EquipmentContents {
            private final int type;
            private final String data;
            public EquipmentContents(JsonObject object) {
                this.type = object.get("type").getAsInt();
                this.data = object.get("data").getAsString(); // TODO
            }
            public int getType() {
                return type;
            }

            public String getData() {
                return data;
            }
        }

        public Inventory(JsonObject object) {
            this.enderChestPageIcons = new ArrayList<>();
            JsonArray enderChestPageIconsArray = object.getAsJsonArray("ender_chest_page_icons");
            for (JsonElement jsonElement : enderChestPageIconsArray) {
                enderChestPageIcons.add(jsonElement.getAsString());
            }
            this.enderChestContents = new EnderChestContents(object.getAsJsonObject("ender_chest_contents"));
            this.inventoryContents = new InventoryContents(object.getAsJsonObject("inv_contents"));
            this.inventoryArmor = new InventoryArmor(object.getAsJsonObject("inv_armor"));
            this.equipmentContents = new EquipmentContents(object.getAsJsonObject("equipment_contents"));
        }
    }

    public class PlayerData {
        private final List<String> visitedZones;
        private final long lastDeath;
        private final HashMap<String, Integer> perks;
        //private final List<Effect> activeEffects; // TODO
        //private final List<Effect> pausedEffects; // TODO
        private final int reaperPeppersEaten;
        private final List<TempStatBuff> buffs;
        private final int deathCount;
        //private final List<Effect> disabledPotionEffects; // TODO
        private final List<String> achievementSpawnedIslandTypes;
        private final List<String> visitedModes;
        private final List<String> unlockedCollTiers;
        private final List<String> craftedGenerators;
        private final int fishingTreasureCaught;
        private final HashMap<String, BigInteger> experience;


        public PlayerData(JsonObject object) {
            this.visitedZones = new ArrayList<>();
            JsonArray visitedZonesArray = object.getAsJsonArray("visited_zones");
            for (JsonElement jsonElement : visitedZonesArray) {
                visitedZones.add(jsonElement.getAsString());
            }
            this.lastDeath = object.get("last_death").getAsLong();
            this.perks = new HashMap<>();
            JsonObject perksObject = object.getAsJsonObject("perks");
            for (Map.Entry<String, JsonElement> entry : perksObject.entrySet()) {
                String string = entry.getKey();
                perks.put(string, perksObject.get(string).getAsInt());
            }
            this.reaperPeppersEaten = object.get("reaper_peppers_eaten").getAsInt();
            this.buffs = new ArrayList<>();
            JsonArray buffsArray = object.getAsJsonArray("temp_stat_buffs");
            for (JsonElement jsonElement : buffsArray) {
                buffs.add(new TempStatBuff(jsonElement.getAsJsonObject()));
            }
            this.deathCount = object.get("death_count").getAsInt();
            this.achievementSpawnedIslandTypes = new ArrayList<>();
            JsonArray achievementSpawnedIslandTypesArray = object.get("achievement_spawned_island_types").getAsJsonArray();
            for (JsonElement jsonElement : achievementSpawnedIslandTypesArray) {
                achievementSpawnedIslandTypes.add(jsonElement.getAsString());
            }
            this.visitedModes = new ArrayList<>();
            JsonArray visitedModesArray = object.get("visited_modes").getAsJsonArray();
            for (JsonElement jsonElement : visitedModesArray) {
                visitedModes.add(jsonElement.getAsString());
            }
            this.unlockedCollTiers = new ArrayList<>();
            JsonArray unlockedCollTiersArray = object.get("unlocked_coll_tiers").getAsJsonArray();
            for (JsonElement jsonElement : unlockedCollTiersArray) {
                unlockedCollTiers.add(jsonElement.getAsString());
            }
            this.craftedGenerators = new ArrayList<>();
            JsonArray craftedGeneratorsArray = object.get("crafted_generators").getAsJsonArray();
            for (JsonElement jsonElement : craftedGeneratorsArray) {
                craftedGenerators.add(jsonElement.getAsString());
            }
            this.fishingTreasureCaught = object.get("fishing_treasure_caught").getAsInt();
            this.experience = new HashMap<>();
            JsonObject experienceObject = object.getAsJsonObject("experience");
            for (Map.Entry<String, JsonElement> entry : experienceObject.entrySet()) {
                String string = entry.getKey();
                experience.put(string, experienceObject.get(string).getAsBigInteger());
            }

        }


    }

    public class Dungeons {
        private final Catacombs catacombs;
        private final Catacombs masterCatacombs;
        private final HashMap<String, BigInteger> playerClasses;
        private final List<String> unlockedJournals;
        private final List<String> dungeonsBlahBlah;

        private final DailyRuns dailyRuns;

        public Dungeons(JsonObject object) {
            JsonObject typeObject = object.getAsJsonObject("dungeon_types");
            this.catacombs = new Catacombs(typeObject.get("catacombs").getAsJsonObject(), false);
            this.masterCatacombs = new Catacombs(typeObject.get("master_catacombs").getAsJsonObject(), true);
            this.playerClasses = new HashMap<>();
            JsonObject playerClassesObject = object.getAsJsonObject("player_classes");
            for (Map.Entry<String, JsonElement> entry  : playerClassesObject.entrySet()) {
                String string = entry.getKey();
                playerClasses.put(string, playerClassesObject.get(string).getAsBigInteger());
            }
            this.unlockedJournals = new ArrayList<>();
            this.dungeonsBlahBlah = new ArrayList<>();
            this.dailyRuns = new DailyRuns(object.getAsJsonObject("daily_runs"));
            JsonArray unlockedJournalsArray = object.getAsJsonObject("dungeon_journal").get("unlocked_journals").getAsJsonArray();
            for (JsonElement jsonElement : unlockedJournalsArray) {
                unlockedJournals.add(jsonElement.getAsString());
            }
            JsonArray dungeonsBlahBlahArray = object.getAsJsonArray("dungeons_blah_blah");
            for (JsonElement jsonElement : dungeonsBlahBlahArray) {
                dungeonsBlahBlah.add(jsonElement.getAsString());
            }
        }

        public Catacombs getCatacombs() {
            return catacombs;
        }

        public Catacombs getMasterCatacombs() {
            return masterCatacombs;
        }

        public DailyRuns getDailyRuns() {
            return dailyRuns;
        }

        public HashMap<String, BigInteger> getPlayerClasses() {
            return playerClasses;
        }

        public List<String> getDungeonsBlahBlah() {
            return dungeonsBlahBlah;
        }

        public List<String> getUnlockedJournals() {
            return unlockedJournals;
        }
        // TODO line 8991

        public class DailyRuns {
            private final int currentDayStamp;
            private final int completedRunsCount;

            public DailyRuns(JsonObject object) {
                this.currentDayStamp = object.get("current_day_stamp").getAsInt();
                this.completedRunsCount = object.get("completed_runs_count").getAsInt();
            }

            public int getCompletedRunsCount() {
                return completedRunsCount;
            }

            public int getCurrentDayStamp() {
                return currentDayStamp;
            }

        }

        public class Catacombs {
            private final HashMap<String, Float> mobsKilled;
            private final HashMap<String, Float> fastestTimeS;
            private final HashMap<String, Float> mostDamageTank;
            private final HashMap<String, Float> fastestTime;
            private final HashMap<String, Float> mostDamageMage;
            private final HashMap<String, Float> tierCompletions;
            private final HashMap<String, Float> mostDamageHealer;
            private final HashMap<String, Float> mostDamageArcher;
            private final HashMap<String, Float> watcherKills;
            private final HashMap<String, Float> mostHealing;
            private final HashMap<String, Float> bestScore;
            private final HashMap<String, Float> mostDamageBerserk;
            private final HashMap<String, Float> fastestTimeSPlus;
            private final HashMap<String, Float> mostMobsKilled;
            private final HashMap<String, Float> timesPlayed;
            private final HashMap<String, Float> milestoneCompletions;
            private final BigInteger experience;
            private final HashMap<String, List<BestRun>> bestRuns;
            private final boolean master;

            public Catacombs(JsonObject object, boolean master) {
                this.mobsKilled = new HashMap<>();
                JsonObject mobsKilledObject = object.getAsJsonObject("mobs_killed");
                for (Map.Entry<String, JsonElement> entry : mobsKilledObject.entrySet()) {
                    String string = entry.getKey();
                    mobsKilled.put(string, mobsKilledObject.get(string).getAsFloat());
                }
                this.fastestTimeS = new HashMap<>();
                JsonObject fastestTimeSObject = object.getAsJsonObject("fastest_time_s");
                for (Map.Entry<String, JsonElement> entry : fastestTimeSObject.entrySet()) {
                    String string = entry.getKey();
                    fastestTimeS.put(string, fastestTimeSObject.get(string).getAsFloat());
                }
                this.mostDamageTank = new HashMap<>();
                JsonObject mostDamageTankObject = object.getAsJsonObject("most_damage_tank");
                for (Map.Entry<String, JsonElement> entry : mostDamageTankObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageTank.put(string, mostDamageTankObject.get(string).getAsFloat());
                }
                this.fastestTime = new HashMap<>();
                JsonObject fastestTimeObject = object.getAsJsonObject("fastest_time");
                for (Map.Entry<String, JsonElement> entry : fastestTimeObject.entrySet()) {
                    String string = entry.getKey();
                    fastestTime.put(string, fastestTimeObject.get(string).getAsFloat());
                }
                this.mostDamageMage = new HashMap<>();
                JsonObject mostDamageMageObject = object.getAsJsonObject("most_damage_mage");
                for (Map.Entry<String, JsonElement> entry : mostDamageMageObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageMage.put(string, mostDamageMageObject.get(string).getAsFloat());
                }
                this.tierCompletions = new HashMap<>();
                JsonObject tierCompletionsObject = object.getAsJsonObject("tier_completions");
                for (Map.Entry<String, JsonElement> entry : tierCompletionsObject.entrySet()) {
                    String string = entry.getKey();
                    tierCompletions.put(string, tierCompletionsObject.get(string).getAsFloat());
                }
                this.mostDamageHealer = new HashMap<>();
                JsonObject mostDamageHealerObject = object.getAsJsonObject("most_damage_healer");
                for (Map.Entry<String, JsonElement> entry : mostDamageHealerObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageHealer.put(string, mostDamageHealerObject.get(string).getAsFloat());
                }
                this.mostDamageArcher = new HashMap<>();
                JsonObject mostDamageArcherObject = object.getAsJsonObject("most_damage_archer");
                for (Map.Entry<String, JsonElement> entry : mostDamageArcherObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageArcher.put(string, mostDamageArcherObject.get(string).getAsFloat());
                }
                this.watcherKills = new HashMap<>();
                JsonObject watcherKillsObject = object.getAsJsonObject("watcher_kills");
                for (Map.Entry<String, JsonElement> entry : watcherKillsObject.entrySet()) {
                    String string = entry.getKey();
                    watcherKills.put(string, watcherKillsObject.get(string).getAsFloat());
                }
                this.mostHealing = new HashMap<>();
                JsonObject mostHealingObject = object.getAsJsonObject("most_healing");
                for (Map.Entry<String, JsonElement> entry : mostHealingObject.entrySet()) {
                    String string = entry.getKey();
                    mostHealing.put(string, mostHealingObject.get(string).getAsFloat());
                }
                this.bestScore = new HashMap<>();
                JsonObject bestScoreObject = object.getAsJsonObject("best_score");
                for (Map.Entry<String, JsonElement> entry : bestScoreObject.entrySet()) {
                    String string = entry.getKey();
                    bestScore.put(string, bestScoreObject.get(string).getAsFloat());
                }
                this.mostDamageBerserk = new HashMap<>();
                JsonObject mostDamageBerserkObject = object.getAsJsonObject("most_damage_berserk");
                for (Map.Entry<String, JsonElement> entry : mostDamageBerserkObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageBerserk.put(string, mostDamageBerserkObject.get(string).getAsFloat());
                }
                this.fastestTimeSPlus = new HashMap<>();
                JsonObject fastestTimeSPlusObject = object.getAsJsonObject("fastest_time_s_plus");
                for (Map.Entry<String, JsonElement> entry : fastestTimeSPlusObject.entrySet()) {
                    String string = entry.getKey();
                    fastestTimeSPlus.put(string, fastestTimeSPlusObject.get(string).getAsFloat());
                }
                this.mostMobsKilled = new HashMap<>();
                JsonObject mostMobsKilledObject = object.getAsJsonObject("most_mobs_killed");
                for (Map.Entry<String, JsonElement> entry : mostMobsKilledObject.entrySet()) {
                    String string = entry.getKey();
                    mostMobsKilled.put(string, mostMobsKilledObject.get(string).getAsFloat());
                }
                this.timesPlayed = new HashMap<>();
                JsonObject timesPlayedObject = object.getAsJsonObject("times_played");
                for (Map.Entry<String, JsonElement> entry : timesPlayedObject.entrySet()) {
                    String string = entry.getKey();
                    timesPlayed.put(string, timesPlayedObject.get(string).getAsFloat());
                }
                this.milestoneCompletions = new HashMap<>();
                JsonObject milestoneCompletionsObject = object.getAsJsonObject("milestone_completions");
                for (Map.Entry<String, JsonElement> entry : milestoneCompletionsObject.entrySet()) {
                    String string = entry.getKey();
                    milestoneCompletions.put(string, milestoneCompletionsObject.get(string).getAsFloat());
                }
                this.experience = object.get("experience").getAsBigInteger();
                this.bestRuns = new HashMap<>();
                JsonObject bestRunsObject = object.getAsJsonObject("best_runs");
                for (Map.Entry<String, JsonElement> entry : bestRunsObject.entrySet()) {
                    String string = entry.getKey();
                    bestRuns.put(string, new ArrayList<>());
                    JsonArray bestRunsArray = bestRunsObject.getAsJsonArray(string);
                    for (JsonElement jsonElement : bestRunsArray) {
                        bestRuns.get(string).add(new BestRun(jsonElement.getAsJsonObject()));
                    }
                }
                this.master = master;
            }

            public BigInteger getExperience() {
                return experience;
            }

            public HashMap<String, Float> getMobsKilled() {
                return mobsKilled;
            }
            public HashMap<String, Float> getFastestTimeS() {
                return fastestTimeS;
            }
            public HashMap<String, Float> getMostDamageTank() {
                return mostDamageTank;
            }
            public HashMap<String, Float> getFastestTime() {
                return fastestTime;
            }
            public HashMap<String, Float> getMostDamageMage() {
                return mostDamageMage;
            }
            public HashMap<String, Float> getTierCompletions() {
                return tierCompletions;
            }
            public HashMap<String, Float> getMostDamageHealer() {
                return mostDamageHealer;
            }
            public HashMap<String, Float> getMostDamageArcher() {
                return mostDamageArcher;
            }
            public HashMap<String, Float> getWatcherKills() {
                return watcherKills;
            }
            public HashMap<String, Float> getMostHealing() {
                return mostHealing;
            }
            public HashMap<String, Float> getBestScore() {
                return bestScore;
            }
            public HashMap<String, Float> getMostDamageBerserk() {
                return mostDamageBerserk;
            }
            public HashMap<String, Float> getFastestTimeSPlus() {
                return fastestTimeSPlus;
            }
            public HashMap<String, Float> getMostMobsKilled() {
                return mostMobsKilled;
            }
            public HashMap<String, Float> getTimesPlayed() {
                return timesPlayed;
            }
            public HashMap<String, Float> getMilestoneCompletions() {
                return milestoneCompletions;
            }

            public boolean isMaster() {
                return master;
            }

            public HashMap<String, List<BestRun>> getBestRuns() {
                return bestRuns;
            }

            public class BestRun {
                private final long timeStamp;
                private final int scoreExploration;
                private final int scoreSpeed;
                private final int scoreSkill;
                private final int scoreBonus;
                private final String dungeonClass;
                private final List<String> teammates;
                private final int elapsedTime;
                private final double damageDealt;
                private final int deaths;
                private final int mobsKilled;
                private final int secretsFound;
                private final int damageMitigated;
                private final int allyHealing;

                public BestRun(JsonObject object) {
                    this.timeStamp = object.get("time_stamp").getAsLong();
                    this.scoreExploration = object.get("score_exploration").getAsInt();
                    this.scoreSpeed = object.get("score_speed").getAsInt();
                    this.scoreSkill = object.get("score_skill").getAsInt();
                    this.scoreBonus = object.get("score_bonus").getAsInt();
                    this.dungeonClass = object.get("dungeon_class").getAsString();
                    this.teammates = new ArrayList<>();
                    JsonArray teammatesArray = object.getAsJsonArray("teammates");
                    for (JsonElement jsonElement : teammatesArray) {
                        teammates.add(jsonElement.getAsString());
                    }
                    this.elapsedTime = object.get("elapsed_time").getAsInt();
                    this.damageDealt = object.get("damage_dealt").getAsDouble();
                    this.deaths = object.get("deaths").getAsInt();
                    this.mobsKilled = object.get("mobs_killed").getAsInt();
                    this.secretsFound = object.get("secrets_found").getAsInt();
                    this.damageMitigated = object.get("damage_mitigated").getAsInt();
                    this.allyHealing = object.get("ally_healing").getAsInt();
                }

                public double getDamageDealt() {
                    return damageDealt;
                }

                public int getAllyHealing() {
                    return allyHealing;
                }

                public int getDamageMitigated() {
                    return damageMitigated;
                }

                public int getDeaths() {
                    return deaths;
                }

                public int getElapsedTime() {
                    return elapsedTime;
                }

                public int getMobsKilled() {
                    return mobsKilled;
                }

                public int getScoreExploration() {
                    return scoreExploration;
                }

                public int getScoreSkill() {
                    return scoreSkill;
                }

                public int getScoreBonus() {
                    return scoreBonus;
                }

                public int getScoreSpeed() {
                    return scoreSpeed;
                }

                public int getSecretsFound() {
                    return secretsFound;
                }

                public List<String> getTeammates() {
                    return teammates;
                }

                public long getTimeStamp() {
                    return timeStamp;
                }

                public String getDungeonClass() {
                    return dungeonClass;
                }
            }
        }

    }
    public class Currencies {
        private final double coinPurse;
        private final double motesPurse;
        private final HashMap<String, Integer> essence;

        public Currencies(JsonObject object) {
            this.coinPurse = object.get("coin_purse").getAsDouble();
            this.motesPurse = object.get("motes_purse").getAsDouble();
            this.essence = new HashMap<>();
            JsonObject essenceObject = object.getAsJsonObject("essence");
            for (Map.Entry<String, JsonElement> entry : essenceObject.entrySet()) {
                String string = entry.getKey();
                essence.put(string, essenceObject.getAsJsonObject(string).get("current").getAsInt());
            }
        }

        public double getCoinPurse() {
            return coinPurse;
        }

        public double getMotesPurse() {
            return motesPurse;
        }

        public HashMap<String, Integer> getEssence() {
            return essence;
        }
    }
    public class JacobsContest {
        private final HashMap<String, Integer> medalsInv;
        private final HashMap<String, Integer> perks;
        private final List<JacobsContestData> jacobsContestDataList;
        private final boolean talked;
        private final HashMap<String, List<String>> uniqueBrackets;
        private final boolean migration;
        private final HashMap<String, Integer> personalBests;

        public JacobsContest(JsonObject object) {
            this.medalsInv = new HashMap<>();
            JsonObject medalsInvObject = object.getAsJsonObject("medals_inv");
            for (Map.Entry<String, JsonElement> entry: medalsInvObject.entrySet()) {
                String string = entry.getKey();
                medalsInv.put(string, medalsInvObject.get(string).getAsInt());
            }
            this.perks = new HashMap<>();
            JsonObject perksObject = object.getAsJsonObject("perks");
            for (Map.Entry<String, JsonElement> entry: perksObject.entrySet()) {
                String string = entry.getKey();
                perks.put(string, perksObject.get(string).getAsInt());
            }
            this.jacobsContestDataList = new ArrayList<>();
            JsonArray jacobsContestDataListArray = object.getAsJsonArray("contests");
            for (JsonElement jsonElement : jacobsContestDataListArray) {
                JsonObject contestObject = jsonElement.getAsJsonObject();
            }
            this.talked = object.get("talked").getAsBoolean();
            this.uniqueBrackets = new HashMap<>();
            JsonObject uniqueBracketsObject = object.getAsJsonObject("unique_brackets");
            for (Map.Entry<String, JsonElement> entry: uniqueBracketsObject.entrySet()) {
                String string = entry.getKey();
                uniqueBrackets.put(string, new ArrayList<>());
                JsonArray stringArray = uniqueBracketsObject.getAsJsonArray(string);
                for (JsonElement jsonElement : stringArray) {
                    uniqueBrackets.get(string).add(jsonElement.getAsString());
                }
            }
            this.migration = object.get("migration").getAsBoolean();
            this.personalBests = new HashMap<>();
            JsonObject personalBestsObject = object.getAsJsonObject("personal_bests");
            for (Map.Entry<String, JsonElement> entry : personalBestsObject.entrySet()) {
                String string = entry.getKey();
                personalBests.put(string, personalBestsObject.get(string).getAsInt());
            }
        }

        public HashMap<String, Integer> getMedalsInv() {
            return medalsInv;
        }

        public HashMap<String, Integer> getPerks() {
            return perks;
        }

        public HashMap<String, Integer> getPersonalBests() {
            return personalBests;
        }

        public HashMap<String, List<String>> getUniqueBrackets() {
            return uniqueBrackets;
        }

        public List<JacobsContestData> getJacobsContestDataList() {
            return jacobsContestDataList;
        }

        public boolean isTalked() {
            return talked;
        }

        public boolean isMigration() {
            return migration;
        }

        public class JacobsContestData {
            private final int collected;
            private final Boolean claimedRewards;
            private final int claimedPosition;
            private final String claimedMedal;
            private final int claimedParticipants;

            public JacobsContestData(JsonObject object) {
                this.collected = object.get("collected").getAsInt();
                this.claimedParticipants = object.get("claimed_participants").getAsInt();
                this.claimedMedal = object.get("claimed_medal").getAsString();
                this.claimedPosition = object.get("claimed_position").getAsInt();
                this.claimedRewards = object.get("claimed_rewards").getAsBoolean();
            }

            public Boolean getClaimedRewards() {
                return claimedRewards;
            }

            public String getClaimedMedal() {
                return claimedMedal;
            }

            public int getClaimedParticipants() {
                return claimedParticipants;
            }

            public int getClaimedPosition() {
                return claimedPosition;
            }

            public int getCollected() {
                return collected;
            }
        }
    }
    public class ItemData {
        private final int soulflow;
        private final int favoriteArrow;
        public ItemData(JsonObject object) {
            this.soulflow = object.get("soulflow").getAsInt();
            this.favoriteArrow = object.get("favorite_arrow").getAsInt();
        }

        public int getFavoriteArrow() {
            return favoriteArrow;
        }

        public int getSoulflow() {
            return soulflow;
        }
    }
    public class Levelling {
        private final int experience;
        private final HashMap<String, Integer> completions;
        private final List<String> completed;
        private final boolean migratedCompletions;
        private final List<String> completedTasks;
        private final String highestPetScore;
        private final int miningFiestaOresMined;
        private final int fishingFestivalSharksKilled;
        private final boolean migrated;
        private final boolean migratedCompletions2;
        private final List<String> lastViewedTasks;
        private final boolean claimedTalisman;
        private final String bopBonus;
        private final boolean categoryExpanded;
        private final List<String> emblemUnlocks;
        private final String taskSort;

        public Levelling(JsonObject object) {
            this.experience = object.get("experience").getAsInt();
            this.completions = new HashMap<>();
            JsonObject completionsObject = object.getAsJsonObject("completions");
            for (Map.Entry<String, JsonElement> entry : completionsObject.entrySet()) {
                String string = entry.getKey();
                completions.put(string, completionsObject.get(string).getAsInt());
            }
            this.completed = new ArrayList<>();
            JsonArray completedArray = object.getAsJsonArray("completed");
            for (JsonElement jsonElement : completedArray) {
                completed.add(jsonElement.getAsString());
            }
            this.migratedCompletions = object.get("migrated_completions").getAsBoolean();
            this.completedTasks = new ArrayList<>();
            JsonArray completedTasksArray = object.getAsJsonArray("completed_tasks");
            for (JsonElement jsonElement : completedTasksArray) {
                completedTasks.add(jsonElement.getAsString());
            }
            this.highestPetScore = object.get("highest_pet_score").getAsString();
            this.miningFiestaOresMined = object.get("mining_fiesta_ores_mined").getAsInt();
            this.fishingFestivalSharksKilled = object.get("fishing_festival_sharks_killed").getAsInt();
            this.migrated = object.get("migrated").getAsBoolean();
            this.migratedCompletions2 = object.get("migrated_completions2").getAsBoolean();
            this.lastViewedTasks = new ArrayList<>();
            JsonArray lastViewedTasksArray = object.getAsJsonArray("last_viewed_tasks");
            for (JsonElement jsonElement : lastViewedTasksArray) {
                lastViewedTasks.add(jsonElement.getAsString());
            }
            this.claimedTalisman = object.get("claimed_talisman").getAsBoolean();
            this.bopBonus = object.get("bop_bonus").getAsString();
            this.categoryExpanded = object.get("category_expanded").getAsBoolean();
            this.emblemUnlocks = new ArrayList<>();
            JsonArray emblemUnlocksArray = object.getAsJsonArray("emblem_unlocks");
            for (JsonElement jsonElement : emblemUnlocksArray) {
                emblemUnlocks.add(jsonElement.getAsString());
            }
            this.taskSort = object.get("task_sort").getAsString();
        }

        public HashMap<String, Integer> getCompletions() {
            return completions;
        }

        public int getExperience() {
            return experience;
        }

        public int getFishingFestivalSharksKilled() {
            return fishingFestivalSharksKilled;
        }

        public int getMiningFiestaOresMined() {
            return miningFiestaOresMined;
        }

        public List<String> getCompleted() {
            return completed;
        }

        public List<String> getCompletedTasks() {
            return completedTasks;
        }

        public List<String> getEmblemUnlocks() {
            return emblemUnlocks;
        }

        public List<String> getLastViewedTasks() {
            return lastViewedTasks;
        }

        public String getBopBonus() {
            return bopBonus;
        }

        public String getHighestPetScore() {
            return highestPetScore;
        }

        public String getTaskSort() {
            return taskSort;
        }

        public boolean isMigrated() {
            return migrated;
        }

        public boolean isClaimedTalisman() {
            return claimedTalisman;
        }

        public boolean isCategoryExpanded() {
            return categoryExpanded;
        }

        public boolean isMigratedCompletions() {
            return migratedCompletions;
        }

        public boolean isMigratedCompletions2() {
            return migratedCompletions2;
        }
    }
    public class AccessoryBagStorage {
        private final Tuning tuning;
        private final String selectedPower;
        private final List<String> unlockedPowers;
        private final int bagUpgradesPurchased;
        private final int highestMagicalPower;

        public AccessoryBagStorage(JsonObject object) {
            this.tuning = new Tuning(object.getAsJsonObject("tuning"));
            this.selectedPower = object.getAsJsonPrimitive("selected_power").getAsString();
            this.bagUpgradesPurchased = object.getAsJsonPrimitive("bag_upgrades_purchased").getAsInt();
            this.unlockedPowers = new ArrayList<>();
            JsonArray unlockedPowersArray = object.getAsJsonArray("unlocked_powers");
            for (JsonElement jsonElement : unlockedPowersArray) {
                unlockedPowers.add(jsonElement.getAsString());
            }
            this.highestMagicalPower = object.getAsJsonPrimitive("highest_magical_power").getAsInt();
        }

        public int getBagUpgradesPurchased() {
            return bagUpgradesPurchased;
        }

        public int getHighestMagicalPower() {
            return highestMagicalPower;
        }

        public List<String> getUnlockedPowers() {
            return unlockedPowers;
        }

        public String getSelectedPower() {
            return selectedPower;
        }

        public Tuning getTuning() {
            return tuning;
        }

        public class Tuning {
            private final HashMap<String, Integer> slot0;
            public Tuning(JsonObject object) {
                this.slot0 = new HashMap<>();
                JsonObject slot0Object = object.getAsJsonObject("slot_0");
                for (Map.Entry<String, JsonElement> entry : slot0Object.entrySet()) {
                    String string = entry.getKey();
                    slot0.put(string, slot0Object.get(string).getAsInt());
                }
            }

            public HashMap<String, Integer> getSlot0() {
                return slot0;
            }
        }

    }
    public class PetsData {
        public class PetCare {
            private final BigInteger coinsSpent;
            private final List<String> petTypesSacrificed;

            public PetCare(JsonObject object) {
                this.coinsSpent = object.get("coins_spent").getAsBigInteger();
                this.petTypesSacrificed = new ArrayList<>();
                JsonArray petTypesSacrificedArray = object.getAsJsonArray("pet_types_sacrificed");
                for (JsonElement jsonElement : petTypesSacrificedArray) {
                    petTypesSacrificed.add(jsonElement.getAsString());
                }
            }

            public BigInteger getCoinsSpent() {
                return coinsSpent;
            }

            public List<String> getPetTypesSacrificed() {
                return petTypesSacrificed;
            }
        }
        public class AutoPet {
            private final int rulesLimit;
            private final List<AutoPetRule> autoPetRules;
            private final boolean migrated;
            private final boolean migrated2;

            public AutoPet(JsonObject object) {
                this.rulesLimit = object.get("rules_limit").getAsInt();
                this.autoPetRules = new ArrayList<>();
                JsonArray autoPetRulesArray = object.getAsJsonArray("rules");
                for (JsonElement jsonElement : autoPetRulesArray) {
                    autoPetRules.add(new AutoPetRule(jsonElement.getAsJsonObject()));
                }
                this.migrated = object.get("migrated").getAsBoolean();
                this.migrated2 = object.get("migrated_2").getAsBoolean();

            }

            public boolean isMigrated() {
                return migrated;
            }

            public boolean isMigrated2() {
                return migrated2;
            }

            public List<AutoPetRule> getAutoPetRules() {
                return autoPetRules;
            }

            public int getRulesLimit() {
                return rulesLimit;
            }

            public class AutoPetRule {
                private final UUID uuid;
                private final String id;
                private final String name;
                private final UUID uniqueID;
                private final List<AutoPetRuleException> autoPetRuleExceptions;
                private final boolean disabled;
                private final HashMap<String, String> data;
                public AutoPetRule(JsonObject object) {
                    this.uuid = UUID.fromString(object.get("uuid").getAsString());
                    this.id = object.get("id").getAsString();
                    this.name = object.get("name").getAsString();
                    this.uniqueID = UUID.fromString(object.get("uniqueId").getAsString());
                    this.autoPetRuleExceptions = new ArrayList<>();
                    JsonArray autoPetRuleExceptionsArray = object.getAsJsonArray("exceptions");
                    for (JsonElement jsonElement : autoPetRuleExceptionsArray) {
                        autoPetRuleExceptions.add(new AutoPetRuleException(jsonElement.getAsJsonObject()));
                    }
                    this.disabled = object.get("disabled").getAsBoolean();
                    this.data = new HashMap<>();
                    JsonObject dataObject = object.getAsJsonObject("data");
                    for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                        String string = entry.getKey();
                        data.put(string, dataObject.get(string).getAsString());
                    }

                }

                public boolean isDisabled() {
                    return disabled;
                }

                public HashMap<String, String> getData() {
                    return data;
                }

                public String getId() {
                    return id;
                }

                public UUID getUniqueID() {
                    return uniqueID;
                }

                public UUID getUuid() {
                    return uuid;
                }

                public List<AutoPetRuleException> getAutoPetRuleExceptions() {
                    return autoPetRuleExceptions;
                }

                public String getName() {
                    return name;
                }

                public class AutoPetRuleException {
                    private final String id;
                    private final HashMap<String, String> data;

                    public AutoPetRuleException(JsonObject object) {
                        this.id = object.get("id").getAsString();
                        this.data = new HashMap<>();
                        JsonObject dataObject = object.getAsJsonObject("data");
                        for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                            String string = entry.getKey();
                            data.put(string, dataObject.get(string).getAsString());
                        }
                    }

                    public HashMap<String, String> getData() {
                        return data;
                    }

                    public String getId() {
                        return id;
                    }
                }
            }
        }
        private final List<Pet> pets;
        private final PetCare petCare;
        private final AutoPet autoPet;

        public PetsData(JsonObject object) {
            this.petCare = new PetCare(object.getAsJsonObject("pet_care"));
            this.autoPet = new AutoPet(object.getAsJsonObject("autopet"));
            this.pets = new ArrayList<>();
            JsonArray petsArray = object.getAsJsonArray("pets");
            for (JsonElement jsonElement : petsArray) {
                pets.add(new Pet(jsonElement.getAsJsonObject()));
            }
        }

        public AutoPet getAutoPet() {
            return autoPet;
        }

        public List<Pet> getPets() {
            return pets;
        }

        public PetCare getPetCare() {
            return petCare;
        }

        public class Pet {
            private final UUID uuid;
            private final UUID uniqueId;
            private final String type;
            private final BigInteger exp;
            private final boolean active;
            private final String tier;
            private final String heldItem;
            private final int candyUsed;
            private final String skin;

            public Pet(JsonObject object) {
                this.uuid = UUID.fromString(object.get("uuid").getAsString());
                this.uniqueId = UUID.fromString(object.get("uniqueId").getAsString());
                this.type = object.get("type").getAsString();
                this.exp = object.get("exp").getAsBigInteger();
                this.active = object.get("active").getAsBoolean();
                this.tier = object.get("tier").getAsString();
                this.heldItem = object.get("heldItem").getAsString();
                this.candyUsed = object.get("candyUsed").getAsInt();
                this.skin = object.get("skin").getAsString();
            }

            public UUID getUuid() {
                return uuid;
            }

            public String getType() {
                return type;
            }

            public int getCandyUsed() {
                return candyUsed;
            }

            public BigInteger getExp() {
                return exp;
            }

            public String getHeldItem() {
                return heldItem;
            }

            public String getSkin() {
                return skin;
            }

            public String getTier() {
                return tier;
            }

            public UUID getUniqueId() {
                return uniqueId;
            }

            public boolean isActive() {
                return active;
            }
        }
    }
    public class GardenPlayerData {
        private final int copper;
        private final int larvaConsumed;

        public GardenPlayerData(JsonObject object) {
            this.copper = object.get("copper").getAsInt();
            this.larvaConsumed = object.get("larva_consumed").getAsInt();
        }

        public int getCopper() {
            return copper;
        }

        public int getLarvaConsumed() {
            return larvaConsumed;
        }
    }

    public class Events {
        private final Easter easter;

        public Events(JsonObject object) {
            this.easter = new Easter(object.getAsJsonObject("easter"));
        }

        public Easter getEaster() {
            return easter;
        }

        public class Easter {
            private final long chocolate;
            private final long chocolateSincePrestige;
            private final long totalChocolate;
            private final HashMap<String, Integer> employees;
            private final long lastViewedChocolateFactory;
            private final Rabbits rabbits;
            private final Shop shop;
            private final int rabbitBarnCapacityLevel;
            private final String rabbitSort;
            private final int chocolateLevel;
            private final TimeTower timeTower;
            private final String rabbitFilter;
            private final int chocolateMultiplierUpgrades;
            private final int clickUpgrades;
            private final int rabbitRarityUpgrades;
            private final int supremeChocolateBars;
            private final int refinedDarkCacaoTruffles;

            public Easter(JsonObject object) {
                this.chocolate = object.get("chocolate").getAsLong();
                this.chocolateSincePrestige = object.get("chocolate_since_prestige").getAsLong();
                this.totalChocolate = object.get("total_chocolate").getAsLong();
                this.employees = new HashMap<>();
                JsonObject employeesObject = object.getAsJsonObject("employees");
                for (Map.Entry<String, JsonElement> entry : employeesObject.entrySet()) {
                    String string = entry.getKey();
                    employees.put(string, employeesObject.get(string).getAsInt());
                }
                this.lastViewedChocolateFactory = object.get("last_viewed_chocolate_factory").getAsInt();
                this.rabbits = new Rabbits(object.getAsJsonObject("rabbits"));
                this.shop = new Shop(object.getAsJsonObject("shop"));
                this.rabbitBarnCapacityLevel = object.get("rabbit_barn_capacity_level").getAsInt();
                this.rabbitSort = object.get("rabbit_sort").getAsString();
                this.chocolateLevel = object.get("chocolate_level").getAsInt();
                this.timeTower = new TimeTower(object.getAsJsonObject("time_tower"));
                this.rabbitFilter = object.get("rabbit_filter").getAsString();
                this.chocolateMultiplierUpgrades = object.get("chocolate_multiplier_upgrades").getAsInt();
                this.clickUpgrades = object.get("click_upgrades").getAsInt();
                this.rabbitRarityUpgrades = object.get("rabbit_rarity_upgrades").getAsInt();
                this.supremeChocolateBars = object.get("supreme_chocolate_bars").getAsInt();
                this.refinedDarkCacaoTruffles = object.get("refined_dark_cacao_truffles").getAsInt();
            }

            public class TimeTower {
                private final int charges;
                private final long lastChargeTime;
                private final int level;
                private final long activationTime;

                public TimeTower(JsonObject object) {
                    this.charges = object.get("charges").getAsInt();
                    this.lastChargeTime = object.get("last_charge_time").getAsLong();
                    this.level = object.get("level").getAsInt();
                    this.activationTime = object.get("activation_time").getAsLong();
                }

                public int getCharges() {
                    return charges;
                }

                public int getLevel() {
                    return level;
                }

                public long getActivationTime() {
                    return activationTime;
                }

                public long getLastChargeTime() {
                    return lastChargeTime;
                }
            }

            public class Shop {
                private final int year;
                private final List<String> rabbitsShop;
                private final List<String> rabbitsPurchased;
                private final long chocolateSpent;
                private final int cocoaFortuneUpgrades;

                public Shop(JsonObject object) {
                    this.year = object.get("year").getAsInt();
                    this.rabbitsShop = new ArrayList<>();
                    JsonArray rabbitsShopArray = object.getAsJsonArray("rabbits");
                    for (JsonElement jsonElement : rabbitsShopArray) {
                        rabbitsShop.add(jsonElement.getAsString());
                    }
                    this.rabbitsPurchased = new ArrayList<>();
                    JsonArray rabbitsPurchasedArray = object.getAsJsonArray("rabbits_purchased");
                    for (JsonElement jsonElement : rabbitsPurchasedArray) {
                        rabbitsPurchased.add(jsonElement.getAsString());
                    }
                    this.chocolateSpent = object.getAsJsonPrimitive("chocolate").getAsLong();
                    this.cocoaFortuneUpgrades = object.getAsJsonPrimitive("cocoa_fortune_upgrades").getAsInt();
                }

                public int getCocoaFortuneUpgrades() {
                    return cocoaFortuneUpgrades;
                }

                public int getYear() {
                    return year;
                }

                public List<String> getRabbitsPurchased() {
                    return rabbitsPurchased;
                }

                public List<String> getRabbitsShop() {
                    return rabbitsShop;
                }

                public long getChocolateSpent() {
                    return chocolateSpent;
                }
            }
            public class Rabbits {
                private final HashMap<String, Long> collectedEggs;
                private final HashMap<String, List<String>> collectedLocations;

                public Rabbits(JsonObject object) {
                    this.collectedEggs = new HashMap<>();
                    JsonObject collectedEggsObject = object.getAsJsonObject("collected_eggs");
                    for (Map.Entry<String, JsonElement> entry : collectedEggsObject.entrySet()) {
                        String string = entry.getKey();
                        collectedEggs.put(string, collectedEggsObject.get(string).getAsLong());
                    }
                    this.collectedLocations = new HashMap<>();
                    JsonObject collectedLocationsObject = object.getAsJsonObject("collected_locations");
                    for (Map.Entry<String, JsonElement> entry : collectedLocationsObject.entrySet()) {
                        String string = entry.getKey();
                        collectedLocations.put(string, new ArrayList<>());
                        JsonArray locationArray = collectedLocationsObject.getAsJsonArray(string);
                        for (JsonElement jsonElement : locationArray) {
                            collectedLocations.get(string).add(jsonElement.getAsString());
                        }
                    }
                }

                public HashMap<String, List<String>> getCollectedLocations() {
                    return collectedLocations;
                }

                public HashMap<String, Long> getCollectedEggs() {
                    return collectedEggs;
                }
            }

            public HashMap<String, Integer> getEmployees() {
                return employees;
            }

            public int getChocolateLevel() {
                return chocolateLevel;
            }

            public long getChocolate() {
                return chocolate;
            }

            public int getChocolateMultiplierUpgrades() {
                return chocolateMultiplierUpgrades;
            }

            public int getClickUpgrades() {
                return clickUpgrades;
            }

            public int getRabbitBarnCapacityLevel() {
                return rabbitBarnCapacityLevel;
            }

            public int getRabbitRarityUpgrades() {
                return rabbitRarityUpgrades;
            }

            public int getRefinedDarkCacaoTruffles() {
                return refinedDarkCacaoTruffles;
            }

            public int getSupremeChocolateBars() {
                return supremeChocolateBars;
            }

            public long getChocolateSincePrestige() {
                return chocolateSincePrestige;
            }

            public long getLastViewedChocolateFactory() {
                return lastViewedChocolateFactory;
            }

            public long getTotalChocolate() {
                return totalChocolate;
            }

            public Rabbits getRabbits() {
                return rabbits;
            }

            public Shop getShop() {
                return shop;
            }

            public String getRabbitFilter() {
                return rabbitFilter;
            }

            public String getRabbitSort() {
                return rabbitSort;
            }

            public TimeTower getTimeTower() {
                return timeTower;
            }
        }
    }

    public class GlacitePlayerData {
        private final List<String> fossilsDonated;
        private final double fossilDust;
        private final HashMap<String, Integer> corpsesLooted;
        private final int mineshaftsEntered;

        public GlacitePlayerData(JsonObject object) {
            this.fossilsDonated = new ArrayList<>();
            JsonArray fossilsDonatedArray = object.getAsJsonArray("fossils_donated");
            for (JsonElement jsonElement : fossilsDonatedArray) {
                fossilsDonated.add(jsonElement.getAsString());
            }
            this.corpsesLooted = new HashMap<>();
            JsonObject corpsesLootedObject = object.getAsJsonObject("corpses_looted");
            for (Map.Entry<String, JsonElement> entry : corpsesLootedObject.entrySet()) {
                String string = entry.getKey();
                corpsesLooted.put(string, corpsesLootedObject.get(string).getAsInt());
            }
            this.fossilDust = object.getAsJsonPrimitive("fossil_dust").getAsDouble();
            this.mineshaftsEntered = object.getAsJsonPrimitive("mineshafts_entered").getAsInt();
        }

        public double getFossilDust() {
            return fossilDust;
        }

        public HashMap<String, Integer> getCorpsesLooted() {
            return corpsesLooted;
        }

        public int getMineshaftsEntered() {
            return mineshaftsEntered;
        }

        public List<String> getFossilsDonated() {
            return fossilsDonated;
        }
    }

    public class TempStatBuff {
        private final int stat;
        private final String key;
        private final int amount;
        private final long expireAt;

        public TempStatBuff(JsonObject object) {
            this.stat = object.get("stat").getAsInt();
            this.key = object.get("key").getAsString();
            this.amount = object.get("amount").getAsInt();
            this.expireAt = object.get("expire_at").getAsLong();
        }

        public int getAmount() {
            return amount;
        }

        public int getStat() {
            return stat;
        }

        public long getExpireAt() {
            return expireAt;
        }

        public String getKey() {
            return key;
        }
    }

    public UUID getProfileUUID(UUID playerUUID) {
        JsonObject activeProfile = getActiveProfile(playerUUID);
        SkyblockProfile profile = new SkyblockProfile(activeProfile);
        return profile.getProfileID();
    }
}
