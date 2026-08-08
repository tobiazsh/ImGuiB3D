// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.versioning;

public record Version(int major, int minor, int patch) implements Comparable<Version> {
    @Override
    public int compareTo(Version o) {
        int result = Integer.compare(major, o.major);
        if (result != 0) return result;

        result = Integer.compare(minor, o.minor);
        if (result != 0) return result;

        return Integer.compare(patch, o.patch);
    }

    /**
     * Checks if the provided VersionConstraint satisfies the version.
     */
    public boolean satisfies(VersionConstraint constraint) {
        return VersionComparator.satisfies(this, constraint);
    }

    /**
     * Checks if multiple provided version constraints satisfy the version.
     */
    public boolean satisfies(VersionConstraint... constraints) {
        return VersionComparator.satisfies(this, constraints);
    }
}
