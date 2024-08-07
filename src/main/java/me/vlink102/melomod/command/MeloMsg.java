package me.vlink102.melomod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

@Command(
        value = "mmsg",
        description = "Chat with other " + MeloMod.NAME + " users.",
        aliases = {
                "melomsg",
                "melomodmsg",
                "mm",
        }
)
public class MeloMsg {
    private final MeloMod mod;

    public MeloMsg(MeloMod mod) {
        this.mod = mod;
    }

    @Main
    public void handle(@Greedy String message) {
        if (message.contains("<item>")) {
            String data = ItemSerializer.INSTANCE.serialize(Minecraft.getMinecraft().thePlayer.getHeldItem());
            CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, null, data));
        } else {
            CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, null, null));
        }

    }
}
