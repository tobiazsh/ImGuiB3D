# Basic Usage

To get started with ImGui Blaze3D, you need to decide first how exactly you want to render your ImGui GUI. There
are two options to do this:

1. [Rendering ImGui inside a Minecraf Screen](#rendering-imgui-inside-a-minecraft-screen)
2. [Rendering ImGui directly in the window](#rendering-imgui-directly-in-the-window)

## Rendering ImGui inside a Minecraft Screen

Rendering ImGui in a Minecraft Screen has the advantage of the screen being managed by Minecraft, meaning you don't
have to worry about visibility, freezing the game for the user to interact, etc... The main tradeoff is that you are
constrained by how Minecraft manages its screens, meaning you cannot have a ImGui window float around while the player
is interacting with the game.

### Explanation

To render ImGui inside a Minecraft Screen, you need to create a class which extends Minecraft's `Screen` class (actual
name may differ depending on the Minecraft version) and implements the `ImGuiDrawable` interface. The `draw` method of
interface must be overridden, and it will be called every frame to render what's inside the method.

To display your screen, you must assign the Player's current screen to your screen class. The implementation of this
will vary from version to version, therefore it's best to check with Fabric's documentation on how to set the current
screen for the player in your specific case.

### Code Example

```java
public class ExampleScreen extends Screen implements ImGuiDrawable {
    
    @Override
    public void draw(ImGuiIO io) {
        ImGui.begin("Example Window");
        ImGui.text("Hello, world!");
        ImGui.end();
    }
    
}
```

## Rendering ImGui directly in the window

Rendering ImGui directly in the game's window has the advantage of being totally customizable, meaning you can render
it at any given point in time. The tradeoff is that you have to manage the visibility of the ImGui GUI yourself,
meaning you must track whether the `Overlay` is currently rendering or not.

### Explanation

To render ImGui directly inside, you first need to create a new class which implements `ImGuiOverlay`. You will notice
that the interface has a few methods that you _must_ implement:

- `void draw()`: This draw the actual ImGui GUI.
- `boolean isVisible()`: This is the interface to tell the renderer whether the overlay should be drawn or not.
- `int priority()`: This is the priority of the overlay, meaning that overlays with a higher priority will be drawn
  on top of overlays with a lower priority. In everyday use, this may not matter since the ImGui windows can be called
  into focus anyway, but it may still be useful to have a priority system in place for certain use cases.
- `String getId()`: This is the unique identifier of the overlay, meaning that you cannot have two overlays with the same
  ID. The ID is used to identify the overlay in the manager, and it is also used to save the state of the overlay
  between sessions.

If you are finished with writing your overlay, you need to register it with the `ImGuiOverlayManager`.
This is done by calling the `add` method of the `ImGuiOverlayManager` singleton instance, passing in your overlay as
the parameter. You can either pass a new instance of your screen, or you can pass an existing instance of your screen
if you want easy access to your overlay in the future. More information about  the `ImGuiOverlayManager` can be found 
in the [documentation of the `ImGuiOverlayManager` class](/documentation/technical/IMGUI_OVERLAY_MANAGER.md).

### Code Example

**Actual overlay class:** 
```java
public class ExampleOverlay implements ImGuiOverlay {
    
    private boolean isVisible = true;
    
    @Override
    public void draw() {
        ImGui.begin("Example Window");
        ImGui.text("Hello, world!");
        ImGui.end();
    }
    
    @Override
    public boolean isVisible() {
        return isVisible;
    }
    
    @Override
    public int priority() {
        return 10;
    }
    
    @Override
    public String getId() {
        return "example_overlay";
    }
    
}
```

<hr/>

**Registering the overlay:**

```java
public class TestClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ImGuiOverlayManager.getInstance().add(new ExampleOverlay());
    }

}
```