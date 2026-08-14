// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client;

import dev.tobiazsh.imguib3d.client.compatibility.CompatibilityChecker;
import dev.tobiazsh.imguib3d.client.exception.NoImplementationException;
import dev.tobiazsh.imguib3d.client.font.FontManager;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.ServiceLoader;

public abstract class ImGuiImplementation implements CompatibilityChecker {

    private static final @Nullable ImGuiImplementation INSTANCE =
            ServiceLoader.load(ImGuiImplementation.class)
                    .stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(ImGuiImplementation::isCompatibleWithEnvironment)
                    .max(Comparator.comparingInt(ImGuiImplementation::priority))
                    .orElse(null);

    public static ImGuiImplementation getInstance() throws NoImplementationException {
        if (INSTANCE == null)
            throw new NoImplementationException(
            """
            There's no ImGui Implementation available for the current environment.
            Maybe you're using an incompatible version?
            """
            );

        return INSTANCE;
    }

    public final @Nullable String id;
    private static final int CONFIG_FLAGS = ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable;

    protected ImGuiImplementation(@Nullable String id) {
        this.id = id;
    }
    public ImGuiImplementation() {
        this(null);
    }

    public abstract void draw(ImGuiDrawable imGuiDrawable);

    protected abstract void init(final long windowHandle);

    public void initialize(final long windowHandle) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO io = ImGui.getIO();

        configureFonts(io);
        configureFlags(io);
        configureConfig(io);

        init(windowHandle);
    }

    protected void configureFonts(final ImGuiIO io) {
        addFonts(io);
        buildFontsAtlas(io);
    }

    protected void addFonts(final ImGuiIO io) {
        io.getFonts().addFontDefault();
        FontManager.getInstance().reregisterAllLoaded(io.getFonts()); // Reregister old fonts
        FontManager.getInstance().registerAllQueued(io.getFonts());   // Add new fonts
    }

    protected void buildFontsAtlas(final ImGuiIO io) {
        io.getFonts().build();
    }

    protected void rebuildFontsAtlas(final ImGuiIO io) {
        io.getFonts().clear();
        FontManager.getInstance().unloadAll();
        addFonts(io);
        buildFontsAtlas(io);
    }

    protected boolean rebuildFontAtlasIfNeeded(final ImGuiIO io) {
        final boolean hasQueuedFonts = FontManager.getInstance().hasQueuedFonts();

        if (hasQueuedFonts)
            rebuildFontsAtlas(io);

        return hasQueuedFonts;
    }

    protected void configureFlags(final ImGuiIO io) {
        io.setConfigFlags(CONFIG_FLAGS);
    }

    protected void configureConfig(final ImGuiIO io) {
        io.setIniFilename(id == null ? null : id + ".ini");
    }

    public void destroy() {
        ImPlot.destroyContext();
        ImGui.destroyContext();
    }

    public abstract int priority();
}
