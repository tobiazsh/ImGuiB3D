# Texture Manager

The ImGuiB3D's texture manager stores all the textures created through the ImGuiB3D API.

When a texture is registered, it will first be stored inside a queue until the next frame happens. Then
it will be registered and uploaded to the GPU. The texture object is moved out of the queue and into the official
set of usable textures. Note that you CAN encounter unusable textures inside the set in between the frames since
they're freed each frame. Therefore, when using a texture, it's always worth checking whether it's actually usable
through the `isUsable()` method.