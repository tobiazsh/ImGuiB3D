// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.overlay;

import dev.tobiazsh.imguib3d.TestModInitializer;
import dev.tobiazsh.imguib3d.client.font.FontIdentifier;
import dev.tobiazsh.imguib3d.client.font.FontManager;
import dev.tobiazsh.imguib3d.client.font.ImGuiFont;
import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTexture;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTextureImpl;
import imgui.ImGui;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class TestOverlay implements ImGuiOverlay {

    private boolean isVisible = true;
    private boolean showImage = false;
    private boolean customFontPushed = false;
    private boolean isRobotoActive = false;
    private @Nullable ImGuiTexture flowerTexture;
    private @Nullable ImGuiFont robotoSlab;
    private @Nullable ImGuiFont sekuya;

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void draw() {
        createTextures();
        createFonts();

        if (robotoSlab != null && robotoSlab.isLoaded() && sekuya != null && sekuya.isLoaded()) {
            ImGui.pushFont(isRobotoActive ? robotoSlab.getImFont() : sekuya.getImFont(), ImGui.getFontSize() * 2);
            customFontPushed = true;
        }

        if (ImGui.begin("Test Overlay")) {
            ImGui.text("Hello");

            if (ImGui.button((showImage ? "Hide" : "Show") + " highly confidential image (fr fr)"))
                showImage = !showImage;

            if (ImGui.button("Switch font"))
                isRobotoActive = !isRobotoActive;

            if (ImGui.button("Dispose fonts"))
                FontManager.getInstance().disposeAll();

            if (showImage)
                ImGui.image(flowerTexture.getTextureId(), 512, 512);

            ImGui.end();
        }

        if (customFontPushed) {
            ImGui.popFont();
            customFontPushed = false;
        }
    }

    private void createTextures() {
        if (flowerTexture == null) {
            try (InputStream is = TestOverlay.class.getResourceAsStream("/assets/testmod/textures/flower.png")) {
                if (is == null)
                    throw new RuntimeException("Failed to load flower texture: resource not found");

                flowerTexture = new ImGuiTextureImpl(is, "flower_texture");
            } catch (IOException e) {
                throw new RuntimeException("Failed to load flower texture", e);
            }
        }
    }

    private void createFonts() {
        if (robotoSlab == null) {
            try (InputStream is = TestOverlay.class.getResourceAsStream("/assets/testmod/fonts/RobotoSlab-Regular.ttf")) {
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
            try (InputStream is = TestOverlay.class.getResourceAsStream("/assets/testmod/fonts/Sekuya-Regular.ttf")) {
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
    public int priority() {
        return 0;
    }

    @Override
    public String getId() {
        return "";
    }
}
