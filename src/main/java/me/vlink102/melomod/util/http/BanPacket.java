package me.vlink102.melomod.util.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.concurrent.TimeUnit;

@Getter
public class BanPacket {
    private final String admin;
    private final String reason;
    private final String summary;
    private final Long timestamp;
    private final Long duration;
    private final Long expiry;

    public BanPacket(String admin, String reason, String summary, Long timestamp, Long duration, Long expiry) {
        this.admin = admin;
        this.reason = reason;
        this.summary = summary;
        this.timestamp = timestamp;
        this.duration = duration;
        this.expiry = expiry == null ? System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1) : expiry;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("admin", admin);
        object.addProperty("reason", reason);
        object.addProperty("summary", summary);
        object.addProperty("timestamp", timestamp);
        object.addProperty("expiry", expiry);
        object.addProperty("duration", duration);
        return object;
    }

    public static BanPacket parse(JsonObject object) {
        String admin = SkyblockUtil.getAsString("admin",object);
        String reason = SkyblockUtil.getAsString("reason",object);
        String summary = SkyblockUtil.getAsString("summary",object);
        Long timestamp = object.get("timestamp").getAsLong();
        Long duration = object.get("duration").getAsLong();
        Long expiry = object.get("expiry").getAsLong();
        return new BanPacket(admin, reason, summary, timestamp, duration, expiry);
    }

    public static BanPacket parse(String jsonObject) {
        JsonElement element = new JsonParser().parse(jsonObject);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            return parse(object);
        }
        return null;
    }

}