package me.vlink102.melomod.command.client;

import cc.polyfrost.oneconfig.images.OneImage;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.util.*;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.http.DataThread;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        if (!MainConfiguration.enableTestingCommands) return;
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
    public void testwipe() {
        if (!MainConfiguration.enableTestingCommands) return;
        WipeMe.scheduleWipe(false, true);
        System.out.println("Complete");
    }

    @SubCommand
    public void nbt() {
        if (!MainConfiguration.enableTestingCommands) return;
        String data = ItemSerializer.INSTANCE.serializeToNBT(Minecraft.getMinecraft().thePlayer.getHeldItem());
        StringSelection selection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        System.out.println("Complete");
    }

    @SubCommand
    public void copydata() {
        if (!MainConfiguration.enableTestingCommands) return;
        BufferedImage image = ImageUtils.frameBuffer(Minecraft.getMinecraft().thePlayer.getHeldItem());
        String data = ApiUtil.imgToBase64String(image, "png");
        StringSelection selection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @SubCommand
    public void helpme() {
        if (!MainConfiguration.enableTestingCommands) return;
        System.out.println("=======================================================");
        System.out.println("Beginning render process");
        ImageUtils.helpMe(Minecraft.getMinecraft().thePlayer.getHeldItem());
        System.out.println("=======================================================");
    }

    @SubCommand
    public void render() {
        if (!MainConfiguration.enableTestingCommands) return;
        System.out.println("=======================================================");
        System.out.println("Beginning render process");
        BufferedImage bufferedImage = ImageGeneration.getToolTipImage(
                new ChatComponentText(
                        "Testing renderer..."
                ).appendSibling(new ChatComponentText(
                        "Another line..."
                )),
                true
        );
        System.out.println("Generated: " + bufferedImage + " (" + bufferedImage.getWidth() + "*" + bufferedImage.getHeight() + ")");

        //Minecraft.getMinecraft().fontRendererObj.drawString()
        try {
            //byte[] data = ImageUtils.toArray(bufferedImage);
            //Image image = Toolkit.getDefaultToolkit().createImage(data);
            OneImage oneImage = new OneImage(bufferedImage);
            oneImage.save(MeloMod.createNewRandomUUID("png").getAbsolutePath());
            oneImage.copyToClipboard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("=======================================================");
    }

    @SubCommand
    public void erm() {
        if (!MainConfiguration.enableTestingCommands) return;
        System.out.println("=======================================================");
        System.out.println("Beginning render process");

        ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
        MeloMod.runAsync(() -> BitMapFont.writeFileToDownloads(BitMapFont.getTooltipBackground(stack)));

        System.out.println("=======================================================");
    }

    @SubCommand
    public void whatthehell(@Greedy String text) {
        if (!MainConfiguration.enableTestingCommands) return;
        System.out.println("=======================================================");
        System.out.println("Beginning render process");

        ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
        BitMapFont.writeFileToDownloads(ImageUtils.frameBuffer(stack));

        System.out.println("=======================================================");
    }

    @SubCommand
    public void renderchar() {
        if (!MainConfiguration.enableTestingCommands) return;
        System.out.println("=======================================================");
        System.out.println("Beginning render process");
        String randomChar = RandomStringUtils.randomAlphabetic(1);
        System.out.println("Using random character: " + randomChar);
        BitMapFont fontProvider = MeloMod.getFontProvider(randomChar.substring(0, 1));
        int width = fontProvider.getCharacterWidth(randomChar);
        int height = fontProvider.getHeight();
        BufferedImage bufferedImage = fontProvider.printCharacter(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB),
                randomChar.substring(0, 1),
                0,
                0,
                8,
                0,
                CharacterData.getRandom(),
                getRandomDecorations()).getImage();
        System.out.println("Generated: " + bufferedImage + " (" + bufferedImage.getWidth() + "*" + bufferedImage.getHeight() + ")");

        try {
            //byte[] data = ImageUtils.toArray(bufferedImage);
            //Image image = Toolkit.getDefaultToolkit().createImage(data);
            OneImage oneImage = new OneImage(bufferedImage);
            oneImage.save(MeloMod.createNewRandomUUID("png").getAbsolutePath());
            oneImage.copyToClipboard();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("=======================================================");
    }

    public List<BitMapFont.TextDecoration> getRandomDecorations() {
        Random random = new Random();
        List<BitMapFont.TextDecoration> decorations = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            BitMapFont.TextDecoration decor = BitMapFont.TextDecoration.values()[i];
            if (random.nextBoolean()) decorations.add(decor);
        }
        return decorations;
    }

    @SubCommand
    public void vcp() {
        if (!MainConfiguration.enableTestingCommands) return;
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
