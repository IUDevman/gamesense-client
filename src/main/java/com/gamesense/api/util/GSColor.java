package com.gamesense.api.util;

import java.awt.Color;

/**
* @author lukflug
*/
// Why would anyone ever need to use JavaDoc properly?

public class GSColor extends Color {
	GSColor(int rgb) {
		super(rgb);
	}
	
	GSColor(int rgba, boolean hasalpha) {
		super(rgba,hasalpha);
	}
	
	GSColor(int r, int g, int b) {
		super(r,g,b);
	}
	
	GSColor(int r, int g, int b, int a) {
		super(r,g,b,a);
	}
	
	float getHue() {
		return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[0];
	}
	
	float getSaturation() {
		return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[1];
	}
	
	float getBrightness() {
		return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[2];
	}
}
