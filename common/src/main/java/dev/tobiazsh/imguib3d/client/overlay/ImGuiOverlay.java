// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.overlay;

public interface ImGuiOverlay {

    /**
     * Returns whether the ImGuiOverlay should render or not.
     */
    boolean isVisible();

    /**
     * Draw ImGui components to the overlay.
     */
    void draw();

    /**
     * Hook for creating any textures that should be created.
     */
    default void createTextures() {}

    /**
     * Hook for creating any fonts that should be created.
     */
    default void createFonts() {}

    /**
     * Hook for disposing anything that should be disposed.
     */
    default void dispose() {}

    /**
     * Renders the method if `isVisible` is `true`, otherwise not.
     */
    default void render() {
        if (isVisible()) {
            createTextures();
            createFonts();
            draw();
        }
    }

    int priority();
    String getId();

}
