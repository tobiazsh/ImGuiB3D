package dev.tobiazsh.imguib3d.client.versioning;

public record VersionConstraint(ComparisonOperator operator, Version version) {
}
