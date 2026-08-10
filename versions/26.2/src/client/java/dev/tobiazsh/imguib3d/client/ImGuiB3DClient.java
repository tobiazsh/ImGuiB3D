// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client;

import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlayManager;
import dev.tobiazsh.imguib3d.client.shader.ShaderManager;
import dev.tobiazsh.imguib3d.client.shader.ShaderManagerImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class ImGuiB3DClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("ImGuiB3D CLIENT");
    public static final String VERSION = getVersion();
    private static final ShaderManager SHADER_MANAGER = new ShaderManagerImpl();

    private static final String MINECRAFT_DEPENDENCY_ID = "minecraft";

    @Override
    public void onInitializeClient() {
        LOGGER.info("Using ImGuiB3D version: {}", VERSION);
        LOGGER.info("Registering ImGui Overlays...");
    }

    private static String getVersion() {
        return FabricLoader.getInstance()
                .getModContainer("imguib3d")
                .map(
                        modContainer -> modContainer.getMetadata()
                                .getVersion()
                                .getFriendlyString()
                )
                .orElse("unknown");
    }

    private static Collection<VersionPredicate> getMinecraftVersionPredicates() {
        final ModContainer imguib3d = FabricLoader.getInstance()
                .getModContainer("imguib3d")
                .orElseThrow(() -> new IllegalStateException("ImGuiB3D mod container not found"));

        final ModDependency minecraft = imguib3d.getMetadata()
                .getDependencies()
                .stream()
                .filter(dependency -> dependency.getModId().equals(MINECRAFT_DEPENDENCY_ID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Minecraft dependency not found in ImGuiB3D mod metadata"));

        return minecraft.getVersionRequirements();
    }

    private static Version getMinecraftVersion() {
        final ModContainer minecraft = FabricLoader.getInstance()
                .getModContainer(MINECRAFT_DEPENDENCY_ID)
                .orElseThrow(() -> new IllegalStateException("Minecraft mod container not found"));

        return minecraft.getMetadata().getVersion();
    }

    public static boolean isMCVersionCompatible() {
        final Version minecraftVersion = ImGuiB3DClient.getMinecraftVersion();

        for (VersionPredicate predicate : ImGuiB3DClient.getMinecraftVersionPredicates()) {
            if (!predicate.test(minecraftVersion))
                return false;
        }

        return true;
    }

    public static ShaderManager getShaderManager() {
        return SHADER_MANAGER;
    }
}
