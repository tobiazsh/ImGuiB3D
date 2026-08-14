// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.font;

public enum FontImportance implements Comparable<FontImportance> {
    HIGH(0),
    MEDIUM(1),
    LOW(2);

    private final int priority;

    FontImportance(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
