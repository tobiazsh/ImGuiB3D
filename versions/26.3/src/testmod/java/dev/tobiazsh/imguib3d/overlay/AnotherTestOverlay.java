package dev.tobiazsh.imguib3d.overlay;

import dev.tobiazsh.imguib3d.TestModInitializer;
import dev.tobiazsh.imguib3d.client.font.FontIdentifier;
import dev.tobiazsh.imguib3d.client.font.ImGuiFont;
import dev.tobiazsh.imguib3d.client.font.ImGuiFontScope;
import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import imgui.ImGui;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class AnotherTestOverlay implements ImGuiOverlay {

    private @Nullable ImGuiFont sekuya;
    private @Nullable ImGuiFont robotoSlab;
    private ImGuiFontScope fontScope = ImGuiFontScope.create();

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void draw() {
        if (ImGui.begin("Another Test Overlay")) {

            fontScope.push(robotoSlab);
            ImGui.text("Hello with Roboto Slab!");
            fontScope.pop();

            fontScope.push(sekuya);
            ImGui.text("Hello with Sekuya!");
            fontScope.pop();

            ImGui.end();
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createFonts() {
        if (robotoSlab == null) {
            try (InputStream is = TestOverlay.class.getResourceAsStream("/assets/testmod/fonts/RobotoSlab-Regular.ttf")) {
                if (is == null)
                    throw new RuntimeException("Failed to load Roboto Slab font: resource not found");

                ImGuiFont.loadFromStreamTTF(
                        is,
                        FontIdentifier.of(TestModInitializer.getModId(), "Roboto Slab 2"),
                        ImGui.getFontSize() * 2
                ).thenAccept(
                        font -> robotoSlab = font
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Roboto Slab font", e);
            }
        }

        if (sekuya == null) {
            try (InputStream is = TestOverlay.class.getResourceAsStream("/assets/testmod/fonts/Sekuya-Regular.ttf")) {
                if (is == null)
                    throw new RuntimeException("Failed to load Sekuya Slab font: resource not found");

                ImGuiFont.loadFromStreamTTF(
                        is,
                        FontIdentifier.of(TestModInitializer.getModId(), "Sekuya 2"),
                        ImGui.getFontSize() * 2
                ).thenAccept(
                        font -> sekuya = font
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Sekuya font", e);
            }
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String getId() {
        return "another_test_overlay";
    }
}
