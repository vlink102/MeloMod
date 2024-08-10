package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class VillagePlaza {
    private final Boolean gotScammed;
    @Getter
    private final Murder murder;
    @Getter
    private final BarryCenter barryCenter;
    @Getter
    private final Cowboy cowboy;
    @Getter
    private final Lonely lonely;
    @Getter
    private final Seraphine seraphine;

    public VillagePlaza(JsonObject object) {
        this.gotScammed = SkyblockUtil.getAsBoolean("got_scammed", object);
        this.murder = new Murder(SkyblockUtil.getAsJsonObject("murder", object));
        this.barryCenter = new BarryCenter(SkyblockUtil.getAsJsonObject("barry_center", object));
        this.cowboy = new Cowboy(SkyblockUtil.getAsJsonObject("cowboy", object));
        this.lonely = new Lonely(SkyblockUtil.getAsJsonObject("lonely", object));
        this.seraphine = new Seraphine(SkyblockUtil.getAsJsonObject("seraphine", object));
    }

    public boolean isGotScammed() {
        return gotScammed;
    }

    public static class Murder {
        private final Integer stepIndex;
        @Getter
        private final List<String> roomClues;

        public Murder(JsonObject object) {
            this.stepIndex = SkyblockUtil.getAsInteger("step_index", object);
            this.roomClues = new ArrayList<>();
            JsonArray roomCluesArray = SkyblockUtil.getAsJsonArray("room_clues", object);
            for (JsonElement jsonElement : roomCluesArray) {
                roomClues.add(jsonElement.getAsString());
            }
        }

        public int getStepIndex() {
            return stepIndex;
        }
    }

    public static class BarryCenter {
        private final Boolean firstTalkToBarry;
        @Getter
        private final List<String> convinced;
        private final Boolean receivedReward;

        public BarryCenter(JsonObject object) {
            this.firstTalkToBarry = SkyblockUtil.getAsBoolean("first_talk_to_barry", object);
            this.convinced = new ArrayList<>();
            JsonArray convincedArray = SkyblockUtil.getAsJsonArray("convinced", object);
            for (JsonElement jsonElement : convincedArray) {
                convinced.add(jsonElement.getAsString());
            }
            this.receivedReward = SkyblockUtil.getAsBoolean("received_reward", object);
        }

        public boolean isFirstTalkToBarry() {
            return firstTalkToBarry;
        }

        public boolean isReceivedReward() {
            return receivedReward;
        }
    }

    public static class Cowboy {
        private final Integer stage;
        private final Integer hayEaten;
        @Getter
        private final String rabbitName;
        private final Integer exportedCarrots;

        public Cowboy(JsonObject object) {
            this.stage = SkyblockUtil.getAsInteger("stage", object);
            this.hayEaten = SkyblockUtil.getAsInteger("hay_eaten", object);
            this.rabbitName = SkyblockUtil.getAsString("rabbit_name", object);
            this.exportedCarrots = SkyblockUtil.getAsInteger("exported_carrots", object);
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

    }

    public static class Lonely {
        private final Integer secondsSitting;

        public Lonely(JsonObject object) {
            this.secondsSitting = SkyblockUtil.getAsInteger("seconds_sitting", object);
        }

        public int getSecondsSitting() {
            return secondsSitting;
        }
    }

    public static class Seraphine {
        private final Integer stepIndex;

        public Seraphine(JsonObject object) {
            this.stepIndex = SkyblockUtil.getAsInteger("step_index", object);
        }

        public int getStepIndex() {
            return stepIndex;
        }
    }
}
