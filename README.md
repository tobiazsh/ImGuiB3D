<div align="center">

# ImGui Blaze3D

<img src="/res/imguib3d.png" alt="ImGui Blaze3D logo which features a yellow-black Minecraft blaze in an ImGui window on a stone background." width="200" height="200">
</div>

ImGui Blaze3D is a multi-version library, supported from 26.2 and onwards,
which allows you to use ImGui with Minecraft Fabric.

The library is designed to be used with the Fabric mod loader
and provides a simple way to integrate ImGui into your Minecraft mods. It is compatible with multiple versions
of Minecraft, making it easy to use in your projects.

<hr/>

<div>
❗❗❗

This mod is a dependency and not its own standalone mod.

If you're a player wanting to play a mod using this library, please download it from either
[Modrinth](https://www.example.com) or 
[the release page](https://www.example.com) of this repository.
If you're a developer wanting to use this mod, please
refer to the [documentation](/documentation/README.md),
which guides you through the process of installing and
using this library within your own project.

</div>

<hr/>

## Demonstration
<img src="/res/imgui_demo.png" alt="ImGui Demo window inside a Minecraft 26.2 window">

## [Developer Installation](documentation/technical/INSTALLATION.md)

## Adding support for new versions
To add support to new Minecraft versions, you first must create a new module in the `versions` directory,
specify the new module in the root `settings.gradle` and add the `:common` module as a dependency to the
according `build.gradle`.

### Dealing with the `fabric.mod.json`

Each version module has its own `fabric.mod.json`, in which you must specify which Minecraft version the module works
with. ImGuiBlaze3D uses the Minecraft dependency, which uses semantic versioning, to check which implementation of
`ImGuiImplementation` is currently supported and loads the next available and compatible implementation.

For example, if you set the dependency to `~26.2`, it will support everything between including 26.2 and excluding 26.3
(snapshots too!). If you set it to `<=26.2`, it will support everything up to and including 26.2, and so on.

For more information on semantic versioning, check the Fabric Wiki
[here](https://docs.fabricmc.net/develop/loader/fabric-mod-json#semantic-versioning).

### `ImGuiImplementation` Service

ImGuiBlaze3D always stores the best compatible implementation of `ImGuiImplementation` in the class itself, which can
be found in the `:common` module. If you add a new version module, you must have exactly one class which extends
`ImGuiImplementation` and is annotated with `@AutoService(ImGuiImplementation.class)`.
 
Furthermore, please make sure that you don't leave the method `isCompatibleWithEnvironment` empty. It should give
information about whether the current Minecraft version is compatible with the implementation or not. A pseudo code
example might look like this:

```java
@Override
public boolean isCompatibleWithEnvironment() {
    Collection<VersionPredicate> predicates = getMinecraftVersionPredicates();
    Version minecraftVersion = getMinecraftVersion();
    
    for (VersionPredicate predicate : predicates) {
        if (!predicate.test(minecraftVersion))
            return false; 
    }
    
    return true;
}
```

The extracted methods `getMinecraftVersionPredicates` and `getMinecraftVersion` both use the Fabric Loader API to get
the version predicated for the dependency `minecraft` and the current version of `minecraft` from the `fabric.mod.json`.

## Motivation

I wanted to make an independent ImGui library for Minecraft Blaze3D, which is compatible with multiple versions of
Minecraft. It is extended with an easy method to load fonts and its own shader manager, which can load shaders from
basically anywhere (implementation currently support loading shaders from resources), making it completely independent
of Minecraft's kind of limited shader system.

This library was intended to be used in my own projects, specifically in my mod
[MyWorld Traffic Addition](https://github.com/tobiazsh/MyWorld-Traffic-Addition), but I thought it would be a good idea
to make it its own library so that other developers can use it in their own projects as well.

## Credit

Thanks to Enaium for his implementation of ImGui for Minecraft, which served as a reference for this library.
You can find their respository [here](https://github.com/Enaium/fabric-mod-ImGui).

## Licensing
The project is licensed under the LGPL-3.0 License. The full license text can be found in the LICENSE.txt file.

**Note:**
Some parts of the library are based on the `ìmgui-java` library, which is licensed under MIT, and some parts are based
on Enaium's implementation of ImGui for Minecraft, which is licensed under the Apache 2.0 license. You can find Enaium's
repository [here](https://github.com/Enaium/fabric-mod-ImGui). You'll find the appropriate license texts in
`licenses/third_party/enaium_imgui_fabric` and `licenses/third_party/imgui_java` directories accordingly. The affected
classes, files and co. contain a header comment with the license information.