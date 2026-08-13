// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.overlay;

import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTexture;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTextureImpl;
import imgui.ImGui;

import java.io.IOException;
import java.io.InputStream;

public class TestOverlay implements ImGuiOverlay {

    private boolean isVisible = true;
    private boolean showImage = false;
    private ImGuiTexture flowerTexture;
    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void draw() {
        createTextures();

        if (ImGui.begin("Test Overlay")) {
            ImGui.text("Hello");

            if (ImGui.button((showImage ? "Hide" : "Show") + " highly confidential image (fr fr)"))
                showImage = !showImage;

            if (showImage)
                ImGui.image(flowerTexture.getTextureId(), 512, 512);

            ImGui.end();
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

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String getId() {
        return "";
    }
}
