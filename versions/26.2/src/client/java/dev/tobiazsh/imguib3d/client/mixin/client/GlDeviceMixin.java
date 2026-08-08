package dev.tobiazsh.imguib3d.client.mixin.client;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.FrameBufferCache;
import dev.tobiazsh.imguib3d.client.access.GlDeviceAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = com.mojang.blaze3d.opengl.GlDevice.class)
public class GlDeviceMixin implements GlDeviceAccessor {

    @Shadow
    @Final
    private DirectStateAccess directStateAccess;

    @Shadow
    @Final
    private FrameBufferCache frameBufferCache;

    @Override
    public DirectStateAccess imGuiB3D$getDirectStateAccess() {
        return directStateAccess;
    }

    @Override
    public FrameBufferCache imGuiB3D$getFrameBufferCache() {
        return frameBufferCache;
    }

}
