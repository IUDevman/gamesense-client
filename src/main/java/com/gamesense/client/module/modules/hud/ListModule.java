package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;

import com.gamesense.api.config.PositionConfig;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.hud.HUDComponent;

/**
 * @author lukflug
 */
public class ListModule {
	protected static interface HUDList {
		public int getSize();
		public String getItem (int index);
		public Color getItemColor (int index);
		public boolean sortUp();
		public boolean sortRight();
	}
	
	
	protected static class ListComponent extends HUDComponent implements PositionConfig {
		protected HUDList list;
		protected boolean lastUp=false;
		
		public ListComponent (String name, Point position, HUDList list) {
			super(name,GameSenseGUI.theme.getPanelRenderer(),position);
			this.list=list;
		}

		@Override
		public void render (Context context) {
			super.render(context);
			for (int i=0;i<list.getSize();i++) {
				String s=list.getItem(i);
				Point p=context.getPos();
				if (list.sortUp()) {
					p.translate(0,context.getSize().height-(i+1)*context.getInterface().getFontHeight());
				} else {
					p.translate(0,i*context.getInterface().getFontHeight());
				}
				if (list.sortRight()) {
					p.translate(getWidth(context.getInterface())-context.getInterface().getFontWidth(s),0);
				}
				context.getInterface().drawString(p,s,list.getItemColor(i));
			}
		}
		
		@Override
		public Point getPosition (Interface inter) {
			int height=renderer.getHeight()+(list.getSize()-1)*inter.getFontHeight();
			if (lastUp!=list.sortUp()) {
				if (list.sortUp()) position.translate(0,height);
				else position.translate(0,-height);
				lastUp=list.sortUp();
			}
			if (list.sortUp()) return new Point(position.x,position.y-height);
			else return new Point(position);
		}
		
		@Override
		public void setPosition (Interface inter, Point position) {
			int height=renderer.getHeight()+(list.getSize()-1)*inter.getFontHeight();
			if (list.sortUp()) this.position=new Point(position.x,position.y+height);
			else this.position=new Point(position);
		}

		@Override
		public int getWidth(Interface inter) {
			int width=inter.getFontWidth(getTitle());
			for (int i=0;i<list.getSize();i++) {
				String s=list.getItem(i);
				width=Math.max(width,inter.getFontWidth(s));
			}
			return width;
		}

		@Override
		public void getHeight(Context context) {
			context.setHeight(renderer.getHeight()+(list.getSize()-1)*context.getInterface().getFontHeight());
		}

		@Override
		public Point getConfigPos() {
			return position;
		}

		@Override
		public void setConfigPos(Point pos) {
			position=pos;
			lastUp=list.sortUp();
		}
	}
}
