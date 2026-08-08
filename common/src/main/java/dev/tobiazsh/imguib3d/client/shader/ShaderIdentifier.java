// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.shader;

import org.jspecify.annotations.NonNull;

/**
 * Record to store information about a shader identifier. Roughly the same as Mojang's Identifier. Used to remove the
 * dependency of specific Minecraft versions for the common code.
 *
 * @param namespace The
 * @param path
 */
public record ShaderIdentifier(String namespace, String path) {

    public static final String SHADER_PATH = "shaders";

    public @NonNull String toStringShort() {
        return namespace + ":" + path;
    }

    public @NonNull String toString() {
        return "Identifier(" + namespace + ":" + path + ")";
    }

    /**
     * Produces the default directory path for a shader based on the namespace and path of the identifier.
     * The path is constructed as follows:
     * `/assets/{namespace}/shaders/{shaderType.toFileName(path)}`
     *
     * @param shaderType The type of shader (e.g. VERTEX_GLSL)
     * @return The produced path.
     */
    public @NonNull String defaultShaderPath(ShaderType shaderType) {

        // EXAMPLE:
        // - Let ShaderType be ShaderType.VERTEX_GLSL
        // - Let Identifier be "imguib3d:core/imgui"
        // - ShaderType#toFileName produces "core/imgui.vsh"
        // - Full file name is "/assets/imguib3d/shaders/core/imgui.vsh"

        return "/assets/" + namespace + "/" + SHADER_PATH + "/" + shaderType.toFileName(path);
    }

}
