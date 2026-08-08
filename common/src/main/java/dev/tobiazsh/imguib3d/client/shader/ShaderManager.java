// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.shader;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class to load and get shaders from resources. Used because Blaze3D's pipeline uses an EMPTY shader
 * manager to try and load shaders (which will obviously not work) instead of the default one, which has all the shaders
 * stores inside.
 */
public abstract class ShaderManager {

    private final Map<ShaderKey, Shader> shaders = new HashMap<>();

    /**
     * Tries to get shader from map. If shader could not be found, it will return null.
     * @param shaderKey The specific shader to get.
     * @return Shader if available, otherwise null.
     */
    public @Nullable Shader getShader(final ShaderKey shaderKey) {
        return shaders.get(shaderKey);
    }


    protected void putShader(final Shader shader) {
        shaders.put(shader.shaderKey(), shader);
    }

    /**
     * Removes specified shader from map.
     */
    public void forget(final ShaderKey shaderKey) {
        shaders.remove(shaderKey);
    }

    /**
     * Removes specified shader from map.
     */
    public void forget(final Shader shader) {
        shaders.remove(shader.shaderKey());
    }

    /**
     * Tries to use stored shader. If shader could not be found, it will try to load the shader from the specified path.
     *
     * @param shaderKey The specific shader to get or load.
     * @param path The path in resources to load the shader from.
     * @return The shader, either from the stored shaders or loaded from resources.
     */
    public Shader getOrLoad(final ShaderKey shaderKey, final String path) {
        return shaders.computeIfAbsent(shaderKey, _ -> loadFromResources(shaderKey, path));
    }

    /**
     * Tries to use stored shader. If shader could not be found, it will try to load the shader from the specified path.
     * If the path is null, tries to load shader from the default shader directory in resources.
     *
     * @param shaderKey The specific shader to get or load.
     * @param path The path to the shader in the resources. If null, tries to load shader
     *             from the default shader directory in resources.
     * @return The shader, either from the stored shaders or loaded from resources.
     */
    public Shader getOrLoadDefault(
            final ShaderKey shaderKey,
            final @Nullable String path
    ) {
        final String shaderPath =
                path == null ? shaderKey.shaderIdentifier().defaultShaderPath(shaderKey.shaderType()) : path;

        return getOrLoad(shaderKey, shaderPath);
    }

    /**
     * Tries to load the specified shader from resources. Automatically detects shader type from file suffix.
     * Throws a runtime exception if InputStream could not be gotten.
     *
     * @param shaderIdentifier The shader's identifier.
     * @param path The path to the shader file in the resources.
     * @return The loaded shader.
     */
    public Shader loadFromResources(final ShaderIdentifier shaderIdentifier, final String path) {
        final int dot = path.lastIndexOf('.');
        final String suffix = dot == -1 ? "" : path.substring(dot);

        final ShaderType shaderType = ShaderType.mapFromSuffix(suffix);
        return loadFromResources(ShaderKey.of(shaderIdentifier, shaderType), path);
    }

    /**
     * Tries to load the specified shader from resources.
     * Throws a runtime exception if InputStream could not be gotten.
     *
     * @param shaderKey The shader's key inside resources.
     * @param path The path to the shader file in the resources.
     * @return The loaded shader.
     */
    public Shader loadFromResources(
            final ShaderKey shaderKey,
            final String path
    ) {
        try (InputStream is = getClassInputStream(path)) {
            final byte[] shaderBytes = is.readAllBytes();
            final String sourceCode = new String(shaderBytes, StandardCharsets.UTF_8);

            return new Shader(shaderKey, sourceCode, path);
        } catch (IOException e) {
            throw new RuntimeException("Could not get InputStream from class", e);
        }
    }


    /**
     * Returns an input stream for the specified resource path.
     *
     * @param path The path to the resource.
     * @return An input stream for the resource.
     * @throws RuntimeException if the resource could not be found.
     */
    protected abstract InputStream getClassInputStream(String path);

}
