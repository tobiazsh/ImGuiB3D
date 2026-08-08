#version 450 core

// Portions of this code are derived from Enaium's "fabric-mod-ImGui" project:
// https://github.com/Enaium/fabric-mod-ImGui
//
// Specifically, code was derived from:
// https://github.com/Enaium/fabric-mod-ImGui/blob/2fe781209243484223931175b59d439872bea934/game/26.2/src/main/resources/assets/fabric-gui-imgui/shaders/core/imgui.vsh
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

layout(std140, binding = 0) uniform projectionMatrix {
    mat4 Value;
};

in vec2 position;
in vec2 uv;
in vec4 color;

out vec2 outUv;
out vec4 outColor;

void main() {
    outUv = uv;
    outColor = color;
    gl_Position = Value * vec4(position.xy, 0, 1);
}