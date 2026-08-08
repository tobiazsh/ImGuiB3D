// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.versioning;

public enum ComparisonOperator {
    LT,
    GT,
    EQ,
    NEQ,
    LTE,
    GTE;

    public static ComparisonOperator fromString(String op) {
        return switch (op) {
            case "<" -> LT;
            case ">" -> GT;
            case "=" -> EQ;
            case "!=" -> NEQ;
            case "<=" -> LTE;
            case ">=" -> GTE;
            default -> throw new IllegalArgumentException("Unknown comparison operator: " + op);
        };
    }
}
