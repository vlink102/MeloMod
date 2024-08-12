package me.vlink102.melomod.util;

import it.unimi.dsi.fastutil.chars.CharObjectPair;
import lombok.Getter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.translation.Language;
import net.minecraft.util.IChatComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ImageUtils {
    public static final Color TEXT_BACKGROUND_COLOR = new Color(0, 0, 0, 180);
    public static final double CHAT_COLOR_BACKGROUND_FACTOR = 0.19;
    private static final double[] GAUSSIAN_CONSTANTS = new double[] {0.00598, 0.060626, 0.241843, 0.383103, 0.241843, 0.060626, 0.00598};

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
            MinecraftFont.FontRenderResult result = MeloMod.SPACE.printCharacter(textImage, character.toString(), x, 1 + image.getHeight(), fontSize, lastItalicExtraWidth, characterData.getColor(), characterData.getDecorations());
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

    @Getter
    public static class ComponentPrintResult {

        private final BufferedImage image;
        private final int textWidth;

        public ComponentPrintResult(BufferedImage image, int textWidth) {
            this.image = image;
            this.textWidth = textWidth;
        }

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
}
