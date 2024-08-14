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

import java.awt.image.BufferedImage;

public class FontTextureResource extends FontResource {

    private final TextureResource resource;
    private final char resourceWidth;
    private final char resourceHeight;
    private final char x;
    private final char y;

    public FontTextureResource(TextureResource resource, char resourceWidth, char resourceHeight, char x, char y, char width, char height) {
        super(width, height);
        this.resource = resource;
        this.resourceWidth = resourceWidth;
        this.resourceHeight = resourceHeight;
        this.x = x;
        this.y = y;
    }

    public FontTextureResource(TextureResource resource, int resourceWidth, int resourceHeight, int x, int y, int width, int height) {
        this(resource, (char) resourceWidth, (char) resourceHeight, (char) x, (char) y, (char) width, (char) height);
    }

    public FontTextureResource(TextureResource resource, int x, int y, int width, int height) {
        this(resource, 0, 0, x, y, width, height);
    }

    public FontTextureResource(TextureResource resource) {
        this(resource, 0, 0, 0, 0, 0, 0);
    }

    @SuppressWarnings("deprecation")
    public BufferedImage getFontImage() {
        BufferedImage image;
        if (resourceWidth < 1 || resourceHeight < 1) {
            image = resource.getTexture();
        } else {
            image = resource.getTexture(resourceWidth, resourceHeight);
        }
        if (width < 1 || height < 1) {
            return image;
        }
        return image.getSubimage(x, y, width, height);
    }

    public TextureResource getResource() {
        return resource;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
