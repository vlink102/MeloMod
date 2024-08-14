package me.vlink102.melomod.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.*;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.http.ApiUtil;
import me.vlink102.melomod.util.translation.Language;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class BitMapFont {
    public static final int DEFAULT_HEIGHT = 8;

    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;
    public static final int OBFUSCATE_OVERLAP_COUNT = 3;

    private Int2ObjectMap<FontResource> charImages;
    private Int2IntMap charWidth;
    private int height;
    private int ascent;
    private int scale;
    private List<String> chars;
    private final File file;
    private final Font font;

    private final boolean space;
    private final boolean unifont;

    public int getHeight() {
        return height;
    }

    public boolean isUnifont() {
        return unifont;
    }

    private final BufferedImage image;

    public BitMapFont(Font font) {
        this.file = null;
        this.image = null;
        this.font = font;
        space = false;
        unifont = true;
        this.height = 8;
    }

    public BitMapFont(Font font, Int2IntMap map) {
        this.file = null;
        this.image = null;
        this.font = font;
        space = true;
        unifont = false;
        this.charAdvances = map;
    }

    public BitMapFont(int height, int ascent, List<String> chars, File file, BufferedImage image, Font font) {
        this.height = height;
        this.ascent = ascent;
        this.chars = chars;
        this.file = file;
        this.image = image;
        this.font = font;
        space = false;
        unifont = false;
    }

    public Font getFont() {
        return font;
    }

    private Int2IntMap charAdvances = null;
    private Int2ObjectMap<BufferedImage> charImages2;

    public BufferedImage getUnifontChar(String character) {
        BufferedImage temp = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = temp.createGraphics();

        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        //graphics.drawGlyphVector(font.createGlyphVector(graphics.getFontRenderContext(), character), 0, metrics.getAscent());
        graphics.drawString(character, 0, metrics.getAscent());
        graphics.dispose();
        return temp.getSubimage(0, 0, metrics.charWidth(character.codePointAt(0)), metrics.getHeight());
    }

    public void manageFont() {
        if (space || unifont) {
            this.charImages2 = new Int2ObjectOpenHashMap<>();
            this.charWidth = new Int2IntOpenHashMap();
            // idk man
            return;
        }
        TextureResource resource = new TextureResource(file, image);
        BufferedImage fontBaseImage = resource.getTexture();
        Graphics2D graphics = fontBaseImage.createGraphics();
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();

        this.charImages2 = new Int2ObjectOpenHashMap<>();
        this.charWidth = new Int2IntOpenHashMap();

        if (chars.isEmpty()) {
            return;
        }

        int yIncrement = fontBaseImage.getHeight() / chars.size();
        this.scale = Math.abs(height == 0 ? 0 : yIncrement / height);
        int y = 0;
        for (String line : chars) {
            if (!line.isEmpty()) {
                int xIncrement = fontBaseImage.getWidth() / line.codePointCount(0, line.length());
                int x = 0;
                for (int i = 0; i < line.length(); ) {
                    int character = line.codePointAt(i);
                    i += character < 0x10000 ? 1 : 2;
                    if (i != 0 && i != 32) {
                        int lastX = 0;
                        for (int x0 = x; x0 < x + xIncrement; x0++) {
                            for (int y0 = y; y0 < y + yIncrement; y0++) {
                                int alpha = getAlpha(fontBaseImage.getRGB(x0, y0));
                                if (alpha != 0) {
                                    lastX = x0 - x + 1;
                                    break;
                                }
                            }
                        }
                        if (x + lastX > fontBaseImage.getWidth()) {
                            lastX = fontBaseImage.getWidth() - x;
                        }
                        if (lastX > 0) {
                            charImages2.put(character, getSubImage(x, y, lastX, yIncrement, fontBaseImage));
                            charWidth.put(character, lastX);
                        }
                    }
                    x += xIncrement;
                }
            }
            y += yIncrement;
        }


//        int currentX;
//        int currentY = 0;
//        for (String charString : chars) {
//            currentX = 0;
//            List<String> decompressed = decompress(charString);
//            for (String decompressedChar : decompressed) {
//                if (decompressedChar.isEmpty() || StringUtils.isBlank(decompressedChar)) continue;
//                char toCharacter = decompressedChar.charAt(0);
//                int charWidth = metrics.charWidth(toCharacter);
//                System.out.println("Character: " + toCharacter + ", Width: " + charWidth + ", CurrentX: " + currentX + ", CurrentY: " + currentY);
//                charImages2.put(toCharacter, getSubImage(currentX, currentY, charWidth, metrics.getHeight(), fontBaseImage));
//                currentX += charWidth;
//            }
//            currentY += metrics.getAscent() + metrics.getDescent();
//        }
    }

    public BufferedImage getSubImage(int x, int y, int width, int height, BufferedImage image) {
        return image.getSubimage(x, y, width, height);
    }

    public List<String> decompress(String large) {
        List<String> decompressed = new ArrayList<>();
        for (char c : large.toCharArray()) {
            decompressed.add(String.valueOf(c));
        }
        return decompressed;
    }

    public List<String> decompress(List<String> large) {
        List<String> decompressed = new ArrayList<>();
        for (String s : large) {
            char[] characters = s.toCharArray();
            for (char character : characters) {
                decompressed.add(String.valueOf(character));
            }
        }
        return decompressed;
    }

    public static void writeFileToDownloads(RenderedImage image) {
        try {
            ImageIO.write(image, "png", new File("C:\\Users\\vlink\\Downloads", UUID.randomUUID() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    public void reloadFont() {
        this.charImages = new Int2ObjectOpenHashMap<>();
        this.charWidth = new Int2IntOpenHashMap();


        if (chars.isEmpty()) {
            System.out.println("dawg");
            return;
        }

        TextureResource resource = new TextureResource(file, image);
        if (resource == null || !resource.isTexture()) {
            throw new ResourceLoadingException("Uh oh spaghettio");
        }
        BufferedImage fontBaseImage = resource.getTexture();


        // switch resource, manually recode for hardcoded size values
        // TODO

        int yIncrement = fontBaseImage.getHeight() / chars.size();
        this.scale = Math.abs(height == 0 ? 0 : yIncrement / height);
        int y = 0;
        for (String line : chars) {
            if (!line.isEmpty()) {
                int xIncrement = fontBaseImage.getWidth() / line.codePointCount(0, line.length());
                int x = 0;
                for (int i = 0; i < line.length(); ) {
                    int character = line.codePointAt(i);
                    i += character < 0x10000 ? 1 : 2;
                    if (i != 0 && i != 32) {
                        int lastX = 0;
                        for (int x0 = x; x0 < x + xIncrement; x0++) {
                            for (int y0 = y; y0 < y + yIncrement; y0++) {
                                int alpha = getAlpha(fontBaseImage.getRGB(x0, y0));
                                if (alpha != 0) {
                                    lastX = x0 - x + 1;
                                    break;
                                }
                            }
                        }
                        if (x + lastX > fontBaseImage.getWidth()) {
                            lastX = fontBaseImage.getWidth() - x;
                        }
                        if (lastX > 0) {
                            charImages.put(character, new FontTextureResource(resource, x, y, lastX, yIncrement));
                            charWidth.put(character, lastX);
                        }
                    }
                    x += xIncrement;
                }
            }
            y += yIncrement;
        }
    }
*/


    public static int getAlpha(int color) {
        return color >> 24 & 255;
    }

    public boolean canDisplayUnifontCharacter(String character) {
        return font.canDisplay(character.codePointAt(0));
    }

    public boolean canDisplayCharacter(String character) {
        if (unifont) return canDisplayUnifontCharacter(character);
        if (space) return charAdvances.containsKey(character.codePointAt(0));
        return charImages2.containsKey(character.codePointAt(0));
    }

    public Optional<BufferedImage> getCharacterImage(String character, float fontSize, Color color) {
        if (unifont) return Optional.of(getUnifontChar(character));
        BufferedImage charImage = charImages2.get(character.codePointAt(0));
        float descent = height - this.ascent - 1;
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.abs(Math.round(fontSize + (ascent + descent) * scale)));
        charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), color));
        return Optional.of(charImage);
    }

    public enum TextDecoration {
        OBFUSCATED("obfuscated"),
        BOLD("bold"),
        STRIKETHROUGH("strikethrough"),
        UNDERLINED("underlined"),
        ITALIC("italic");

        private final String name;

        TextDecoration(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final List<TextDecoration> DECORATIONS_ORDER = new ArrayList<>();

    static {
        DECORATIONS_ORDER.add(TextDecoration.OBFUSCATED);
        DECORATIONS_ORDER.add(TextDecoration.BOLD);
        DECORATIONS_ORDER.add(TextDecoration.ITALIC);
        DECORATIONS_ORDER.add(TextDecoration.STRIKETHROUGH);
        DECORATIONS_ORDER.add(TextDecoration.UNDERLINED);
    }

    public static List<TextDecoration> sortDecorations(List<TextDecoration> decorations) {
        List<TextDecoration> list = new ArrayList<>(DECORATIONS_ORDER.size());
        for (TextDecoration decoration : DECORATIONS_ORDER) {
            if (decorations.contains(decoration)) {
                list.add(decoration);
            }
        }
        return list;
    }

    public FontRenderResult printSpace(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, Color color, List<TextDecoration> decorations) {
        decorations = sortDecorations(decorations);
        int advance = (int) Math.round(charAdvances.get(character.codePointAt(0)) * 0.75);
        if (advance != 0) {
            int sign = advance < 0 ? -1 : 1;
            advance = Math.abs(advance);
            int originalW = advance;
            int w = (int) Math.round(originalW * ((double) Math.round(fontSize) / (double) DEFAULT_HEIGHT));
            int h = Math.round(fontSize);
            int beforeTransformW = w;
            int pixelSize = Math.round((float) beforeTransformW / (float) originalW);
            int strikeSize = (int) (fontSize / 8);
            int boldSize = (int) (fontSize / 16.0 * 2);
            int italicExtraWidth = 0;
            boolean italic = false;
            BufferedImage charImage = null;
            boolean underlineStrikethroughExpanded = false;
            for (TextDecoration decoration : decorations) {
                switch (decoration) {
                    case BOLD:
                        w += boldSize - 1;
                        break;
                    case ITALIC:
                        int extraWidth = (int) ((double) h * (4.0 / 14.0));
                        charImage = new BufferedImage(w + extraWidth * 2, h, BufferedImage.TYPE_INT_ARGB);
                        italicExtraWidth = (int) Math.round(-ITALIC_SHEAR_X * h);
                        italic = true;
                        break;
                    case STRIKETHROUGH:
                        if (charImage == null) {
                            charImage = new BufferedImage(w + (underlineStrikethroughExpanded ? 0 : pixelSize), h, BufferedImage.TYPE_INT_ARGB);
                        } else {
                            charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, underlineStrikethroughExpanded ? 0 : pixelSize);
                        }
                        Graphics2D g = charImage.createGraphics();
                        g.setColor(color);
                        g.fillRect(0, Math.round((fontSize / 2) - ((float) strikeSize / 2)), w + pixelSize, strikeSize);
                        g.dispose();
                        break;
                    case UNDERLINED:
                        if (charImage == null) {
                            charImage = new BufferedImage(w + (underlineStrikethroughExpanded ? 0 : pixelSize), h + (strikeSize * 2), BufferedImage.TYPE_INT_ARGB);
                        } else {
                            charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, underlineStrikethroughExpanded ? 0 : pixelSize);
                        }
                        g = charImage.createGraphics();
                        g.setColor(color);
                        g.fillRect(0, Math.round(fontSize), w + pixelSize, strikeSize);
                        g.dispose();
                        break;
                    default:
                        break;
                }
            }
            int extraWidth = italic ? 0 : lastItalicExtraWidth;
            if (charImage != null) {
                Graphics2D g = image.createGraphics();
                if (sign > 0) {
                    g.drawImage(charImage, x + extraWidth, y, null);
                } else {
                    g.drawImage(charImage, x, y, -w, h, null);
                }
                g.dispose();
            }
            return new FontRenderResult(image, w * sign + extraWidth, h, pixelSize, italicExtraWidth);
        } else {
            return new FontRenderResult(image, 0, 0, 0, lastItalicExtraWidth);
        }
    }

    public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, Color color, List<TextDecoration> decorations) {
        if (space) {
            return printSpace(image, character, x, y, fontSize, lastItalicExtraWidth, color, decorations);
        }
        if (!unifont) {
            y += font.getSize();
        }
        decorations = sortDecorations(decorations);

        Optional<BufferedImage> optionalBufferedImage = getCharacterImage(character, fontSize, color);
        BufferedImage charImage;
        if (optionalBufferedImage.isPresent()) {
            charImage = optionalBufferedImage.get();
        } else {
            throw new IllegalArgumentException();
        }
        int originalW = charImage.getWidth();
        float scale = fontSize / 8;
        float ascent = this.ascent - 7;
        float descent = height - this.ascent - 1;
        int fillHeight = (int) Math.floor(fontSize + (ascent + descent) * scale);
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.abs(fillHeight));
        int w = charImage.getWidth();
        int h = charImage.getHeight();
        charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), color));
        int beforeTransformW = w;
        double accuratePixelSize = (double) beforeTransformW / (double) originalW;
        int pixelSize = (int) Math.round(accuratePixelSize);
        int strikeSize = (int) (fontSize / 8.0);
        int boldSize = (int) (fontSize / 16.0 * 3);
        int italicExtraWidth = 0;
        boolean italic = false;
        boolean underlineStrikethroughExpanded = false;
        for (TextDecoration decoration : decorations) {
            switch (decoration) {
                case OBFUSCATED:
                    charImage = new BufferedImage(charImage.getWidth(), charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = charImage.createGraphics();
                    for (int i = 0; i < OBFUSCATE_OVERLAP_COUNT; i++) {
                        String magicCharacter = toMagic(this, character);
                        BufferedImage magicImage = MeloMod.getFontProvider(magicCharacter).getCharacterImage(magicCharacter, fontSize, color).orElse(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
                        g.drawImage(magicImage, 0, 0, charImage.getWidth(), charImage.getHeight(), null);
                    }
                    g.dispose();
                    break;
                case BOLD:
                    BufferedImage boldImage = new BufferedImage(charImage.getWidth() + boldSize, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int x0 = 0; x0 < charImage.getWidth(); x0++) {
                        for (int y0 = 0; y0 < charImage.getHeight(); y0++) {
                            int pixelColor = charImage.getRGB(x0, y0);
                            int alpha = getAlpha(pixelColor);
                            if (alpha != 0) {
                                for (int i = 0; i < boldSize; i++) {
                                    boldImage.setRGB(x0 + i, y0, pixelColor);
                                }
                            }
                        }
                    }
                    charImage = boldImage;
                    w += boldSize - 1;
                    break;
                case ITALIC:
                    int extraWidth = (int) ((double) charImage.getHeight() * (4.0 / 14.0));
                    BufferedImage italicImage = new BufferedImage(charImage.getWidth() + extraWidth * 2, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    g = italicImage.createGraphics();
                    //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.transform(AffineTransform.getShearInstance(ITALIC_SHEAR_X, 0));
                    g.drawImage(charImage, extraWidth, 0, null);
                    g.dispose();
                    charImage = italicImage;
                    italicExtraWidth = (int) Math.round(-ITALIC_SHEAR_X * h);
                    italic = true;
                    break;
                case STRIKETHROUGH:
                    charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, underlineStrikethroughExpanded ? 0 : (pixelSize * this.scale));
                    g = charImage.createGraphics();
                    g.setColor(color);
                    g.fillRect(0, Math.round((fontSize / 2) - ((float) strikeSize / 2)), w + pixelSize * this.scale, strikeSize);
                    g.dispose();
                    break;
                case UNDERLINED:
                    charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, underlineStrikethroughExpanded ? 0 : (pixelSize * this.scale));
                    g = charImage.createGraphics();
                    g.setColor(color);
                    g.fillRect(0, Math.round(fontSize), w + pixelSize * this.scale, strikeSize);
                    g.dispose();
                    break;
                default:
                    break;
            }
        }
        Graphics2D g = image.createGraphics();
        int extraWidth = italic ? 0 : lastItalicExtraWidth;
        int sign = fillHeight >= 0 ? 1 : -1;
        int spaceWidth;
        if (unifont) {
            spaceWidth = (int) Math.floor(accuratePixelSize * this.scale);
        } else {
            spaceWidth = (int) (fontSize / 8f);
        }

        if (sign > 0) {
            g.drawImage(charImage, x + extraWidth, (int) (y - ascent * scale), null);
        } else {
            g.drawImage(ImageUtils.flipVertically(charImage), x + extraWidth, (int) (y - ascent * scale), -w, -h, null);
            spaceWidth += Math.round(2 * scale);
        }
        g.dispose();
        return new FontRenderResult(image, w * sign + extraWidth, h, spaceWidth, italicExtraWidth);
    }

    public String toMagic(BitMapFont provider, String str) {
        if (provider == null) {
            return RandomStringUtils.random(str.length());
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            String currentChar = str.substring(i, i + 1);
            int width = MeloMod.getFontProvider(currentChar).getCharacterWidth(currentChar);
            IntList list = provider.getDisplayableCharactersByWidth().get(width);
            sb.append(new String(Character.toChars(list.getInt(ThreadLocalRandom.current().nextInt(list.size())))));
        }
        return sb.toString();
    }

    private Int2ObjectMap<IntList> getDisplayableCharactersByWidth;

    public Int2ObjectMap<IntList> getDisplayableCharactersByWidth() {
        if (getDisplayableCharactersByWidth == null) {
            Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<>();
            getDisplayableCharactersAsStream().forEach(i -> {
                String c = new String(Character.toChars(i));
                int width = getCharacterWidth(c);
                IntList characters = charactersByWidth.get(width);
                if (characters == null) {
                    charactersByWidth.put(width, characters = new IntArrayList());
                }
                characters.add(i);
            });
            for (Int2ObjectMap.Entry<IntList> entry : charactersByWidth.int2ObjectEntrySet()) {
                entry.setValue(IntLists.unmodifiable(entry.getValue()));
            }
            this.getDisplayableCharactersByWidth = Int2ObjectMaps.unmodifiable(charactersByWidth);
        }
        return getDisplayableCharactersByWidth;
    }

    private static boolean intSetHasIntStream;

    static {
        try {
            IntSet.class.getMethod("intStream");
            intSetHasIntStream = true;
        } catch (NoSuchMethodException e) {
            intSetHasIntStream = false;
        }
    }

    public IntStream getDisplayableCharactersAsStream() {
        if (intSetHasIntStream) {
            return getDisplayableCharacters().intStream();
        } else {
            return getDisplayableCharacters().stream().mapToInt(i -> i);
        }
    }

    public int getCharacterWidth(String character) {
        return charWidth.get(character.codePointAt(0));
    }

    public IntSet getDisplayableCharacters() {
        return IntSets.unmodifiable(charImages.keySet());
    }

    public static class FontRenderResult {

        private final BufferedImage image;
        private final int width;
        private final int height;
        private final int spaceWidth;
        private final int italicExtraWidth;

        public FontRenderResult(BufferedImage image, int width, int height, int spaceWidth, int italicExtraWidth) {
            this.image = image;
            this.width = width;
            this.height = height;
            this.spaceWidth = spaceWidth;
            this.italicExtraWidth = italicExtraWidth;
        }

        public BufferedImage getImage() {
            return image;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getSpaceWidth() {
            return spaceWidth;
        }

        public int getItalicExtraWidth() {
            return italicExtraWidth;
        }

    }

    public static final Color TOOLTIP_BACKGROUND_COLOR = new Color(-267386864, true);
    public static final Color TOOLTIP_OUTLINE_TOP_COLOR = new Color(1347420415, true);
    public static final Color TOOLTIP_OUTLINE_BOTTOM_COLOR = new Color(1344798847, true);

    public static List<String> getTooltip(ItemStack stack, EntityPlayerSP playerSP, boolean advanced) {
        List<String> list = Lists.newArrayList();
        String s = stack.getDisplayName();
        if (stack.hasDisplayName()) {
            if (Minecraft.getMinecraft().isSingleplayer()) {
                s = EnumChatFormatting.ITALIC + s;
            }
        }

        s = s + EnumChatFormatting.RESET;
        if (advanced) {
            String s1 = "";
            if (s.length() > 0) {
                s = s + " (";
                s1 = ")";
            }

            int i = Item.getIdFromItem(stack.getItem());
            if (stack.getHasSubtypes()) {
                s = s + String.format("#%04d/%d%s", i, stack.getItemDamage(), s1);
            } else {
                s = s + String.format("#%04d%s", i, s1);
            }
        } else if (!stack.hasDisplayName() && stack.getItem() == Items.filled_map) {
            s = s + " #" + stack.getItemDamage();
        }

        list.add(s);
        int i1 = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("HideFlags", 99)) {
            i1 = stack.getTagCompound().getInteger("HideFlags");
        }

        if ((i1 & 32) == 0) {
            stack.getItem().addInformation(stack, playerSP, list, advanced);
        }

        int l1;
        NBTTagList nbttaglist3;
        if (stack.hasTagCompound()) {
            if ((i1 & 1) == 0) {
                NBTTagList nbttaglist = stack.getEnchantmentTagList();
                if (nbttaglist != null) {
                    for (int j = 0; j < nbttaglist.tagCount(); ++j) {
                        l1 = nbttaglist.getCompoundTagAt(j).getShort("id");
                        int l = nbttaglist.getCompoundTagAt(j).getShort("lvl");
                        if (Enchantment.getEnchantmentById(l1) != null) {
                            list.add(Enchantment.getEnchantmentById(l1).getTranslatedName(l));
                        }
                    }
                }
            }

            if (stack.getTagCompound().hasKey("display", 10)) {
                NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");
                if (nbttagcompound.hasKey("color", 3)) {
                    if (advanced) {
                        list.add("Color: #" + Integer.toHexString(nbttagcompound.getInteger("color")).toUpperCase());
                    } else {
                        list.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
                    }
                }

                if (nbttagcompound.getTagId("Lore") == 9) {
                    nbttaglist3 = nbttagcompound.getTagList("Lore", 8);
                    if (nbttaglist3.tagCount() > 0) {
                        for (l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                            if (Minecraft.getMinecraft().isSingleplayer()) {
                                list.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + nbttaglist3.getStringTagAt(l1));
                            } else {
                                list.add(nbttaglist3.getStringTagAt(l1));
                            }
                        }
                    }
                }
            }
        }

        Multimap<String, AttributeModifier> multimap = stack.getAttributeModifiers();
        if (!multimap.isEmpty() && (i1 & 2) == 0) {
            list.add("");
            Iterator var19 = multimap.entries().iterator();

            while (var19.hasNext()) {
                Map.Entry<String, AttributeModifier> entry = (Map.Entry) var19.next();
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
                double d0 = attributemodifier.getAmount();
                if (attributemodifier.getID() == itemModifierUUID) {
                    d0 += (double) EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                }

                double d1;
                if (attributemodifier.getOperation() != 1 && attributemodifier.getOperation() != 2) {
                    d1 = d0;
                } else {
                    d1 = d0 * 100.0;
                }

                if (d0 > 0.0) {
                    list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier.getOperation(), new Object[]{DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String) entry.getKey())}));
                } else if (d0 < 0.0) {
                    d1 *= -1.0;
                    list.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier.getOperation(), new Object[]{DECIMALFORMAT.format(d1), StatCollector.translateToLocal("attribute.name." + (String) entry.getKey())}));
                }
            }
        }

        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("Unbreakable") && (i1 & 4) == 0) {
            list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
        }

        Block block1;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CanDestroy", 9) && (i1 & 8) == 0) {
            nbttaglist3 = stack.getTagCompound().getTagList("CanDestroy", 8);
            if (nbttaglist3.tagCount() > 0) {
                list.add("");
                list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canBreak"));

                for (l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                    block1 = Block.getBlockFromName(nbttaglist3.getStringTagAt(l1));
                    if (block1 != null) {
                        list.add(EnumChatFormatting.DARK_GRAY + block1.getLocalizedName());
                    } else {
                        list.add(EnumChatFormatting.DARK_GRAY + "missingno");
                    }
                }
            }
        }

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CanPlaceOn", 9) && (i1 & 16) == 0) {
            nbttaglist3 = stack.getTagCompound().getTagList("CanPlaceOn", 8);
            if (nbttaglist3.tagCount() > 0) {
                list.add("");
                list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canPlace"));

                for (l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                    block1 = Block.getBlockFromName(nbttaglist3.getStringTagAt(l1));
                    if (block1 != null) {
                        list.add(EnumChatFormatting.DARK_GRAY + block1.getLocalizedName());
                    } else {
                        list.add(EnumChatFormatting.DARK_GRAY + "missingno");
                    }
                }
            }
        }

        if (advanced) {
            if (stack.isItemDamaged()) {
                list.add("Durability: " + (stack.getMaxDamage() - stack.getItemDamage()) + " / " + stack.getMaxDamage());
            }

            list.add(EnumChatFormatting.DARK_GRAY + ((ResourceLocation) Item.itemRegistry.getNameForObject(stack.getItem())).toString());
            if (stack.hasTagCompound()) {
                list.add(EnumChatFormatting.DARK_GRAY + "NBT: " + stack.getTagCompound().getKeySet().size() + " tag(s)");
            }
        }

        ForgeEventFactory.onItemTooltip(stack, playerSP, list, advanced);
        return list;
    }

    protected static final UUID itemModifierUUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");

    public static BufferedImage getTooltipBackground(ItemStack stack) {
        List<String> prints = getTooltip(stack, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        for (int i = 0; i < prints.size(); ++i) {
            prints.set(i, prints.get(i) + "§r");
            if (i == 0) {
                prints.set(i, stack.getRarity().rarityColor + prints.get(i));
            } else {
                prints.set(i, "§7" + prints.get(i));
            }
        }
        if (prints.isEmpty()) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        int fontSize = 64;
        float fontPixelSize = fontSize / 8f;

        BufferedImage largerImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Font derived = MeloMod.unifont.deriveFont(Font.PLAIN, fontSize);
        FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
        Graphics2D g = largerImage.createGraphics();
        g.setFont(derived);

        float totalHeight = 0;
        float totalWidth = 0;
        FontRenderContext context = g.getFontRenderContext();
        FontMetrics fontMetrics = g.getFontMetrics();
        g.dispose();
        if (prints.size() > 1) {
            totalHeight += fontPixelSize * 3;
        }
        for (int i = 0; i < prints.size(); i++) {
            String print = prints.get(i);
            LineMetrics metrics = derived.getLineMetrics(print, context);
            totalHeight += metrics.getHeight();

            int width = renderer.getStringWidth(print) * 8; // fixme
            //int width = fontMetrics.charsWidth(print.toCharArray(), 0, print.length());
            //System.out.println("Testing \"" + print + "\", Unicode width: " + width + ", MC Width: " + renderer.getStringWidth(print));

            if (width >= totalWidth) {
                totalWidth = width;
            }
            if (prints.size() > 1) {
                totalHeight += fontPixelSize * 2;
            }
        }

        totalWidth += 8 * fontPixelSize;
        totalHeight += 8 * fontPixelSize;

        int roundedPixelSize = (int) fontPixelSize;

        BufferedImage bufferedImage = new BufferedImage((int) totalWidth, (int) totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferedGraphics = bufferedImage.createGraphics();

        bufferedGraphics.setColor(TOOLTIP_BACKGROUND_COLOR);
        bufferedGraphics.fillRect(roundedPixelSize, 0, bufferedImage.getWidth() - (roundedPixelSize * 2), bufferedImage.getHeight());
        bufferedGraphics.fillRect(0, roundedPixelSize, roundedPixelSize, bufferedImage.getHeight() - (roundedPixelSize * 2));
        bufferedGraphics.fillRect(bufferedImage.getWidth() - roundedPixelSize, roundedPixelSize, roundedPixelSize, bufferedImage.getHeight() - (roundedPixelSize * 2));
        bufferedGraphics.setColor(TOOLTIP_OUTLINE_TOP_COLOR);
        bufferedGraphics.fillRect(roundedPixelSize * 2, roundedPixelSize, bufferedImage.getWidth() - (roundedPixelSize * 4), roundedPixelSize);
        GradientPaint gradientPaint = new GradientPaint(0, 0, TOOLTIP_OUTLINE_TOP_COLOR, 0, bufferedImage.getHeight() - (roundedPixelSize * 2), TOOLTIP_OUTLINE_BOTTOM_COLOR);
        bufferedGraphics.setPaint(gradientPaint);
        bufferedGraphics.fillRect(roundedPixelSize, roundedPixelSize, roundedPixelSize, bufferedImage.getHeight() - (roundedPixelSize * 2));
        bufferedGraphics.fillRect(bufferedImage.getWidth() - (roundedPixelSize * 2), roundedPixelSize, roundedPixelSize, bufferedImage.getHeight() - (roundedPixelSize * 2));
        bufferedGraphics.setColor(TOOLTIP_OUTLINE_BOTTOM_COLOR);
        bufferedGraphics.fillRect(roundedPixelSize * 2, bufferedImage.getHeight() - (roundedPixelSize * 2), bufferedImage.getWidth() - (roundedPixelSize * 4), roundedPixelSize);

        int startX = roundedPixelSize * 4;
        int startY = roundedPixelSize * 4;

        for (int i = 0; i < prints.size(); i++) {
            String print = prints.get(i);
            ImageUtils.printComponent(bufferedImage, print, Language.ENGLISH, true, startX, -fontSize + startY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);

            LineMetrics metrics = derived.getLineMetrics(print, context);
            startY += (int) (metrics.getHeight() + (fontPixelSize * 2));
            if (i + 1 == 1) {
                startY += roundedPixelSize * 3;
            }
        }
        bufferedGraphics.dispose();
        return bufferedImage;
    }

    public static final double CHAT_COLOR_BACKGROUND_FACTOR = 0.19;


}
