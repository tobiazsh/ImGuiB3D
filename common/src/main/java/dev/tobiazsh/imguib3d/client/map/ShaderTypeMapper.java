// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.map;

import dev.tobiazsh.imguib3d.client.shader.ShaderType;

/**
 * Interface to map Minecraft's shader type to our custom implementation of {@link ShaderType} and vice versa.
 * @param <C> The integrated ShaderType class type.
 */
public interface ShaderTypeMapper<C> extends MinecraftMapper<ShaderType, C> {
}
