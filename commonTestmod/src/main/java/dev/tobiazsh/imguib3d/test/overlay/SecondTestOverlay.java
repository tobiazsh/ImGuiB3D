package dev.tobiazsh.imguib3d.test.overlay;

import dev.tobiazsh.imguib3d.client.font.FontManager;
import dev.tobiazsh.imguib3d.client.font.ImGuiFont;
import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import dev.tobiazsh.imguib3d.client.texture.ImGuiTexture;
import dev.tobiazsh.imguib3d.test.ImplementableTestPart;
import imgui.ImGui;
import imgui.type.ImString;
import org.jspecify.annotations.Nullable;

import java.nio.ByteBuffer;

public abstract class SecondTestOverlay implements ImGuiOverlay, ImplementableTestPart {

    protected boolean isVisible = true;
    protected boolean showImage = false;
    protected boolean customFontPushed = false;
    protected boolean isRobotoActive = false;
    protected @Nullable ImGuiTexture flowerTexture;
    protected @Nullable ImGuiFont robotoSlab;
    protected @Nullable ImGuiFont sekuya;

    protected @Nullable ByteBuffer testBuffer;

    protected ImString inputBuffer = new ImString(128);

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void draw() {
        createTextures();
        createFonts();

        if (robotoSlab != null && robotoSlab.isLoaded() && sekuya != null && sekuya.isLoaded()) {
            ImGui.pushFont(isRobotoActive ? robotoSlab.getImFont() : sekuya.getImFont(), ImGui.getFontSize() * 2);
            customFontPushed = true;
        }

        if (ImGui.begin("Test Overlay")) {
            ImGui.text("Hello");

            if (ImGui.button((showImage ? "Hide" : "Show") + " highly confidential image (fr fr)"))
                showImage = !showImage;

            if (ImGui.button("Switch font"))
                isRobotoActive = !isRobotoActive;

            if (ImGui.button("Dispose fonts"))
                FontManager.getInstance().disposeAll();

            if (showImage)
                ImGui.image(flowerTexture.getTextureId(), 512, 512);

            ImGui.inputText("Input", inputBuffer);

            ImGui.end();
        }

        if (customFontPushed) {
            ImGui.popFont();
            customFontPushed = false;
        }
    }

}
