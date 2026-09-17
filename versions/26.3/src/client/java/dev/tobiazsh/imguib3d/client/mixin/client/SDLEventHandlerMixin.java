package dev.tobiazsh.imguib3d.client.mixin.client;

import com.mojang.blaze3d.platform.SDLEventHandler;
import dev.tobiazsh.imguib3d.client.ImGuiImpl;
import dev.tobiazsh.imguib3d.client.ImGuiImplementation;
import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SDLEventHandler.class)
public class SDLEventHandlerMixin {

    @Redirect(method = "pollEvents", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDL_Event;type()I"))
    public int imguib3d$pollEvents(SDL_Event instance) {
        int eventType = instance.type();

        if (ImGuiImplementation.getInstance() instanceof ImGuiImpl impl && impl.isInitialized()) {
            impl.getSdl3Implementation().processEvent(instance.address());
        }

        return eventType;
    }
}
