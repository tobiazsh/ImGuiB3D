package dev.tobiazsh.imguib3d.client;

import imgui.ImGui;

public class ImGuiTest {

    public static void render() {
        ImGuiImplementation.getInstance().draw(_ -> {
            ImGui.showDemoWindow();
        });
    }

}
