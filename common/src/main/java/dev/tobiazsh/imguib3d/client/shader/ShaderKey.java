// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.shader;

/**
 * Unique shader identifier that bundles both the original identifier and type.
 *
 * @param shaderIdentifier The shader identifier, which is a combination of namespace and path (e.g. minecraft:box_blur).
 * @param shaderType The type of shader.
 */
public record ShaderKey(ShaderIdentifier shaderIdentifier, ShaderType shaderType) {

    /**
     * Wrapper of the ShaderSource constructor for shorter and more readable code, because ShaderSource often
     * involves repetition.
     *
     * @param shaderIdentifier The shader identifier, which is a combination of namespace
     *                         and path (e.g. minecraft:box_blur).
     * @param shaderType The type of shader.
     * @return
     */
    public static ShaderKey of(ShaderIdentifier  shaderIdentifier, ShaderType shaderType) {
        return new ShaderKey(shaderIdentifier, shaderType);
    }

}
