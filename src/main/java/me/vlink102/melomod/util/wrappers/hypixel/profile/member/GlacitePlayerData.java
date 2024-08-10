package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlacitePlayerData {
    private final List<String> fossilsDonated;
    private final Double fossilDust;
    private final HashMap<String, Integer> corpsesLooted;
    private final Integer mineshaftsEntered;

    public GlacitePlayerData(JsonObject object) {
        this.fossilsDonated = new ArrayList<>();
        JsonArray fossilsDonatedArray = SkyblockUtil.getAsJsonArray("fossils_donated", object);
        for (JsonElement jsonElement : fossilsDonatedArray) {
            fossilsDonated.add(jsonElement.getAsString());
        }
        this.corpsesLooted = new HashMap<>();
        JsonObject corpsesLootedObject = SkyblockUtil.getAsJsonObject("corpses_looted", object);
        for (Map.Entry<String, JsonElement> entry : corpsesLootedObject.entrySet()) {
            String string = entry.getKey();
            corpsesLooted.put(string, SkyblockUtil.getAsInteger(string, corpsesLootedObject));
        }
        this.fossilDust = SkyblockUtil.getAsDouble("fossil_dust", object);
        this.mineshaftsEntered = SkyblockUtil.getAsInteger("mineshafts_entered", object);
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
