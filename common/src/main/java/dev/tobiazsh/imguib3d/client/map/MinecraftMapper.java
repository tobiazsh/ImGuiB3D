// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.map;

/**
 * Interface to map any Minecraft-native object to a custom object and vice versa.
 *
 * @param <C> The custom object.
 * @param <I> The integrated (Minecraft-native) object.
 */
public interface MinecraftMapper<C, I> {
    I toMinecraft(C customValue);
    C fromMinecraft(I minecraftValue);
}
