// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.mixin.client;

import com.mojang.renderpearl.backend.api.GpuDeviceBackend;
import com.mojang.renderpearl.frontend.FrontendGpuDevice;
import dev.tobiazsh.imguib3d.client.access.GpuDeviceAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FrontendGpuDevice.class)
public class GpuDeviceMixin implements GpuDeviceAccessor {

    @Shadow
    @Final
    private GpuDeviceBackend backend;

    @Override
    public GpuDeviceBackend imGuiB3D$getBackend() {
        return backend;
    }
}
