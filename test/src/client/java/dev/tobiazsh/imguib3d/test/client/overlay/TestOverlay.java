package dev.tobiazsh.imguib3d.test.client.overlay;

import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import imgui.ImGui;

public class TestOverlay implements ImGuiOverlay {

    private boolean isVisible = true;
    private boolean showImage = false;

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void draw() {
        if (ImGui.begin("Test Overlay")) {
            ImGui.button((showImage ? "Hide" : "Show") + " highly confidential image (fr fr)");
            ImGui.text("Hello");

            if (showImage)
                ImGui.image();

            ImGui.end();
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
