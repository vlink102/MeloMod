package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.Packet;

public class ClientBoundDisconnectPacket extends Packet {
    private final String quitName;
    private final Exception rawData;
    private final ParsedException data;

    public static class ParsedException {
        private final String message;
        private final String localized;
        private final String clazz;
        private final ParsedCause cause;

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("message", message);
            json.addProperty("localized", localized);
            json.addProperty("class", clazz);
            json.add("cause", cause.toJson());
            return json;
        }

        public ParsedException(Exception data) {
            this.message = data.getMessage();
            this.localized = data.getLocalizedMessage();
            this.clazz = data.getClass().getName();
            this.cause = new ParsedCause(data.getCause());
        }

        public ParsedException(JsonObject object) {
            this.message = SkyblockUtil.getAsString("message", object);
            this.localized = SkyblockUtil.getAsString("localized", object);
            this.clazz = SkyblockUtil.getAsString("class", object);
            this.cause = new ParsedCause(SkyblockUtil.getAsJsonObject("cause", object));
        }

        public String getMessage() {
            return message;
        }

        public String getClazz() {
            return clazz;
        }

        public String getLocalized() {
            return localized;
        }

        public ParsedCause getCause() {
            return cause;
        }

        public static class ParsedCause {
            private final String clazz;
            private final String message;
            private final String localized;

            public ParsedCause(Throwable cause) {
                this.clazz = cause.getClass().getName();
                this.message = cause.getMessage();
                this.localized = cause.getClass().getName();
            }

            public ParsedCause(JsonObject object) {
                this.clazz = SkyblockUtil.getAsString("class", object);
                this.message = SkyblockUtil.getAsString("message", object);
                this.localized = SkyblockUtil.getAsString("localized", object);
            }

            public JsonObject toJson() {
                JsonObject json = new JsonObject();
                json.addProperty("class", clazz);
                json.addProperty("message", message);
                json.addProperty("localized", localized);
                return json;
            }

            public String getClazz() {
                return clazz;
            }

            public String getMessage() {
                return message;
            }

            public String getLocalized() {
                return localized;
            }
        }
    }

    public ClientBoundDisconnectPacket(String quitName, JsonObject exception) {
        this.quitName = quitName;
        this.rawData = null;
        this.data = new ParsedException(exception);
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("name", quitName);
        o.add("data", data.toJson());
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public Packet.PacketID bind() {
        return PacketID.CLOSED_CONNECTION;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            String quitName = SkyblockUtil.getAsString("quit-name", o);
            return new ClientBoundDisconnectPacket(quitName, SkyblockUtil.getAsJsonObject("data", o));
        }
        return null;
    }

    public ParsedException getData() {
        return data;
    }

    public Exception getRawData() {
        return rawData;
    }

    public String getQuitName() {
        return quitName;
    }
}
