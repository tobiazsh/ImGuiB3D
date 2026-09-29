package dev.tobiazsh.imguib3d.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.tobiazsh.imguib3d.screen.TestScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TestCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommands.literal("testscr")
                        .executes(context -> {
                            // Open the TestScreen when the command is executed
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreenAndShow(new TestScreen()));
                            return 1;
                        })
        );
    }
}
