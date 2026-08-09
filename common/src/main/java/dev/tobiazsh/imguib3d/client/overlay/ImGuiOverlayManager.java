// Licensen

package dev.tobiazsh.imguib3d.client.overlay;

import java.util.*;

/**
 * A manager for registering {@link ImGuiOverlay}s.
 * Note that you do not need to use the default instance of the manager
 * using {@link #getInstance()}. Instead, you can also create your own manager for your mod specifically, if that's
 * what you need, although discouraged. To do that, first create your own instance of the manager with a fitting ID,
 * then make a mixin into the game renderer and render every overlay inside the manager.
 */
public final class ImGuiOverlayManager {

    public static final ImGuiOverlayManager INSTANCE = new ImGuiOverlayManager("ImGuiB3D Default");

    private final String id;

    private final Map<String, ImGuiOverlay> overlays = new HashMap<>();

    /**
     * Construct a new ImGuiOverlayManager.
     * @param id The id for the overlay manager. Not used at the moment, but can be used to identify
     *           the manager in the future.
     */
    public ImGuiOverlayManager(String id) {
        this.id = id;
    }

    /**
     * Returns the default instance of the ImGuiOverlayManager.
     */
    public static ImGuiOverlayManager getInstance() {
        return INSTANCE;
    }

    public String getId() {
        return id;
    }

    /**
     * Returns a copy of the collection of the current registered overlays.
     */
    public Collection<ImGuiOverlay> getOverlays() {
        return List.copyOf(overlays.values());
    }

    /**
     * Adds provided overlay.
     * @param overlay The new overlay to add.
     */
    public void add(ImGuiOverlay overlay) {
        overlays.put(overlay.getId(), overlay);
    }

    /**
     * Searches for an overlay with the provided id.
     * @param id The id of the overlay to search for.
     * @return {@link Optional<ImGuiOverlay>}, or {@link Optional#empty()} if no overlay with the provided id was found.
     */
    public Optional<ImGuiOverlay> find(String id) {
        return Optional.ofNullable(overlays.get(id));
    }

    /**
     * Remove the provided overlay from the map.
     * @param overlay The overlay to remove.
     */
    public void remove(ImGuiOverlay overlay) {
        remove(overlay.getId());
    }

    /**
     * Removes the provided overlay from the map.
     * @param id The id of the overlay to remove.
     */
    public void remove(String id) {
        overlays.remove(id);
    }

    /**
     * Returns information whether the collection with the provided id is present or not.
     * @param id The id of the overlay to look up.
     * @return `true` if the overlay is present, `false` otherwise.
     */
    public boolean contains(String id) {
        return overlays.containsKey(id);
    }

    /**
     * Returns the registered overlays sorted by priority.
     * Overlays with a higher priority are returned later in the list
     * and therefore rendered on top of lower-priority overlays.
     */
    public List<ImGuiOverlay> getOverlaysSorted() {
        return overlays.values().stream().sorted(Comparator.comparingInt(ImGuiOverlay::priority)).toList();
    }
}
