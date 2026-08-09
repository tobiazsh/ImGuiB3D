package dev.tobiazsh.imguib3d.client;

import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import imgui.ImGui;

public class ImGuiTestOverlay implements ImGuiOverlay {

    private boolean isVisible = true;

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void draw() {
        ImGui.showDemoWindow();
        if (ImGui.begin("Hello from Blaze3D")) {
            ImGui.text("Hello, world!");
            ImGui.end();
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String getId() {
        return "TestOverlay";
    }
}
