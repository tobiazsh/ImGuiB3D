// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.font;

import com.google.common.collect.ImmutableMap;
import dev.tobiazsh.imguib3d.client.util.MutableInt;

import java.util.HashMap;
import java.util.Map;

public final class RegistrationBudget {
    private final ImmutableMap<FontImportance, Integer> limitMap;
    private final Map<FontImportance, MutableInt> budgetMap;

    public RegistrationBudget(final Map<FontImportance, Integer> limitMap) {
        this.limitMap = ImmutableMap.copyOf(limitMap);
        this.budgetMap = limitMap
                .entrySet()
                .stream()
                .collect(
                        HashMap::new,
                        (map, entry) ->
                                map.put(entry.getKey(), new MutableInt(entry.getValue())), HashMap::putAll
                );
    }

    public static RegistrationBudget defaultBudget() {
        return new RegistrationBudget(Map.of(
            FontImportance.HIGH, -1,
            FontImportance.MEDIUM, 5,
            FontImportance.LOW, 2
        ));
    }

    public boolean pass(final FontImportance importance) {
        MutableInt budget = budgetMap.get(importance);

        if (budget == null)
            return false;

        int remaining = budget.get();

        if (remaining == -1)
            return true; // Pass immediately if the budget is unlimited

        if (remaining > 0) {
            budget.decrement();
            return true;
        }

        return false;
    }

    public void reset() {
        this.limitMap.forEach((importance, limit) -> budgetMap.get(importance).set(limit));
    }
}
