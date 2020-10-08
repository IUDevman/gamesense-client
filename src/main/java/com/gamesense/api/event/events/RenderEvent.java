package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;

public class RenderEvent extends GameSenseEvent{
		private final float partialTicks;

		public RenderEvent(float ticks){
			super();
			partialTicks = ticks;
		}

		public float getPartialTicks(){
			return partialTicks;
		}
}
