package me.vlink102.melomod.events;

import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.chatcooldownmanager.ServerTracker;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutDisconnect;
import me.vlink102.melomod.util.translation.Feature;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class PlayerDisconnect {

    @SubscribeEvent
    public void onQuit(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ConnectionHandler.online = false;
        ServerTracker.isHypixel = false;
        MeloMod.addDebug("&e" + Feature.GENERIC_DEBUG_DISCONNECT + "&r");
        if (CommunicationHandler.thread != null) {
            PacketPlayOutDisconnect disconnect = new PacketPlayOutDisconnect(MeloMod.playerUUID.toString(), MeloMod.playerName);
            CommunicationHandler.thread.sendPacket(disconnect);
        }
    }
}
