// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.map;

import dev.tobiazsh.imguib3d.client.shader.ShaderType;

public class ShaderTypeMapperImpl implements ShaderTypeMapper<ShaderType> {

    private static final ShaderTypeMapperImpl INSTANCE = new ShaderTypeMapperImpl();

    public static ShaderTypeMapperImpl getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("UnnecessaryDefault")
    @Override
    public ShaderType toMinecraft(dev.tobiazsh.imguib3d.client.shader.ShaderType customValue) {
        return switch (customValue) {
            case VERTEX_GLSL -> ShaderType.VERTEX_GLSL;
            case FRAGMENT_GLSL -> ShaderType.FRAGMENT_GLSL;
            default -> throw new IllegalArgumentException("Cannot map custom shader type to Minecraft shader type: "
                    + customValue + "\nNo equivalent!");
        };
    }

    @SuppressWarnings("UnnecessaryDefault")
    @Override
    public dev.tobiazsh.imguib3d.client.shader.ShaderType fromMinecraft(ShaderType minecraftValue) {
        return switch (minecraftValue) {
            case VERTEX_GLSL -> dev.tobiazsh.imguib3d.client.shader.ShaderType.VERTEX_GLSL;
            case FRAGMENT_GLSL -> dev.tobiazsh.imguib3d.client.shader.ShaderType.FRAGMENT_GLSL;
            default -> throw new IllegalArgumentException("Cannot map Minecraft shader type to custom shader type: "
                    + minecraftValue + "\nNo equivalent!");
        };
    }

}
