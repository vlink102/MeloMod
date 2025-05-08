package me.vlink102.melomod.util.http;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S00PacketKeepAlive;

import static me.vlink102.melomod.util.http.ApiUtil.sendLater;

public class CustomPingTracker {
    private final Minecraft mc = Minecraft.getMinecraft();
    private long lastPingSent = -1;

    public void sendPingRequest(ApiUtil.ChatChannel channel) {
        if (mc.thePlayer == null || mc.getNetHandler() == null) return;

        lastPingSent = System.currentTimeMillis();
        mc.getNetHandler().getNetworkManager().sendPacket(
                new C00PacketKeepAlive((int) lastPingSent) // using timestamp as ID
        );
    }

    public void handleIncomingPacket(Packet<?> packet, ApiUtil.ChatChannel channel) {
        if (packet instanceof S00PacketKeepAlive && lastPingSent > 0) {
            long now = System.currentTimeMillis();
            long ping = now - ((S00PacketKeepAlive) packet).func_149134_c();
            sendLater("»»» Ping: " + ping + "ms «««", channel);
            lastPingSent = -1; // reset
        }
    }
}