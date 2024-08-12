package me.vlink102.melomod.events;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.WorldLoadEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutDisconnect;
import me.vlink102.melomod.util.http.packets.ServerBoundLocrawPacket;
import me.vlink102.melomod.util.translation.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler {

    public ConnectionHandler() {
        online = false;
    }

    public static boolean online;

    @SubscribeEvent()
    public void onJoin(EntityJoinWorldEvent event) {
        Entity entity = event.entity;
        if (entity == Minecraft.getMinecraft().thePlayer) {
            online = true;
            MeloMod.addDebug("&e" + Feature.GENERIC_DEBUG_JOINED_WORLD + ": &7" + event.toString());
            MeloMod.INSTANCE.getNewScheduler().scheduleDelayedTask(new SkyblockRunnable() {
                @Override
                public void run() {
                    if (!HypixelUtils.INSTANCE.isHypixel()) {
                        ServerBoundLocrawPacket packet = new ServerBoundLocrawPacket(null, null, null, null, LocrawHandler.getType(), LocrawHandler.serverIP());
                        CommunicationHandler.thread.sendPacket(packet);
                        ServerTracker.isHypixel = false;
                    } else {
                        ServerTracker.isHypixel = true;
                        MeloMod.addDebug("&e" + Feature.GENERIC_DEBUG_HYPIXEL_DETECTED + "&r");
                    }
                    if (!MeloMod.queue.isEmpty()) {
                        List<VChatComponent> currentQueue = new ArrayList<>(MeloMod.queue);
                        List<VChatComponent> worked = new ArrayList<>();
                        for (VChatComponent s : currentQueue) {
                            if (MeloMod.addMessage(s)) {
                                worked.add(s);
                            }
                        }
                        MeloMod.queue.removeAll(worked);
                        int left = currentQueue.size() - worked.size();
                        if (left > 0) {
                            String languageValue = Feature.GENERIC_DEBUG_FAILED_SYNC.getMessage();
                            String[] splitLanguage = languageValue.split("\\{int}");
                            MeloMod.addWarn("&6" + splitLanguage[0] + "&e" + left + "&6" + splitLanguage[1] + "&r");
                        }
                    }
                }
            }, 2 * 20);
        }


    }


}
