// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client;

import dev.tobiazsh.imguib3d.client.exception.NoImplementationException;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.ServiceLoader;

public abstract class ImGuiImplementation {

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

    protected ImGuiImplementation(@Nullable String id) {
        this.id = id;
    }
    public ImGuiImplementation() {
        this(null);
    }

    public abstract void onFontAtlasBuild();
    public abstract void onFontAtlasDispose();
    public abstract void onFontAtlasRebuild();

    public abstract void draw(ImGuiDrawable imGuiDrawable);

    protected abstract void addFonts(final ImGuiIO io);
    protected abstract void init(final long windowHandle);

    public void initialize(final long windowHandle) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO io = ImGui.getIO();
        configureFonts(io);
        init(windowHandle);
    }

    public void configureFonts(final ImGuiIO io) {
        io.getFonts().addFontDefault();
        addFonts(io);
        io.getFonts().build();
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setIniFilename(id == null ? null : id + ".ini");
    }

    public void destroy() {
        ImPlot.destroyContext();
        ImGui.destroyContext();
    }

    public abstract boolean isCompatibleWithEnvironment();
    public abstract int priority();
}
