package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends Module{
	public Notifications(){
		super("Notifications", Category.HUD);
	}

	Setting.Integer notX;
	Setting.Integer notY;
	Setting.Boolean sortUp;
	Setting.Boolean sortRight;
	public static Setting.Boolean disableChat;

	public void setup(){
		disableChat = registerBoolean("No Chat Msg", "NoChatMsg", true);
		sortUp = registerBoolean("Sort Up", "SortUp", false);
		sortRight = registerBoolean("Sort Right", "SortRight", false);
		notX = registerInteger("X", "X", 0,0,1000);
		notY = registerInteger("Y", "Y", 50, 0 , 1000);
	}

	int sort;
	int notCount;
	int waitCounter;
	TextFormatting notColor;
	static List<TextComponentString> list = new ArrayList<>();

	public void onUpdate(){
		if (waitCounter < 500){
			waitCounter++;
			return;
		} else{
			waitCounter = 0;
		}
		if(list.size() > 0)
			list.remove(0);
	}

	public void onRender(){
		if (sortUp.getValue()){
			sort = -1; }
		else{
			sort = 1; }
		notCount = 0;
		for(TextComponentString s : list){
			notCount = list.indexOf(s) + 1;
			notColor = s.getStyle().getColor();

			if (sortUp.getValue()){
				if (sortRight.getValue()){
					drawStringWithShadow(s.getText(), notX.getValue() - getWidth(s.getText()), notY.getValue() + (notCount * 10), 0xffffffff);
				}
				else{
					drawStringWithShadow(s.getText(), notX.getValue(),notY.getValue() + (notCount * 10), 0xffffffff);
				}
			}
			else{
				if (sortRight.getValue()){
					drawStringWithShadow(s.getText(), notX.getValue() - getWidth(s.getText()), notY.getValue() + (notCount * -10), 0xffffffff);
				}
				else{
					drawStringWithShadow(s.getText(), notX.getValue(),notY.getValue() + (notCount * -10), 0xffffffff);
				}
			}
		}
	}

	public static void addMessage(TextComponentString m){
		if(list.size() < 3){
			list.remove(m);
			list.add(m);
		}else{
			list.remove(0);
			list.remove(m);
			list.add(m);
		}
	}

	//bullshit port from HUD
	private void drawStringWithShadow (String text,int x, int y, int color){
		if (HUD.customFont.getValue())
			GameSenseMod.fontRenderer.drawStringWithShadow(text, x, y, color);
		else
			mc.fontRenderer.drawStringWithShadow(text, x, y, color);
	}

	//bullshit port from HUD
	private int getWidth(String s){
		if(HUD.customFont.getValue()) return GameSenseMod.fontRenderer.getStringWidth(s);
		else return mc.fontRenderer.getStringWidth(s);
	}
}