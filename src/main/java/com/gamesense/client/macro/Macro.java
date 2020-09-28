package com.gamesense.client.macro;

import net.minecraft.client.Minecraft;

public class Macro{
	int key;
	String value;

	public Macro(int inputKey, String inputValue){
		key = inputKey;
		value = inputValue;
	}

	public void onMacro(){
		if (Minecraft.getMinecraft().player != null) {
			Minecraft.getMinecraft().player.sendChatMessage(value);
		}
	}

	public int getKey(){
		return key;
	}

	public String getValue(){
		return value;
	}
}