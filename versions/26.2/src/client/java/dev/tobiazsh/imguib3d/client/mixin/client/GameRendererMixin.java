// Portions of this code are derived from Enaium's "fabric-mod-ImGui" project:
// https://github.com/Enaium/fabric-mod-ImGui
//
// Specifically, code was derived from:
// https://github.com/Enaium/fabric-mod-ImGui/blob/2fe781209243484223931175b59d439872bea934/game/26.2/src/main/java/cn/enaium/fabric/imgui/mixin/GameRendererMixin.java
//
// The original work is licensed under the Apache License 2.0.
// A copy of the license is available at:
// /licenses/third_party/enaium_imgui_fabric/LICENSE.txt
//
// Enaium's implementation also appears to be based, at least in part, on the OpenGL backend from
// the imgui-java project, which is licensed under the MIT License.
// A copy of the MIT License is available at:
// /licenses/third_party/imgui_java/LICENSE.txt
//
// Modifications made in this project are licensed under LGPL-3.0.
// Copyright © 2026 Tobiazsh


package dev.tobiazsh.imguib3d.client.mixin.client;

import dev.tobiazsh.imguib3d.client.ImGuiDrawable;
import dev.tobiazsh.imguib3d.client.ImGuiImplementation;
import dev.tobiazsh.imguib3d.client.ImGuiTest;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
        if (minecraft.gui.screen() instanceof final ImGuiDrawable drawable)
            ImGuiImplementation.getInstance().draw(drawable);

        ImGuiTest.render();
    }
}
