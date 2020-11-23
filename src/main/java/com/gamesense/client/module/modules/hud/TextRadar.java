package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.players.enemy.Enemies;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.client.module.modules.gui.ColorMain;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

// PanelStudio rewrite by lukflug
public class TextRadar extends ListModule {
	private static Setting.Boolean sortUp;
	private static Setting.Boolean sortRight;
	private static Setting.Integer range;
	private static Setting.Mode display;
	private static PlayerList list=new PlayerList();
	
	public TextRadar(){
		super(new ListModule.ListComponent("TextRadar",new Point(0,50),list),new Point(0,50));
	}

	public void setup(){
		ArrayList<String> displayModes = new ArrayList<>();
		displayModes.add("All");
		displayModes.add("Friend");
		displayModes.add("Enemy");
		display = registerMode("Display", "Display", displayModes, "All");
		sortUp = registerBoolean("Sort Up", "SortUp", false);
		sortRight = registerBoolean("Sort Right", "SortRight", false);
		range = registerInteger("Range", "Range", 100, 1, 260);
	}

	public void onRender() {
		list.players.clear();
		mc.world.loadedEntityList.stream()
				.filter(e->e instanceof EntityPlayer)
				.filter(e->e != mc.player)
				.forEach(e->{
					if (mc.player.getDistance(e) > range.getValue()){
						return;
					}
					if (display.getValue().equalsIgnoreCase("Friend") && !(Friends.isFriend(e.getName()))){
						return;
					}
					if (display.getValue().equalsIgnoreCase("Enemy") && !(Enemies.isEnemy(e.getName()))){
						return;
					}
					list.players.add((EntityPlayer)e);
				});
	}
	
	
	private static class PlayerList implements ListModule.HUDList {
		public List<EntityPlayer> players=new ArrayList<EntityPlayer>();
		
		@Override
		public int getSize() {
			return players.size();
		}

		@Override
		public String getItem(int index) {
			EntityPlayer e=players.get(index);
			TextFormatting friendcolor;
			if (Friends.isFriend(e.getName())) {
				friendcolor = ColorMain.getFriendColor();
			} else if (Enemies.isEnemy(e.getName())) {
				friendcolor = ColorMain.getEnemyColor();
			} else {
				friendcolor = TextFormatting.GRAY;
			}
			TextFormatting healthcolor;
			float health=e.getHealth()+e.getAbsorptionAmount();
			if (health<=5) {
				healthcolor = TextFormatting.RED;
			} else if (health>5 && health<15) {
				healthcolor = TextFormatting.YELLOW;
			} else {
				healthcolor = TextFormatting.GREEN;
			}
			TextFormatting distancecolor;
			float distance=mc.player.getDistance(e);
			if (distance<20) {
				distancecolor = TextFormatting.RED;
			} else if (distance>=20 && distance<50){
				distancecolor = TextFormatting.YELLOW;
			} else {
				distancecolor = TextFormatting.GREEN;
			}
			return TextFormatting.GRAY+"["+healthcolor+((int)health)+TextFormatting.GRAY +"] "+friendcolor+e.getName()+TextFormatting.GRAY+" ["+distancecolor+((int)distance)+TextFormatting.GRAY+"]";
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