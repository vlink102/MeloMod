package me.vlink102.melomod.util.wrappers.hypixel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.*;

@Getter
public class Guild {
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
        this.name = SkyblockUtil.getAsString("name", object);
        this.nameLower = SkyblockUtil.getAsString("name_lower", object);
        this.coins = SkyblockUtil.getAsInteger("coins", object);
        this.coinsEver = SkyblockUtil.getAsInteger("coinsEver", object);
        this.created = SkyblockUtil.getAsLong("created", object);
        this.members = new ArrayList<>();
        JsonArray membersArray = SkyblockUtil.getAsJsonArray("members", object);
        for (JsonElement member : membersArray) {
            members.add(new GuildMember(member.getAsJsonObject()));
        }
        this.ranks = new ArrayList<>();
        JsonArray ranksArray = SkyblockUtil.getAsJsonArray("ranks", object);
        for (JsonElement rank : ranksArray) {
            ranks.add(new GuildRank(rank.getAsJsonObject()));
        }
        this.guildAchievements = new GuildAchievements(SkyblockUtil.getAsJsonObject("achievements", object));
        this.preferredGames = new ArrayList<>();
        JsonArray preferredGamesArray = SkyblockUtil.getAsJsonArray("preferredGames", object);
        for (JsonElement preferredGame : preferredGamesArray) {
            preferredGames.add(preferredGame.getAsString());
        }
        this.publiclyListed = SkyblockUtil.getAsBoolean("publiclyListed", object);
        this.exp = SkyblockUtil.getAsLong("exp", object);
        this.chatMute = SkyblockUtil.getAsInteger("chatMute", object);
        this.tag = SkyblockUtil.getAsString("tag", object);
        this.tagColor = SkyblockUtil.getAsString("tagColor", object);
        this.description = SkyblockUtil.getAsString("description", object);
        this.guildExpByGameType = new HashMap<>();
        JsonObject guildExpObject = SkyblockUtil.getAsJsonObject("guildExpByGameType", object);
        for (Map.Entry<String, JsonElement> entry : guildExpObject.entrySet()) {
            String string = entry.getKey();
            guildExpByGameType.put(string, entry.getValue().getAsInt());
        }

    }

    public String getGuildID() {
        return "???";// TODO
    }

    @Getter
    public static class GuildAchievements {
        private final Integer onlinePlayers;
        private final Integer experienceKings;
        private final Integer winners;

        public GuildAchievements(JsonObject object) {
            this.onlinePlayers = SkyblockUtil.getAsInteger("ONLINE_PLAYERS", object);
            this.experienceKings = SkyblockUtil.getAsInteger("EXPERIENCE_KINGS", object);
            this.winners = SkyblockUtil.getAsInteger("WINNERS", object);
        }

    }

    public static class GuildRank {
        @Getter
        private final String name;
        private final Boolean isDefault;
        @Getter
        private final String tag;
        @Getter
        private final Long created;
        @Getter
        private final Integer priority;

        public GuildRank(JsonObject object) {
            this.name = SkyblockUtil.getAsString("name", object);
            this.isDefault = SkyblockUtil.getAsBoolean("default", object);
            this.tag = SkyblockUtil.getAsString("tag", object);
            this.created = SkyblockUtil.getAsLong("created", object);
            this.priority = SkyblockUtil.getAsInteger("priority", object);
        }

        public Boolean getDefault() {
            return isDefault;
        }

    }

    @Getter
    public static class GuildMember {
        private final UUID uuid;
        private final String rank;
        private final Long joined;
        private final Integer questParticipation;
        private final HashMap<String, Integer> expHistory;

        public GuildMember(JsonObject object) {
            this.uuid = SkyblockUtil.fromString(SkyblockUtil.getAsString("uuid", object));
            this.rank = SkyblockUtil.getAsString("rank", object);
            this.joined = SkyblockUtil.getAsLong("joined", object);
            this.questParticipation = SkyblockUtil.getAsInteger("questParticipation", object);
            this.expHistory = new HashMap<>();
            JsonObject expHistoryObject = SkyblockUtil.getAsJsonObject("expHistory", object);
            expHistory.replaceAll((s, v) -> SkyblockUtil.getAsInteger(s, expHistoryObject));
        }

        public UUID getUUID() {
            return uuid;
        }

    }
}
