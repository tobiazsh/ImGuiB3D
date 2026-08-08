// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.versioning;

public class VersionComparator {

    /**
     * Checks if the provided VersionConstraint satisfies the provided version.
     */
    public static boolean satisfies(Version version, VersionConstraint constraint) {
        int comparison = version.compareTo(constraint.version());

        return switch (constraint.operator()) {
            case LT -> comparison < 0;
            case GT -> comparison > 0;
            case EQ -> comparison == 0;
            case NEQ -> comparison != 0;
            case LTE -> comparison <= 0;
            case GTE -> comparison >= 0;
        };
    }

    /**
     * Checks if multiple provided version constraints satisfy the provided version.
     */
    public static boolean satisfies(Version version, VersionConstraint... constraint) {
        for (VersionConstraint constraint1 : constraint) {
            if (!satisfies(version, constraint1))
                return false;
        }

        return true;
    }
}
