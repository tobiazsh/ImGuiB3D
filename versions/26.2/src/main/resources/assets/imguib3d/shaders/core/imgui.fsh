#version 450 core

// Portions of this code are derived from Enaium's "fabric-mod-ImGui" project:
// https://github.com/Enaium/fabric-mod-ImGui
//
// Specifically, code was derived from:
// https://github.com/Enaium/fabric-mod-ImGui/blob/2fe781209243484223931175b59d439872bea934/game/26.2/src/main/resources/assets/fabric-gui-imgui/shaders/core/imgui.fsh
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

layout(binding = 1) uniform sampler2D textureSampler;

in vec2 uv;
in vec4 inputColor;

out vec4 outputColor;

void main() {
    outputColor = inputColor * texture(textureSampler, uv.st);
}