package me.vlink102.melomod.command.server;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.BitMapFont;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutChat;
import net.minecraft.client.Minecraft;

@Command(
        value = "mmsg",
        description = "Chat with other " + MeloMod.NAME + " users.",
        aliases = {
                "melomsg",
                "melomodmsg",
                "mm",
        }
)
public class SocketMessage {
    private final MeloMod mod;

    public SocketMessage(MeloMod mod) {
        this.mod = mod;
    }

    @Main
    public void handle(@Greedy String message) {
        if (message.contains("<item>")) {
            String image = ApiUtil.imgToBase64String(BitMapFont.getTooltipBackground(Minecraft.getMinecraft().thePlayer.getHeldItem()), "png");
            String data = ItemSerializer.INSTANCE.serialize(Minecraft.getMinecraft().thePlayer.getHeldItem());
            CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, null, data, image));
        } else {
            CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, null, null, null));
        }

    }
}
