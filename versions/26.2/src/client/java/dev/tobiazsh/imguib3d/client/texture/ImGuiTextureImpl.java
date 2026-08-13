// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh


package dev.tobiazsh.imguib3d.client.texture;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImGuiTextureImpl implements ImGuiTexture {

    private boolean isDisposed;
    private boolean isUploaded;
    private boolean isBound;

    private GpuTexture gpuTexture;
    private GpuTextureView gpuTextureView;
    private GpuSampler gpuSampler;
    private ByteBuffer imageBuffer;

    private final int width;
    private final int height;

    private final String label;

    /**
     * Creates a new ImGuiTextureImpl instance with the given parameters.
     *
     * @param gpuTexture The GPU texture object.
     * @param textureView The GPU texture view object.
     * @param sampler The GPU sampler object.
     * @param imageObject The NativeImage object containing the texture data. Closed after construction.
     * @param label The label for the texture, used for debugging purposes.
     */
    public ImGuiTextureImpl(
            final GpuTexture gpuTexture,
            final GpuTextureView textureView,
            final GpuSampler sampler,
            final NativeImage imageObject,
            final String label
    ) {
        if (imageObject.isClosed())
            throw new IllegalArgumentException("NativeImage must not be closed when passed to ImGuiTextureImpl constructor");

        final int textureWidth = gpuTexture.getWidth(0);
        final int textureHeight = gpuTexture.getHeight(0);

        if (imageObject.getWidth() != textureWidth)
            throw new IllegalArgumentException("NativeImage width does not match GPU texture width: " + imageObject.getWidth() + " != " + textureWidth);

        if (imageObject.getHeight() != textureHeight)
            throw new IllegalArgumentException("NativeImage height does not match GPU texture height: " + imageObject.getHeight() + " != " + textureHeight);

        final ByteBuffer srcBuffer = imageObject.getPixelBytes();

        // Allocate a fresh off-heap direct buffer that survives imageObject.close()
        final ByteBuffer directCopy = MemoryUtil.memAlloc(srcBuffer.remaining());
        directCopy.put(srcBuffer);
        directCopy.flip();

        this(gpuTexture, textureView, sampler, directCopy, textureWidth, textureHeight, label);

        imageObject.close(); // Close the NativeImage after storing its data in the GPU texture
    }

    /**
     * Creates a new ImGuiTextureImpl instance with the given parameters.
     *
     * @param gpuTexture The GPU texture object.
     * @param textureView The GPU texture view object.
     * @param sampler The GPU sampler object.
     * @param imageBuffer The pixel data as a ByteBuffer. Do not close this buffer after passing it to this constructor, as
     *               it will be used for uploading the texture data to the GPU.
     *               The buffer will be copied. After passing, you need to close the passed buffer if you don't
     *               need it anymore.
     * @param width The width of the texture in imageBuffer.
     * @param height The height of the texture in imageBuffer.
     * @param label The label for the texture, used for debugging purposes.
     */
    public ImGuiTextureImpl(
            final GpuTexture gpuTexture,
            final GpuTextureView textureView,
            final GpuSampler sampler,
            final ByteBuffer imageBuffer,
            final int width,
            final int height,
            final String label
    ) {
        this.gpuTexture = gpuTexture;
        this.gpuTextureView = textureView;
        this.gpuSampler = sampler;
        this.width = width;
        this.height = height;
        this.label = label;

        final ByteBuffer ownedCopy = MemoryUtil.memAlloc(imageBuffer.remaining());
        ownedCopy.put(imageBuffer.duplicate());
        ownedCopy.flip();
        this.imageBuffer = ownedCopy;

        TextureManager.getInstance().queueTexture(this);
    }

    public ImGuiTextureImpl(final NativeImage image, final @Nullable String label) {
        final GpuDevice device = RenderSystem.getDevice();

        final GpuTexture gpuTexture = device.createTexture(
                label,
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST,
                GpuFormat.RGBA8_UNORM,
                image.getWidth(),
                image.getHeight(),
                1,
                1
        );

        final GpuTextureView gpuTextureView = device.createTextureView(gpuTexture);
        final GpuSampler gpuSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);

        this(gpuTexture, gpuTextureView, gpuSampler, image, label);
    }

    public ImGuiTextureImpl(final InputStream inputStream, final @Nullable String label) throws IOException {
        this(NativeImage.read(inputStream), label);
    }

    public ImGuiTextureImpl(final Path path, final @Nullable String label) throws IOException {
        this(NativeImage.read(Files.newInputStream(path)), label);
    }

    public void bind(final String samplerName, final RenderPass renderPass) {
        renderPass.bindTexture(
                samplerName,
                gpuTextureView,
                gpuSampler
        );

        this.isBound = true;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void dispose() {
        if (gpuTexture != null) {
            gpuTexture.close();
            gpuTexture = null;
        }

        if (gpuTextureView != null) {
            gpuTextureView.close();
            gpuTextureView = null;
        }

        if (gpuSampler != null) {
            gpuSampler.close();
            gpuSampler = null;
        }

        if (imageBuffer != null) {
            MemoryUtil.memFree(imageBuffer);
            imageBuffer = null;
        }

        isUploaded = false;
        isBound = false;
        isDisposed = true;
    }

    /**
     * Uploads the texture data to the GPU using a new CommandEncoder.
     * This method should be called before rendering the texture.
     * Registers the texture with the TextureManager.
     */
    @Override
    public void upload() {
        final GpuDevice device = RenderSystem.getDevice();
        final CommandEncoder commandEncoder = device.createCommandEncoder();
        upload(commandEncoder);
    }

    /**
     * Uploads the texture data to the GPU using the provided CommandEncoder.
     * This method should be called before rendering the texture.
     * Registers the texture with the TextureManager.
     *
     * @param commandEncoder The CommandEncoder used to write the texture data to the GPU.
     */
    public void upload(final CommandEncoder commandEncoder) {
        if (isDisposed)
            throw new IllegalStateException("Cannot upload a disposed texture");

        if (isUploaded)
            return;

        commandEncoder.writeToTexture(
                gpuTexture,
                imageBuffer,
                0, 0, 0, 0,
                width, height
        );

        this.isUploaded = true;

        TextureManager.getInstance().registerTexture(this);
    }

    @Override
    public long getTextureId() {
        return switch (gpuTexture) {
            case VulkanGpuTexture vkGpuTexture -> vkGpuTexture.vkImage();
            case GlTexture glGpuTexture -> glGpuTexture.glId();
            default -> throw new IllegalStateException("Unexpected value: " + gpuTexture);
        };
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }

    @Override
    public boolean isUsable() {
        return isUploaded && isBound && !isDisposed;
    }

    @Override
    public boolean isUploaded() {
        return isUploaded;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public boolean isBound() {
        return isBound;
    }

    public GpuTextureView getGpuTextureView() {
        return gpuTextureView;
    }

    public GpuTexture getGpuTexture() {
        return gpuTexture;
    }

    public GpuSampler getGpuSampler() {
        return gpuSampler;
    }
}
