// Portions of this code are derived from Enaium's "fabric-mod-ImGui" project:
// https://github.com/Enaium/fabric-mod-ImGui
//
// Specifically, code was derived from:
// https://github.com/Enaium/fabric-mod-ImGui/blob/2fe781209243484223931175b59d439872bea934/game/26.2/src/main/java/cn/enaium/fabric/imgui/blaze3d/ImGuiImplBlaze3D.java
//
// The original work is licensed under the Apache License 2.0.
// A copy of the license is available at:
// /licenses/third_party/enaium_imgui_fabric/LICENSE.txt
//
// Enaium's implementation also appears to be based, at least in part, on the OpenGL backend from
// the imgui-java project, which is licensed under the MIT License.
// A copy of the MIT License is available at:
// /licenses/third_party/imgui_java/LICENSE.txt
//
// Modifications made in this project are licensed under LGPL-3.0.
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.backend;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.*;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.tobiazsh.imguib3d.ImGuiB3D;
import dev.tobiazsh.imguib3d.client.Framebuffer;
import dev.tobiazsh.imguib3d.client.ImGuiB3DClient;
import dev.tobiazsh.imguib3d.client.map.ShaderIdentifierMapperImpl;
import dev.tobiazsh.imguib3d.client.map.ShaderTypeMapperImpl;
import dev.tobiazsh.imguib3d.client.shader.ShaderKey;
import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.type.ImInt;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;

public class ImGuiImplBlaze3D {

    private static final Logger LOGGER = LogManager.getLogger("ImGuiB3D ImGuiImplBlaze3D");
    private static final Identifier SHADER_ID = Identifier.fromNamespaceAndPath(ImGuiB3D.MOD_ID, "core/imgui");

    private static final VertexFormat IMGUI_VERTEX_FORMAT = VertexFormat.builder(0)
            .addAttribute("position", GpuFormat.RG32_FLOAT)
            .addAttribute("uv", GpuFormat.RG32_FLOAT)
            .addAttribute("color", GpuFormat.RGBA8_UNORM)
            .build();

    private @Nullable GpuTexture fontTexture;
    private @Nullable GpuTextureView fontTextureView;
    private @Nullable GpuSampler fontTextureSampler;

    private @Nullable RenderPipeline renderPipeline;
    private @Nullable CompiledRenderPipeline compiledRenderPipeline;

    private final ByteBuffer projectionMatrixBuffer =
            ByteBuffer.allocateDirect(4 * 4 * Float.BYTES)
                    .order(ByteOrder.nativeOrder());

    private @Nullable GpuBuffer projectionMatrixUniform;

    private @Nullable GpuBuffer indexBuffer;
    private @Nullable GpuBuffer vertexBuffer;

    private final ImVec4 clipRect = new ImVec4();

    /**
     * Creates the render pipeline, pre-compiles the render pipeline and (re-)creates the projection matrix buffer.
     */
    private void createRenderPipeline() {
        final GpuDevice gpuDevice = RenderSystem.getDevice();

        renderPipeline = RenderPipeline.builder()
                .withLocation(SHADER_ID)
                .withVertexShader(SHADER_ID)
                .withFragmentShader(SHADER_ID)
                .withVertexBinding(0, IMGUI_VERTEX_FORMAT)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                .withCull(false)
                .withPolygonMode(PolygonMode.FILL)
                .withColorTargetState(new ColorTargetState(
                        new BlendFunction(
                                BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA,
                                BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA
                        )))
                .withBindGroupLayout(
                        BindGroupLayout.builder()
                                .withUniform("projectionMatrix", UniformType.UNIFORM_BUFFER)
                                .build())
                .withBindGroupLayout(
                        BindGroupLayout.builder()
                                .withSampler("textureSampler")
                                .build())
                .build();

        compiledRenderPipeline = gpuDevice.precompilePipeline(
                renderPipeline,
                (id, type) ->
                        ImGuiB3DClient.getShaderManager().getOrLoadDefault(
                                ShaderKey.of(
                                        ShaderIdentifierMapperImpl.getInstance().fromMinecraft(id),
                                        ShaderTypeMapperImpl.getInstance().fromMinecraft(type)
                                ),
                                null
                        ).sourceCode()
        );

        if (!compiledRenderPipeline.isValid())
            LOGGER.error("Failed to recompile ImGui render pipeline!");

        if (projectionMatrixUniform != null)
            projectionMatrixUniform.close();

        projectionMatrixUniform = gpuDevice.createBuffer(
                () -> "ImGui Projection Matrix",
                GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 64
        );
    }

    /**
     * Creates a new frame for rendering.
     * Checks if the render pipeline is valid and creates it if necessary.
     * Also ensures that the font textures are created and available for rendering.
     */
    public void newFrame() {
        if (renderPipeline == null) {
            createRenderPipeline();
        } else if (compiledRenderPipeline != null && !compiledRenderPipeline.isValid()) {
            LOGGER.warn("ImGui pipeline is invalid! Clearing cache and recreating pipeline...");
            RenderSystem.getDevice().clearPipelineCache();
            createRenderPipeline();
        }

        if (fontTexture == null)
            createFontTextures();
    }

    /**
     * Renders the provided draw data on the provided render pass with the according pipeline.
     *
     * @param drawData The draw data to render.
     * @param renderPass The render pass to use.
     */
    public void renderDrawData(final ImDrawData drawData, final RenderPass renderPass) {
        final Framebuffer framebuffer = Framebuffer.fromDrawData(drawData);
        final int commandListsCount = drawData.getCmdListsCount();

        if (!framebuffer.isArea())
            return;

        renderPass.setPipeline(renderPipeline);
        renderPass.setUniform("projectionMatrix", projectionMatrixUniform);
        renderPass.bindTexture("textureSampler", fontTextureView, fontTextureSampler);

        final float clipOffX = drawData.getDisplayPosX();
        final float clipOffY = drawData.getDisplayPosY();
        final float clipScaleX = drawData.getFramebufferScaleX();
        final float clipScaleY = drawData.getFramebufferScaleY();

        final IndexType indexType = ImDrawData.sizeOfImDrawIdx() == 2 ? IndexType.SHORT : IndexType.INT;

        long vertexOffset = 0;
        long indexOffset = 0;

        for (int i = 0; i < commandListsCount; i++) {
            final int vertexBufferSize = drawData.getCmdListVtxBufferSize(i) * ImDrawData.sizeOfImDrawVert();
            final int indexBufferSize = drawData.getCmdListIdxBufferSize(i) * ImDrawData.sizeOfImDrawIdx();

            if (vertexBufferSize == 0 || indexBufferSize == 0)
                continue;

            renderPass.setVertexBuffer(0, vertexBuffer.slice(vertexOffset, vertexBufferSize));
            renderPass.setIndexBuffer(indexBuffer, indexType);

            for (int j = 0; j < drawData.getCmdListCmdBufferSize(i); j++) {
                drawData.getCmdListCmdBufferClipRect(clipRect, i, j);

                final float clipMinX = (clipRect.x - clipOffX) * clipScaleX;
                final float clipMinY = (clipRect.y - clipOffY) * clipScaleY;
                final float clipMaxX = (clipRect.z - clipOffX) * clipScaleX;
                final float clipMaxY = (clipRect.w - clipOffY) * clipScaleY;

                if (clipMaxX <= clipMinX || clipMaxY <= clipMinY)
                    continue;

                final int scissorX = (int) clipMinX;
                final int scissorY = (int) (framebuffer.height() - clipMaxY);
                final int scissorWidth = (int) (clipMaxX - clipMinX);
                final int scissorHeight = (int) (clipMaxY - clipMinY);

                renderPass.enableScissor(scissorX, scissorY, scissorWidth, scissorHeight);

                final int elementCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int vertexBufferOffset = drawData.getCmdListCmdBufferVtxOffset(i, j);
                final int indexBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);

                final int firstIndex = (int) (indexOffset / ImDrawData.sizeOfImDrawIdx()) + indexBufferOffset;

                renderPass.drawIndexed(elementCount, 1, firstIndex, vertexBufferOffset, 0);
            }

            vertexOffset += vertexBufferSize;
            indexOffset += indexBufferSize;
        }
    }

    /**
     * Uploads the current draw data from imgui onto the GPU.
     *
     * @param drawData The draw data to upload.
     * @param commandEncoder The command encoder to use for uploading the data.
     */
    public void uploadDrawData(final ImDrawData drawData, final CommandEncoder commandEncoder) {
        final GpuDevice gpuDevice = RenderSystem.getDevice();

        final Framebuffer framebuffer = Framebuffer.fromDrawData(drawData);

        final int commandListsCount = drawData.getCmdListsCount();

        if (!framebuffer.isArea() ||  commandListsCount <= 0)
            return;

        long totalVertexSize = 0;
        long totalIndexSize = 0;

        for (int i = 0; i < commandListsCount; i++) {
            totalVertexSize += (long) drawData.getCmdListVtxBufferSize(i) * ImDrawData.sizeOfImDrawVert();
            totalIndexSize += (long) drawData.getCmdListIdxBufferSize(i) * ImDrawData.sizeOfImDrawIdx();
        }

        vertexBuffer = ensureBufferCapacity(
                gpuDevice,
                vertexBuffer,
                "ImGui Vertex Buffer",
                GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
                totalVertexSize,
                4096
        );

        indexBuffer = ensureBufferCapacity(
                gpuDevice,
                indexBuffer,
                "ImGui Index Buffer",
                GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST,
                totalIndexSize,
                1024
        );

        uploadDrawLists(
                drawData,
                commandEncoder,
                commandListsCount
        );

        uploadProjectionMatrix(
                drawData,
                commandEncoder
        );
    }

    private void uploadProjectionMatrix(
            final ImDrawData drawData,
            final CommandEncoder commandEncoder
    ) {
        final float left = drawData.getDisplayPosX();
        final float right = drawData.getDisplayPosX() + drawData.getDisplaySizeX();
        final float top = drawData.getDisplayPosY();
        final float bottom = drawData.getDisplayPosY() + drawData.getDisplaySizeY();

        // Column-major order, as expected by the shader

        projectionMatrixBuffer.clear();

        projectionMatrixBuffer.putFloat(2.0f / (right - left));
        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(0.0f);

        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(2.0f / (top - bottom));
        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(0.0f);

        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(-1.0f);
        projectionMatrixBuffer.putFloat(0.0f);

        projectionMatrixBuffer.putFloat((right + left) / (left - right));
        projectionMatrixBuffer.putFloat((top + bottom) / (bottom - top));
        projectionMatrixBuffer.putFloat(0.0f);
        projectionMatrixBuffer.putFloat(1.0f);

        projectionMatrixBuffer.flip();

        commandEncoder.writeToBuffer(projectionMatrixUniform.slice(), projectionMatrixBuffer);
    }

    /**
     * Uploads both the index and the vertex buffer to GPU.
     *
     * @param drawData The current ImDrawData.
     * @param commandEncoder The current command encoder to use for uploading the buffers.
     * @param commandListsCount The count of command lists inside the draw data.
     */
    private void uploadDrawLists(
            final ImDrawData drawData,
            final CommandEncoder commandEncoder,
            final int commandListsCount // Is a parameter because it is already calculated in the uploadDrawData method, and we don't want to calculate it again
    ) {
        long vertexOffset = 0;
        long indexOffset = 0;

        for (int i = 0; i < commandListsCount; i++) {
            final int vertexSize = drawData.getCmdListVtxBufferSize(i) * ImDrawData.sizeOfImDrawVert();
            final int indexSize = drawData.getCmdListIdxBufferSize(i) * ImDrawData.sizeOfImDrawIdx();

            if (vertexSize > 0) {
                final ByteBuffer vertexData = drawData.getCmdListVtxBufferData(i);
                commandEncoder.writeToBuffer(vertexBuffer.slice(vertexOffset, vertexSize), vertexData);
                vertexOffset += vertexSize;
            }

            if (indexSize > 0) {
                final ByteBuffer indexData = drawData.getCmdListIdxBufferData(i);
                commandEncoder.writeToBuffer(indexBuffer.slice(indexOffset, indexSize), indexData);
                indexOffset += indexSize;
            }
        }
    }

    /**
     * Ensures that the provided buffer has enough capacity to hold the specified size.
     * If the buffer is null or too small, a new buffer is created with the specified label,
     * usage, and size (including extra space).
     *
     * @param gpuDevice The used GpuDevice to create the buffer if needed.
     * @param buffer The current buffer.
     * @param label The label for the new buffer if created.
     * @param usage The usage flags for the new buffer if created.
     * @param size The size to ensure the buffer can hold.
     * @param extraSpace Extra space to reduce reallocations
     *
     * @return Either the current buffer or the newly created one, if applicable.
     */
    private static GpuBuffer ensureBufferCapacity(
            final GpuDevice gpuDevice,
            GpuBuffer buffer,
            String label,
            int usage,
            long size,
            long extraSpace
    ) {
        if (buffer == null || buffer.size() < size) {
            if (buffer != null)
                buffer.close();

            return gpuDevice.createBuffer(
                    () -> label,
                    usage,
                    size + extraSpace
            );
        }

        return buffer;
    }

    /**
     * Creates and uploads the font textures from the current ImFontAtlas
     */
    private void createFontTextures() {
        disposeFontTextures();

        final GpuDevice gpuDevice = RenderSystem.getDevice();
        final ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        final CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();

        final ImInt textureWidth = new ImInt();
        final ImInt textureHeight = new ImInt();

        ByteBuffer fontTexturePixels = fontAtlas.getTexDataAsRGBA32(textureWidth, textureHeight);

        fontTexture = gpuDevice.createTexture(
                "ImGui Font Textures",
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST,
                GpuFormat.RGBA8_UNORM,
                textureWidth.get(), textureHeight.get(),
                1, 1
        );

        commandEncoder.writeToTexture(
                fontTexture,
                fontTexturePixels,
                0, 0,
                0, 0,
                textureWidth.get(),
                textureHeight.get()
        );

        fontTextureView = gpuDevice.createTextureView(fontTexture);

        fontTextureSampler = gpuDevice.createSampler(
                AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR, FilterMode.LINEAR,
                1,
                OptionalDouble.empty()
        );

        // Upload and mark as available
        commandEncoder.submit();
        fontAtlas.setTexID(1);
    }

    /**
     * Disposes the font textures and sampler, releasing their resources.
     * This method should be called when the font textures are no longer needed,
     * such as when the application is shutting down or when the font textures need to be recreated.
     */
    private void disposeFontTextures() {
        if (fontTextureSampler != null) {
            fontTextureSampler.close();
            fontTextureSampler = null;
        }

        if (fontTextureView != null) {
            fontTextureView.close();
            fontTextureView = null;
        }

        if (fontTexture != null) {
            fontTexture.close();
            fontTexture = null;
        }
    }

    public void dispose() {
        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }

        if (indexBuffer != null) {
            indexBuffer.close();
            indexBuffer = null;
        }

        if (projectionMatrixUniform != null) {
            projectionMatrixUniform.close();
            projectionMatrixUniform = null;
        }

        disposeFontTextures();
        renderPipeline = null;
        compiledRenderPipeline = null;
    }

}
