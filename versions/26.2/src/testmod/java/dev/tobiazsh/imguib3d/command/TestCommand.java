package dev.tobiazsh.imguib3d.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.tobiazsh.imguib3d.screen.ImplTestScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class TestCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommands.literal("testscr")
                        .executes(context -> {
                            // Open the TestScreen when the command is executed
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreenAndShow(new ImplTestScreen()));
                            return 1;
                        })
        );
    }
}
