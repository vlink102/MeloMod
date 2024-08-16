package me.vlink102.melomod.util.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.events.ChatEventHandler;
import me.vlink102.melomod.events.LocrawHandler;
import me.vlink102.melomod.util.enums.skyblock.Gamemode;
import me.vlink102.melomod.util.enums.skyblock.Location;
import me.vlink102.melomod.util.wrappers.hypixel.profile.CommunityUpgrade;
import me.vlink102.melomod.util.wrappers.hypixel.profile.member.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class SkyblockUtil {
    private final MeloMod meloMod;

    public SkyblockUtil(MeloMod meloMod) {
        this.meloMod = meloMod;
    }

    public static Location getPlayerLocation() {
        return LocrawHandler.getLocation();
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
                MeloMod.setPlayerProfile(new SkyblockProfile(profileObject));

                HashMap<String, Integer> kills = MeloMod.getPlayerProfile().getMembers().get(MeloMod.playerUUID.toString().replaceAll("-", "")).getBestiary().getKills();
                for (Map.Entry<String, Integer> entry : kills.entrySet()) {
                    String string = entry.getKey();
                    ChatEventHandler.seaCreatureSession.put(ChatEventHandler.SeaCreature.convertBestiaryMob(string), entry.getValue());
                }
                return;
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
