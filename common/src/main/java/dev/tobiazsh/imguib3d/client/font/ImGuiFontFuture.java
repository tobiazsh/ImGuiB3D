// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.font;

import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * A future that represents a font that is being loaded asynchronously. Typically, the font data is registered in the
 * render thread between frames, not immediately, which is why this future exists.
 */
public final class ImGuiFontFuture extends CompletableFuture<ImGuiFont> {

    private final FontImportance importance;
    private final ImGuiFontKey fontKey;
    private volatile byte @Nullable [] fontData;

    private ImGuiFontFuture(
            final ImGuiFontKey fontKey,
            final byte @Nullable [] fontData,
            final FontImportance importance
    ) {
        this.fontKey = fontKey;
        this.fontData = fontData;
        this.importance = importance;
    }

    public ImGuiFontKey getFontKey() {
        return fontKey;
    }

    /**
     * Returns the bytes of the font file, not the usable font itself!
     * Even if this is null, it does not mean that the future is useless. Maybe the future is already completed.
     */
    public byte[] getFontData() {
        return fontData;
    }

    /**
     * Returns the importance of the font for registration budget purposes.
     * See {@link FontImportance} for more details.
     */
    public FontImportance getImportance() {
        return importance;
    }

    /**
     * Sets the font data for this future. This method can only be called if the font data is empty, and will
     * throw an exception if byte array is not empty.
     *
     * @param fontData The font data to set for this future.
     * @throws IllegalStateException Thrown if the font data is already set for this future.
     * @throws IllegalArgumentException Thrown if the font data is null or empty.
     */
    public void setFontData(byte[] fontData) throws IllegalStateException, IllegalArgumentException {
        if (isFontDataReady())
            throw new IllegalStateException("Font data is already set for font " + fontKey);

        if (fontData == null || fontData.length == 0)
            throw new IllegalArgumentException("Font data cannot be null or empty for font " + fontKey);

        this.fontData = fontData;
    }

    /**
     * Checks whether the font data is empty (null) or not.
     * Even if the font data is empty, it does not mean that the future is useless. Maybe the future is already completed.
     */
    public boolean isFontDataReady() {
        return fontData != null;
    }

    /**
     * Creates a new ImGuiFontFuture with the given font key and an empty byte array. This method should be used when
     * creating the ImGuiFontFuture on the main thread while reading the font file's data on a separate thread (for
     * performance reasons) and later setting the font data using {@link #setFontData(byte[])}.
     *
     * @param fontKey The font key for the future.
     * @param importance The importance of the font for registration budget purposes.
     *                   See {@link FontImportance} for more details.
     * @return A new ImGuiFontFuture with the given font key and an empty byte array.
     */
    public static ImGuiFontFuture ofDelayed(final ImGuiFontKey fontKey, final FontImportance importance) {
        return new ImGuiFontFuture(fontKey, null, importance);
    }

    /**
     * Creates a new ImGuiFontFuture with the given font key and font data. This method should be used when the font's
     * data is already available at the point of creation, and the font can be loaded immediately.
     *
     * @param fontKey The font key for the future.
     * @param fontData The font data for the future.
     * @param importance The importance of the font for registration budget purposes.
     *                   See {@link FontImportance} for more details.
     * @return A new ImGuiFontFuture with the given font key and font data.
     */
    public static ImGuiFontFuture of(
            final ImGuiFontKey fontKey,
            final byte[] fontData,
            final FontImportance importance
    ) {
        return new ImGuiFontFuture(fontKey, fontData, importance);
    }

    /**
     * Creates a new ImGuiFontFuture that is already completed with the given font.
     * Font data will be null, as it is not needed anymore. If you need the font data, use {@link ImGuiFontFuture#get()}
     * in combination with {@link ImGuiFont#getImFont()} instead.
     *
     * @param font The font to complete the future with.
     * @return A new ImGuiFontFuture that is already completed with the given font.
     */
    public static ImGuiFontFuture completedFuture(final ImGuiFont font) {
        final ImGuiFontFuture future = ImGuiFontFuture.ofDelayed(font.getFontKey(), FontImportance.HIGH);
        future.complete(font);
        return future;
    }
}
