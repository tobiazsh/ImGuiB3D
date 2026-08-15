# Font API

ImGui Blaze3D comes with a Font API, which allows you to load and manage fonts for your mod even **during** runtime,
meaning you can dynamically load and dispose of fonts as needed.

## Explanation

The Font API consists of a `FontManager`, which stores all loaded fonts as `ImGuiFonts` along with their associated
`ImGuiFontKey`.

### Rebuilding the font atlas

During runtime, it could occur that the font atlas needs a rebuild, of which large portions are also
handled by `FontManager`. First, the font atlas is cleared and _all_ loaded fonts are marked as unloaded. Then,
`FontManager` will re-register all currently loaded fonts. In the end, all the fonts will be marked as loaded again.
Then, the font manager will continue with the [registration of new fonts](#registration-of-new-fonts).

### Registration of new fonts

Fonts, which are currently not loaded but will be loaded anytime soon are stored in a Queue along their `ImGuiFontKey`.
After all the currently loaded fonts are re-registered, `FontManager` will attempt to register fonts in the queue as
long as the `RegistrationBudget` allows it. The `RegistrationBudget` is like a wallet, which tells the `FontManager`
how many fonts of importance X can be registered. Currently, the `RegistrationBudget` allocates the following:

- `FontImportance.LOW`: 2 fonts per frame
- `FontImportance.MEDIUM`: 5 fonts per frame
- `FontImportance.HIGH`: unlimited fonts per frame

(Adjustments may be made to the `RegistrationBudget` in the future.)

Then `ImGuiImplementation` takes over and does the rest (e.g. building font atlas).

## Usage

To load a font, you should first create an `ImGuiFont` object. Expect it to be null, so do not blindly use it in an
`ImGui#pushFont(ImFont font, int size)` call. First check for its null-ness, then check whether it is loaded using
`ImGuiFont#isLoaded()`, and only then use it in the `pushFont` call. Expect crashes otherwise.

The font object can be created using:

- `ImGuiFont#loadFontFromStream(InputStream fontStream,
FontIdentifier fontIdentifier, int size, FontImportance importance)` to load fonts from a stream (useful for loading
fonts from resource)

- `ImGuiFont#loadFontFromStream(InputStream fontStream, FontIdentifier fontIdentifier, int size)` same as the above one,
but defaults to `FontImportance.MEDIUM`

- `ImGuiFont#loadFontFromFile(Path fontFile, FontIdentifier fontIdentifier, int size, FontImportance importance)` to
load fonts from a file (useful for loading fonts from disk, outside the mod's resources)

- `ImGuiFont#loadFontFromFile(Path fontFile, FontIdentifier fontIdentifier, int size)` same as the above one, but 
defaults to `FontImportance.MEDIUM`

Now, this may all look a bit overwhelming, but I promise it isn't at all. Let's break down the following method:
`ImGuiFont#loadFontFromStream(InputStream fontStream,
FontIdentifier fontIdentifier, int size, FontImportance importance)`

Parameters:
- `InputStream fontStream`: The stream from which the font will be loaded, e.g.
`ExampleMod.class.getResourceAsStream("/assets/examplemod/fonts/ExampleFont.ttf")`.
- `FontIdentifier fontIdentifier`: A unique identifier for the font, which should consist of your mod's ID and the
font's name. A new one can be created using `FontIdentifier#of(String modId, String fontName)`.
- `int size`: The size of the font in pixels. Smaller values mean that the font will look blurry if scaled up. You
should use a size that fits your needs and isn't too big and too small. It is recommended to use the current font size
of ImGui, which can be queried using `ImGui#getFontSize()`.
- `FontImportance importance`: The importance of the font, which determines how many fonts of this importance can be
registered per frame. The higher the importance, the more fonts can be registered per frame. This is useful to prevent
performance issues when loading non-critical fonts. Please do not abuse `FontImportance.HIGH` for non-critical fonts,
as it will impact performance and make the experience for your users worse.
If you are unsure, it's best to assume your font is not critical and to use either `FontImportance.LOW` or
`FontImportance.MEDIUM`.

All the methods above will return an `ImGuiFontFuture`, which is a `CompletableFuture<ImGuiFont>`. You can use the
`thenAccept` method to get the loaded font and store it in your font object. It will be completed as soon as the font
is either found (see below) or loaded and registered.

Not all called by these methods will be registered. If the font had already been registered, the registration call
will be ignored and a complete `ImGuiFontFuture` will be returned immediately.

### Changing a font size

To change a font's size, you first should dispose your font and then create a new one with the desired size. This is
because the font size is baked into the font atlas and cannot be changed without rebuilding the font atlas. For
information on how to dispose of a font, see [Disposal](#disposal).

### Example

```java
public class TestOverlay implements ImGuiOverlay {
    
    private @Nullable ImGuiFont robotoFont;
    private boolean robotoFontPushed = false;
    
    @Override
    public void draw() {
        if (robotoFont != null && robotoFont.isLoaded()) {
            ImGui.pushFont(robotoFont);
            robotoFontPushed = true;
        }
        
        if (ImGui.begin("Test Overlay")) {
            ImGui.text("Hello, world!");
        }
        
        if (robotoFontPushed) {
            ImGui.popFont();
            robotoFontPushed = false;
        }
    }
    
    @Override
    public void createFonts() {
        if (robotoFont == null || robotoFont.isDisposed()) {
            try {
                ImGuiFont.loadFromStreamTTF(
                        TestOverlay.class.getResourceAsStream("/assets/examplemod/fonts/Roboto-Regular.ttf"),
                        FontIdentifier.of("examplemod", "Roboto Regular"), 16, FontImportance.LOW
                ).thenAccept(font -> robotoFont = font);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Roboto font", e);
            }
        }
    }
}
```

You do not need to call `ImGuiFont#disposeLater()` in the `dispose()` interface method of your overlay. All fonts are
disposed automatically when the overlay is disposed.

## Disposal

To dispose of a font, you can either call `ImGuiFont#disposeLater()` or `ImGuiFont#disposeNow()`. The former will
dispose of the font when the font atlas is rebuilt next, which takes little to no hit on performance. The latter will
dispose of the font before the next frame, which may take a hit on performance because an extra `O(n)` operation must
be performed on the loaded fonts.

Disposed fonts are automatically marked as unusable.