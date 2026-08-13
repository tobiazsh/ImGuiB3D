#  Shader Manager

ImGuiB3D brings its own shader manager as a result to Minecraft's texture manager loads some textures from the
resources into an empty shader manager, obviously making the shader not discoverable in the main set. The shader
manager is a service class, meaning each version brings its own implementation of the shader manager.

The shader manager theoretically supports shaders other than in the GLSL language, although Minecraft currently only
supports GLSL shaders.

The shader manager stores each shader's source code in a `String` along with the shader's type and the shader's ID
(often the file name) inside a `Shader` object. Both the shader's type and ID are bundled inside a `ShaderKey` object.
This is necessary since shaders of the same name may of a different type (e.g. vertex shader and fragment shader) and
therefore need to be stored separately.