// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.texture;

public interface ImGuiTexture {

    /**
     * Disposes the texture and releases any associated resources.
     */
    void dispose();

    /**
     * Uploads the texture data to the GPU. This method should be called before rendering the texture.
     */
    void upload();

    /**
     * Returns the texture id in the GPU. This id is used to bind the texture for rendering.
     */
    long getTextureId();

    /**
     * Checks if the texture has already been disposed. Disposed automatically should assume unusable.
     */
    boolean isDisposed();

    /**
     * Checks if the texture is currently bound to the GPU for rendering and can be used to do so.
     * An unusable texture does not automatically mean that it is disposed, but a disposed texture is always unusable.
     */
    boolean isUsable();

    /**
     * Checks whether the texture has been uploaded to the GPU.
     * Do not assume that a texture is usable just because it has been uploaded. There may be more factors that
     * determine whether a texture is usable. It's best to use {@link #isUsable()} instead.
     */
    boolean isUploaded();

    /**
     * Returns the label associated with the texture. This label is used for identification and debugging purposes.
     */
    String getLabel();
}
