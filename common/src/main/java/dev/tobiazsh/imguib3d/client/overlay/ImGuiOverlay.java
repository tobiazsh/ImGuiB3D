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
     * Hook for disposing anything that should be disposed.
     */
    default void dispose() {}

    /**
     * Renders the method if `isVisible` is `true`, otherwise not.
     */
    default void render() {
        if (isVisible())
            draw();
    }

    int priority();
    String getId();

}
