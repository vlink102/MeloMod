package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class Dreadfarm {
    private final Integer shaniaStage;
    @Getter
    private final List<Long> caducousFeederUses;

    public Dreadfarm(JsonObject object) {
        this.shaniaStage = SkyblockUtil.getAsInteger("shania_stage", object);
        this.caducousFeederUses = new ArrayList<>();
        JsonArray caducousFeederUsesArray = SkyblockUtil.getAsJsonArray("caducous_feeder_uses", object);
        for (JsonElement jsonElement : caducousFeederUsesArray) {
            caducousFeederUses.add(jsonElement.getAsLong());
        }
    }

    public int getShaniaStage() {
        return shaniaStage;
    }

}
