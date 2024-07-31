package me.vlink102.melomod.util.game;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.events.ChatEvent;
import me.vlink102.melomod.events.InternalLocraw;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;

import java.util.*;

public class SkyblockUtil {
    private final MeloMod meloMod;

    public SkyblockUtil(MeloMod meloMod) {
        this.meloMod = meloMod;
    }

    public static Location getPlayerLocation() {
        return InternalLocraw.getLocation();
    }

    public static JsonObject getAsJsonObject(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonObject()) {
                return parent.getAsJsonObject(key);
            }
        }
        return new JsonObject();
    }

    public static JsonArray getAsJsonArray(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonArray()) {
                return parent.getAsJsonArray(key);
            }
        }
        return new JsonArray();
    }

    public static Boolean getAsBoolean(String key, JsonObject parent) {
        if (parent == null) return false;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonPrimitive()) {
                if (parent.getAsJsonPrimitive(key).isBoolean()) {
                    return parent.get(key).getAsBoolean();
                }
            }
        }
        return false;
    }

    public static String getAsString(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonPrimitive()) {
                if (parent.getAsJsonPrimitive(key).isString()) {
                    return parent.get(key).getAsString();
                }
            }
        }
        return null;
    }

    public static Integer getAsInteger(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonPrimitive()) {
                if (parent.getAsJsonPrimitive(key).isNumber()) {
                    return parent.get(key).getAsInt();
                }
            }
        }
        return null;
    }

    public static Float getAsFloat(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonPrimitive()) {
                if (parent.getAsJsonPrimitive(key).isNumber()) {
                    return parent.get(key).getAsFloat();
                }
            }
        }
        return null;
    }

    public static Double getAsDouble(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonPrimitive()) {
                if (parent.getAsJsonPrimitive(key).isNumber()) {
                    return parent.get(key).getAsDouble();
                }
            }
        }
        return null;
    }

    public static Long getAsLong(String key, JsonObject parent) {
        if (parent == null) return null;
        if (parent.has(key)) {
            if (!parent.get(key).isJsonNull() && parent.get(key) != null && parent.get(key).isJsonPrimitive()) {
                if (parent.getAsJsonPrimitive(key).isNumber()) {
                    return parent.get(key).getAsLong();
                }
            }
        }
        return null;
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

    public static UUID fromString(String uuid) {
        if (uuid == null) return null;
        if (!uuid.contains("-")) {
            return fixMalformed(uuid);
        }
        return UUID.fromString(uuid);
    }

    public static UUID fixMalformed(String uuid) {
        return fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20));
    }

    public void updateInformation(JsonObject object) {
        JsonArray profiles = object.get("profiles").getAsJsonArray();
        for (JsonElement profile : profiles) {
            JsonObject profileObject = profile.getAsJsonObject();
            if (profileObject.get("selected").getAsBoolean()) {
                meloMod.setPlayerProfile(new SkyblockProfile(profileObject));

                HashMap<String, Integer> kills = meloMod.getPlayerProfile().getMembers().get(MeloMod.playerUUID.toString().replaceAll("-", "")).getBestiary().getKills();
                for (Map.Entry<String, Integer> entry : kills.entrySet()) {
                    String string = entry.getKey();
                    ChatEvent.seaCreatureSession.put(ChatEvent.SeaCreature.convertBestiaryMob(string), entry.getValue());
                }
                return;
            }
        }
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

        public static Location parseFromLocraw(String locrawGamemode) {
            if (locrawGamemode == null) return null;
            for (Location value : Location.values()) {
                if (locrawGamemode.equalsIgnoreCase(value.getInternal())) {
                    return value;
                }
            }
            return null;
        }

        public String getInternal() {
            return internal;
        }
    }

    public enum Gamemode {
        NORMAL,
        IRONMAN,
        STRANDED,
        BINGO;

        public static Gamemode parseFromJSON(String gamemode) {
            if (gamemode == null) return null;
            if (gamemode.equalsIgnoreCase("ironman")) return IRONMAN;
            if (gamemode.equalsIgnoreCase("island")) return STRANDED;
            if (gamemode.equalsIgnoreCase("bingo")) return BINGO;
            return NORMAL;
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

        public static ItemRarity parseRarity(String rarity) {
            if (rarity == null) {
                return null;
            }
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

        public String getColor() {
            return "§" + color;
        }
    }

    public static class Guild {
        //private final String guildID;
        private final String name;
        private final String nameLower;
        private final Integer coins;
        private final Integer coinsEver;
        private final Long created;
        private final List<GuildMember> members;
        private final List<GuildRank> ranks;
        private final GuildAchievements guildAchievements;
        private final List<String> preferredGames;
        private final Boolean publiclyListed;
        private final Long exp;
        private final Integer chatMute;
        private final String tag;
        private final String tagColor;
        private final String description;
        private final HashMap<String, Integer> guildExpByGameType;

        public Guild(JsonObject object) {
            //this.guildID = getAsString("_id", object);
            this.name = getAsString("name", object);
            this.nameLower = getAsString("name_lower", object);
            this.coins = getAsInteger("coins", object);
            this.coinsEver = getAsInteger("coinsEver", object);
            this.created = getAsLong("created", object);
            this.members = new ArrayList<>();
            JsonArray membersArray = getAsJsonArray("members", object);
            for (JsonElement member : membersArray) {
                members.add(new GuildMember(member.getAsJsonObject()));
            }
            this.ranks = new ArrayList<>();
            JsonArray ranksArray = getAsJsonArray("ranks", object);
            for (JsonElement rank : ranksArray) {
                ranks.add(new GuildRank(rank.getAsJsonObject()));
            }
            this.guildAchievements = new GuildAchievements(getAsJsonObject("achievements", object));
            this.preferredGames = new ArrayList<>();
            JsonArray preferredGamesArray = getAsJsonArray("preferredGames", object);
            for (JsonElement preferredGame : preferredGamesArray) {
                preferredGames.add(preferredGame.getAsString());
            }
            this.publiclyListed = getAsBoolean("publiclyListed", object);
            this.exp = getAsLong("exp", object);
            this.chatMute = getAsInteger("chatMute", object);
            this.tag = getAsString("tag", object);
            this.tagColor = getAsString("tagColor", object);
            this.description = getAsString("description", object);
            this.guildExpByGameType = new HashMap<>();
            JsonObject guildExpObject = getAsJsonObject("guildExpByGameType", object);
            for (Map.Entry<String, JsonElement> entry : guildExpObject.entrySet()) {
                String string = entry.getKey();
                guildExpByGameType.put(string, entry.getValue().getAsInt());
            }

        }

        public String getTag() {
            return tag;
        }

        public String getName() {
            return name;
        }

        public Long getCreated() {
            return created;
        }

        public String getDescription() {
            return description;
        }

        public Boolean getPubliclyListed() {
            return publiclyListed;
        }

        public GuildAchievements getGuildAchievements() {
            return guildAchievements;
        }

        public HashMap<String, Integer> getGuildExpByGameType() {
            return guildExpByGameType;
        }

        public Integer getChatMute() {
            return chatMute;
        }

        public Integer getCoins() {
            return coins;
        }

        public Integer getCoinsEver() {
            return coinsEver;
        }

        public List<GuildMember> getMembers() {
            return members;
        }

        public List<GuildRank> getRanks() {
            return ranks;
        }

        public List<String> getPreferredGames() {
            return preferredGames;
        }

        public Long getExp() {
            return exp;
        }

        public String getGuildID() {
            return "???";// TODO
        }

        public String getNameLower() {
            return nameLower;
        }

        public String getTagColor() {
            return tagColor;
        }

        public static class GuildAchievements {
            private final Integer onlinePlayers;
            private final Integer experienceKings;
            private final Integer winners;

            public GuildAchievements(JsonObject object) {
                this.onlinePlayers = getAsInteger("ONLINE_PLAYERS", object);
                this.experienceKings = getAsInteger("EXPERIENCE_KINGS", object);
                this.winners = getAsInteger("WINNERS", object);
            }

            public Integer getExperienceKings() {
                return experienceKings;
            }

            public Integer getOnlinePlayers() {
                return onlinePlayers;
            }

            public Integer getWinners() {
                return winners;
            }
        }

        public static class GuildRank {
            private final String name;
            private final Boolean isDefault;
            private final String tag;
            private final Long created;
            private final Integer priority;

            public GuildRank(JsonObject object) {
                this.name = getAsString("name", object);
                this.isDefault = getAsBoolean("default", object);
                this.tag = getAsString("tag", object);
                this.created = getAsLong("created", object);
                this.priority = getAsInteger("priority", object);
            }

            public String getName() {
                return name;
            }

            public Boolean getDefault() {
                return isDefault;
            }

            public String getTag() {
                return tag;
            }

            public Long getCreated() {
                return created;
            }

            public Integer getPriority() {
                return priority;
            }
        }

        public static class GuildMember {
            private final UUID uuid;
            private final String rank;
            private final Long joined;
            private final Integer questParticipation;
            private final HashMap<String, Integer> expHistory;

            public GuildMember(JsonObject object) {
                this.uuid = fromString(getAsString("uuid", object));
                this.rank = getAsString("rank", object);
                this.joined = getAsLong("joined", object);
                this.questParticipation = getAsInteger("questParticipation", object);
                this.expHistory = new HashMap<>();
                JsonObject expHistoryObject = getAsJsonObject("expHistory", object);
                for (String s : expHistory.keySet()) {
                    expHistory.put(s, getAsInteger(s, expHistoryObject));
                }
            }

            public UUID getUUID() {
                return uuid;
            }

            public String getRank() {
                return rank;
            }

            public Long getJoined() {
                return joined;
            }

            public Integer getQuestParticipation() {
                return questParticipation;
            }

            public UUID getUuid() {
                return uuid;
            }

            public HashMap<String, Integer> getExpHistory() {
                return expHistory;
            }
        }
    }

    public static class CommunityUpgrade {
        private final String upgradeName;
        private final Integer tier;
        private final Long startedMS;
        private final Long claimedMS;
        private final UUID startedBy;
        private final UUID claimedBy;
        private final Boolean fastTracked;

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
            this.upgradeName = getAsString("upgrade", object);
            this.tier = getAsInteger("tier", object);
            this.startedMS = getAsLong("started_ms", object);
            this.claimedMS = getAsLong("claimed_ms", object);
            this.claimedBy = fromString(getAsString("claimed_by", object));
            this.fastTracked = getAsBoolean("fast_tracked", object);
            this.startedBy = fromString(getAsString("started_by", object));
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
        private final Double balance;
        private final List<Transaction> transactions;

        @Deprecated
        public Banking(double balance, List<Transaction> transactions) {
            this.balance = balance;
            this.transactions = transactions;
        }

        public Banking(JsonObject object) {
            this.balance = getAsDouble("balance", object);
            JsonArray transactionArray = getAsJsonArray("transactions", object);
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
            private final Long timestamp;
            private final Action action;
            private final String initiatorName;
            private final Double amount;

            @Deprecated
            public Transaction(Long timestamp, Action action, String initiatorName, Double amount) {
                this.timestamp = timestamp;
                this.action = action;
                this.initiatorName = initiatorName;
                this.amount = amount;
            }

            public Transaction(JsonObject object) {
                this.amount = getAsDouble("amount", object);
                this.timestamp = getAsLong("timestamp", object);
                this.action = Action.parseFromJSON(getAsString("action", object));
                this.initiatorName = getAsString("initiator_name", object);
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

            public enum Action {
                DEPOSIT,
                WITHDRAW;

                public static Action parseFromJSON(String action) {
                    if (action == null) return null;
                    if (action.equalsIgnoreCase("DEPOSIT")) return DEPOSIT;
                    if (action.equalsIgnoreCase("WITHDRAW")) return WITHDRAW;
                    return null;
                }
            }
        }
    }

    public static class SkyblockProfile {
        private final UUID profileID;
        private final Long createdAt;
        private final Boolean selected;
        private final HashMap<String, ProfileMember> members;
        private final String cuteName;
        private final List<CommunityUpgrade> communityUpgrades;
        private final Gamemode gamemode;

        @Deprecated
        public SkyblockProfile(UUID profileID, Long createdAt, boolean selected, HashMap<String, ProfileMember> members, Gamemode gamemode) {
            this.profileID = profileID;
            this.selected = selected;
            this.createdAt = createdAt;
            this.members = members;
            this.gamemode = gamemode;
            this.cuteName = null;
            this.communityUpgrades = null;
        }
        
        public SkyblockProfile(JsonObject profileObject) {
            this.profileID = fromString(getAsString("profile_id", profileObject));
            JsonObject communityUpgrades = getAsJsonObject("community_upgrades", profileObject);
            JsonArray upgradeStates = getAsJsonArray("upgrade_states", communityUpgrades);
            JsonArray currentlyUpgrading = getAsJsonArray("currently_upgrading", communityUpgrades);
            List<CommunityUpgrade> upgrades = new ArrayList<>();
            for (JsonElement upgradeState : upgradeStates) {
                upgrades.add(new CommunityUpgrade(upgradeState.getAsJsonObject()));
            }
            for (JsonElement jsonElement : currentlyUpgrading) {
                upgrades.add(new CommunityUpgrade(jsonElement.getAsJsonObject()));
            }
            this.communityUpgrades = upgrades;
            this.createdAt = getAsLong("created_at", profileObject);
            this.members = new HashMap<>();
            JsonObject membersObject = getAsJsonObject("members", profileObject);
            for (Map.Entry<String, JsonElement> entry : membersObject.entrySet()) {
                String string = entry.getKey();
                members.put(string, new ProfileMember(fromString(string), getAsJsonObject(string, membersObject)));
            }
            this.selected = getAsBoolean("selected", profileObject);
            this.cuteName = getAsString("cute_name", profileObject);
            this.gamemode = Gamemode.parseFromJSON(getAsString("game_mode", profileObject));
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

        public HashMap<String, ProfileMember> getMembers() {
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

    public static class ProfileMember {
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
        private final Bestiary bestiary;

        public ProfileMember(UUID playerID, JsonObject object) {
            this.playerID = playerID;
            this.riftData = new RiftData(getAsJsonObject("rift", object));
            this.playerData = new PlayerData(getAsJsonObject("player_data", object));
            this.glacitePlayerData = new GlacitePlayerData(getAsJsonObject("glacite_player_data", object));
            this.events = new Events(getAsJsonObject("events", object));
            this.gardenPlayerData = new GardenPlayerData(getAsJsonObject("garden_player_data", object));
            this.petsData = new PetsData(getAsJsonObject("pets_data", object));
            this.accessoryBagStorage = new AccessoryBagStorage(getAsJsonObject("accessory_bag", object));
            this.levelling = new Levelling(getAsJsonObject("levelling", object));
            this.itemData = new ItemData(getAsJsonObject("item_data", object));
            this.jacobsContest = new JacobsContest(getAsJsonObject("jacobs_contest", object));
            this.currencies = new Currencies(getAsJsonObject("currencies", object));
            this.dungeons = new Dungeons(getAsJsonObject("dungeons", object));
            this.bestiary = new Bestiary(getAsJsonObject("bestiary", object));
        }

        public Bestiary getBestiary() {
            return bestiary;
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

        public UUID getPlayerID() {
            return playerID;
        }

        public static class DeletionNotice {
            private final Integer timestamp;

            public DeletionNotice(int timestamp) {
                this.timestamp = timestamp;
            }

            public int getTimestamp() {
                return timestamp;
            }
        }
    }

    public static class Bestiary {
        private final Boolean migratedStats;
        private final Boolean migration;
        private final HashMap<String, Integer> kills;
        // todo complete

        public Bestiary(JsonObject object) {
            this.migratedStats = getAsBoolean("migrated_stats", object);
            this.migration = getAsBoolean("migration", object);
            this.kills = new HashMap<>();
            JsonObject killsObject = getAsJsonObject("kills", object);
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : killsObject.entrySet()) {
                String string = stringJsonElementEntry.getKey();
                if (Objects.equals(string, "last_killed_mob")) {
                    continue;
                }
                kills.put(string, killsObject.get(string).getAsInt());
            }
        }

        public HashMap<String, Integer> getKills() {
            return kills;
        }

        public boolean isMigration() {
            return migration;
        }

        public boolean isMigratedStats() {
            return migratedStats;
        }
    }

    public static class RiftData {
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
            JsonArray lifetimePurchasedBoundariesArray = getAsJsonArray("lifetime_purchased_boundaries", object);
            for (JsonElement jsonElement : lifetimePurchasedBoundariesArray) {
                lifetimePurchasedBoundaries.add(jsonElement.getAsString());
            }
            this.villagePlaza = new VillagePlaza(getAsJsonObject("village_plaza", object));
            this.witherCage = new WitherCage(getAsJsonObject("wither_cage", object));
            this.blackLagoon = new BlackLagoon(getAsJsonObject("black_lagoon", object));
            this.deadCats = new DeadCats(getAsJsonObject("dead_cats", object));
            this.wizardTower = new WizardTower(getAsJsonObject("wizard_tower", object));
            this.enigma = new Enigma(getAsJsonObject("enigma", object));
            this.gallery = new Gallery(getAsJsonObject("gallery", object));
            this.westVillage = new WestVillage(getAsJsonObject("west_village", object));
            this.wyldWoods = new WyldWoods(getAsJsonObject("wyld_woods", object));
            this.castle = new Castle(getAsJsonObject("castle", object));
            this.access = new Access(getAsJsonObject("access", object));
            this.dreadfarm = new Dreadfarm(getAsJsonObject("dreadfarm", object));
            this.inventory = new Inventory(getAsJsonObject("inventory", object));
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

        public static class VillagePlaza {
            private final Boolean gotScammed;
            private final Murder murder;
            private final BarryCenter barryCenter;
            private final Cowboy cowboy;
            private final Lonely lonely;
            private final Seraphine seraphine;

            public VillagePlaza(JsonObject object) {
                this.gotScammed = getAsBoolean("got_scammed", object);
                this.murder = new Murder(getAsJsonObject("murder", object));
                this.barryCenter = new BarryCenter(getAsJsonObject("barry_center", object));
                this.cowboy = new Cowboy(getAsJsonObject("cowboy", object));
                this.lonely = new Lonely(getAsJsonObject("lonely", object));
                this.seraphine = new Seraphine(getAsJsonObject("seraphine", object));
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

            public static class Murder {
                private final Integer stepIndex;
                private final List<String> roomClues;

                public Murder(JsonObject object) {
                    this.stepIndex = getAsInteger("step_index", object);
                    this.roomClues = new ArrayList<>();
                    JsonArray roomCluesArray = getAsJsonArray("room_clues", object);
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

            public static class BarryCenter {
                private final Boolean firstTalkToBarry;
                private final List<String> convinced;
                private final Boolean receivedReward;

                public BarryCenter(JsonObject object) {
                    this.firstTalkToBarry = getAsBoolean("first_talk_to_barry", object);
                    this.convinced = new ArrayList<>();
                    JsonArray convincedArray = getAsJsonArray("convinced", object);
                    for (JsonElement jsonElement : convincedArray) {
                        convinced.add(jsonElement.getAsString());
                    }
                    this.receivedReward = getAsBoolean("received_reward", object);
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

            public static class Cowboy {
                private final Integer stage;
                private final Integer hayEaten;
                private final String rabbitName;
                private final Integer exportedCarrots;

                public Cowboy(JsonObject object) {
                    this.stage = getAsInteger("stage", object);
                    this.hayEaten = getAsInteger("hay_eaten", object);
                    this.rabbitName = getAsString("rabbit_name", object);
                    this.exportedCarrots = getAsInteger("exported_carrots", object);
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

            public static class Lonely {
                private final Integer secondsSitting;

                public Lonely(JsonObject object) {
                    this.secondsSitting = getAsInteger("seconds_sitting", object);
                }

                public int getSecondsSitting() {
                    return secondsSitting;
                }
            }

            public static class Seraphine {
                private final Integer stepIndex;

                public Seraphine(JsonObject object) {
                    this.stepIndex = getAsInteger("step_index", object);
                }

                public int getStepIndex() {
                    return stepIndex;
                }
            }
        }

        public static class WitherCage {
            private final List<String> eyesKilled;

            public WitherCage(JsonObject object) {
                this.eyesKilled = new ArrayList<>();
                JsonArray killedEyes = getAsJsonArray("killed_eyes", object);
                for (JsonElement killedEye : killedEyes) {
                    eyesKilled.add(killedEye.getAsString());
                }
            }

            public List<String> getEyesKilled() {
                return eyesKilled;
            }
        }

        public static class BlackLagoon {
            private final Boolean talkedToEdwin;
            private final Boolean receivedSciencePaper;
            private final Integer completedStep;
            private final Boolean deliveredSciencePaper;

            public BlackLagoon(JsonObject object) {
                this.talkedToEdwin = getAsBoolean("talked_to_edwin", object);
                this.receivedSciencePaper = getAsBoolean("received_science_paper", object);
                this.completedStep = getAsInteger("completed_step", object);
                this.deliveredSciencePaper = getAsBoolean("delivered_science_paper", object);
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

        public static class DeadCats {
            private final Boolean talkedToJacquelle;
            private final Boolean pickedUpDetector;
            private final List<String> foundCats;
            private final Boolean unlockedPet;
            private final Montezuma montezuma;

            public DeadCats(JsonObject object) {
                this.talkedToJacquelle = getAsBoolean("talked_to_jacquelle", object);
                this.pickedUpDetector = getAsBoolean("picked_up_detector", object);
                this.foundCats = new ArrayList<>();
                JsonArray foundCatsArray = getAsJsonArray("found_cats", object);
                for (JsonElement foundCat : foundCatsArray) {
                    foundCats.add(foundCat.getAsString());
                }
                this.unlockedPet = getAsBoolean("unlocked_pet", object);
                this.montezuma = new Montezuma(getAsJsonObject("montezuma", object));
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

            public static class Montezuma {
                //private final UUID uuid; // ??
                private final UUID uniqueID;
                private final String type;
                private final Double exp;
                private final Boolean active;
                private final ItemRarity rarity;
                //private final ItemStack heldItem;
                private final Integer candyUsed;
                //private final Skin skin;

                public Montezuma(JsonObject object) {
                    //this.uuid = fromString(getAsString("uuid", object));
                    this.type = getAsString("type", object);
                    this.exp = getAsDouble("exp", object);
                    this.active = getAsBoolean("active", object);
                    this.candyUsed = getAsInteger("candy_used", object);
                    this.uniqueID = fromString(getAsString("uniqueId", object));
                    this.rarity = ItemRarity.parseRarity(getAsString("tier", object));
                }

                public Double getExp() {
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


                public ItemRarity getRarity() {
                    return rarity;
                }

                public boolean isActive() {
                    return active;
                }
            }
        }

        public static class WizardTower {
            private final Integer wizardQuestStep;
            private final Integer crumbsLaidOut;

            public WizardTower(JsonObject object) {
                this.wizardQuestStep = getAsInteger("wizard_quest_step", object);
                this.crumbsLaidOut = getAsInteger("crumbs_laid_out", object);
            }

            public int getCrumbsLaidOut() {
                return crumbsLaidOut;
            }

            public int getWizardQuestStep() {
                return wizardQuestStep;
            }
        }

        public static class Enigma {
            private final Boolean boughtCloak;
            private final List<String> foundSouls;
            private final Integer claimedBonusIndex;

            public Enigma(JsonObject object) {
                this.boughtCloak = getAsBoolean("bought_cloak", object);
                this.claimedBonusIndex = getAsInteger("claimed_bonus_index", object);
                this.foundSouls = new ArrayList<>();
                JsonArray foundSoulsArray = getAsJsonArray("found_souls", object);
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

        public static class Gallery {
            private final Integer eliseStep;
            private final List<SecuredTrophy> securedTrophies;
            private final List<String> sentTrophyDialogues;

            public Gallery(JsonObject object) {
                this.eliseStep = getAsInteger("elise_step", object);
                this.securedTrophies = new ArrayList<>();
                JsonArray securedTrophiesArray = getAsJsonArray("secured_trophies", object);
                for (JsonElement securedTrophy : securedTrophiesArray) {
                    securedTrophies.add(new SecuredTrophy(securedTrophy.getAsJsonObject()));
                }
                this.sentTrophyDialogues = new ArrayList<>();
                JsonArray sentTrophyDialoguesArray = getAsJsonArray("sent_trophy_dialogues", object);
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

            public static class SecuredTrophy {
                private final String type;
                private final Long timestamp;
                private final Integer visits;

                public SecuredTrophy(JsonObject object) {
                    this.type = getAsString("type", object);
                    this.timestamp = getAsLong("timestamp", object);
                    this.visits = getAsInteger("visits", object);
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

        public static class WestVillage {
            private final MirrorVerse mirrorVerse;
            private final CrazyKloon crazyKloon;
            private final KatHouse katHouse;
            private final Glyphs glyphs;

            public WestVillage(JsonObject object) {
                this.mirrorVerse = new MirrorVerse(getAsJsonObject("mirrorverse", object));
                this.crazyKloon = new CrazyKloon(getAsJsonObject("crazy_kloon", object));
                this.katHouse = new KatHouse(getAsJsonObject("kat_house", object));
                this.glyphs = new Glyphs(getAsJsonObject("glyphs", object));
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

            public static class MirrorVerse {
                private final List<String> roomsVisited;
                private final Boolean upsideDownHard;
                private final List<String> claimedChestItems;
                private final Boolean claimedReward;

                public MirrorVerse(JsonObject object) {
                    this.roomsVisited = new ArrayList<>();
                    this.upsideDownHard = getAsBoolean("upside_down_hard", object);
                    this.claimedChestItems = new ArrayList<>();
                    this.claimedReward = getAsBoolean("claimed_reward", object);
                    JsonArray visitedRoomsArray = getAsJsonArray("visited_rooms", object);
                    for (JsonElement visitedRoom : visitedRoomsArray) {
                        roomsVisited.add(visitedRoom.getAsString());
                    }
                    JsonArray claimedChestItemsArray = getAsJsonArray("claimed_chest_items", object);
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

            public static class CrazyKloon {
                private final HashMap<String, String> selectedColors;
                private final Boolean talked;
                private final List<String> hackedTerminals;
                private final Boolean questComplete;

                public CrazyKloon(JsonObject object) {
                    this.selectedColors = new HashMap<>();
                    JsonObject selectedColorsMap = getAsJsonObject("selected_colors", object);
                    for (Map.Entry<String, JsonElement> entry : selectedColorsMap.entrySet()) {
                        String string = entry.getKey();
                        selectedColors.put(string, getAsString(string, selectedColorsMap));
                    }
                    this.talked = getAsBoolean("talked", object);
                    this.hackedTerminals = new ArrayList<>();
                    JsonArray hackedTerminalsArray = getAsJsonArray("hacked_terminals", object);
                    for (JsonElement hackedTerminal : hackedTerminalsArray) {
                        hackedTerminals.add(hackedTerminal.getAsString());
                    }
                    this.questComplete = getAsBoolean("quest_complete", object);
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

            public static class KatHouse {
                private final Integer binCollectedMosquito;
                private final Integer binCollectedSilverfish;
                private final Integer binCollectedSpider;

                public KatHouse(JsonObject object) {
                    this.binCollectedMosquito = getAsInteger("bin_collected_mosquito", object);
                    this.binCollectedSilverfish = getAsInteger("bin_collected_silverfish", object);
                    this.binCollectedSpider = getAsInteger("bin_collected_spider", object);
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

            public static class Glyphs {
                private final Boolean claimedWand;
                private final Boolean currentGlyphDelivered;
                private final Boolean currentGlyphCompleted;
                private final Integer currentGlyph;
                private final Boolean completed;
                private final Boolean claimedBracelet;

                public Glyphs(JsonObject object) {
                    this.claimedWand = getAsBoolean("claimed_wand", object);
                    this.currentGlyphDelivered = getAsBoolean("current_glyph_delivered", object);
                    this.currentGlyphCompleted = getAsBoolean("current_glyph_completed", object);
                    this.currentGlyph = getAsInteger("current_glyph", object);
                    this.completed = getAsBoolean("completed", object);
                    this.claimedBracelet = getAsBoolean("claimed_bracelet", object);
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

        public static class WyldWoods {
            private final List<String> talkedThreeBrothers;
            private final Boolean siriusStartedQA;
            private final Boolean siriusQAChainDone;
            private final Boolean siriusCompletedQA;
            private final Boolean siriusClaimedDoubloon;
            private final Integer bughunter_step;

            public WyldWoods(JsonObject object) {
                this.talkedThreeBrothers = new ArrayList<>();
                JsonArray talkedThreeBrothersArray = getAsJsonArray("talked_threebrothers", object);
                for (JsonElement jsonElement : talkedThreeBrothersArray) {
                    talkedThreeBrothers.add(jsonElement.getAsString());
                }
                this.siriusStartedQA = getAsBoolean("sirius_started_q_a", object);
                this.siriusQAChainDone = getAsBoolean("sirius_q_a_chain_done", object);
                this.siriusCompletedQA = getAsBoolean("sirius_completed_q_a", object);
                this.siriusClaimedDoubloon = getAsBoolean("sirius_claimed_doubloon", object);
                this.bughunter_step = getAsInteger("bighunter_step", object);
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

        public static class Castle {
            private final Boolean unlockedPathwaySkip;
            private final Integer fairyStep;
            private final Integer grubberStacks;

            public Castle(JsonObject object) {
                this.unlockedPathwaySkip = getAsBoolean("unlocked_pathway_skip", object);
                this.fairyStep = getAsInteger("fairy_step", object);
                this.grubberStacks = getAsInteger("grubber_stacks", object);
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

        public static class Access {
            private final Long lastFree;
            private final Boolean consumedPrism;

            public Access(JsonObject object) {
                this.lastFree = getAsLong("last_free", object);
                this.consumedPrism = getAsBoolean("consumed_prism", object);
            }

            public long getLastFree() {
                return lastFree;
            }

            public boolean isConsumedPrism() {
                return consumedPrism;
            }

        }

        public static class Dreadfarm {
            private final Integer shaniaStage;
            private final List<Long> caducousFeederUses;

            public Dreadfarm(JsonObject object) {
                this.shaniaStage = getAsInteger("shania_stage", object);
                this.caducousFeederUses = new ArrayList<>();
                JsonArray caducousFeederUsesArray = getAsJsonArray("caducous_feeder_uses", object);
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
    }

    public static class Inventory {
        private final EnderChestContents enderChestContents;
        private final InventoryContents inventoryContents;
        private final InventoryArmor inventoryArmor;
        private final EquipmentContents equipmentContents;
        private final List<String> enderChestPageIcons;

        public Inventory(JsonObject object) {
            this.enderChestPageIcons = new ArrayList<>();
            JsonArray enderChestPageIconsArray = getAsJsonArray("ender_chest_page_icons", object);
            for (JsonElement jsonElement : enderChestPageIconsArray) {
                if (jsonElement.isJsonNull()) {
                    enderChestPageIcons.add(null);
                } else {
                    if (jsonElement.isJsonPrimitive()) {
                        if (jsonElement.getAsJsonPrimitive().isString()) {
                            enderChestPageIcons.add(jsonElement.getAsString());
                        }
                    }
                }
            }
            this.enderChestContents = new EnderChestContents(getAsJsonObject("ender_chest_contents", object));
            this.inventoryContents = new InventoryContents(getAsJsonObject("inv_contents", object));
            this.inventoryArmor = new InventoryArmor(getAsJsonObject("inv_armor", object));
            this.equipmentContents = new EquipmentContents(getAsJsonObject("equipment_contents", object));
        }

        public static class EnderChestContents {
            private final Integer type;
            private final String data;

            public EnderChestContents(JsonObject object) {
                this.type = getAsInteger("type", object);
                this.data = getAsString("data", object); // TODO
            }

            public int getType() {
                return type;
            }

            public String getData() {
                return data;
            }
        }

        public static class InventoryContents {
            private final Integer type;
            private final String data;

            public InventoryContents(JsonObject object) {
                this.type = getAsInteger("type", object);
                this.data = getAsString("data", object);// TODO
            }

            public String getData() {
                return data;
            }

            public int getType() {
                return type;
            }
        }

        public static class InventoryArmor {
            private final Integer type;
            private final String data;

            public InventoryArmor(JsonObject object) {
                this.type = getAsInteger("type", object);
                this.data = getAsString("data", object); // TODO
            }

            public int getType() {
                return type;
            }

            public String getData() {
                return data;
            }
        }

        public static class EquipmentContents {
            private final Integer type;
            private final String data;

            public EquipmentContents(JsonObject object) {
                this.type = getAsInteger("type", object);
                this.data = getAsString("data", object); // TODO
            }

            public int getType() {
                return type;
            }

            public String getData() {
                return data;
            }
        }
    }

    public static class PlayerData {
        private final List<String> visitedZones;
        private final Long lastDeath;
        private final HashMap<String, Integer> perks;
        //private final List<Effect> activeEffects; // TODO
        //private final List<Effect> pausedEffects; // TODO
        private final Integer reaperPeppersEaten;
        private final List<TempStatBuff> buffs;
        private final Integer deathCount;
        //private final List<Effect> disabledPotionEffects; // TODO
        private final List<String> achievementSpawnedIslandTypes;
        private final List<String> visitedModes;
        private final List<String> unlockedCollTiers;
        private final List<String> craftedGenerators;
        private final Integer fishingTreasureCaught;
        private final HashMap<String, Double> experience;


        public PlayerData(JsonObject object) {
            this.visitedZones = new ArrayList<>();
            JsonArray visitedZonesArray = getAsJsonArray("visited_zones", object);
            for (JsonElement jsonElement : visitedZonesArray) {
                visitedZones.add(jsonElement.getAsString());
            }
            this.lastDeath = getAsLong("last_death", object);
            this.perks = new HashMap<>();
            JsonObject perksObject = getAsJsonObject("perks", object);
            for (Map.Entry<String, JsonElement> entry : perksObject.entrySet()) {
                String string = entry.getKey();
                perks.put(string, getAsInteger(string, perksObject));
            }
            this.reaperPeppersEaten = getAsInteger("reaper_peppers_eaten", object);
            this.buffs = new ArrayList<>();
            JsonArray buffsArray = getAsJsonArray("temp_stat_buffs", object);
            for (JsonElement jsonElement : buffsArray) {
                buffs.add(new TempStatBuff(jsonElement.getAsJsonObject()));
            }
            this.deathCount = getAsInteger("death_count", object);
            this.achievementSpawnedIslandTypes = new ArrayList<>();
            JsonArray achievementSpawnedIslandTypesArray = getAsJsonArray("achievement_spawned_island_types", object);
            for (JsonElement jsonElement : achievementSpawnedIslandTypesArray) {
                achievementSpawnedIslandTypes.add(jsonElement.getAsString());
            }
            this.visitedModes = new ArrayList<>();
            JsonArray visitedModesArray = getAsJsonArray("visited_modes", object);
            for (JsonElement jsonElement : visitedModesArray) {
                visitedModes.add(jsonElement.getAsString());
            }
            this.unlockedCollTiers = new ArrayList<>();
            JsonArray unlockedCollTiersArray = getAsJsonArray("unlocked_coll_tiers", object);
            for (JsonElement jsonElement : unlockedCollTiersArray) {
                unlockedCollTiers.add(jsonElement.getAsString());
            }
            this.craftedGenerators = new ArrayList<>();
            JsonArray craftedGeneratorsArray = getAsJsonArray("crafted_generators", object);
            for (JsonElement jsonElement : craftedGeneratorsArray) {
                craftedGenerators.add(jsonElement.getAsString());
            }
            this.fishingTreasureCaught = getAsInteger("fishing_treasure_caught", object);
            this.experience = new HashMap<>();
            JsonObject experienceObject = getAsJsonObject("experience", object);
            for (Map.Entry<String, JsonElement> entry : experienceObject.entrySet()) {
                String string = entry.getKey();
                experience.put(string, getAsDouble(string, experienceObject));
            }

        }


    }

    public static class Dungeons {
        private final Catacombs catacombs;
        private final Catacombs masterCatacombs;
        private final HashMap<String, Double> playerClasses;
        private final List<String> unlockedJournals;
        private final List<String> dungeonsBlahBlah;
        private final Integer secrets;

        private final DailyRuns dailyRuns;

        public Dungeons(JsonObject object) {
            JsonObject typeObject = getAsJsonObject("dungeon_types", object);
            this.catacombs = new Catacombs(getAsJsonObject("catacombs", typeObject), false);
            this.masterCatacombs = new Catacombs(getAsJsonObject("master_catacombs", typeObject), true);
            this.playerClasses = new HashMap<>();
            JsonObject playerClassesObject = getAsJsonObject("player_classes", object);
            for (Map.Entry<String, JsonElement> entry : playerClassesObject.entrySet()) {
                String string = entry.getKey();
                playerClasses.put(string, getAsDouble(string, playerClassesObject));
            }
            this.unlockedJournals = new ArrayList<>();
            this.dungeonsBlahBlah = new ArrayList<>();
            this.dailyRuns = new DailyRuns(getAsJsonObject("daily_runs", object));
            JsonArray unlockedJournalsArray = getAsJsonArray("unlocked_journals", getAsJsonObject("dungeon_journal", object));
            for (JsonElement jsonElement : unlockedJournalsArray) {
                unlockedJournals.add(jsonElement.getAsString());
            }
            JsonArray dungeonsBlahBlahArray = getAsJsonArray("dungeons_blah_blah", object);
            for (JsonElement jsonElement : dungeonsBlahBlahArray) {
                dungeonsBlahBlah.add(jsonElement.getAsString());
            }
            // todo
            this.secrets = getAsInteger("secrets", object);
        }

        public Integer getSecrets() {
            return secrets;
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

        public HashMap<String, Double> getPlayerClasses() {
            return playerClasses;
        }

        public List<String> getDungeonsBlahBlah() {
            return dungeonsBlahBlah;
        }

        public List<String> getUnlockedJournals() {
            return unlockedJournals;
        }
        // TODO line 8991

        public static class DailyRuns {
            private final Integer currentDayStamp;
            private final Integer completedRunsCount;

            public DailyRuns(JsonObject object) {
                this.currentDayStamp = getAsInteger("current_day_stamp", object);
                this.completedRunsCount = getAsInteger("completed_runs_count", object);
            }

            public int getCompletedRunsCount() {
                return completedRunsCount;
            }

            public int getCurrentDayStamp() {
                return currentDayStamp;
            }

        }

        public static class Catacombs {
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
            private final Double experience;
            private final HashMap<String, List<BestRun>> bestRuns;
            private final Boolean master;

            public Catacombs(JsonObject object, boolean master) {
                this.mobsKilled = new HashMap<>();
                JsonObject mobsKilledObject = getAsJsonObject("mobs_killed", object);
                for (Map.Entry<String, JsonElement> entry : mobsKilledObject.entrySet()) {
                    String string = entry.getKey();
                    mobsKilled.put(string, getAsFloat(string, mobsKilledObject));
                }
                this.fastestTimeS = new HashMap<>();
                JsonObject fastestTimeSObject = getAsJsonObject("fastest_time_s", object);
                for (Map.Entry<String, JsonElement> entry : fastestTimeSObject.entrySet()) {
                    String string = entry.getKey();
                    fastestTimeS.put(string, getAsFloat(string, fastestTimeSObject));
                }
                this.mostDamageTank = new HashMap<>();
                JsonObject mostDamageTankObject = getAsJsonObject("most_damage_tank", object);
                for (Map.Entry<String, JsonElement> entry : mostDamageTankObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageTank.put(string, getAsFloat(string, mostDamageTankObject));
                }
                this.fastestTime = new HashMap<>();
                JsonObject fastestTimeObject = getAsJsonObject("fastest_time", object);
                for (Map.Entry<String, JsonElement> entry : fastestTimeObject.entrySet()) {
                    String string = entry.getKey();
                    fastestTime.put(string, getAsFloat(string, fastestTimeObject));
                }
                this.mostDamageMage = new HashMap<>();
                JsonObject mostDamageMageObject = getAsJsonObject("most_damage_mage", object);
                for (Map.Entry<String, JsonElement> entry : mostDamageMageObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageMage.put(string, getAsFloat(string, mostDamageMageObject));
                }
                this.tierCompletions = new HashMap<>();
                JsonObject tierCompletionsObject = getAsJsonObject("tier_completions", object);
                for (Map.Entry<String, JsonElement> entry : tierCompletionsObject.entrySet()) {
                    String string = entry.getKey();
                    tierCompletions.put(string, getAsFloat(string, tierCompletionsObject));
                }
                this.mostDamageHealer = new HashMap<>();
                JsonObject mostDamageHealerObject = getAsJsonObject("most_damage_healer", object);
                for (Map.Entry<String, JsonElement> entry : mostDamageHealerObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageHealer.put(string, getAsFloat(string, mostDamageHealerObject));
                }
                this.mostDamageArcher = new HashMap<>();
                JsonObject mostDamageArcherObject = getAsJsonObject("most_damage_archer", object);
                for (Map.Entry<String, JsonElement> entry : mostDamageArcherObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageArcher.put(string, getAsFloat(string, mostDamageArcherObject));
                }
                this.watcherKills = new HashMap<>();
                JsonObject watcherKillsObject = getAsJsonObject("watcher_kills", object);
                for (Map.Entry<String, JsonElement> entry : watcherKillsObject.entrySet()) {
                    String string = entry.getKey();
                    watcherKills.put(string, getAsFloat(string, watcherKillsObject));
                }
                this.mostHealing = new HashMap<>();
                JsonObject mostHealingObject = getAsJsonObject("most_healing", object);
                for (Map.Entry<String, JsonElement> entry : mostHealingObject.entrySet()) {
                    String string = entry.getKey();
                    mostHealing.put(string, getAsFloat(string, mostHealingObject));
                }
                this.bestScore = new HashMap<>();
                JsonObject bestScoreObject = getAsJsonObject("best_score", object);
                for (Map.Entry<String, JsonElement> entry : bestScoreObject.entrySet()) {
                    String string = entry.getKey();
                    bestScore.put(string, getAsFloat(string, bestScoreObject));
                }
                this.mostDamageBerserk = new HashMap<>();
                JsonObject mostDamageBerserkObject = getAsJsonObject("most_damage_berserk", object);
                for (Map.Entry<String, JsonElement> entry : mostDamageBerserkObject.entrySet()) {
                    String string = entry.getKey();
                    mostDamageBerserk.put(string, getAsFloat(string, mostDamageBerserkObject));
                }
                this.fastestTimeSPlus = new HashMap<>();
                JsonObject fastestTimeSPlusObject = getAsJsonObject("fastest_time_s_plus", object);
                for (Map.Entry<String, JsonElement> entry : fastestTimeSPlusObject.entrySet()) {
                    String string = entry.getKey();
                    fastestTimeSPlus.put(string, getAsFloat(string, fastestTimeSPlusObject));
                }
                this.mostMobsKilled = new HashMap<>();
                JsonObject mostMobsKilledObject = getAsJsonObject("most_mobs_killed", object);
                for (Map.Entry<String, JsonElement> entry : mostMobsKilledObject.entrySet()) {
                    String string = entry.getKey();
                    mostMobsKilled.put(string, getAsFloat(string, mostMobsKilledObject));
                }
                this.timesPlayed = new HashMap<>();
                JsonObject timesPlayedObject = getAsJsonObject("times_played", object);
                for (Map.Entry<String, JsonElement> entry : timesPlayedObject.entrySet()) {
                    String string = entry.getKey();
                    timesPlayed.put(string, getAsFloat(string, timesPlayedObject));
                }
                this.milestoneCompletions = new HashMap<>();
                JsonObject milestoneCompletionsObject = getAsJsonObject("milestone_completions", object);
                for (Map.Entry<String, JsonElement> entry : milestoneCompletionsObject.entrySet()) {
                    String string = entry.getKey();
                    milestoneCompletions.put(string, getAsFloat(string, milestoneCompletionsObject));
                }
                this.experience = getAsDouble("experience", object);
                this.bestRuns = new HashMap<>();
                JsonObject bestRunsObject = getAsJsonObject("best_runs", object);
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

            public Double getExperience() {
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

            public static class BestRun {
                private final Long timeStamp;
                private final Integer scoreExploration;
                private final Integer scoreSpeed;
                private final Integer scoreSkill;
                private final Integer scoreBonus;
                private final String dungeonClass;
                private final List<String> teammates;
                private final Integer elapsedTime;
                private final Double damageDealt;
                private final Integer deaths;
                private final Integer mobsKilled;
                private final Integer secretsFound;
                private final Integer damageMitigated;
                private final Integer allyHealing;

                public BestRun(JsonObject object) {
                    this.timeStamp = getAsLong("time_stamp", object);
                    this.scoreExploration = getAsInteger("score_exploration", object);
                    this.scoreSpeed = getAsInteger("score_speed", object);
                    this.scoreSkill = getAsInteger("score_skill", object);
                    this.scoreBonus = getAsInteger("score_bonus", object);
                    this.dungeonClass = getAsString("dungeon_class", object);
                    this.teammates = new ArrayList<>();
                    JsonArray teammatesArray = getAsJsonArray("teammates", object);
                    for (JsonElement jsonElement : teammatesArray) {
                        teammates.add(jsonElement.getAsString());
                    }
                    this.elapsedTime = getAsInteger("elapsed_time", object);
                    this.damageDealt = getAsDouble("damage_dealt", object);
                    this.deaths = getAsInteger("deaths", object);
                    this.mobsKilled = getAsInteger("mobs_killed", object);
                    this.secretsFound = getAsInteger("secrets_found", object);
                    this.damageMitigated = getAsInteger("damage_mitigated", object);
                    this.allyHealing = getAsInteger("ally_healing", object);
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

    public static class Currencies {
        private final Double coinPurse;
        private final Double motesPurse;
        private final HashMap<String, Integer> essence;

        public Currencies(JsonObject object) {
            this.coinPurse = getAsDouble("coin_purse", object);
            this.motesPurse = getAsDouble("motes_purse", object);
            this.essence = new HashMap<>();
            JsonObject essenceObject = getAsJsonObject("essence", object);
            for (Map.Entry<String, JsonElement> entry : essenceObject.entrySet()) {
                String string = entry.getKey();
                essence.put(string, getAsInteger("current", getAsJsonObject(string, essenceObject)));
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

    public static class JacobsContest {
        private final HashMap<String, Integer> medalsInv;
        private final HashMap<String, Integer> perks;
        private final List<JacobsContestData> jacobsContestDataList;
        private final Boolean talked;
        private final HashMap<String, List<String>> uniqueBrackets;
        private final Boolean migration;
        private final HashMap<String, Integer> personalBests;

        public JacobsContest(JsonObject object) {
            this.medalsInv = new HashMap<>();
            JsonObject medalsInvObject = getAsJsonObject("medals_inv", object);
            for (Map.Entry<String, JsonElement> entry : medalsInvObject.entrySet()) {
                String string = entry.getKey();
                medalsInv.put(string, getAsInteger(string, medalsInvObject));
            }
            this.perks = new HashMap<>();
            JsonObject perksObject = getAsJsonObject("perks", object);
            for (Map.Entry<String, JsonElement> entry : perksObject.entrySet()) {
                String string = entry.getKey();
                perks.put(string, getAsInteger(string, perksObject));
            }
            this.jacobsContestDataList = new ArrayList<>();
            JsonArray jacobsContestDataListArray = getAsJsonArray("contests", object);
            for (JsonElement jsonElement : jacobsContestDataListArray) {
                JsonObject contestObject = jsonElement.getAsJsonObject();
            }
            this.talked = getAsBoolean("talked", object);
            this.uniqueBrackets = new HashMap<>();
            JsonObject uniqueBracketsObject = getAsJsonObject("unique_brackets", object);
            for (Map.Entry<String, JsonElement> entry : uniqueBracketsObject.entrySet()) {
                String string = entry.getKey();
                uniqueBrackets.put(string, new ArrayList<>());
                JsonArray stringArray = uniqueBracketsObject.getAsJsonArray(string);
                for (JsonElement jsonElement : stringArray) {
                    uniqueBrackets.get(string).add(jsonElement.getAsString());
                }
            }
            this.migration = getAsBoolean("migration", object);
            this.personalBests = new HashMap<>();
            JsonObject personalBestsObject = getAsJsonObject("personal_bests", object);
            for (Map.Entry<String, JsonElement> entry : personalBestsObject.entrySet()) {
                String string = entry.getKey();
                personalBests.put(string, getAsInteger(string, personalBestsObject));
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

        public static class JacobsContestData {
            private final Integer collected;
            private final Boolean claimedRewards;
            private final Integer claimedPosition;
            private final String claimedMedal;
            private final Integer claimedParticipants;

            public JacobsContestData(JsonObject object) {
                this.collected = getAsInteger("collected", object);
                this.claimedParticipants = getAsInteger("claimed_participants", object);
                this.claimedMedal = getAsString("claimed_medal", object);
                this.claimedPosition = getAsInteger("claimed_position", object);
                this.claimedRewards = getAsBoolean("claimed_rewards", object);
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

    public static class ItemData {
        private final Integer soulflow;
        private final Integer favoriteArrow;

        public ItemData(JsonObject object) {
            this.soulflow = getAsInteger("soulflow", object);
            this.favoriteArrow = getAsInteger("favorite_arrow", object);
        }

        public int getFavoriteArrow() {
            return favoriteArrow;
        }

        public int getSoulflow() {
            return soulflow;
        }
    }

    public static class Levelling {
        private final Integer experience;
        private final HashMap<String, Integer> completions;
        private final List<String> completed;
        private final Boolean migratedCompletions;
        private final List<String> completedTasks;
        private final String highestPetScore;
        private final Integer miningFiestaOresMined;
        private final Integer fishingFestivalSharksKilled;
        private final Boolean migrated;
        private final Boolean migratedCompletions2;
        private final List<String> lastViewedTasks;
        private final Boolean claimedTalisman;
        private final String bopBonus;
        private final Boolean categoryExpanded;
        private final List<String> emblemUnlocks;
        private final String taskSort;

        public Levelling(JsonObject object) {
            this.experience = getAsInteger("experience", object);
            this.completions = new HashMap<>();
            JsonObject completionsObject = getAsJsonObject("completions", object);
            for (Map.Entry<String, JsonElement> entry : completionsObject.entrySet()) {
                String string = entry.getKey();
                completions.put(string, getAsInteger(string, completionsObject));
            }
            this.completed = new ArrayList<>();
            JsonArray completedArray = getAsJsonArray("completed", object);
            for (JsonElement jsonElement : completedArray) {
                completed.add(jsonElement.getAsString());
            }
            this.migratedCompletions = getAsBoolean("migrated_completions", object);
            this.completedTasks = new ArrayList<>();
            JsonArray completedTasksArray = getAsJsonArray("completed_tasks", object);
            for (JsonElement jsonElement : completedTasksArray) {
                completedTasks.add(jsonElement.getAsString());
            }
            this.highestPetScore = getAsString("highest_pet_score", object);
            this.miningFiestaOresMined = getAsInteger("mining_fiesta_ores_mined", object);
            this.fishingFestivalSharksKilled = getAsInteger("fishing_festival_sharks_killed", object);
            this.migrated = getAsBoolean("migrated", object);
            this.migratedCompletions2 = getAsBoolean("migrated_completions2", object);
            this.lastViewedTasks = new ArrayList<>();
            JsonArray lastViewedTasksArray = getAsJsonArray("last_viewed_tasks", object);
            for (JsonElement jsonElement : lastViewedTasksArray) {
                lastViewedTasks.add(jsonElement.getAsString());
            }
            this.claimedTalisman = getAsBoolean("claimed_talisman", object);
            this.bopBonus = getAsString("bop_bonus", object);
            this.categoryExpanded = getAsBoolean("category_expanded", object);
            this.emblemUnlocks = new ArrayList<>();
            JsonArray emblemUnlocksArray = getAsJsonArray("emblem_unlocks", object);
            for (JsonElement jsonElement : emblemUnlocksArray) {
                emblemUnlocks.add(jsonElement.getAsString());
            }
            this.taskSort = getAsString("task_sort", object);
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

    public static class AccessoryBagStorage {
        private final Tuning tuning;
        private final String selectedPower;
        private final List<String> unlockedPowers;
        private final Integer bagUpgradesPurchased;
        private final Integer highestMagicalPower;

        public AccessoryBagStorage(JsonObject object) {
            this.tuning = new Tuning(getAsJsonObject("tuning", object));
            this.selectedPower = getAsString("selected_power", object);
            this.bagUpgradesPurchased = getAsInteger("bag_upgrades_purchased", object);
            this.unlockedPowers = new ArrayList<>();
            JsonArray unlockedPowersArray = getAsJsonArray("unlocked_powers", object);
            for (JsonElement jsonElement : unlockedPowersArray) {
                unlockedPowers.add(jsonElement.getAsString());
            }
            this.highestMagicalPower = getAsInteger("highest_magical_power", object);
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

        public static class Tuning {
            private final HashMap<String, Integer> slot0;

            public Tuning(JsonObject object) {
                this.slot0 = new HashMap<>();
                JsonObject slot0Object = getAsJsonObject("slot_0", object);
                for (Map.Entry<String, JsonElement> entry : slot0Object.entrySet()) {
                    String string = entry.getKey();
                    slot0.put(string, getAsInteger(string, slot0Object));
                }
            }

            public HashMap<String, Integer> getSlot0() {
                return slot0;
            }
        }

    }

    public static class PetsData {
        private final List<Pet> pets;
        private final PetCare petCare;
        private final AutoPet autoPet;

        public PetsData(JsonObject object) {
            this.petCare = new PetCare(getAsJsonObject("pet_care", object));
            this.autoPet = new AutoPet(getAsJsonObject("autopet", object));
            this.pets = new ArrayList<>();
            JsonArray petsArray = getAsJsonArray("pets", object);
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

        public static class PetCare {
            private final Double coinsSpent;
            private final List<String> petTypesSacrificed;

            public PetCare(JsonObject object) {
                this.coinsSpent = getAsDouble("coins_spent", object);
                this.petTypesSacrificed = new ArrayList<>();
                JsonArray petTypesSacrificedArray = getAsJsonArray("pet_types_sacrificed", object);
                for (JsonElement jsonElement : petTypesSacrificedArray) {
                    petTypesSacrificed.add(jsonElement.getAsString());
                }
            }

            public Double getCoinsSpent() {
                return coinsSpent;
            }

            public List<String> getPetTypesSacrificed() {
                return petTypesSacrificed;
            }
        }

        public static class AutoPet {
            private final Integer rulesLimit;
            private final List<AutoPetRule> autoPetRules;
            private final Boolean migrated;
            private final Boolean migrated2;

            public AutoPet(JsonObject object) {
                this.rulesLimit = getAsInteger("rules_limit", object);
                this.autoPetRules = new ArrayList<>();
                JsonArray autoPetRulesArray = getAsJsonArray("rules", object);
                for (JsonElement jsonElement : autoPetRulesArray) {
                    autoPetRules.add(new AutoPetRule(jsonElement.getAsJsonObject()));
                }
                this.migrated = getAsBoolean("migrated", object);
                this.migrated2 = getAsBoolean("migrated_2", object);

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

            public static class AutoPetRule {
                private final UUID uuid;
                private final String id;
                private final String name;
                private final UUID uniqueID;
                private final List<AutoPetRuleException> autoPetRuleExceptions;
                private final Boolean disabled;
                private final HashMap<String, String> data;

                public AutoPetRule(JsonObject object) {
                    this.uuid = fromString(getAsString("uuid", object));
                    this.id = getAsString("id", object);
                    this.name = getAsString("name", object);
                    this.uniqueID = fromString(getAsString("uniqueId", object));
                    this.autoPetRuleExceptions = new ArrayList<>();
                    JsonArray autoPetRuleExceptionsArray = getAsJsonArray("exceptions", object);
                    for (JsonElement jsonElement : autoPetRuleExceptionsArray) {
                        autoPetRuleExceptions.add(new AutoPetRuleException(jsonElement.getAsJsonObject()));
                    }
                    this.disabled = getAsBoolean("disabled", object);
                    this.data = new HashMap<>();
                    JsonObject dataObject = getAsJsonObject("data", object);
                    for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                        String string = entry.getKey();
                        data.put(string, getAsString(string, dataObject));
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

                public static class AutoPetRuleException {
                    private final String id;
                    private final HashMap<String, String> data;

                    public AutoPetRuleException(JsonObject object) {
                        this.id = getAsString("id", object);
                        this.data = new HashMap<>();
                        JsonObject dataObject = getAsJsonObject("data", object);
                        for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                            String string = entry.getKey();
                            data.put(string, getAsString(string, dataObject));
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

        public static class Pet {
            private final UUID uuid;
            private final UUID uniqueId;
            private final String type;
            private final Double exp;
            private final Boolean active;
            private final String tier;
            private final String heldItem;
            private final Integer candyUsed;
            private final String skin;

            public Pet(JsonObject object) {
                this.uuid = fromString(getAsString("uuid", object));
                this.uniqueId = fromString(getAsString("uniqueId", object));
                this.type = getAsString("type", object);
                this.exp = getAsDouble("exp", object);
                this.active = getAsBoolean("active", object);
                this.tier = getAsString("tier", object);
                this.heldItem = getAsString("heldItem", object);
                this.candyUsed = getAsInteger("candyUsed", object);
                this.skin = getAsString("skin", object);
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

            public Double getExp() {
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

    public static class GardenPlayerData {
        private final Integer copper;
        private final Integer larvaConsumed;

        public GardenPlayerData(JsonObject object) {
            this.copper = getAsInteger("copper", object);
            this.larvaConsumed = getAsInteger("larva_consumed", object);
        }

        public int getCopper() {
            return copper;
        }

        public int getLarvaConsumed() {
            return larvaConsumed;
        }
    }

    public static class Events {
        private final Easter easter;

        public Events(JsonObject object) {
            this.easter = new Easter(getAsJsonObject("easter", object));
        }

        public Easter getEaster() {
            return easter;
        }

        public static class Easter {
            private final Long chocolate;
            private final Long chocolateSincePrestige;
            private final Long totalChocolate;
            private final HashMap<String, Integer> employees;
            private final Long lastViewedChocolateFactory;
            private final Rabbits rabbits;
            private final Shop shop;
            private final Integer rabbitBarnCapacityLevel;
            private final String rabbitSort;
            private final Integer chocolateLevel;
            private final TimeTower timeTower;
            private final String rabbitFilter;
            private final Integer chocolateMultiplierUpgrades;
            private final Integer clickUpgrades;
            private final Integer rabbitRarityUpgrades;
            private final Integer supremeChocolateBars;
            private final Integer refinedDarkCacaoTruffles;

            public Easter(JsonObject object) {
                this.chocolate = getAsLong("chocolate", object);
                this.chocolateSincePrestige = getAsLong("chocolate_since_prestige", object);
                this.totalChocolate = getAsLong("total_chocolate", object);
                this.employees = new HashMap<>();
                JsonObject employeesObject = getAsJsonObject("employees", object);
                for (Map.Entry<String, JsonElement> entry : employeesObject.entrySet()) {
                    String string = entry.getKey();
                    employees.put(string, getAsInteger(string, employeesObject));
                }
                this.lastViewedChocolateFactory = getAsLong("last_viewed_chocolate_factory", object);
                this.rabbits = new Rabbits(getAsJsonObject("rabbits", object));
                this.shop = new Shop(getAsJsonObject("shop", object));
                this.rabbitBarnCapacityLevel = getAsInteger("rabbit_barn_capacity_level", object);
                this.rabbitSort = getAsString("rabbit_sort", object);
                this.chocolateLevel = getAsInteger("chocolate_level", object);
                this.timeTower = new TimeTower(getAsJsonObject("time_tower", object));
                this.rabbitFilter = getAsString("rabbit_filter", object);
                this.chocolateMultiplierUpgrades = getAsInteger("chocolate_multiplier_upgrades", object);
                this.clickUpgrades = getAsInteger("click_upgrades", object);
                this.rabbitRarityUpgrades = getAsInteger("rabbit_rarity_upgrades", object);
                this.supremeChocolateBars = getAsInteger("supreme_chocolate_bars", object);
                this.refinedDarkCacaoTruffles = getAsInteger("refined_dark_cacao_truffles", object);
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

            public static class TimeTower {
                private final Integer charges;
                private final Long lastChargeTime;
                private final Integer level;
                private final Long activationTime;

                public TimeTower(JsonObject object) {
                    this.charges = getAsInteger("charges", object);
                    this.lastChargeTime = getAsLong("last_charge_time", object);
                    this.level = getAsInteger("level", object);
                    this.activationTime = getAsLong("activation_time", object);
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

            public static class Shop {
                private final Integer year;
                private final List<String> rabbitsShop;
                private final List<String> rabbitsPurchased;
                private final Long chocolateSpent;
                private final Integer cocoaFortuneUpgrades;

                public Shop(JsonObject object) {
                    this.year = getAsInteger("year", object);
                    this.rabbitsShop = new ArrayList<>();
                    JsonArray rabbitsShopArray = getAsJsonArray("rabbits", object);
                    for (JsonElement jsonElement : rabbitsShopArray) {
                        rabbitsShop.add(jsonElement.getAsString());
                    }
                    this.rabbitsPurchased = new ArrayList<>();
                    JsonArray rabbitsPurchasedArray = getAsJsonArray("rabbits_purchased", object);
                    for (JsonElement jsonElement : rabbitsPurchasedArray) {
                        rabbitsPurchased.add(jsonElement.getAsString());
                    }
                    this.chocolateSpent = getAsLong("chocolate", object);
                    this.cocoaFortuneUpgrades = getAsInteger("cocoa_fortune_upgrades", object);
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

            public static class Rabbits {
                private final HashMap<String, Long> collectedEggs;
                private final HashMap<String, List<String>> collectedLocations;

                public Rabbits(JsonObject object) {
                    this.collectedEggs = new HashMap<>();
                    JsonObject collectedEggsObject = getAsJsonObject("collected_eggs", object);
                    for (Map.Entry<String, JsonElement> entry : collectedEggsObject.entrySet()) {
                        String string = entry.getKey();
                        collectedEggs.put(string, getAsLong(string, collectedEggsObject));
                    }
                    this.collectedLocations = new HashMap<>();
                    JsonObject collectedLocationsObject = getAsJsonObject("collected_locations", object);
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
        }
    }

    public static class GlacitePlayerData {
        private final List<String> fossilsDonated;
        private final Double fossilDust;
        private final HashMap<String, Integer> corpsesLooted;
        private final Integer mineshaftsEntered;

        public GlacitePlayerData(JsonObject object) {
            this.fossilsDonated = new ArrayList<>();
            JsonArray fossilsDonatedArray = getAsJsonArray("fossils_donated", object);
            for (JsonElement jsonElement : fossilsDonatedArray) {
                fossilsDonated.add(jsonElement.getAsString());
            }
            this.corpsesLooted = new HashMap<>();
            JsonObject corpsesLootedObject = getAsJsonObject("corpses_looted", object);
            for (Map.Entry<String, JsonElement> entry : corpsesLootedObject.entrySet()) {
                String string = entry.getKey();
                corpsesLooted.put(string, getAsInteger(string, corpsesLootedObject));
            }
            this.fossilDust = getAsDouble("fossil_dust", object);
            this.mineshaftsEntered = getAsInteger("mineshafts_entered", object);
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

    public static class TempStatBuff {
        private final Integer stat;
        private final String key;
        private final Integer amount;
        private final Long expireAt;

        public TempStatBuff(JsonObject object) {
            this.stat = getAsInteger("stat", object);
            this.key = getAsString("key", object);
            this.amount = getAsInteger("amount", object);
            this.expireAt = getAsLong("expire_at", object);
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

        /*
    public JsonObject getCurrentProfile(UUID uuid) {
        return meloMod.getPlayerProfile().getProfileMember(uuid);
        try {
            CompletableFuture<JsonObject> player = meloMod.getPlayerProfile();
            JsonObject object = player.get();

            JsonArray profiles = getAsJsonArray("profiles", object);
            for (JsonElement profile : profiles) {
                JsonObject profileObj = profile.getAsJsonObject();
                if (profileObj.get("selected").getAsBoolean()) {
                    JsonObject members = getAsJsonObject("members", profileObj);
                    for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
                        String s = entry.getKey();
                        if (fixMalformed(s).equals(uuid)) {
                            JsonObject member = members.get(s).getAsJsonObject();
                            return member;
                        }
                    }
                }
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§cERROR: Null profile (Have you added a valid API Key?)"));
            return null;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

         */

}
