/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.vlink102.melomod.util;

import it.unimi.dsi.fastutil.chars.CharObjectImmutablePair;
import it.unimi.dsi.fastutil.chars.CharObjectPair;
import lombok.Getter;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.core.helpers.Strings;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;

import static me.vlink102.melomod.util.StringUtils.cleanColour;

@Getter
public class CharacterData {

    public static Color getRandom() {
        return fromHex(randomCollection(COLORS.values().toArray(new String[0])));
    }

    public static <T> T randomCollection(T[] collection) {
        return collection[RandomUtils.nextInt(0, collection.length)];
    }

    public static Color fromHex(String hex) {
        return new Color(Integer.parseInt(hex.substring(0, 2), 16), Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(hex.substring(4, 6), 16));
    }

    public static Color convert(char color) {
        return fromHex(COLORS.get(color));
    }



    public static final HashMap<Character, String> COLORS = new HashMap<Character, String>() {{
        this.put('0', "000000");
        this.put('1', "0000AA");
        this.put('2', "00AA00");
        this.put('3', "00AAAA");
        this.put('4', "AA0000");
        this.put('5', "AA00AA");
        this.put('6', "FFAA00");
        this.put('7', "AAAAAA");
        this.put('8', "555555");
        this.put('9', "5555FF");
        this.put('a', "55FF55");
        this.put('b', "FF5555");
        this.put('c', "FF5555");
        this.put('d', "FF55FF");
        this.put('e', "FFFF55");
        this.put('f', "FFFFFF");
    }};

    private static java.util.List<CharObjectPair<CharacterData>> decompose(String rawText) {
        boolean isBold = false, isItalic = false, isStrikethrough = false, isUnderline = false;
        Color color = convert('f');

        java.util.List<CharObjectPair<CharacterData>> data = new ArrayList<>();

        char[] rawChars = rawText.toCharArray();
        for (int i = 0; i < rawChars.length; i++) {
            char character = rawChars[i];
            if (character == '§') {
                char modifier = rawChars[i + 1];
                switch (modifier) {
                    case 'l':
                        isBold = true;
                        break;
                    case 'o':
                        isItalic = true;
                        break;
                    case 'm':
                        isStrikethrough = true;
                        break;
                    case 'n':
                        isUnderline = true;
                        break;
                    case 'r':
                        isBold = false;
                        isItalic = false;
                        isStrikethrough = false;
                        isUnderline = false;
                        break;
                }
                if (String.valueOf(modifier).matches("[0-9a-f]")) {
                    color = convert(modifier);
                }
                i++;
            } else {
                data.add(new CharObjectImmutablePair<>(character, new CharacterData(color, buildFromValues(isBold, isItalic, isStrikethrough, isUnderline))));
            }
        }
        //printData(data);
        return data;
    }


    public static void printData(List<CharObjectPair<CharacterData>> data) {
        System.out.println("---------------------------------------");
        for (CharObjectPair<CharacterData> datum : data) {
            System.out.println("Pair (" + datum.leftChar() + ": " + datum.right() + ")");
        }
        System.out.println("---------------------------------------");
    }

    private static List<CharObjectPair<CharacterData>> decompose(IChatComponent component) {
        if (component == null) return new ArrayList<>();
        String rawText = component.getUnformattedText();
        boolean isBold = false, isItalic = false, isStrikethrough = false, isUnderline = false;
        Color color = convert('f');

        List<CharObjectPair<CharacterData>> data = new ArrayList<>();

        char[] rawChars = rawText.toCharArray();
        for (int i = 0; i < rawChars.length; i++) {
            char character = rawChars[i];
            if (character == '§') {
                char modifier = rawChars[i + 1];
                switch (modifier) {
                    case 'l':
                        isBold = true;
                        break;
                    case 'o':
                        isItalic = true;
                        break;
                    case 'm':
                        isStrikethrough = true;
                        break;
                    case 'n':
                        isUnderline = true;
                        break;
                    case 'r':
                        isBold = false;
                        isItalic = false;
                        isStrikethrough = false;
                        isUnderline = false;
                        break;
                }
                if (String.valueOf(modifier).matches("[0-9a-f]")) {
                    color = convert(modifier);
                }
                i++;
            } else {
                data.add(new CharObjectImmutablePair<>(character, new CharacterData(color, buildFromValues(isBold, isItalic, isStrikethrough, isUnderline))));
            }
        }
        return data;
    }

    public static List<BitMapFont.TextDecoration> buildFromValues(boolean isBold, boolean isItalic, boolean isStrikethrough, boolean isUnderline) {
        List<BitMapFont.TextDecoration> decorations = new ArrayList<>();
        if (isBold) decorations.add(BitMapFont.TextDecoration.BOLD);
        if (isItalic) decorations.add(BitMapFont.TextDecoration.ITALIC);
        if (isUnderline) decorations.add(BitMapFont.TextDecoration.UNDERLINED);
        if (isStrikethrough) decorations.add(BitMapFont.TextDecoration.STRIKETHROUGH);
        return decorations;
    }
/*
    public static ChatStyle buildFromValues(boolean isBold, boolean isItalic, boolean isStrikethrough, boolean isUnderline) {
        return new ChatStyle()
                .setBold(isBold)
                .setItalic(isItalic)
                .setStrikethrough(isStrikethrough)
                .setUnderlined(isUnderline);
    }*/

    public static List<BitMapFont.TextDecoration> deserialize(ChatStyle style) {
        List<BitMapFont.TextDecoration> decorations = new ArrayList<>();
        if (style.getBold()) decorations.add(BitMapFont.TextDecoration.BOLD);
        if (style.getItalic()) decorations.add(BitMapFont.TextDecoration.ITALIC);
        if (style.getUnderlined()) decorations.add(BitMapFont.TextDecoration.UNDERLINED);
        if (style.getStrikethrough()) decorations.add(BitMapFont.TextDecoration.STRIKETHROUGH);
        return decorations;
    }

    public static ValuePairs<String, List<CharObjectPair<CharacterData>>> fromComponent(String raw, UnaryOperator<String> shaper) {

        String content = raw;
        String stripped = cleanColour(content);
        String resultStr = shaper.apply(stripped);

        return new ValuePairs<>(resultStr, decompose(raw));
    }

    public static ValuePairs<String, List<CharObjectPair<CharacterData>>> fromComponent(IChatComponent component, UnaryOperator<String> shaper) {
        component = ComponentFlattening.flatten(component);
        String content = component.getUnformattedText();
        String stripped = cleanColour(content);
        String resultStr = shaper.apply(stripped);

        return new ValuePairs<>(resultStr, decompose(component));
    }

    private final Color color;
    private final List<BitMapFont.TextDecoration> decorations;

    public CharacterData(Color color, List<BitMapFont.TextDecoration> decorations) {
        this.color = color;
        this.decorations = decorations;
    }

}