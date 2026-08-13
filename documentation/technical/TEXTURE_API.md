# Texture API

The texture API consists of the [Texture Manager](TEXTURE_MANAGER.md) and the `ImGuiTexture` interface.

The ImGuiTexture interface provides a few common methods to interact with textures, such as `isUsable()`,
`isUploaded()`, etc. The ImGuiTexture interface is supposed to be implemented by each version of the ImGuiB3D API, since
each version brings slightly different needs for textures. It is worth taking a look into the implemented interface
on your specific version and read the JavaDoc if you want to know more about the methods and their usage.