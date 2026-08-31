package dev.tobiazsh.imguib3d.client.font;

import imgui.ImGui;

/**
 * A scope for pushing/popping ImGui fonts painlessly.
 * <hr/>
 * <p>
 *     ⚠️ Attention! ⚠️<br>
 *     This class is NOT thread-safe, because of race conditions with the pushing/popping.
 *     It is therefore EXTREMELY IMPORTANT that each push is followed by a pop <b>in the same thread, in the same
 *     {@link ImGuiFontScope} instance.</b>
 * </p>
 * <p>
 *     As mentioned above, you should <b>always match the number of pushed and pops.</b> If you don't, ImGui is
 *     probable to crash.
 *     <br>
 *     Theoretically speaking, you can't have too many pops since this class will just ignore the rest, but you
 *     <b>can</b> have <b>not enough pops.</b>
 * </p>
 * <p> <b>It is best practice to always match the number of pushes and pops.</b>
 */
public final class ImGuiFontScope {

    /*
    * --------------------------------- NOTE ---------------------------------------------------------------------------
    * If there's a push/pop mismatch, ImGui will take care of it and crash accordingly. No need for us to check if
    * `pushDepth == 0` when each frame ends.
    * ------------------------------------------------------------------------------------------------------------------
    * */

    private int pushDepth;

    private ImGuiFontScope() {
        this.pushDepth = 0;
    }

    /**
     * Pushes the given font with the given size to ImGui.
     *
     * <p>
     *     <b>A push is not guaranteed to happen</b>, depending on whether the font has been loaded yet or not.
     *     It is therefore extremely important to always use this classes
     *     {@link #pop()} method to pop the font, not ImGui's {@link ImGui#popFont()} because ImGui will have no idea
     *     whether it got actually pushed or not.
     * </p>
     * @param font The font to push. If the font is null or not loaded, no push will happen.
     * @param size The size of the font to push.
     */
    public void push(ImGuiFont font, int size) {
        if (font != null && font.isLoaded()) {
            ImGui.pushFont(font.getImFont(), size);
            pushDepth++;
        }
    }

    /**
     * Pushes the given font with the size of {@link ImGui#getFontSize()} to ImGui.
     *
     * <p>
     *     <b>A push is not guaranteed to happen</b>, depending on whether the font has been loaded yet or not.
     *     It is therefore extremely important to always use this classes
     *     {@link #pop()} method to pop the font, not ImGui's {@link ImGui#popFont()} because ImGui will have no idea
     *     whether it got actually pushed or not.
     * </p>
     * @param font The font to push. If the font is null or not loaded, no push will happen.
     */
    public void push(ImGuiFont font) {
        push(font, ImGui.getFontSize());
    }

    /**
     * Pops the last pushed font from ImGui.
     *
     * <p>
     *     <b>Important:</b> This method will only pop a font if there was a push before. If there was no push, this
     *     method will do nothing. This is to prevent ImGui from crashing due to a push/pop mismatch.
     * </p>
     */
    public void pop() {
        if (pushDepth > 0) {
            ImGui.popFont();
            pushDepth--;
        }
    }

    /**
     * Returns a new instance of {@link ImGuiFontScope}.
     */
    public static ImGuiFontScope create() {
        return new ImGuiFontScope();
    }
}
