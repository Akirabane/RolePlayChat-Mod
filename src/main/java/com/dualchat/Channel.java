package com.dualchat;

public enum Channel {
    RP,
    HRP;

    public static Channel byOrdinal(int o) {
        return values()[Math.floorMod(o, values().length)];
    }

    public String displayName() {
        return this == RP ? "RP" : "HRP";
    }

    /** Color used both for the toggle button highlight and the message prefix. */
    public int displayColor() {
        return this == RP ? 0xFFFFAA00 /* amber */ : 0xFF55AAFF /* sky blue */;
    }
}
