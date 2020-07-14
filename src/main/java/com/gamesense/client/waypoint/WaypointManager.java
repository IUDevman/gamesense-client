package com.gamesense.client.waypoint;

import java.util.ArrayList;
import java.util.List;

public class WaypointManager {
    List<Waypoint> waypoints;

    public  WaypointManager(){
        waypoints = new ArrayList<>();
    }

    public List<Waypoint> getWaypoints(){
        return  waypoints;
    }

    public void addWaypoint(Waypoint waypoint){
        delWaypoint(waypoint);
        waypoints.add(waypoint);
    }

    public void delWaypoint(Waypoint waypoint){
        waypoints.remove(waypoint);
    }

    public Waypoint getWaypointByName(String name){
        Waypoint point = waypoints.stream().filter(w -> w.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return point;
    }

}
