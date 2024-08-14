package me.vlink102.melomod.util;

import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.configuration.MainConfiguration;
import me.vlink102.melomod.util.translation.Language;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class ImageGeneration {

    public static final Color TOOLTIP_BACKGROUND_COLOR = new Color(-267386864, true);
    public static final Color TOOLTIP_OUTLINE_TOP_COLOR = new Color(1347420415, true);
    public static final Color TOOLTIP_OUTLINE_BOTTOM_COLOR = new Color(1344798847, true);


    public static BufferedImage getToolTipImage(IChatComponent print, boolean allowLineBreaks) {
        return getToolTipImage(Collections.singletonList(ToolTipComponent.text(print)), allowLineBreaks);
    }

    public static BufferedImage getToolTipImage(List<ToolTipComponent<?>> prints) {
        return getToolTipImage(prints, false);
    }

    public static BufferedImage getToolTipImage(List<ToolTipComponent<?>> prints, boolean allowLineBreaks) {
        if (prints.isEmpty() || !(prints.get(0).getType().equals(ToolTipComponent.ToolTipType.TEXT))) {
            MeloMod.addDebug("ImageGeneration creating tooltip image");
        } else {
            MeloMod.addDebug("ImageGeneration creating tooltip image of " + (prints.get(0).getToolTipComponent(ToolTipComponent.ToolTipType.TEXT)));
        }
        System.out.println("Prints: " + prints);

        int requiredHeight = prints.stream().mapToInt(each -> {
            ToolTipComponent.ToolTipType<?> type = each.getType();
            if (type.equals(ToolTipComponent.ToolTipType.TEXT)) {
                return 20;
            } else if (type.equals(ToolTipComponent.ToolTipType.IMAGE)) {
                return each.getToolTipComponent(ToolTipComponent.ToolTipType.IMAGE).getHeight() + 16;
            } else {
                return 0;
            }
        }).sum() + 415;

        System.out.println("Required Height: " + requiredHeight);

        BufferedImage image = new BufferedImage(2240, requiredHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int topX = image.getWidth() / 5 * 2;
        int maxX = 0;
        int currentY = 208;
        for (ToolTipComponent<?> print : prints) {
            System.out.println("Printing Component: " + print);
            ToolTipComponent.ToolTipType<?> type = print.getType();
            if (type == ToolTipComponent.ToolTipType.TEXT) {
                System.out.println("Component is text: " + type.getTypeClass());
                ImageUtils.ComponentPrintResult printResult = ImageUtils.printComponent(image,
                        print.getToolTipComponent(ToolTipComponent.ToolTipType.TEXT),
                        Language.getById(MainConfiguration.language),
                        true,
                        topX + 8,
                        currentY,
                        16);
                System.out.println("Print Result: " + printResult);
                int textWidth = printResult.getTextWidth();
                if (textWidth > maxX) {
                    maxX = textWidth;
                }
                currentY += 20;
            } else if (type == ToolTipComponent.ToolTipType.IMAGE) {
                currentY += 5;
                BufferedImage componentImage = print.getToolTipComponent(ToolTipComponent.ToolTipType.IMAGE);
                g.drawImage(componentImage, topX + 8, currentY, null);
                if (componentImage.getWidth() > maxX) {
                    maxX = componentImage.getWidth();
                }
                currentY += componentImage.getHeight() + 11;
            }
        }
        g.dispose();
        maxX += 14;

        int firstX = 0;
        outer:
        for (int x = 0; x < image.getWidth() - 9; x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) != 0) {
                    firstX = x;
                    break outer;
                }
            }
        }
        int lastX = 0;
        for (int x = firstX; x < image.getWidth() - 9; x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) != 0) {
                    lastX = x;
                    break;
                }
            }
        }
        firstX = Math.max(0, firstX - 8);

        int firstY = 0;
        outer:
        for (int y = 0; y < image.getHeight() - 9; y++) {
            for (int x = firstX; x <= lastX; x++) {
                if (image.getRGB(x, y) != 0) {
                    firstY = y;
                    break outer;
                }
            }
        }
        int lastY = 0;
        for (int y = firstY; y < image.getHeight() - 9; y++) {
            for (int x = firstX; x <= lastX; x++) {
                if (image.getRGB(x, y) != 0) {
                    lastY = y;
                    break;
                }
            }
        }
        firstY = Math.max(0, firstY - 8);
        System.out.println("Drawing Background...");
        BufferedImage background = new BufferedImage(maxX + 4, currentY - 196, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = background.createGraphics();
        g2.setColor(TOOLTIP_BACKGROUND_COLOR);
        g2.fillRect(2, 0, background.getWidth() - 4, background.getHeight());
        g2.fillRect(0, 2, 2, background.getHeight() - 4);
        g2.fillRect(background.getWidth() - 2, 2, 2, background.getHeight() - 4);
        g2.setColor(TOOLTIP_OUTLINE_TOP_COLOR);
        g2.fillRect(4, 2, background.getWidth() - 8, 2);
        GradientPaint gradientPaint = new GradientPaint(0, 0, TOOLTIP_OUTLINE_TOP_COLOR, 0, background.getHeight() - 4, TOOLTIP_OUTLINE_BOTTOM_COLOR);
        g2.setPaint(gradientPaint);
        g2.fillRect(2, 2, 2, background.getHeight() - 4);
        g2.fillRect(background.getWidth() - 4, 2, 2, background.getHeight() - 4);
        g2.setColor(TOOLTIP_OUTLINE_BOTTOM_COLOR);
        g2.fillRect(4, background.getHeight() - 4, background.getWidth() - 8, 2);
        g2.dispose();

        int offsetX = Math.max(topX - firstX, 0);
        BufferedImage output = new BufferedImage(offsetX + lastX - topX + 9, lastY - firstY + 7, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3 = output.createGraphics();
        g3.drawImage(background, offsetX, 201 - firstY, null);
        g3.drawImage(image, -firstX, -firstY, null);
        g3.dispose();
        return output;
    }

}
