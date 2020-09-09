package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;

public class PlayerJoinEvent extends GameSenseEvent{
	private final String name;

	public PlayerJoinEvent(String n){
		super();
		name = n;
	}

	public String getName(){
		return name;
	}
}
