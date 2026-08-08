// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client;

import imgui.ImDrawData;

public record Framebuffer(int width, int height) {

    /**
     * Returns whether the framebuffer is a valid area (width > 0 and height > 0)
     */
    public boolean isArea() {
        return width > 0 && height > 0;
    }

    /**
     * Creates a new framebuffer from ImGui draw data
     */
    public static Framebuffer fromDrawData(final ImDrawData drawData) {
        return new Framebuffer(
                (int) (drawData.getDisplaySizeX() * drawData.getFramebufferScaleX()),
                (int) (drawData.getDisplaySizeY() * drawData.getFramebufferScaleY())
        );
    }
}
