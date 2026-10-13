package dev.tobiazsh.imguib3d.overlay;

import dev.tobiazsh.imguib3d.TestModInitializer;
import dev.tobiazsh.imguib3d.test.log.PrimitiveLogger;
import dev.tobiazsh.imguib3d.test.overlay.FirstTestOverlay;

public class ImplFirstTestOverlay extends FirstTestOverlay {

    @Override
    public String getModId() {
        return TestModInitializer.getModId();
    }

    @Override
    public PrimitiveLogger getLogger() {
        return TestModInitializer.getPrimitiveLogger();
    }

}