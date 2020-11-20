package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import com.gamesense.client.clickgui.GameSenseGUI;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;
import com.lukflug.panelstudio.settings.Toggleable;

public class ListModule extends HUDModule {
	
	public ListModule(FixedComponent component) {
		super(component);
	}

	
	protected static interface HUDList {
		public int getListSize();
		public String getListItem (int index);
		public Color getItemColor (int index);
	}
	
	
	protected static class ListComponent extends HUDComponent {
		protected Toggleable sortUp,sortRight;
		protected HUDList list;
		
		public ListComponent (String name, Point position, HUDList list) {
			super(name,GameSenseGUI.theme.getPanelRenderer(),position);
			this.list=list;
			//super("Notifications",GameSenseGUI.theme.getPanelRenderer(),new Point(0,50));
		}

		@Override
		public void render (Context context) {
			super.render(context);
			for (int i=0;i<list.getListSize();i++) {
				String s=list.getListItem(i);
				Point p=context.getPos();
				if (sortUp.isOn()) {
					p.translate(0,context.getSize().height-(i+1)*context.getInterface().getFontHeight());
				} else {
					p.translate(0,i*context.getInterface().getFontHeight());
				}
				if (sortRight.isOn()) {
					p.translate(getWidth(context.getInterface())-context.getInterface().getFontWidth(s),0);
				}
				context.getInterface().drawString(p,s,list.getItemColor(i));
			}
		}

		@Override
		public int getWidth(Interface inter) {
			int width=inter.getFontWidth(getTitle());
			for (int i=0;i<list.getListSize();i++) {
				String s=list.getListItem(i);
				width=Math.max(width,inter.getFontWidth(s));
			}
			return width;
		}

		@Override
		public void getHeight(Context context) {
			context.setHeight(renderer.getHeight()+(-1)*context.getInterface().getFontHeight());
		}
	}
}
