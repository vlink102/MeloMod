package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WitherCage {
    private final List<String> eyesKilled;

    public WitherCage(JsonObject object) {
        this.eyesKilled = new ArrayList<>();
        JsonArray killedEyes = SkyblockUtil.getAsJsonArray("killed_eyes", object);
        for (JsonElement killedEye : killedEyes) {
            eyesKilled.add(killedEye.getAsString());
        }
    }

}
