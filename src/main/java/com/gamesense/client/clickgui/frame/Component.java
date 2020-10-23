package com.gamesense.client.clickgui.frame;

import net.minecraft.client.Minecraft;

public abstract class Component{
	protected Minecraft mc = Minecraft.getMinecraft();

	public void updateComponent(final int mouseX, final int mouseY){
	}

	public void renderComponent(){
	}

	public void setOff(final int newOff){
	}

	public int getHeight(){
		return 0;
	}

	public int getParentHeight(){
		return 0;
	}

	public void mouseClicked(final int mouseX, final int mouseY, final int button){
	}

	public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton){
	}

	public void keyTyped(final char typedChar, final int key){
	}
}