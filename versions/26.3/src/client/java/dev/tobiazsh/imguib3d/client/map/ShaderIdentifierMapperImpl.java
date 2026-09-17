// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.map;

import dev.tobiazsh.imguib3d.client.shader.ShaderIdentifier;
import net.minecraft.resources.Identifier;

public class ShaderIdentifierMapperImpl implements ShaderIdentifierMapper<Identifier> {

    private static final ShaderIdentifierMapperImpl INSTANCE = new ShaderIdentifierMapperImpl();

    public static ShaderIdentifierMapperImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public Identifier toMinecraft(ShaderIdentifier customValue) {
        return Identifier.fromNamespaceAndPath(customValue.namespace(), customValue.path());
    }

    @Override
    public ShaderIdentifier fromMinecraft(Identifier minecraftValue) {
        return new ShaderIdentifier(minecraftValue.getNamespace(), minecraftValue.getPath());
    }

}
