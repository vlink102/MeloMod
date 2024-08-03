package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.WorldLoadEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutDisconnect;
import me.vlink102.melomod.util.http.packets.ServerBoundLocrawPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PlayerConnection {

    public PlayerConnection() {
        this.online = false;
        EventManager.INSTANCE.register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean online;

    @Subscribe
    public void onJoin(WorldLoadEvent event) {
        online = true;
        MeloMod.addDebug("§eSuccessfully joined world: §7" + event.toString());
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.schedule(() -> {
            if (!HypixelUtils.INSTANCE.isHypixel()) {
                ServerBoundLocrawPacket packet = new ServerBoundLocrawPacket(null, null, null, null, InternalLocraw.getType(), InternalLocraw.serverIP());
                CommunicationHandler.thread.sendPacket(packet);
                ServerTracker.isHypixel = false;
            } else {
                ServerTracker.isHypixel = true;
                MeloMod.addDebug("§eServer instance detected as Hypixel");
            }
            if (!MeloMod.queue.isEmpty()) {
                List<String> currentQueue = new ArrayList<>(MeloMod.queue);
                List<String> worked = new ArrayList<>();
                for (String s : currentQueue) {
                    if (MeloMod.addMessage(s)) {
                        worked.add(s);
                    }
                }
                MeloMod.queue.removeAll(worked);
                int left = currentQueue.size() - worked.size();
                if (left > 0) {
                    MeloMod.addWarn("§6Failed to sync §e" + left + "§6 queued chat(s). Retrying...");
                }
            }
        }, 2L, TimeUnit.SECONDS);

    }

    @SubscribeEvent
    public void onQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        online = false;
        ServerTracker.isHypixel = false;
        MeloMod.addDebug("§eSuccessfully disconnected from server");
        PacketPlayOutDisconnect disconnect = new PacketPlayOutDisconnect(MeloMod.playerUUID.toString(), MeloMod.playerName);
        CommunicationHandler.thread.sendPacket(disconnect);
    }

}
