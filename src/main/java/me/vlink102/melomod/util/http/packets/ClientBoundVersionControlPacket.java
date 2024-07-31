package me.vlink102.melomod.util.http.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.Ban;
import me.vlink102.melomod.util.http.Packet;
import me.vlink102.melomod.util.http.Version;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class ClientBoundVersionControlPacket extends Packet {
    private final String correctVersion;
    private final Version.VersionStability correct;
    private final String updateLink;
    private final Boolean banned;
    private final Ban banReason;

    public ClientBoundVersionControlPacket(String correctVersion, Version.VersionStability correct, String updateLink, Boolean banned, Ban banReason) {
        this.correctVersion = correctVersion;
        this.correct = correct;
        this.updateLink = updateLink;
        this.banned = banned;
        this.banReason = banReason;
    }

    public Ban getBanReason() {
        return banReason;
    }

    public Boolean isBanned() {
        return banned;
    }

    public Version.VersionStability getCorrect() {
        return correct;
    }

    public String getCorrectVersion() {
        return correctVersion;
    }

    public String getUpdateLink() {
        return updateLink;
    }

    @Override
    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("version-control-result", correct.toString());
        o.addProperty("correct-version", correctVersion);
        o.addProperty("update-link", updateLink);
        o.addProperty("banned", banned);
        o.add("ban", banReason.toJson());
        o.addProperty("packet-id", bind().getPacketID());
        return o.toString();
    }

    @Override
    public PacketID bind() {
        return PacketID.VERSION_CONTROL_RESULT;
    }

    @Override
    public Packet parse(String json) {
        JsonElement element = new JsonParser().parse(json);
        if (element.isJsonObject()) {
            JsonObject o = element.getAsJsonObject();
            Version.VersionStability correct = Version.VersionStability.valueOf(o.get("version-control-result").getAsString());
            boolean banned = SkyblockUtil.getAsBoolean("banned", o);
            Ban banReason = null;
            if (banned) {
                banReason = Ban.parse(SkyblockUtil.getAsJsonObject("ban", o));
            }
            String correctVersion = SkyblockUtil.getAsString("correct-version", o);
            String updateLink = SkyblockUtil.getAsString("update-link", o);
            return new ClientBoundVersionControlPacket(correctVersion, correct, updateLink, banned, banReason);
        }
        return null;
    }
}
