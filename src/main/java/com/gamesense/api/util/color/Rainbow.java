package com.gamesense.api.util.color;

import com.gamesense.api.event.EventProcessor;

import java.awt.*;

public class Rainbow{
	public static int getInt(){
		return EventProcessor.INSTANCE.getRgb();
	}

	public static Color getColor(){
		return EventProcessor.INSTANCE.getC();
	}

	public static Color getColorWithOpacity(int opacity){
		return new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), opacity);
	}

	public static int getIntWithOpacity(int opacity){
		return getColorWithOpacity(opacity).getRGB();
	}
}
