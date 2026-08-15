// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.font;

public record FontIdentifier(String modId, String fontName) {

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return modId + "/" + fontName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() &&
               obj.toString().equals(this.toString());
    }

    public static FontIdentifier fromString(final String str) {
        String[] parts = str.split("/");
        if (parts.length != 2)
            throw new IllegalArgumentException("Invalid font identifier: " + str);

        return new FontIdentifier(parts[0], parts[1]);
    }

    public static FontIdentifier of(final String modId, final String fontName) {
        return new FontIdentifier(modId, fontName);
    }

}
