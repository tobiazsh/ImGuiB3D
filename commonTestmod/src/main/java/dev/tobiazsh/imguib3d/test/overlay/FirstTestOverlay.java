package dev.tobiazsh.imguib3d.test.overlay;

import dev.tobiazsh.imguib3d.client.font.FontIdentifier;
import dev.tobiazsh.imguib3d.client.font.ImGuiFont;
import dev.tobiazsh.imguib3d.client.font.ImGuiFontScope;
import dev.tobiazsh.imguib3d.client.natives.drawlist.ImDrawListExt;
import dev.tobiazsh.imguib3d.client.overlay.ImGuiOverlay;
import dev.tobiazsh.imguib3d.test.ImplementableTestPart;
import imgui.ImDrawList;
import imgui.ImGui;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class FirstTestOverlay implements ImGuiOverlay, ImplementableTestPart {

    private @Nullable ImGuiFont sekuya;
    private @Nullable ImGuiFont robotoSlab;
    private final ImGuiFontScope fontScope = ImGuiFontScope.create();

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

            ImGui.dummy(0, 100); // room above so the rotated text isn't clipped

            ImDrawList dl = ImGui.getWindowDrawList();

            int idxBefore = ImDrawListExt.getVtxBufferSize(dl.ptr);
            ImGui.text("Rotated text (45 degrees):");
            int idxAfter = ImDrawListExt.getVtxBufferSize(dl.ptr);

            ImGui.dummy(0, 100); // room below

            rotate(dl, 45, idxBefore, idxAfter);

            ImGui.end();
        }
    }

    /**
     * Rotates the vertices in the draw list between the specified indices by the given angle.
     *
     * @param drawList ImGui's draw list to modify.
     * @param angle The angle in degrees to rotate the vertices.
     * @param dlBeforeIdx The index in the draw list's vertex buffer before the vertices to rotate.
     * @param dlAfterIdx The index in the draw list's vertex buffer after the vertices to rotate.
     */
    @SuppressWarnings("SameParameterValue")
    private void rotate(ImDrawList drawList, int angle, int dlBeforeIdx, int dlAfterIdx) {
        ByteBuffer vtx = ImDrawListExt.getVtxBuffer(drawList.ptr).order(ByteOrder.nativeOrder());

        // Pivot = center of the new vertices' bounding box
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        for (int i = dlBeforeIdx; i < dlAfterIdx; i++) {
            float x = vtx.getFloat(i * 20);
            float y = vtx.getFloat(i * 20 + 4);
            minX = Math.min(minX, x); maxX = Math.max(maxX, x);
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
        }
        float cx = (minX + maxX) / 2f, cy = (minY + maxY) / 2f;

        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad), sin = (float) Math.sin(rad);

        for (int i = dlBeforeIdx; i < dlAfterIdx; i++) {
            int o = i * 20;
            float dx = vtx.getFloat(o) - cx;
            float dy = vtx.getFloat(o + 4) - cy;
            vtx.putFloat(o,     cx + dx * cos - dy * sin);
            vtx.putFloat(o + 4, cy + dx * sin + dy * cos);
            vtx.putInt(o + 16, 0xFF2050FF); // debug tint, so you can see what you touched
        }

    }

    @Override
    public void createFonts() {
        if (robotoSlab == null) {
            try (InputStream is = FirstTestOverlay.class.getResourceAsStream("/assets/testmod/fonts/RobotoSlab-Regular.ttf")) {
                if (is == null)
                    throw new RuntimeException("Failed to load Roboto Slab font: resource not found");

                ImGuiFont.loadFromStreamTTF(
                        is,
                        FontIdentifier.of(getModId(), "Roboto Slab 2"),
                        ImGui.getFontSize() * 2
                ).thenAccept(
                        font -> robotoSlab = font
                );
            } catch (IOException e) {
                getLogger().error("Failed to load Roboto Slab font: " + e.getMessage());
                throw new RuntimeException("Failed to load Roboto Slab font", e);
            }
        }

        if (sekuya == null) {
            try (InputStream is = FirstTestOverlay.class.getResourceAsStream("/assets/testmod/fonts/Sekuya-Regular.ttf")) {
                if (is == null)
                    throw new RuntimeException("Failed to load Sekuya Slab font: resource not found");

                ImGuiFont.loadFromStreamTTF(
                        is,
                        FontIdentifier.of(getModId(), "Sekuya 2"),
                        ImGui.getFontSize() * 2
                ).thenAccept(
                        font -> sekuya = font
                );
            } catch (IOException e) {
                getLogger().error("Failed to load Sekuya font: " + e.getMessage());
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
