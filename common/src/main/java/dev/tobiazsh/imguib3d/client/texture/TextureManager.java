// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.texture;

import java.util.*;

public final class TextureManager {

    private static final TextureManager INSTANCE = new TextureManager();

    public static TextureManager getInstance() {
        return INSTANCE;
    }

    private final Map<Long, ImGuiTexture> textures = new HashMap<>(); // Uploaded textures, key is the texture ID
    private final Set<ImGuiTexture> queuedTextures = new HashSet<>(); // Textures that are queued for upload, but not yet uploaded

    public Map<Long, ImGuiTexture> getTextures() {
        return Map.copyOf(textures);
    }

    public Set<ImGuiTexture> getQueuedTextures() {
        return Set.copyOf(queuedTextures);
    }

    /**
     * Registers a texture in the Map of USABLE textures. Do not register unuploaded or disposed textures.
     * Any match in the queued textures will be removed from the queue and added to the usable textures.
     *
     * @apiNote Only register textures that are not disposed.
     *          Disposed textures will be automatically removed from the manager.
     *
     * @param texture The texture to register.
     */
    public void registerTexture(ImGuiTexture texture) {
        if (!texture.isUploaded() || texture.isDisposed())
            throw new IllegalArgumentException("Cannot register an unuploaded or disposed texture");

        queuedTextures.remove(texture);
        textures.put(texture.getTextureId(), texture);
    }

    /**
     * Queues a texture for upload. The texture will be uploaded in the next frame.
     * Do not queue disposed textures.
     * @param texture The texture to queue.
     */
    public void queueTexture(ImGuiTexture texture) {
        if (texture.isDisposed())
            throw new IllegalArgumentException("Cannot queue a disposed texture");

        queuedTextures.add(texture);
    }

    public ImGuiTexture getTexture(long textureId) {
        return textures.get(textureId);
    }

    public void clean() {
        textures.values().removeIf(ImGuiTexture::isDisposed);
        queuedTextures.removeIf(ImGuiTexture::isDisposed);
    }
}
