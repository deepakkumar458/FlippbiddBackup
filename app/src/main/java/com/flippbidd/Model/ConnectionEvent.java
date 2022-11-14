package com.flippbidd.Model;

public class ConnectionEvent {
    private final boolean isConnected;

    public ConnectionEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
