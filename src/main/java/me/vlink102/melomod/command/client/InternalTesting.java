package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.http.DataThread;
import net.minecraft.client.Minecraft;

@Command(
        value = "melotest",
        description = "Test " + MeloMod.NAME + " features (dev usage).",
        aliases = {
                "mtest",
                "mmtest",
                "testfeature"
        }
)
public class InternalTesting {
    @Main
    public void handle() {
        System.out.println("=======================================================");
        System.out.println("Client Version: " + MeloMod.VERSION);
        System.out.println("Server Version: " + MeloMod.serverVersion);
        System.out.println("Version Compatibility: " + MeloMod.compatibility);
        System.out.println();
        System.out.println("Client User: " + Minecraft.getMinecraft().thePlayer.getName());
        System.out.println("Socket Connected: " + DataThread.closed);
        System.out.println("=======================================================");
    }

    @SubCommand
    public void vcp() {
        System.out.println("=======================================================");
        System.out.println("Testing empty components...");
        for (MeloMod.MessageScheme value : MeloMod.MessageScheme.values()) {
            System.out.println(value + " Component: " + new VChatComponent(value));
        }
        System.out.println("Testing filled components...");
        for (MeloMod.MessageScheme value : MeloMod.MessageScheme.values()) {
            System.out.println(value + " Component: " + new VChatComponent(value).add("Example Value"));
        }
        System.out.println("=======================================================");
    }
}
