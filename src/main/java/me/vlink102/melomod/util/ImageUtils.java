package me.vlink102.melomod.util;

import it.unimi.dsi.fastutil.chars.CharObjectPair;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.translation.Language;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {
    public static final Color TEXT_BACKGROUND_COLOR = new Color(0, 0, 0, 180);
    public static final double CHAT_COLOR_BACKGROUND_FACTOR = 0.19;
    private static final double[] GAUSSIAN_CONSTANTS = new double[]{
            0.00598, 0.060626, 0.241843, 0.383103, 0.241843, 0.060626, 0.00598
    };

    public static byte[] toArray(BufferedImage image) throws IOException {
        return toOutputStream(image).toByteArray();
    }

    public static BufferedImage changeColorTo(BufferedImage image, Color color) {
        return changeColorTo(image, color.getRGB());
    }

    public static BufferedImage changeColorTo(BufferedImage image, int color) {
        color = color & 0x00FFFFFF;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int newColor = color | (colorValue & 0xFF000000);
                image.setRGB(x, y, newColor);
            }
        }
        return image;
    }

    public static ByteArrayOutputStream toOutputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream;
    }

    public static BufferedImage expandCenterAligned(BufferedImage image, int up, int down, int left, int right) {
        BufferedImage b = new BufferedImage(image.getWidth() + left + right, image.getHeight() + up + down, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(image, left, up, null);
        g.dispose();
        return b;
    }

    public static BufferedImage resizeImageFillHeight(BufferedImage source, int height) {
        int width = (int) Math.round(source.getWidth() * ((double) height / (double) source.getHeight()));
        return resizeImageAbs(source, width, height);
    }

    public static BufferedImage resizeImageAbs(BufferedImage source, int width, int height) {
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return b;
    }

    public static ComponentPrintResult printComponent(BufferedImage image, String raw, Language language, boolean legacyRGB, int topX, int topY, float fontSize) {
        return printComponent(image, raw, language, legacyRGB, topX, topY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);
    }

    public static ComponentPrintResult printComponent(BufferedImage image, String raw, Language language, boolean legacyRGB, int topX, int topY, float fontSize, double shadowFactor) {
        BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ComponentPrintResult printResult = printComponent0(temp, raw, language, legacyRGB, topX, topY, fontSize, 1);
        temp = printResult.getImage();
        Graphics2D g = image.createGraphics();
        if (shadowFactor != 0) {
            BufferedImage shadow = multiply(copyImage(temp), shadowFactor);
            g.drawImage(shadow, (int) (fontSize / 8f), (int) (fontSize / 8f), null);
        }
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        return new ComponentPrintResult(image, printResult.getTextWidth());
    }

    private static ComponentPrintResult printComponent0(BufferedImage image, String raw, Language language, boolean legacyRGB, int topX, int topY, float fontSize, double factor) {

        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);

        List<CharObjectPair<CharacterData>> data = I18nUtils.bidirectionalReorder(raw, language.isBidirectionalReorder());

        int x = topX;
        int lastItalicExtraWidth = 0;
        int lastSpaceWidth = 0;
        StringBuilder character = null;
        for (int i = 0; i < data.size(); i++) {
            CharObjectPair<CharacterData> pair = data.get(i);
            char c = pair.firstChar();
            if (character == null) {
                character = new StringBuilder(String.valueOf(c));
                if (Character.isHighSurrogate(c)) {
                    continue;
                } else if (Character.isLowSurrogate(c) && i + 1 < data.size()) {
                    character.insert(0, data.get(++i).firstChar());
                }
            } else {
                character.append(c);
            }
            CharacterData characterData = pair.right();
            BitMapFont font = MeloMod.getFontProvider(String.valueOf(c));
            BitMapFont.FontRenderResult result = font.printCharacter(textImage, character.toString(), x, 1 + image.getHeight(), fontSize, lastItalicExtraWidth, characterData.getColor(), characterData.getDecorations());
            textImage = result.getImage();
            x += result.getWidth() + (lastSpaceWidth = result.getSpaceWidth());
            lastItalicExtraWidth = result.getItalicExtraWidth();
            character = null;
        }
        Graphics2D g = image.createGraphics();
        g.drawImage(textImage, 0, topY - image.getHeight(), null);
        g.dispose();
        return new ComponentPrintResult(image, x - lastSpaceWidth - topX);
    }

    public static class ComponentPrintResult {

        private final BufferedImage image;
        private final int textWidth;

        public ComponentPrintResult(BufferedImage image, int textWidth) {
            this.image = image;
            this.textWidth = textWidth;
        }


        public BufferedImage getImage() {
            return image;
        }

        public int getTextWidth() {
            return textWidth;
        }
    }

    public static ComponentPrintResult printComponent(BufferedImage image, IChatComponent component, Language language, boolean legacyRGB, int topX, int topY, float fontSize) {
        return printComponent(image, component, language, legacyRGB, topX, topY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);
    }

    public static ComponentPrintResult printComponent(BufferedImage image, IChatComponent component, Language language, boolean legacyRGB, int topX, int topY, float fontSize, double shadowFactor) {
        BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ComponentPrintResult printResult = printComponent0(temp, component, language, legacyRGB, topX, topY, fontSize, 1);
        temp = printResult.getImage();
        Graphics2D g = image.createGraphics();
        if (shadowFactor != 0) {
            BufferedImage shadow = multiply(copyImage(temp), shadowFactor);
            g.drawImage(shadow, (int) (fontSize * 0.15), (int) (fontSize * 0.15), null);
        }
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        return new ComponentPrintResult(image, printResult.getTextWidth());
    }

    private static ComponentPrintResult printComponent0(BufferedImage image, IChatComponent component, Language language, boolean legacyRGB, int topX, int topY, float fontSize, double factor) {
        IChatComponent text = ComponentFlattening.flatten(component);

        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);

        List<CharObjectPair<CharacterData>> data = I18nUtils.bidirectionalReorder(text, language.isBidirectionalReorder());

        int x = topX;
        int lastItalicExtraWidth = 0;
        int lastSpaceWidth = 0;
        StringBuilder character = null;
        for (int i = 0; i < data.size(); i++) {
            CharObjectPair<CharacterData> pair = data.get(i);
            char c = pair.firstChar();
            if (character == null) {
                character = new StringBuilder(String.valueOf(c));
                if (Character.isHighSurrogate(c)) {
                    continue;
                } else if (Character.isLowSurrogate(c) && i + 1 < data.size()) {
                    character.insert(0, data.get(++i).firstChar());
                }
            } else {
                character.append(c);
            }
            CharacterData characterData = pair.right();
            BitMapFont.FontRenderResult result = MeloMod.getFontProvider(Character.toString(c)).printCharacter(textImage, character.toString(), x, 1 + image.getHeight(), fontSize, lastItalicExtraWidth, characterData.getColor(), characterData.getDecorations());
            textImage = result.getImage();
            x += result.getWidth() + (lastSpaceWidth = result.getSpaceWidth());
            lastItalicExtraWidth = result.getItalicExtraWidth();
            character = null;
        }
        Graphics2D g = image.createGraphics();
        g.drawImage(textImage, 0, topY - image.getHeight(), null);
        g.dispose();
        return new ComponentPrintResult(image, x - lastSpaceWidth - topX);
    }

    public static BufferedImage flipVertically(BufferedImage image) {
        BufferedImage b = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(image, 0, image.getHeight(), image.getWidth(), -image.getHeight(), null);
        g.dispose();
        return b;
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage multiply(BufferedImage image, double value) {
        return multiply(image, value, value, value);
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop) {
        return multiply(image, imageOnTop, false);
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop, boolean ignoreTransparent) {
        for (int y = 0; y < image.getHeight() && y < imageOnTop.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() && x < imageOnTop.getWidth(); x++) {
                int value = image.getRGB(x, y);
                int multiplyValue = imageOnTop.getRGB(x, y);
                if (!ignoreTransparent || getAlpha(multiplyValue) > 0) {
                    int red = (int) Math.round((double) getRed(value) / 255 * (double) getRed(multiplyValue));
                    int green = (int) Math.round((double) getGreen(value) / 255 * (double) getGreen(multiplyValue));
                    int blue = (int) Math.round((double) getBlue(value) / 255 * (double) getBlue(multiplyValue));
                    int color = getIntFromColor(red, green, blue, getAlpha(value));
                    image.setRGB(x, y, color);
                }
            }
        }

        return image;
    }

    public static BufferedImage multiply(BufferedImage image, double xValue, double yValue, double zValue) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int alpha = getAlpha(colorValue);
                if (alpha != 0) {
                    int red = (int) (getRed(colorValue) * xValue);
                    int green = (int) (getGreen(colorValue) * yValue);
                    int blue = (int) (getBlue(colorValue) * zValue);
                    int color = getIntFromColor(red < 0 ? 0 : (Math.min(red, 255)), green < 0 ? 0 : (Math.min(green, 255)), blue < 0 ? 0 : (Math.min(blue, 255)), alpha);
                    image.setRGB(x, y, color);
                }
            }
        }
        return image;
    }

    public static int getRed(int color) {
        return color >> 16 & 255;
    }

    public static int getGreen(int color) {
        return color >> 8 & 255;
    }

    public static int getBlue(int color) {
        return color & 255;
    }

    public static int getAlpha(int color) {
        return color >> 24 & 255;
    }

    public static int getIntFromColor(int r, int g, int b, int a) {
        int red = r << 16 & 16711680;
        int green = g << 8 & '\uff00';
        int blue = b & 255;
        int alpha = a << 24 & -16777216;
        return alpha | red | green | blue;
    }

    public static int getLargestLine(ItemStack stack) {
        int maxLength = 0;
        for (String s : stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips)) {
            int length = Minecraft.getMinecraft().fontRendererObj.getStringWidth(s);
            if (length > maxLength) {
                maxLength = length;
            }
        }
        return maxLength;
    }

    public static void helpMe(ItemStack stack) {
        final int width = Minecraft.getMinecraft().getFramebuffer().framebufferWidth;
        final int height = Minecraft.getMinecraft().getFramebuffer().framebufferHeight;
        Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        FontRenderer renderer = stack.getItem().getFontRenderer(stack);
        renderer = renderer == null ? Minecraft.getMinecraft().fontRendererObj : renderer;
        TooltipDimension maximumBounds = getBounds(
                stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips),
                renderer,
                width,
                height,
                0,
                0,
                256
        );
        Framebuffer buffer = new Framebuffer(maximumBounds.width, maximumBounds.height, true);

        buffer.bindFramebuffer(true);
        GlStateManager.clearColor(0, 0, 0, 0);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

        GlStateManager.pushMatrix();
        renderToolTip(stack, maximumBounds.width, maximumBounds.height, renderer, maximumBounds.width, maximumBounds.height);
        GlStateManager.popMatrix();

        IntBuffer pixels = BufferUtils.createIntBuffer(maximumBounds.width * maximumBounds.height);
        GlStateManager.bindTexture(buffer.framebufferTexture);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);
        int[] vals = new int[maximumBounds.width * maximumBounds.height];
        pixels.get(vals);

        TextureUtil.processPixelValues(vals, maximumBounds.width, maximumBounds.height);
        BufferedImage image = new BufferedImage(maximumBounds.width, maximumBounds.height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, maximumBounds.width, maximumBounds.height, vals, 0, maximumBounds.width);

        File f = new File(Minecraft.getMinecraft().mcDataDir, "result.png");
        try {
            f.createNewFile();
            ImageIO.write(image, "png", f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        buffer.deleteFramebuffer();

        if (fbo != null) {
            fbo.bindFramebuffer(true);
        } else {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            GL11.glViewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        }
    }

    public static BufferedImage frameBuffer(ItemStack stack) {

        FontRenderer fontRenderer = stack.getItem().getFontRenderer(stack);
        fontRenderer = fontRenderer == null ? Minecraft.getMinecraft().fontRendererObj : fontRenderer;
        TooltipDimension maximumBounds = getBounds(
                stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips),
                fontRenderer,
                4096,
                4096,
                0,
                0,
                256
        );

        Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        Framebuffer framebuffer = new Framebuffer(maximumBounds.width, maximumBounds.height, true);

        framebuffer.bindFramebuffer(true);
        int maxWidth = maximumBounds.width;
        int maxHeight = maximumBounds.height;


        Color background = ImageGeneration.TOOLTIP_BACKGROUND_COLOR;

        GlStateManager.clearColor(0, 0, 0, 0);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        //Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(text, 100, 100, getIntFromColor(white.getRed(), white.getGreen(), white.getBlue(), white.getAlpha()));

        System.out.println("Mouse X: " + Mouse.getX() + "(" + Mouse.getDX() + ") Mouse Y: " + Mouse.getY() + "(" + Mouse.getDY() + ")");
        renderToolTip(stack, 0, 0, fontRenderer, maxWidth, maxHeight);

        //FloatBuffer pixels = BufferUtils.createFloatBuffer(maxWidth * maxHeight * 3);

        IntBuffer pixels = BufferUtils.createIntBuffer(maxWidth * maxHeight);
        //GlStateManager.bindTexture(framebuffer.framebufferTexture);
        //GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixels);

        //GL11.glReadPixels(0, 0, maxWidth, maxHeight, GL11.GL_RGB, GL11.GL_FLOAT, pixels);

        //pixels.rewind();
/*
        int[] rgbArray = new int[maxWidth * maxHeight];
        for(int y = 0; y < maxHeight; ++y) {
            for(int x = 0; x < maxWidth; ++x) {
                int r = (int)(pixels.get() * 255) << 16;
                int g = (int)(pixels.get() * 255) << 8;
                int b = (int)(pixels.get() * 255);
                int i = ((maxHeight - 1) - y) * maxWidth + x;
                rgbArray[i] = r + g + b;
            }
        }*/

        GlStateManager.bindTexture(framebuffer.framebufferTexture);

        GL11.glGetTexImage(
                GL11.GL_TEXTURE_2D,
                0,
                GL12.GL_BGRA,
                GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                pixels
        );

        int[] vals = new int[maxWidth * maxHeight];
        pixels.get(vals);

        TextureUtil.processPixelValues(vals, maxWidth, maxHeight);

        //TextureUtil.processPixelValues(vals, maxWidth, maxHeight);
        BufferedImage bufferedImage = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);

        bufferedImage.setRGB(0, 0, maxWidth, maxHeight, vals, 0, maxWidth);

        framebuffer.deleteFramebuffer();
        if (fbo != null) {
            fbo.bindFramebuffer(true);
        } else {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            GL11.glViewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        }
        return bufferedImage;
    }

    public static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    protected static void renderToolTip(ItemStack stack, int x, int y, FontRenderer screen, int maxwidth, int maxheight) {
        List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i) {
            if (i == 0) {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            } else {
                list.set(i, EnumChatFormatting.GRAY + list.get(i));
            }
        }

        drawHoveringText(list, screen, maxwidth, maxheight, x, y);
    }

    public static TooltipDimension getBounds(List<String> textLines, FontRenderer font, int width, int height, int x, int y, int maxTextWidth) {
        int tooltipTextWidth = 0;

        for (String textLine : textLines) {
            int textLineWidth = font.getStringWidth(textLine);

            if (textLineWidth > tooltipTextWidth) {
                tooltipTextWidth = textLineWidth;
            }
        }

        boolean needsWrap = false;

        int titleLinesCount = 1;
        int tooltipX = x + 12;
        if (tooltipX + tooltipTextWidth + 4 > width) {
            tooltipX = x - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (x > width / 2) {
                    tooltipTextWidth = x - 12 - 8;
                } else {
                    tooltipTextWidth = width - 16 - x;
                }
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<String>();
            for (int i = 0; i < textLines.size(); i++) {
                String textLine = textLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                if (i == 0) {
                    titleLinesCount = wrappedLine.size();
                }

                for (String line : wrappedLine) {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth) {
                        wrappedTooltipWidth = lineWidth;
                    }
                    wrappedTextLines.add(line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (x > width / 2) {
                tooltipX = x - 16 - tooltipTextWidth;
            } else {
                tooltipX = x + 12;
            }
        }

        int tooltipY = y - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount) {
                tooltipHeight += 2; // gap between title lines and next lines
            }
        }

        if (tooltipY + tooltipHeight + 6 > height) {
            tooltipY = height - tooltipHeight - 6;
        }

        return new TooltipDimension(tooltipTextWidth, tooltipHeight, tooltipX, tooltipY);
    }

    protected static void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font, int maxwidth, int maxheight) {
        drawHoveringText(textLines, x, y, maxwidth, maxheight, -1, font);
    }

    public static void drawHoveringText(List<String> textLines, FontRenderer font, int width, int height, int x, int y) {
        int maxTextWidth = -1;
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = x + 12;
            if (tooltipX + tooltipTextWidth + 4 > width) {
                tooltipX = x - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (x > width / 2) {
                        tooltipTextWidth = x - 12 - 8;
                    } else {
                        tooltipTextWidth = width - 16 - x;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (x > width / 2) {
                    tooltipX = x - 16 - tooltipTextWidth;
                } else {
                    tooltipX = x + 12;
                }
            }

            int tooltipY = y - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY + tooltipHeight + 6 > height) {
                tooltipY = height - tooltipHeight - 6;
            }

            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldrenderer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        worldrenderer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        worldrenderer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawHoveringText(List<String> textLines, final int mouseX, final int mouseY, final int screenWidth, final int screenHeight, final int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            TooltipDimension dimension = theoreticalMaximum(textLines, font);

            int tooltipX = 4;
            int tooltipY = 4;
            int tooltipTextWidth = dimension.width;
            int tooltipHeight = dimension.height;
            int titleLinesCount = 1;
            final int zLevel = 300;
            final int backgroundColor = 0xF0100010;
            drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            final int borderColorStart = 0x505000FF;
            final int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        double zLevel = 300;
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float g = (float) (startColor >> 16 & 255) / 255.0F;
        float h = (float) (startColor >> 8 & 255) / 255.0F;
        float i = (float) (startColor & 255) / 255.0F;
        float j = (float) (endColor >> 24 & 255) / 255.0F;
        float k = (float) (endColor >> 16 & 255) / 255.0F;
        float l = (float) (endColor >> 8 & 255) / 255.0F;
        float m = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(right, top, zLevel).color(g, h, i, f).endVertex();
        worldRenderer.pos(left, top, zLevel).color(g, h, i, f).endVertex();
        worldRenderer.pos(left, bottom, zLevel).color(k, l, m, j).endVertex();
        worldRenderer.pos(right, bottom, zLevel).color(k, l, m, j).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Getter
    public static class TooltipDimension {
        private final int width;
        private final int height;
        private final int toolx;
        private final int tooly;

        public TooltipDimension(int width, int height, int toolx, int tooly) {
            this.width = width;
            this.height = height;
            this.toolx = toolx;
            this.tooly = tooly;
        }

        @Override
        public String toString() {
            return "TooltipDimension{" +
                    "width=" + width +
                    ", height=" + height +
                    ", toolx=" + toolx +
                    ", tooly=" + tooly +
                    '}';
        }
    }

    public static TooltipDimension theoreticalMaximum(List<String> list, FontRenderer font) {
        font = font == null ? Minecraft.getMinecraft().fontRendererObj : font;
        int textWidth = 0;
        for (String s : list) {
            int lineWidth = font.getStringWidth(s);
            if (lineWidth > textWidth) {
                textWidth = lineWidth;
            }
        }
        int textHeight = 0;
        if (list.size() > 1) {
            textHeight += (list.size() - 1) * 10;
            textHeight += 2; // gap between title lines and next lines
        }
        return new TooltipDimension(textWidth + 7, textHeight + 7, 12, 12);
    }

    public static TooltipDimension calculateTooltipDimensions(List<String> textLines, FontRenderer font, int mouseX, int mouseY, int maxTextWidth) {
        int tooltipTextWidth = 0;

        for (String textLine : textLines) {
            int textLineWidth = font.getStringWidth(textLine);

            if (textLineWidth > tooltipTextWidth) {
                tooltipTextWidth = textLineWidth;
            }
        }

        boolean needsWrap = false;

        int titleLinesCount = 1;
        int tooltipX = mouseX + 12;
  /*      if (tooltipX + tooltipTextWidth + 4 > screenWidth)
        {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                {
                    tooltipTextWidth = mouseX - 12 - 8;
                }
                else
                {
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                }
                needsWrap = true;
            }
        }*/

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }
/*
        if (needsWrap)
        {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<String>();
            for (int i = 0; i < textLines.size(); i++)
            {
                String textLine = textLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                if (i == 0)
                {
                    titleLinesCount = wrappedLine.size();
                }

                for (String line : wrappedLine)
                {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth)
                    {
                        wrappedTooltipWidth = lineWidth;
                    }
                    wrappedTextLines.add(line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (mouseX > screenWidth / 2)
            {
                tooltipX = mouseX - 16 - tooltipTextWidth;
            }
            else
            {
                tooltipX = mouseX + 12;
            }
        }*/

        int tooltipY = mouseY - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount) {
                tooltipHeight += 2; // gap between title lines and next lines
            }
        }
/*
        if (tooltipY + tooltipHeight + 6 > screenHeight)
        {
            tooltipY = screenHeight - tooltipHeight - 6;
        }*/
        return new TooltipDimension(tooltipTextWidth + 7, tooltipHeight + 7, tooltipX, tooltipY);
    }

}
