package dev.tobiazsh.imguib3d.test.screen;

import dev.tobiazsh.imguib3d.client.ImGuiDrawable;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImString;

public interface TestScreen extends ImGuiDrawable {

    ImString inputText = new ImString(128);

    @Override
    default void draw(ImGuiIO io) {
        if (ImGui.begin("Test Window")) {
            ImGui.text("Hello, ImGui!");
            ImGui.inputText("Test", inputText);
            ImGui.end();
        }
    }

}
