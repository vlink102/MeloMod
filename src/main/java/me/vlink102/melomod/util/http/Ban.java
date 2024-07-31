package me.vlink102.melomod.util.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.concurrent.TimeUnit;

public class Ban {
    private final String admin;
    private final String reason;
    private final String summary;
    private final Long timestamp;
    private final Long duration;
    private final Long expiry;

    public Ban(String admin, String reason, String summary, Long timestamp, Long duration, Long expiry) {
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

    public static Ban parse(JsonObject object) {
        String admin = SkyblockUtil.getAsString("admin",object);
        String reason = SkyblockUtil.getAsString("reason",object);
        String summary = SkyblockUtil.getAsString("summary",object);
        Long timestamp = object.get("timestamp").getAsLong();
        Long duration = object.get("duration").getAsLong();
        Long expiry = object.get("expiry").getAsLong();
        return new Ban(admin, reason, summary, timestamp, duration, expiry);
    }

    public static Ban parse(String jsonObject) {
        JsonElement element = new JsonParser().parse(jsonObject);
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            return parse(object);
        }
        return null;
    }

    public Long getDuration() {
        return duration;
    }

    public String getSummary() {
        return summary;
    }

    public String getReason() {
        return reason;
    }

    public Long getExpiry() {
        return expiry;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getAdmin() {
        return admin;
    }
}