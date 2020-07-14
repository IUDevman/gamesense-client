package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import com.gamesense.api.util.Location;


public class JumpEvent extends GameSenseEvent {
    private Location location;

    public JumpEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
