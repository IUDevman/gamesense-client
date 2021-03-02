package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;

public class PlayerLeaveEvent extends GameSenseEvent {

    private final String name;

    public PlayerLeaveEvent(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}