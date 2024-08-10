package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class Gallery {
    private final Integer eliseStep;
    private final List<SecuredTrophy> securedTrophies;
    private final List<String> sentTrophyDialogues;

    public Gallery(JsonObject object) {
        this.eliseStep = SkyblockUtil.getAsInteger("elise_step", object);
        this.securedTrophies = new ArrayList<>();
        JsonArray securedTrophiesArray = SkyblockUtil.getAsJsonArray("secured_trophies", object);
        for (JsonElement securedTrophy : securedTrophiesArray) {
            securedTrophies.add(new SecuredTrophy(securedTrophy.getAsJsonObject()));
        }
        this.sentTrophyDialogues = new ArrayList<>();
        JsonArray sentTrophyDialoguesArray = SkyblockUtil.getAsJsonArray("sent_trophy_dialogues", object);
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
            this.type = SkyblockUtil.getAsString("type", object);
            this.timestamp = SkyblockUtil.getAsLong("timestamp", object);
            this.visits = SkyblockUtil.getAsInteger("visits", object);
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
