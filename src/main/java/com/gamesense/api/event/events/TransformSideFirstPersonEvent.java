package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.util.EnumHandSide;

public class TransformSideFirstPersonEvent extends GameSenseEvent{
	private final EnumHandSide handSide;

	public TransformSideFirstPersonEvent(EnumHandSide handSide){
		this.handSide = handSide;
	}

	public EnumHandSide getHandSide(){
		return handSide;
	}
}
