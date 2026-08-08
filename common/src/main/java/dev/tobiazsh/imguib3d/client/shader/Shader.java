// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.shader;

/**
 * Record to store information about a shader.
 *
 * @param shaderKey The shader's source, which contains the identifier and type.
 * @param sourceCode The shader's sourceCode code.
 * @param path The path to the shader in the resources. Mainly used for debugging.
 */
public record Shader(ShaderKey shaderKey, String sourceCode, String path) {
}
