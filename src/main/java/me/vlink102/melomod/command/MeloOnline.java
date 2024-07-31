package me.vlink102.melomod.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.http.CommunicationHandler;
import me.vlink102.melomod.util.http.packets.PacketPlayOutChat;
import me.vlink102.melomod.util.http.packets.ServerBoundRequestConnectionsPacket;

@Command(
        value = "mlist",
        description = "List other " + MeloMod.NAME + " users.",
        aliases = {
                "mmlist",
                "vlist",
                "zlist",
                "melolist",
                "melomodlist",
                "monline",
                "mf",
                "mo"
        }
)
public class MeloOnline {
    @Main
    public void handle(int page) {
        CommunicationHandler.thread.sendPacket(new ServerBoundRequestConnectionsPacket(page));
    }
}
