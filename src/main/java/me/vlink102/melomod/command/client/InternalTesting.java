package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.images.OneImage;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.RenderManager;
import cc.polyfrost.oneconfig.renderer.TextRenderer;
import cc.polyfrost.oneconfig.renderer.asset.AssetHelper;
import cc.polyfrost.oneconfig.renderer.asset.Icon;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.ImageGeneration;
import me.vlink102.melomod.util.ImageUtils;
import me.vlink102.melomod.util.MinecraftFont;
import me.vlink102.melomod.util.VChatComponent;
import me.vlink102.melomod.util.http.DataThread;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
    public void render() {
        System.out.println("=======================================================");
        System.out.println("Beginning render process");
        BufferedImage bufferedImage = ImageGeneration.getToolTipImage(
                new ChatComponentText(
                        "Testing renderer..."
                ),
                true
        );
        System.out.println("Generated: " + bufferedImage);

        try {
            byte[] data = ImageUtils.toArray(bufferedImage);
            Image image = Toolkit.getDefaultToolkit().createImage(data);
            OneImage oneImage = new OneImage(bufferedImage);
            oneImage.save(MeloMod.createNewRandomUUID("png").getAbsolutePath());
            oneImage.copyToClipboard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
