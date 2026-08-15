// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d;

import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlayManager;
import dev.tobiazsh.imguib3d.overlay.TestOverlay;
import net.fabricmc.api.ClientModInitializer;

public class TestModInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ImGuiOverlayManager.getInstance().add(new TestOverlay());
    }

    public static String getModId() {
        return "testmod";
    }
}