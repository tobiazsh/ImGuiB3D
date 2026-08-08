// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.mixin.client;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.GpuDeviceBackend;
import dev.tobiazsh.imguib3d.client.access.GpuDeviceAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GpuDevice.class)
public class GpuDeviceMixin implements GpuDeviceAccessor {

    @Shadow
    @Final
    private GpuDeviceBackend backend;

    @Override
    public GpuDeviceBackend imGuiB3D$getBackend() {
        return backend;
    }
}
