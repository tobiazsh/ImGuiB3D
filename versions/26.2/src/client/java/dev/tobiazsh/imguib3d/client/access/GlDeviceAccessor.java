package dev.tobiazsh.imguib3d.client.access;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.FrameBufferCache;

public interface GlDeviceAccessor {
    DirectStateAccess imGuiB3D$getDirectStateAccess();
    FrameBufferCache imGuiB3D$getFrameBufferCache();
}
