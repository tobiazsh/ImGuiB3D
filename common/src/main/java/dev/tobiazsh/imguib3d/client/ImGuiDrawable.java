package dev.tobiazsh.imguib3d.client;

import imgui.ImGuiIO;

@FunctionalInterface
public interface ImGuiDrawable {
    void draw(final ImGuiIO io);
}
