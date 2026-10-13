// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d;

import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlayManager;
import dev.tobiazsh.imguib3d.command.TestCommand;
import dev.tobiazsh.imguib3d.overlay.ImplFirstTestOverlay;
import dev.tobiazsh.imguib3d.overlay.ImplSecondTestOverlay;
import dev.tobiazsh.imguib3d.test.log.PrimitiveLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestModInitializer implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("TestMod");

    @Override
    public void onInitializeClient() {
        ImGuiOverlayManager.getInstance().add(new ImplSecondTestOverlay());
        ImGuiOverlayManager.getInstance().add(new ImplFirstTestOverlay());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TestCommand.register(dispatcher);
        });
    }

    public static String getModId() {
        return "testmod";
    }

    public static PrimitiveLogger getPrimitiveLogger() {
        return new PrimitiveLogger() {
            @Override
            public void error(String message) {
                LOGGER.error(message);
            }

            @Override
            public void warn(String message) {
                LOGGER.warn(message);
            }

            @Override
            public void info(String message) {
                LOGGER.info(message);
            }
        };
    }
}