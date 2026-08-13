# ImGui Overlay Manager

The ImGui overlay manager is a singleton class that stores all overlays (which may or may not be visible). The ImGui
overlay manager is an API of ImGui Blaze3D and is the main method to manage overlays in the mod. You can either use
the default singleton instance, or you can create your own instance of the overlay manager, though highly discouraged,
because you'll have to mixin into the `render()` method of Minecraft to draw your overlays yourself.

Overlays, which are not registered in the manager, will not be drawn by default. You would need to mixin into the
`render()` method of Minecraft and call the `draw()` method of your overlay yourself. Doing so is highly discouraged,
though.

The registered overlays are drawn in the render method of Minecraft, along with every `Screen` which implements
ImGuiDrawable. The overlays are drawn in the order of their priority, meaning that overlays with a higher priority will
be drawn before overlays with a lower priority. The priority is defined by the `priority()` method of the
`ImGuiOverlay` interface. In everyday usage, the priority may not matter since the ImGui windows can be called into
focus anyway, but it may still be useful to have for some use cases, which is why the interface exists in the first
place.

The overlay manager just exists on the common side and is not implemented anywhere on the client side.

Calling `add(final ImGuiOverlay overlay)` will add the overlay to the manager. If an overlay with the same ID already
exists, it **won't** be replaced and a warning will be printed to the console. The ID is being pulled from the
`getId()` method of the `ImGuiOverlay` interface. The ID is used to identify the overlay in the manager. You can assign
your own ID to your overlay by calling `add(final String id, final ImGuiOverlay overlay)` in case you want multiple
overlays of the same type.

To change state of your overlay, you can either save the ID of your overlay and pull it out of the manager on demand,
or save a reference of your overlay when you add it to the manager. It is important that you **do not** lose both the
ID and the reference, because otherwise you won't be able to access your overlay anymore.