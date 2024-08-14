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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.function.UnaryOperator;

public class TextureResource {

    public static final String MCMETA_SUFFIX = ".mcmeta";
    public static final String PNG_MCMETA_SUFFIX = ".png" + MCMETA_SUFFIX;

    private final File file;
    private final  boolean isTexture;
    private Reference<BufferedImage> texture;
    private final UnaryOperator<BufferedImage> imageTransformFunction;

    private Unsafe unsafe;

    public TextureResource(File file, BufferedImage image) {
        this.file = file;
        this.isTexture = true;
        this.texture = new WeakReference<>(image);
        this.imageTransformFunction = null;
        this.unsafe = null;
    }

    private synchronized BufferedImage loadImage() {
        if (!isTexture) {
            throw new IllegalStateException(this + " is not a texture!");
        }
        BufferedImage image;
        if (texture != null && (image = texture.get()) != null) {
            return image;
        }
        try {
            image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("Image is null!");
            }
            this.texture = new WeakReference<>(image);
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to load image " + this + " from " + file.getAbsolutePath(), e);
        }
    }

    public boolean isTexture() {
        return isTexture;
    }

    public BufferedImage getTexture(int w, int h) {
        BufferedImage image = loadImage();
        if (imageTransformFunction != null) {
            image = imageTransformFunction.apply(image);
        }
        if (image.getWidth() != w || image.getHeight() != h) {
            image = ImageUtils.resizeImageAbs(image, w, h);
        } else {
            image = ImageUtils.copyImage(image);
        }
        return image;
    }

    public BufferedImage getTexture() {
        BufferedImage image = loadImage();
        if (imageTransformFunction != null) {
            image = imageTransformFunction.apply(image);
        }
        return ImageUtils.copyImage(image);
    }

    public boolean hasFile() {
        return file != null;
    }

    public File getFile() {
        return file;
    }

    public boolean isTextureMeta() {
        return false;
    }

    public boolean hasTextureMeta() {
        return getTextureMeta() != null;
    }

    public boolean hasImageTransformFunction() {
        return imageTransformFunction != null;
    }

    public UnaryOperator<BufferedImage> getImageTransformFunction() {
        return imageTransformFunction;
    }

    public TextureMeta getTextureMeta() {
        return null;
    }

    public class TextureMeta {
    }

    @SuppressWarnings({"DeprecatedIsStillUsed", "Convert2Lambda", "deprecation"})
    @Deprecated
    public Unsafe getUnsafe() {
        if (unsafe != null) {
            return unsafe;
        }
        return unsafe = new Unsafe() {
            @Override
            public Reference<BufferedImage> getTextureReference() {
                return texture;
            }
        };
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public interface Unsafe {

        @Deprecated
        Reference<BufferedImage> getTextureReference();

    }

}
