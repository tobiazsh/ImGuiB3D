// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.map;

import dev.tobiazsh.imguib3d.client.shader.ShaderIdentifier;

/**
 * Interface to map Minecraft's identifier to our custom implementation of {@link ShaderIdentifier} and vice versa.
 * @param <C> The integrated Identifier class type.
 */
public interface ShaderIdentifierMapper<C> extends MinecraftMapper<ShaderIdentifier, C> {
}
