package dev.tobiazsh.imguib3d.screen;

import dev.tobiazsh.imguib3d.client.ImGuiDrawable;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImString;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen implements ImGuiDrawable {

    public TestScreen() {
        super(Component.literal("Test Screen"));
    }

    private final ImString inputText = new ImString(128);

    @Override
    public void draw(ImGuiIO io) {
        if (ImGui.begin("Test Window")) {
            ImGui.text("Hello, ImGui!");
            ImGui.inputText("Test", inputText);
            ImGui.end();
        }
    }
}
