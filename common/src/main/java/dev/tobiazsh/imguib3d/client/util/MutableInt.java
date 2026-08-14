// Licensed under LGPL-3.0
// Copyright © 2026 Tobiazsh

package dev.tobiazsh.imguib3d.client.util;

public final class MutableInt {

    private int value;

    public MutableInt(int value) {
        this.value = value;
    }

    public void decrement() {
        value--;
    }

    public void increment() {
        value++;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }
}
