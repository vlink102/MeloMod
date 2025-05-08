package me.vlink102.melomod.util.game;

import io.netty.channel.ChannelFutureListener;
import me.vlink102.melomod.configuration.ChatConfiguration;
import me.vlink102.melomod.util.http.ApiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PingOverlay {
    private static final double ALPHA = 1 / 3.0;
    private static Double averageTps = null;
    private static int tpsCount = 0;
    private static Long lastTpsSample = null;

    private static Double averagePing = null;
    private static int pingCount = 0;
    private static Long pingStartTime = null;

    private static int ticks = 0;
    private static boolean isPinging = false;
    private static boolean chatNextPing = false;

    private static boolean tps = false;

    private static ApiUtil.ChatChannel channel = null;

    @SubscribeEvent
    public void onPacketReceived(ReceivePacketEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            long currentTime = System.currentTimeMillis();
            if (lastTpsSample != null) {
                long time = currentTime - lastTpsSample;
                double instantTps = Math.min(20.0, 20000.0 / time);
                tpsCount++;
                averageTps = instantTps * ALPHA + (averageTps != null ? averageTps : instantTps) * (1 - ALPHA);
                if (chatNextPing && channel != null && tps && ChatConfiguration.instantTPS) {
                    ApiUtil.sendLater("»»» Current TPS: " + String.format("%.1f", instantTps) + " «««", channel);
                    channel = null;
                    chatNextPing = false;
                }
            }
            lastTpsSample = currentTime;
        } else if (isPinging && event.getPacket() instanceof S37PacketStatistics) {
            isPinging = false;
            if (pingStartTime != null) {
                double instantPing = (System.nanoTime() - pingStartTime) / 1e6;
                pingCount++;
                averagePing = instantPing * ALPHA + (averagePing != null ? averagePing : instantPing) * (1 - ALPHA);
                if (chatNextPing && channel != null && !tps && ChatConfiguration.instantPing) {
                    ApiUtil.sendLater("»»» Current Ping: " + String.format("%.1f", instantPing) + "ms «««", channel);
                    channel = null;
                    chatNextPing = false;
                }
            }
            if (chatNextPing && channel != null) {
                if (tps) {
                    ApiUtil.sendLater("»»» Average TPS: " + getTps() + " «««", channel);
                } else {
                    ApiUtil.sendLater("»»» Average Ping: " + getPing() + "ms «««", channel);
                }
                channel = null;
                chatNextPing = false;
            }
        } else if (event.getPacket() instanceof S01PacketJoinGame) {
            reset();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        reset();
    }

    private void reset() {
        isPinging = false;
        tpsCount = 0;
        averageTps = null;
        pingCount = 0;
        averagePing = null;
    }

    private String getTps() {
        return averageTps != null ? String.format("%.1f", averageTps) : "???";
    }

    private String getPing() {
        return averagePing != null ? String.format("%.1f", averagePing) : "???";
    }

    public static void sendPing(boolean isFromCommand, ApiUtil.ChatChannel channel, boolean tps) {
        PingOverlay.tps = tps;
        PingOverlay.channel = channel;
        if (isFromCommand) chatNextPing = true;
        if (!isPinging) {
            isPinging = true;
            Minecraft.getMinecraft().thePlayer.sendQueue.getNetworkManager().sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS), (ChannelFutureListener) future -> {
                pingStartTime = System.nanoTime();
            });
        }
    }

    private boolean shouldPing() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.theWorld != null ;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!shouldPing() || event.phase != TickEvent.Phase.START) return;
        if (ticks % 40 == 0) {
            sendPing(false, null, false);
        }
        ticks++;
    }

    private String colorizeTps(double tps) {
        if (tps > 19) return "§a";
        if (tps > 18) return "§2";
        if (tps > 17) return "§e";
        if (tps > 15) return "§6";
        return "§c";
    }

    private String colorizePing(double ping) {
        if (ping < 50) return "§a";
        if (ping < 100) return "§2";
        if (ping < 150) return "§e";
        if (ping < 250) return "§6";
        return "§c";
    }
}