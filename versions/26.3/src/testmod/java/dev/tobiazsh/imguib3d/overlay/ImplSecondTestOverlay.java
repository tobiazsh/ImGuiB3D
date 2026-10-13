// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.overlay;

import dev.tobiazsh.imguib3d.TestModInitializer;
import dev.tobiazsh.imguib3d.client.font.FontIdentifier;
import dev.tobiazsh.imguib3d.client.font.ImGuiFont;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTextureImpl;
import dev.tobiazsh.imguib3d.test.log.PrimitiveLogger;
import dev.tobiazsh.imguib3d.test.overlay.SecondTestOverlay;
import imgui.ImGui;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ImplSecondTestOverlay extends SecondTestOverlay {

    @Override
    public void createTextures() {
        if (flowerTexture == null) {
            try (InputStream is = ImplSecondTestOverlay.class.getResourceAsStream("/assets/testmod/textures/flower.png")) {
                if (is == null)
                    throw new RuntimeException("Failed to load flower texture: resource not found");

                flowerTexture = new ImGuiTextureImpl(is, "flower_texture");
            } catch (IOException e) {
                throw new RuntimeException("Failed to load flower texture", e);
            }
        }

        testBuffer = MemoryUtil.memAlloc(1024 * 1024); // Allocate 1MB of memory for testing
        testBuffer.put("Hello, this is a test".getBytes(StandardCharsets.UTF_8));
        testBuffer.flip();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createFonts() {
        if (robotoSlab == null) {
            try (InputStream is = ImplSecondTestOverlay.class.getResourceAsStream("/assets/testmod/fonts/RobotoSlab-Regular.ttf")) {
                if (is == null)
                    throw new RuntimeException("Failed to load Roboto Slab font: resource not found");

                ImGuiFont.loadFromStreamTTF(
                        is,
                        FontIdentifier.of(TestModInitializer.getModId(), "Roboto Slab"),
                        ImGui.getFontSize() * 2
                ).thenAccept(
                        font -> robotoSlab = font
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Roboto Slab font", e);
            }
        }

        if (sekuya == null) {
            try (InputStream is = ImplSecondTestOverlay.class.getResourceAsStream("/assets/testmod/fonts/Sekuya-Regular.ttf")) {
                if (is == null)
                    throw new RuntimeException("Failed to load Sekuya Slab font: resource not found");

                ImGuiFont.loadFromStreamTTF(
                        is,
                        FontIdentifier.of(TestModInitializer.getModId(), "Sekuya"),
                        ImGui.getFontSize() * 2
                ).thenAccept(
                        font -> sekuya = font
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Sekuya font", e);
            }
        }
    }

    @Override
    public void dispose() {
        if (testBuffer != null) {
            MemoryUtil.memFree(testBuffer);
            TestModInitializer.LOGGER.info("TestOverlay disposed!");
            testBuffer = null;
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getModId() {
        return TestModInitializer.getModId();
    }

    @Override
    public PrimitiveLogger getLogger() {
        return TestModInitializer.getPrimitiveLogger();
    }
}
