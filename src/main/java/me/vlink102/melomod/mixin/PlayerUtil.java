package me.vlink102.melomod.mixin;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class PlayerUtil {
    public static class Player {
        private final String id;
        private final String uuid;
        private final String displayName;
        private final Long firstLogin;
        private final Long lastLogin;
        private final String playerName;
        private final Long networkXp;
        private final Long lastLogout;
        private final String chatChannel;
        private final String userLanguage;
        private final String newPackageRank;
        private final String rankPlusColor;
        private String twitter = null;
        private String youtube = null;
        private String instagram = null;
        private String tiktok = null;
        private String twitch = null;
        private String discord = null;
        private String forums = null;
        private final String monthlyRankColor;
        private final String mostRecentGameType;

        public Player(JsonObject object) {
            id = SkyblockUtil.getAsString("_id", object);
            uuid = SkyblockUtil.getAsString("uuid", object);
            displayName = SkyblockUtil.getAsString("displayname", object);
            firstLogin = SkyblockUtil.getAsLong("firstLogin", object);
            lastLogin = SkyblockUtil.getAsLong("lastLogin", object);
            playerName = SkyblockUtil.getAsString("playername", object);
            networkXp = SkyblockUtil.getAsLong("networkExp", object);
            lastLogout = SkyblockUtil.getAsLong("lastLogout", object);
            chatChannel = SkyblockUtil.getAsString("channel", object);
            userLanguage = SkyblockUtil.getAsString("userLanguage", object);
            newPackageRank = SkyblockUtil.getAsString("newPackageRank", object);
            rankPlusColor = SkyblockUtil.getAsString("rankPlusColor", object);


            JsonObject socialMedia = SkyblockUtil.getAsJsonObject("socialMedia", object);
            JsonObject links = SkyblockUtil.getAsJsonObject("links", socialMedia);
            if (links.has("YOUTUBE")) {
                youtube = SkyblockUtil.getAsString("YOUTUBE", links);
            }
            if (links.has("INSTAGRAM")) {
                instagram = SkyblockUtil.getAsString("INSTAGRAM", links);
            }
            if (links.has("TWITTER")) {
                twitter = SkyblockUtil.getAsString("TWITTER", links);
            }
            if (links.has("DISCORD")) {
                discord = SkyblockUtil.getAsString("DISCORD", links);
            }
            if (links.has("HYPIXEL")) {
                forums = SkyblockUtil.getAsString("HYPIXEL", links);
            }
            if (links.has("TIKTOK")) {
                tiktok = SkyblockUtil.getAsString("TIKTOK", links);
            }
            if (links.has("TWITCH")) {
                twitch = SkyblockUtil.getAsString("TWITCH", links);
            }
            if (youtube == null && socialMedia.has("YOUTUBE")) {
                youtube = createYoutubeLink(SkyblockUtil.getAsString("YOUTUBE", socialMedia));
            }
            if (instagram ==  null && socialMedia.has("INSTAGRAM")) {
                instagram = createInstagramLink(SkyblockUtil.getAsString("INSTAGRAM", socialMedia));
            }
            if (twitter ==  null && socialMedia.has("TWITTER")) {
                twitter = createTwitterLink(SkyblockUtil.getAsString("TWITTER", socialMedia));
            }
            if (discord ==  null && socialMedia.has("DISCORD")) {
                discord = SkyblockUtil.getAsString("DISCORD", socialMedia);
            }
            if (twitch ==  null && socialMedia.has("TWITCH")) {
                twitch = createTwitchLink(SkyblockUtil.getAsString("TWITCH", socialMedia));
            }
            if (forums ==  null && socialMedia.has("HYPIXEL")) {
                forums = createForumsLink(SkyblockUtil.getAsString("HYPIXEL", socialMedia));
            }
            if (tiktok ==  null && socialMedia.has("TIKTOK")) {
                tiktok = createTiktokLink(SkyblockUtil.getAsString("TIKTOK", socialMedia));
            }
            monthlyRankColor = SkyblockUtil.getAsString("monthlyRankColor", object);
            mostRecentGameType = SkyblockUtil.getAsString("mostRecentGameType", object);
        }

        public String getId() {
            return id;
        }

        public Long getFirstLogin() {
            return firstLogin;
        }

        public Long getLastLogin() {
            return lastLogin;
        }

        public Long getLastLogout() {
            return lastLogout;
        }

        public Long getNetworkXp() {
            return networkXp;
        }

        public String getChatChannel() {
            return chatChannel;
        }

        public String getDiscord() {
            if (discord == null) return "None";
            return discord;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getForums() {
            if (forums == null) return "None";
            return forums;
        }

        public String getInstagram() {
            if (instagram == null) return "None";
            return instagram;
        }

        public String getMonthlyRankColor() {
            return monthlyRankColor;
        }

        public String getMostRecentGameType() {
            return mostRecentGameType;
        }

        public String getNewPackageRank() {
            return newPackageRank;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getRankPlusColor() {
            return rankPlusColor;
        }

        public String getTiktok() {
            if (tiktok == null) return "None";
            return tiktok;
        }

        public String getTwitch() {
            if (twitch == null) return "None";
            return twitch;
        }

        public String getTwitter() {
            if (twitter == null) return "None";
            return twitter;
        }

        public String getUserLanguage() {
            return userLanguage;
        }

        public String getUuid() {
            return uuid;
        }

        public String getYoutube() {
            if (youtube == null) return "None";
            return youtube;
        }

    }

    public static String createForumsLink(String fromJson) {
        if (fromJson != null && !fromJson.startsWith("https")) {
            return "https://hypixel.net/members/" + fromJson;
        }
        return fromJson;
    }

    public static String createTiktokLink(String fromJson) {
        if (fromJson != null && !fromJson.startsWith("https")) {
            return "https://www.tiktok.com/@" + fromJson;
        }
        return fromJson;
    }

    public static String createYoutubeLink(String fromJson) {
        if (fromJson != null && !fromJson.startsWith("https")) {
            return "https://www.youtube.com/channel/" + fromJson.split(";")[1];
        }
        return fromJson;
    }

    public static String createTwitterLink(String fromJson) {
        if (fromJson != null && !fromJson.startsWith("https")) {
            return "https://www.x.com/" + fromJson;
        }
        return fromJson;
    }

    public static String createInstagramLink(String fromJson) {
        if (fromJson != null && !fromJson.startsWith("https")) {
            return "https://www.instagram.com/" + fromJson;
        }
        return fromJson;
    }

    public static String createTwitchLink(String fromJson) {
        if (fromJson != null && !fromJson.startsWith("https")) {
            return "https://www.twitch.tv/" + fromJson;
        }
        return fromJson;
    }
}
