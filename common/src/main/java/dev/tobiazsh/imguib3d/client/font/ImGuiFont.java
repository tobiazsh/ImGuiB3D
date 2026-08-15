// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.font;

import imgui.ImFont;
import imgui.ImFontConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ImGuiFont {
    private ImFont imFont;
    private final ImGuiFontKey fontKey;
    private final byte[] fontData;

    private volatile boolean isLoaded;
    private volatile boolean isDisposed;

    public static final ImFontConfig DEFAULT_FONT_CONFIG;

    static {
        DEFAULT_FONT_CONFIG = new ImFontConfig();
        DEFAULT_FONT_CONFIG.setFontDataOwnedByAtlas(false);
    }

    ImGuiFont(final ImFont imFont, final ImGuiFontKey fontKey, final byte[] fontData) {
        this.imFont = imFont;
        this.fontKey = fontKey;
        this.fontData = fontData;
        this.isLoaded = false;
    }

    /**
     * Load a font from a file in TTF format. The font will be loaded asynchronously and registered in the
     * FontManager. If a font with the same name and size is already present in the FontManager, the existing font will
     * be returned.
     *
     * @apiNote If you want to load a font from an InputStream (e.g. resources), you may be interested in
     *         {@link #loadFromStreamTTF(InputStream, String, int, FontImportance)} instead
     *
     * @param path The path to the file to read the font data from.
     * @param size The size of the font to load.
     * @param importance The importance of the font for registration budget purposes.
     *                   See {@link FontImportance} for more details.
     * @return A CompletableFuture that will complete as soon as the font is loaded and registered in the FontManager,
     *         meaning it could be ready right away if the font has existed in the FontManager beforehand.
     */
    public static ImGuiFontFuture loadFromFileTTF(final Path path, final int size, final FontImportance importance) {
        final ImGuiFontKey key = new ImGuiFontKey(path.toString(), size);

        // Check if font is already registered in the FontManager
        final Optional<ImGuiFont> loadedFont = FontManager.getInstance().getFont(key);

        if (loadedFont.isPresent())
            return ImGuiFontFuture.completedFuture(loadedFont.get());

        final ImGuiFontFuture future = ImGuiFontFuture.ofDelayed(key, importance);

        // Run font data loading in different thread to not block the render thread.
        CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = Files.readAllBytes(path);
                future.setFontData(bytes);
                FontManager.getInstance().queue(future);
            } catch (IOException e) {
                CompletableFuture.failedFuture(e);
            }
        });

        return future;
    }

    /**
     * Load a font from a file in TTF format. The font will be loaded asynchronously and registered in the
     * FontManager. If a font with the same name and size is already present in the FontManager, the existing font will
     * be returned. Defaults to {@link FontImportance#MEDIUM} for registration budget. To specify a different
     * importance, use {@link #loadFromFileTTF(Path, int, FontImportance)} instead.
     *
     * @apiNote If you want to load a font from an InputStream (e.g. resources), you may be interested in
     *         {@link #loadFromStreamTTF(InputStream, String, int, FontImportance)} instead
     *
     * @param path The path to the file to read the font data from.
     * @param size The size of the font to load.
     * @return A CompletableFuture that will complete as soon as the font is loaded and registered in the FontManager,
     *         meaning it could be ready right away if the font has existed in the FontManager beforehand.
     */
    public static ImGuiFontFuture loadFromFileTTF(final Path path, final int size) {
        return loadFromFileTTF(path, size, FontImportance.MEDIUM);
    }

    /**
     * Load a font from an InputStream in TTF format. The font will be loaded asynchronously and registered in the
     * FontManager. May be used to load fonts from resources or other sources where a file path is not available.
     * If a font with the same name and size is already present in the FontManager, the existing font will be returned.
     *
     * @apiNote If you want to load a font from a file outside of resources, you may be interested in
     *          {@link #loadFromFileTTF(Path, int, FontImportance)} instead.
     *
     * @param stream The InputStream to read the font data from. The stream will be closed after reading.
     * @param name The name of the font to load. This is used as the key together with the size in the FontManager.
     * @param importance The importance of the font for registration budget purposes.
     *                   See {@link FontImportance} for more details.
     * @param size The size of the font to load.
     * @return A CompletableFuture that will complete as soon as the font is loaded and registered in the FontManager,
     *         meaning it could be ready right away if the font has existed in the FontManager beforehand.
     */
    public static ImGuiFontFuture loadFromStreamTTF(
            final InputStream stream,
            final String name,
            final int size,
            final FontImportance importance
    ) {
        final ImGuiFontKey key = new ImGuiFontKey(name, size);

        // Check if font is already registered in the FontManager
        final Optional<ImGuiFont> loadedFont = FontManager.getInstance().getFont(key);

        if (loadedFont.isPresent())
            return ImGuiFontFuture.completedFuture(loadedFont.get());

        final ImGuiFontFuture future = ImGuiFontFuture.ofDelayed(key, importance);

        // Run font data loading in different thread to not block the render thread.
        CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = stream.readAllBytes();
                future.setFontData(bytes);
                FontManager.getInstance().queue(future);
                stream.close();
            } catch (IOException e) {
                CompletableFuture.failedFuture(e);
            }
        });

        return future;
    }

    /**
     * Load a font from an InputStream in TTF format. The font will be loaded asynchronously and registered in the
     * FontManager. May be used to load fonts from resources or other sources where a file path is not available.
     * If a font with the same name and size is already present in the FontManager, the existing font will be returned.
     * Defaults to {@link FontImportance#MEDIUM} for registration budget. To specify a different
     * importance, use {@link #loadFromStreamTTF(InputStream, String, int, FontImportance)} instead.
     *
     * @apiNote If you want to load a font from a file outside of resources, you may be interested in
     *          {@link #loadFromFileTTF(Path, int, FontImportance)} instead.
     *
     * @param stream The InputStream to read the font data from. The stream will be closed after reading.
     * @param name The name of the font to load. This is used as the key together with the size in the FontManager.
     * @param size The size of the font to load.
     * @return A CompletableFuture that will complete as soon as the font is loaded and registered in the FontManager,
     *         meaning it could be ready right away if the font has existed in the FontManager beforehand.
     */
    public static ImGuiFontFuture loadFromStreamTTF(
            final InputStream stream,
            final String name,
            final int size
    ) {
        return loadFromStreamTTF(stream, name, size, FontImportance.MEDIUM);
    }

    void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
    }

    void setFont(final ImFont font) {
        this.imFont = font;
    }

    public ImFont getImFont() {
        return imFont;
    }

    public ImGuiFontKey getFontKey() {
        return fontKey;
    }

    public byte[] getFontData() {
        return fontData;
    }

    public boolean isLoaded() {
        return isLoaded && !isDisposed;
    }

    /**
     * Guaranteed display of the font next frame. This should only be called for critical memory management, as it will
     * cost extra performance. For standard removals, use {@link #disposeLater()} instead.
     */
    public void disposeNow() {
        isDisposed = true;
        FontManager.getInstance().nextDisposal();
    }

    /**
     * Disposes the font when the font atlas is rebuilt next. This could happen next frame or never, depending on
     * whether a new font is registered or not. This should be the standard way to dispose of a font, as it does not
     * cost extra performance like {@link #disposeNow()} does.
     * For critical tasks, use {@link #disposeNow()} instead.
     */
    public void disposeLater() {
        isDisposed = true;
    }

    public boolean isDisposed() {
        return isDisposed;
    }
}
