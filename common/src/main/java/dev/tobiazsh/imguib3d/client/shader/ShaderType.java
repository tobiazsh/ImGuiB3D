// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.shader;

/**
 * Enum to specify the type of shader.
 */
public enum ShaderType {
    VERTEX_GLSL(".vsh"),
    FRAGMENT_GLSL(".fsh");

    private final String suffix;

    ShaderType(final String suffix) {
        this.suffix = suffix;
    }

    public String toFileName(final String fileName) {
        return fileName + suffix;
    }

    public static ShaderType mapFromSuffix(final String suffix) {
        return switch (suffix) {
            case ".vsh" -> VERTEX_GLSL;
            case ".fsh" -> FRAGMENT_GLSL;
            default -> throw new IllegalArgumentException("Unknown shader suffix: " + suffix);
        };
    }
}
