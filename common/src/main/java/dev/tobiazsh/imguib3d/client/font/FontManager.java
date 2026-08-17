// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.font;

import imgui.ImFont;
import imgui.ImFontAtlas;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static dev.tobiazsh.imguib3d.client.font.ImGuiFont.DEFAULT_FONT_CONFIG;

public final class FontManager {

    private static final FontManager INSTANCE = new FontManager();

    private final Map<ImGuiFontKey, ImGuiFontFuture> queuedFonts = new ConcurrentHashMap<>(); // Maybe use ordered map for better performance?
    private final Map<ImGuiFontKey, ImGuiFont> loadedFonts = new ConcurrentHashMap<>();

    private final RegistrationBudget budget = RegistrationBudget.defaultBudget();

    private boolean needsDisposal = false;

    public static FontManager getInstance() {
        return INSTANCE;
    }

    private FontManager() {}

    /**
     * Register all queued fonts to the ImGui context, however a font is not guaranteed to be registered. If the budget
     * of one importance category is empty, no more fonts of this category will be registered. This does not mean that a
     * {@link FontImportance#MEDIUM} font will be registered before a {@link FontImportance#LOW} font will, it is just
     * more likely for a font of higher importance to be registered first. The budget is reset after each call to this
     * method. This method should be called between each frame before building the font atlas.
     *
     * @param atlas The ImFontAtlas to register the fonts to.
     */
    public void registerAllQueued(final ImFontAtlas atlas) {
        budget.reset();

        if (queuedFonts.isEmpty())
            return;

        final Iterator<Map.Entry<ImGuiFontKey, ImGuiFontFuture>> iterator = queuedFonts.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<ImGuiFontKey, ImGuiFontFuture> entry = iterator.next();
            final FontImportance importance = entry.getValue().getImportance();

            if (budget.pass(importance)) {
                registerFont(atlas, entry.getValue());
                iterator.remove();
            }
        }
    }

    /**
     * Register a font to the ImGui context. This method should be called between frames.
     * Does not share the importance budget with other fonts, so it is guaranteed to be registered if called.
     *
     * @param atlas The ImFontAtlas to register the font to.
     * @param fontFuture The font future to register.
     */
    public void registerFont(final ImFontAtlas atlas, final ImGuiFontFuture fontFuture) {
        byte[] fontData = fontFuture.getFontData();

        if (fontData == null || fontData.length == 0) {
            fontFuture.completeExceptionally(new IllegalArgumentException("Font data is null or empty"));
            return;
        }

        final ImFont font = atlas.addFontFromMemoryTTF(fontData, fontFuture.getFontKey().fontSize(), DEFAULT_FONT_CONFIG);
        final ImGuiFont imguiFont = new     ImGuiFont(font, fontFuture.getFontKey(), fontFuture.getFontData());

        imguiFont.setLoaded(true);
        fontFuture.complete(imguiFont);

        loadedFonts.put(fontFuture.getFontKey(), imguiFont);
        queuedFonts.remove(fontFuture.getFontKey());
    }


    /**
     * Reregister all loaded fonts to the ImGui context. This method should be called between frames.
     *
     * @param atlas The ImFontAtlas to register the fonts to.
     */
    public void reregisterAllLoaded(final ImFontAtlas atlas) {
        if (loadedFonts.isEmpty())
            return;

        final Iterator<Map.Entry<ImGuiFontKey, ImGuiFont>> iterator = loadedFonts.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<ImGuiFontKey, ImGuiFont> entry = iterator.next();
            final ImGuiFont font = entry.getValue();

            if (font.isDisposed()) {
                font.setLoaded(false);
                iterator.remove();
                continue;
            }

            reregister(atlas, font);
        }
    }

    /**
     * Reregister a font to the ImGui context. This method should be called between frames.
     *
     * @param atlas The ImFontAtlas to register the font to.
     * @param font The font to register.
     */
    public void reregister(final ImFontAtlas atlas, final ImGuiFont font) {
        final ImFont imFont = atlas.addFontFromMemoryTTF(font.getFontData(), font.getFontKey().fontSize(), DEFAULT_FONT_CONFIG);

        font.setFont(imFont);
        font.setLoaded(true);

        loadedFonts.put(font.getFontKey(), font);
    }

    public void disposeMarkedIfNeeded() {
        if (!needsDisposal)
            return;

        loadedFonts.values().removeIf(font -> {
            if (font.isDisposed()) {
                font.setLoaded(false);
                return true;
            }
            return false;
        });

        needsDisposal = false;
    }

    /**
     * Marks all registered fonts as unloaded.
     *
     * <p>Does <b>NOT</b> actually unload the fonts from the manager; only marks the fonts as unloaded.
     */
    public void unloadAll() {
        loadedFonts.forEach((_, imGuiFont) -> imGuiFont.setLoaded(false));
    }

    /**
     * Catch a queued font by its key before it is loaded in ImGui.
     *
     * @param fontKey The key of the font to catch.
     * @return An Optional containing the font future if it is queued, or an empty Optional if it is not queued.
     */
    public Optional<ImGuiFontFuture> catchQueuedFont(ImGuiFontKey fontKey) {
        return Optional.ofNullable(queuedFonts.get(fontKey));
    }

    /**
     * Get a font by its key. If the font is not loaded, it will return an empty Optional.
     *
     * @param fontKey The key of the font to get.
     * @return An Optional containing the font if it is loaded, or an empty Optional if it is not loaded.
     */
    public Optional<ImGuiFont> getFont(ImGuiFontKey fontKey) {
        return Optional.ofNullable(loadedFonts.get(fontKey));
    }

    private void clearLoaded() {
        loadedFonts.clear();
    }

    private void clearQueued() {
        queuedFonts.clear();
    }

    private void clearAll() {
        clearLoaded();
        clearQueued();
    }

    public void queue(ImGuiFontFuture fontFuture) {
        queuedFonts.put(fontFuture.getFontKey(), fontFuture);
    }

    public boolean hasQueuedFonts() {
        return !queuedFonts.isEmpty();
    }

    /**
     * Marks that the next frame should dispose of all fonts that are marked as disposed.
     * If you call {@link ImGuiFont#disposeNow()} on a font, but not call this method, the font will not be disposed.
     * This exists to improve performance, as otherwise the font manager would have to check the whole map of loaded
     * fonts every frame, which is not necessary if no fonts are disposed.
     */
    public void nextDisposal() {
        needsDisposal = true;
    }

    /**
     * Marks all fonts as disposed
     */
    public void disposeAll() {
        loadedFonts.values().forEach(ImGuiFont::disposeLater); // Dispose later to avoid multiple unnecessary calls to nextDisposal()
        nextDisposal(); // One call to nextDisposal() is enough to dispose all fonts that are marked as disposed
    }

}
