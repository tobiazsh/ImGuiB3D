package dev.tobiazsh.imguib3d.client.access;

import com.mojang.renderpearl.backend.opengl.DirectStateAccess;
import com.mojang.renderpearl.backend.opengl.FrameBufferCache;

public interface GlDeviceAccessor {
    DirectStateAccess imGuiB3D$getDirectStateAccess();
    FrameBufferCache imGuiB3D$getFrameBufferCache();
}
