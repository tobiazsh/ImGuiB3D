// Portions of this code are derived from Enaium's "fabric-mod-ImGui" project:
// https://github.com/Enaium/fabric-mod-ImGui
//
// Specifically, code was derived from:
// https://github.com/Enaium/fabric-mod-ImGui/blob/2fe781209243484223931175b59d439872bea934/game/26.2/src/main/java/cn/enaium/fabric/imgui/DefaultImGui.java
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

package dev.tobiazsh.imguib3d.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.opengl.*;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.*;
import dev.tobiazsh.imguib3d.client.backend.ImGuiImplBlaze3D;
import dev.tobiazsh.imguib3d.client.access.GlDeviceAccessor;
import dev.tobiazsh.imguib3d.client.access.GpuDeviceAccessor;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTexture;
import dev.tobiazsh.imguib3d.client.texture.TextureManager;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PreferredGraphicsApi;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AutoService(ImGuiImplementation.class)
public class ImGuiImpl extends ImGuiImplementation {
    public final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    public @Nullable ImGuiImplGl3 imGuiImplGl3;
    public @Nullable ImGuiImplBlaze3D imGuiImplBlaze3D;

    public ImGuiImpl() {
        this(null);
    }

    public ImGuiImpl(@Nullable String id) {
        super(id);

        if (!Minecraft.getInstance()
                .options
                .preferredGraphicsBackend()
                .get()
                .equals(PreferredGraphicsApi.VULKAN)
        ) {
            imGuiImplGl3 = new ImGuiImplGl3();
        } else {
            imGuiImplBlaze3D = new ImGuiImplBlaze3D();
        }
    }

    @Override
    protected void init(long windowHandle) {
        imGuiImplGlfw.init(windowHandle, true);

        if (imGuiImplGl3 != null)
            imGuiImplGl3.init();

        // ImGuiImplBlaze3D is lazily initialized in newFrame()
    }

    @Override
    public void draw(ImGuiDrawable imGuiDrawable) {
        final RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        final GpuDevice gpuDevice = RenderSystem.getDevice();

        TextureManager.getInstance().clean();

        for (ImGuiTexture queued : TextureManager.getInstance().getQueuedTextures())
            queued.upload();

        if (imGuiImplBlaze3D != null)
            drawBlaze3D(gpuDevice, renderTarget, imGuiDrawable);
        else
            drawOpenGL(gpuDevice, renderTarget, imGuiDrawable);

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long glfwContextPointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(glfwContextPointer); // Restore the original OpenGL context after rendering ImGui windows
        }
    }

    private void drawOpenGL(
            @NonNull final GpuDevice gpuDevice,
            @NonNull final RenderTarget renderTarget,
            @NonNull final ImGuiDrawable imGuiDrawable
    ) {
        if (imGuiImplGl3 == null)
            return; // Just for safety in case the method is being called somewhere where it shouldn't be

        final GpuDeviceBackend gpuDeviceBackend = ((GpuDeviceAccessor) gpuDevice).imGuiB3D$getBackend();
        final DirectStateAccess directStateAccess = ((GlDeviceAccessor) gpuDeviceBackend).imGuiB3D$getDirectStateAccess();
        final FrameBufferCache frameBufferCache = ((GlDeviceAccessor) gpuDeviceBackend).imGuiB3D$getFrameBufferCache();

        final List<FrameBufferAttachment> attachmentList = Collections.singletonList((GlTexture) renderTarget.getColorTexture());

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferCache.getFbo(directStateAccess, attachmentList, null));
        GL11C.glViewport(0, 0, renderTarget.width, renderTarget.height);

        imGuiImplGl3.newFrame();
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();

        imGuiDrawable.draw(ImGui.getIO());

        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    private void drawBlaze3D(
            @NonNull final GpuDevice gpuDevice,
            @NonNull final RenderTarget renderTarget,
            @NonNull final ImGuiDrawable imGuiDrawable
    ) {
        if (imGuiImplBlaze3D == null)
            return; // Just for safety in case the method is being called somewhere where it shouldn't be

        imGuiImplBlaze3D.newFrame();
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();

        imGuiDrawable.draw(ImGui.getIO());

        ImGui.render();

        final ImDrawData drawData = ImGui.getDrawData();
        final CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();

        imGuiImplBlaze3D.uploadDrawData(drawData, commandEncoder);

        try (
                RenderPass renderPass = commandEncoder.createRenderPass(
                        () -> "ImGui RenderPass",
                        renderTarget.getColorTextureView(),
                        Optional.empty()
                )
        ) {
            imGuiImplBlaze3D.renderDrawData(drawData, renderPass);
        }

        commandEncoder.submit();
    }

    @Override
    public void onFontAtlasBuild() {

    }

    @Override
    public void onFontAtlasDispose() {

    }

    @Override
    public void onFontAtlasRebuild() {

    }

    @Override
    protected void addFonts(ImGuiIO io) {

    }

    @Override
    public boolean isCompatibleWithEnvironment() {
        return ImGuiB3DClient.isMCVersionCompatible();
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public void destroy() {
        if (imGuiImplGl3 != null) {
            imGuiImplGl3.shutdown();
            imGuiImplGl3 = null;
        }

        if (imGuiImplBlaze3D != null) {
            imGuiImplBlaze3D.dispose();
            imGuiImplBlaze3D = null;
        }

        imGuiImplGlfw.shutdown();
        super.destroy();
    }
}
