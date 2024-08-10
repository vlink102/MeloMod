package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WestVillage {
    private final MirrorVerse mirrorVerse;
    private final CrazyKloon crazyKloon;
    private final KatHouse katHouse;
    private final Glyphs glyphs;

    public WestVillage(JsonObject object) {
        this.mirrorVerse = new MirrorVerse(SkyblockUtil.getAsJsonObject("mirrorverse", object));
        this.crazyKloon = new CrazyKloon(SkyblockUtil.getAsJsonObject("crazy_kloon", object));
        this.katHouse = new KatHouse(SkyblockUtil.getAsJsonObject("kat_house", object));
        this.glyphs = new Glyphs(SkyblockUtil.getAsJsonObject("glyphs", object));
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
            this.upsideDownHard = SkyblockUtil.getAsBoolean("upside_down_hard", object);
            this.claimedChestItems = new ArrayList<>();
            this.claimedReward = SkyblockUtil.getAsBoolean("claimed_reward", object);
            JsonArray visitedRoomsArray = SkyblockUtil.getAsJsonArray("visited_rooms", object);
            for (JsonElement visitedRoom : visitedRoomsArray) {
                roomsVisited.add(visitedRoom.getAsString());
            }
            JsonArray claimedChestItemsArray = SkyblockUtil.getAsJsonArray("claimed_chest_items", object);
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
            JsonObject selectedColorsMap = SkyblockUtil.getAsJsonObject("selected_colors", object);
            for (Map.Entry<String, JsonElement> entry : selectedColorsMap.entrySet()) {
                String string = entry.getKey();
                selectedColors.put(string, SkyblockUtil.getAsString(string, selectedColorsMap));
            }
            this.talked = SkyblockUtil.getAsBoolean("talked", object);
            this.hackedTerminals = new ArrayList<>();
            JsonArray hackedTerminalsArray = SkyblockUtil.getAsJsonArray("hacked_terminals", object);
            for (JsonElement hackedTerminal : hackedTerminalsArray) {
                hackedTerminals.add(hackedTerminal.getAsString());
            }
            this.questComplete = SkyblockUtil.getAsBoolean("quest_complete", object);
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
            this.binCollectedMosquito = SkyblockUtil.getAsInteger("bin_collected_mosquito", object);
            this.binCollectedSilverfish = SkyblockUtil.getAsInteger("bin_collected_silverfish", object);
            this.binCollectedSpider = SkyblockUtil.getAsInteger("bin_collected_spider", object);
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
            this.claimedWand = SkyblockUtil.getAsBoolean("claimed_wand", object);
            this.currentGlyphDelivered = SkyblockUtil.getAsBoolean("current_glyph_delivered", object);
            this.currentGlyphCompleted = SkyblockUtil.getAsBoolean("current_glyph_completed", object);
            this.currentGlyph = SkyblockUtil.getAsInteger("current_glyph", object);
            this.completed = SkyblockUtil.getAsBoolean("completed", object);
            this.claimedBracelet = SkyblockUtil.getAsBoolean("claimed_bracelet", object);
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
