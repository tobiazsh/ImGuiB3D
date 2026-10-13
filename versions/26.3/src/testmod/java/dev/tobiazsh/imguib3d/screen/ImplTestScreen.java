package dev.tobiazsh.imguib3d.screen;

import dev.tobiazsh.imguib3d.test.screen.TestScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ImplTestScreen extends Screen implements TestScreen {

    public ImplTestScreen() {
        super(Component.literal("Test Screen"));
    }

}
