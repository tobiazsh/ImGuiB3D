// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.texture;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * A factory class which wraps the constructors in {@link ImGuiTexture} to provide a more convenient and comprehensible
 * way to create ImGuiTexture instances.
 */
public class ImGuiTextureFactory {

    /**
     * Creates an ImGuiTexture from a NativeImage.
     *
     * @param image The NativeImage to create the texture from. The image will be closed after the texture is created.
     * @param label The label for the texture, used for debugging purposes.
     * @return An ImGuiTexture instance created from the provided NativeImage.
     */
    public static ImGuiTexture fromNativeImage(final NativeImage image, final String label) {
        return new ImGuiTextureImpl(image, label);
    }

    /**
     * Creates an ImGuiTexture from a file path.
     *
     * @param filePath The path to the image file to create the texture from.
     * @param label The label for the texture, used for debugging purposes.
     * @return An ImGuiTexture instance created from the image file at the provided path.
     * @throws IOException If an I/O error occurs while reading the image file.
     */
    public static ImGuiTexture fromFile(final Path filePath, final String label) throws IOException {
        return new ImGuiTextureImpl(filePath, label);
    }

    /**
     * Creates an ImGuiTexture from an InputStream.
     *
     * @param inputStream The InputStream to read the image data from. The stream will be closed after the texture is
     *                    created.
     * @param label The label for the texture, used for debugging purposes.
     * @return An ImGuiTexture instance created from the image data read from the provided InputStream.
     * @throws IOException If an I/O error occurs while reading from the InputStream.
     */
    public static ImGuiTexture fromStream(final InputStream inputStream, final String label) throws IOException {
        return new ImGuiTextureImpl(inputStream, label);
    }
}
