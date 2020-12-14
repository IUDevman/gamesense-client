package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.gamesense.api.settings.Setting;

import net.minecraft.util.text.TextComponentString;

// PanelStudio rewrite by lukflug
public class Notifications extends HUDModule {
	private static Setting.Boolean sortUp;
	private static Setting.Boolean sortRight;
	public static Setting.Boolean disableChat;
	private static NotificationsList list=new NotificationsList();
	
	public Notifications(){
		super("Notifications",new Point(0,50));
	}
	
	@Override
	public void populate() {
		component=new ListModule.ListComponent(getName(),position,list);
	}

	public void setup() {
		sortUp = registerBoolean("Sort Up", "SortUp", false);
		sortRight = registerBoolean("Sort Right", "SortRight", false);
		disableChat = registerBoolean("No Chat Msg", "NoChatMsg", true);
	}

	private static int waitCounter;

	public void onUpdate() {
		if (waitCounter < 500) {
			waitCounter++;
			return;
		} else {
			waitCounter = 0;
		}
		if (list.list.size() > 0)
			list.list.remove(0);
	}

	public static void addMessage(TextComponentString m) {
		if(list.list.size() < 3) {
			list.list.remove(m);
			list.list.add(m);
		}else {
			list.list.remove(0);
			list.list.remove(m);
			list.list.add(m);
		}
	}


	private static class NotificationsList implements ListModule.HUDList {
		public List<TextComponentString> list = new ArrayList<>();
		
		@Override
		public int getSize() {
			return list.size();
		}
	
		@Override
		public String getItem(int index) {
			return list.get(index).getText();
		}
	
		@Override
		public Color getItemColor(int index) {
			return new Color(255,255,255);
		}

		@Override
		public boolean sortUp() {
			return sortUp.isOn();
		}

		@Override
		public boolean sortRight() {
			return sortRight.isOn();
		}
	}
}