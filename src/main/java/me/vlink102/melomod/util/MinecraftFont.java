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

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import net.minecraft.util.ChatStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class MinecraftFont {

    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;
    public static final int OBFUSCATE_OVERLAP_COUNT = 3;

    public abstract void reloadFonts();

    public abstract boolean canDisplayCharacter(String character);

    public abstract FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, Color color, ChatStyle decorations);

    public abstract Optional<BufferedImage> getCharacterImage(String character, float fontSize, Color color);

    public abstract int getCharacterWidth(String character);

    public abstract IntSet getDisplayableCharacters();

    @Getter
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

    }

}
