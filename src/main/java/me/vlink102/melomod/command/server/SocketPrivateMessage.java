package me.vlink102.melomod.command.server;


import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.BitMapFont;
import me.vlink102.melomod.util.ImageGeneration;
import me.vlink102.melomod.util.ItemSerializer;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutChat;
import net.minecraft.client.Minecraft;

@Command(
        value = "mprivate",
        description = "Chat with another " + MeloMod.NAME + " user privately.",
        aliases = {
                "mp",
                "pm",
                "melopriv",
                "dm"
        }
)
public class SocketPrivateMessage {
    @Main
    public void handle(String target, @Greedy String message) {
        if (message.contains("<item>")) {
            MeloMod.runAsync(() -> {

            String image = ApiUtil.imgToBase64String(BitMapFont.getTooltipBackground(Minecraft.getMinecraft().thePlayer.getHeldItem()), "png");
            String data = ItemSerializer.INSTANCE.serialize(Minecraft.getMinecraft().thePlayer.getHeldItem());

            CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, target, data, image));
                    });
        } else {
            CommunicationHandler.thread.sendPacket(new PacketPlayOutChat(message, MeloMod.playerUUID.toString(), MeloMod.playerName, target, null, null));
        }

    }
}
