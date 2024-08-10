package me.vlink102.melomod.util.wrappers.hypixel;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.translation.Feature;

public class PlayerUtil {
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

    public static class Player {
        @Getter
        private final String id;
        @Getter
        private final String uuid;
        @Getter
        private final String displayName;
        @Getter
        private final Long firstLogin;
        @Getter
        private final Long lastLogin;
        @Getter
        private final String playerName;
        @Getter
        private final Long networkXp;
        @Getter
        private final Long lastLogout;
        @Getter
        private final String chatChannel;
        @Getter
        private final String userLanguage;
        @Getter
        private final String newPackageRank;
        @Getter
        private final String rankPlusColor;
        @Getter
        private final String monthlyRankColor;
        @Getter
        private final String mostRecentGameType;
        private String twitter = null;
        private String youtube = null;
        private String instagram = null;
        private String tiktok = null;
        private String twitch = null;
        private String discord = null;
        private String forums = null;

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
            if (instagram == null && socialMedia.has("INSTAGRAM")) {
                instagram = createInstagramLink(SkyblockUtil.getAsString("INSTAGRAM", socialMedia));
            }
            if (twitter == null && socialMedia.has("TWITTER")) {
                twitter = createTwitterLink(SkyblockUtil.getAsString("TWITTER", socialMedia));
            }
            if (discord == null && socialMedia.has("DISCORD")) {
                discord = SkyblockUtil.getAsString("DISCORD", socialMedia);
            }
            if (twitch == null && socialMedia.has("TWITCH")) {
                twitch = createTwitchLink(SkyblockUtil.getAsString("TWITCH", socialMedia));
            }
            if (forums == null && socialMedia.has("HYPIXEL")) {
                forums = createForumsLink(SkyblockUtil.getAsString("HYPIXEL", socialMedia));
            }
            if (tiktok == null && socialMedia.has("TIKTOK")) {
                tiktok = createTiktokLink(SkyblockUtil.getAsString("TIKTOK", socialMedia));
            }
            monthlyRankColor = SkyblockUtil.getAsString("monthlyRankColor", object);
            mostRecentGameType = SkyblockUtil.getAsString("mostRecentGameType", object);
        }

        public String getDiscord() {
            if (discord == null) return Feature.GENERIC_NONE.toString();
            return discord;
        }

        public String getForums() {
            if (forums == null) return Feature.GENERIC_NONE.toString();
            return forums;
        }

        public String getInstagram() {
            if (instagram == null) return Feature.GENERIC_NONE.toString();
            return instagram;
        }

        public String getTiktok() {
            if (tiktok == null) return Feature.GENERIC_NONE.toString();
            return tiktok;
        }

        public String getTwitch() {
            if (twitch == null) return Feature.GENERIC_NONE.toString();
            return twitch;
        }

        public String getTwitter() {
            if (twitter == null) return Feature.GENERIC_NONE.toString();
            return twitter;
        }

        public String getYoutube() {
            if (youtube == null) return Feature.GENERIC_NONE.toString();
            return youtube;
        }

    }
}
