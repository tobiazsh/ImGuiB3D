// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.shader;

import java.io.InputStream;

public class ShaderManagerImpl extends ShaderManager {

    @Override
    protected InputStream getClassInputStream(String path) {
        final InputStream stream =
                ShaderManagerImpl.class.getResourceAsStream(path);

        if (stream == null)
            throw new RuntimeException("Shader resource not found: " + path);

        return stream;
    }

}
