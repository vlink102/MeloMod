package me.vlink102.melomod.util.wrappers.hypixel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.*;

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
            this.onlinePlayers = SkyblockUtil.getAsInteger("ONLINE_PLAYERS", object);
            this.experienceKings = SkyblockUtil.getAsInteger("EXPERIENCE_KINGS", object);
            this.winners = SkyblockUtil.getAsInteger("WINNERS", object);
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
            this.name = SkyblockUtil.getAsString("name", object);
            this.isDefault = SkyblockUtil.getAsBoolean("default", object);
            this.tag = SkyblockUtil.getAsString("tag", object);
            this.created = SkyblockUtil.getAsLong("created", object);
            this.priority = SkyblockUtil.getAsInteger("priority", object);
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
            this.uuid = SkyblockUtil.fromString(SkyblockUtil.getAsString("uuid", object));
            this.rank = SkyblockUtil.getAsString("rank", object);
            this.joined = SkyblockUtil.getAsLong("joined", object);
            this.questParticipation = SkyblockUtil.getAsInteger("questParticipation", object);
            this.expHistory = new HashMap<>();
            JsonObject expHistoryObject = SkyblockUtil.getAsJsonObject("expHistory", object);
            for (String s : expHistory.keySet()) {
                expHistory.put(s, SkyblockUtil.getAsInteger(s, expHistoryObject));
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
