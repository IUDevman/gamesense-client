package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.*;
import java.util.stream.Collectors;

public class PvPInfo extends Module{
	public PvPInfo(){super("PvPInfo", Category.Misc);}

	List<Entity> knownPlayers = new ArrayList<>();
	List<Entity> antipearlspamplz = new ArrayList<>();
	List<Entity> players;
	List<Entity> pearls;
	private HashMap<String, Integer> popList = new HashMap();
	public Set strengthedPlayers;
	public Set renderPlayers;


	Setting.Boolean visualrange;
	Setting.Boolean pearlalert;
	Setting.Boolean popcounter;
	Setting.Boolean strengthdetect;
	Setting.Mode ChatColor;
	Setting.Mode PlayerColor;
	Setting.Mode CounterColor;
	Setting.Mode PopColor;

	public void setup(){
		ArrayList<String> colors = new ArrayList<>();
		colors.add("Black");
		colors.add("Dark Green");
		colors.add("Dark Red");
		colors.add("Gold");
		colors.add("Dark Gray");
		colors.add("Green");
		colors.add("Red");
		colors.add("Yellow");
		colors.add("Dark Blue");
		colors.add("Dark Aqua");
		colors.add("Dark Purple");
		colors.add("Gray");
		colors.add("Blue");
		colors.add("Aqua");
		colors.add("Light Purple");
		colors.add("White");
		visualrange = registerBoolean("Visual Range", "VisualRange", false);
		pearlalert = registerBoolean("Pearl Alert", "PearlAlert",false);
		popcounter = registerBoolean("Pop Counter", "PopCounter", false);
		strengthdetect = registerBoolean("Strength Detect", "StrengthDetect", false);
		ChatColor = registerMode("ChatColor", "ChatColor", colors, "Dark Gray");
		PlayerColor = registerMode("PlayerColor", "PlayerColor", colors, "Dark Aqua");
		PopColor = registerMode("PopColor", "PopColor", colors, "Gold");
	}

	@EventHandler
	public Listener<TotemPopEvent> totemPopEvent = new Listener<>(event -> {
	 if (popcounter.getValue()){
		 if (popList == null){
			 popList = new HashMap<>();
		}

		 if (popList.get(event.getEntity().getName()) == null){
			 popList.put(event.getEntity().getName(), 1);
			 Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.YELLOW + "" + ChatFormatting.BOLD + "Totem Counter" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + event.getEntity().getName() + ChatFormatting.RESET + getChatColor() + " popped " + ChatFormatting.RESET + getPopColor() + "" + ChatFormatting.BOLD + 1 + ChatFormatting.RESET + getChatColor() + " totem!");
		} else if (!(popList.get(event.getEntity().getName()) == null)){
			 int popCounter = popList.get(event.getEntity().getName());
			 int newPopCounter = popCounter += 1;
			 popList.put(event.getEntity().getName(), newPopCounter);
			 Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.YELLOW + "" + ChatFormatting.BOLD + "Totem Counter" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + event.getEntity().getName() + ChatFormatting.RESET + getChatColor() + " popped " + ChatFormatting.RESET + getPopColor() + "" + ChatFormatting.BOLD + newPopCounter + ChatFormatting.RESET + getChatColor() + " totems!");
		}
	}
	});

	public void onUpdate(){
	if (visualrange.getValue()){
		if (mc.player == null) return;
		players = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
		try{
			for (Entity e : players){
				if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(mc.player.getName())){
					if (!knownPlayers.contains(e)){
						knownPlayers.add(e);
						Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.YELLOW + "" + ChatFormatting.BOLD + "Visual Range" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + e.getName() + ChatFormatting.RESET + getChatColor() + " has been spotted thanks to " + ChatFormatting.RESET + ChatFormatting.WHITE + "Game" + ChatFormatting.RESET + ChatFormatting.DARK_GREEN + "Sense" + ChatFormatting.RESET + getChatColor() + "!");
					}
				}
			}
		} catch (Exception e){
		} // ez no crasherino
		try{
			for (Entity e : knownPlayers){
				if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(mc.player.getName())){
					if (!players.contains(e)){
						knownPlayers.remove(e);
					}
				}
			}
		} catch (Exception e){
		} // ez no crasherino pt.2
	}
	if (pearlalert.getValue()){
		pearls = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderPearl).collect(Collectors.toList());
		try{
			for (Entity e : pearls){
				if (e instanceof EntityEnderPearl){
					if (!antipearlspamplz.contains(e)){
						antipearlspamplz.add(e);
						Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.BLUE + "" + ChatFormatting.BOLD + "Pearl Watcher" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + e.getEntityWorld().getClosestPlayerToEntity(e, 3).getName() + ChatFormatting.RESET + ChatFormatting.DARK_BLUE + " has just thrown a pearl!");
					}
				}
			}
		} catch (Exception e){
		}
	}
	if (popcounter.getValue()){
		for (EntityPlayer player : mc.world.playerEntities){
			if (player.getHealth() <= 0){
				if (popList.containsKey(player.getName())){
					Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.YELLOW + "" + ChatFormatting.BOLD + "Totem Counter" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + player.getName() + ChatFormatting.RESET + getChatColor() + " died after popping " + ChatFormatting.RESET + getPopColor() + "" + ChatFormatting.BOLD + popList.get(player.getName()) + ChatFormatting.RESET + getChatColor() + " totems!");
					popList.remove(player.getName(), popList.get(player.getName()));
				}
			}
		}
	}
	if (strengthdetect.getValue()){
		if (this.isEnabled() && mc.player != null){
			Iterator var1 = mc.world.playerEntities.iterator();
			while (var1.hasNext()){
				EntityPlayer ent = (EntityPlayer) var1.next();
				if (EntityUtil.isLiving(ent) && ent.getHealth() > 0.0F){
					if (ent.isPotionActive(MobEffects.STRENGTH) && !this.strengthedPlayers.contains(ent)){
						Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.DARK_RED + "" + ChatFormatting.BOLD + "Strength Watcher" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + ent.getDisplayNameString() + ChatFormatting.RESET + ChatFormatting.RED + " has (drank) strength!");
						this.strengthedPlayers.add(ent);
					}

					if (this.strengthedPlayers.contains(ent) && !ent.isPotionActive(MobEffects.STRENGTH)){
						Command.sendRawMessage(getChatColor() + "" + ChatFormatting.BOLD + "[" + ChatFormatting.RESET + ChatFormatting.DARK_RED + "" + ChatFormatting.BOLD + "Strength Watcher" + ChatFormatting.RESET + getChatColor() + "" + ChatFormatting.BOLD + "]" + ChatFormatting.RESET + getPlayerColor() + " " + ChatFormatting.BOLD + ent.getDisplayNameString() + ChatFormatting.RESET + ChatFormatting.GREEN + " no longer has strength!");
						this.strengthedPlayers.remove(ent);
					}
					this.checkRender();
				}
			}
		}
	}
	}

	public void checkRender(){
		try{
			this.renderPlayers.clear();
			Iterator var1 = mc.world.playerEntities.iterator();

			EntityPlayer ent;
			while (var1.hasNext()){
				ent = (EntityPlayer) var1.next();
				if (EntityUtil.isLiving(ent) && ent.getHealth() > 0.0F){
					this.renderPlayers.add(ent);
				}
			}
			var1 = this.strengthedPlayers.iterator();
			while (var1.hasNext()){
				ent = (EntityPlayer) var1.next();
				if (!this.renderPlayers.contains(ent)){
					this.strengthedPlayers.remove(ent);
				}
			}
		} catch (Exception var3){
		}

	}

	@EventHandler
	public Listener<PacketEvent.Receive> totemPopListener = new Listener<>(event -> {

		if (mc.world == null || mc.player == null){
			return;
		}
		if (event.getPacket() instanceof SPacketEntityStatus){
			SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
			if (packet.getOpCode() == 35){
				Entity entity = packet.getEntity(mc.world);
				GameSenseMod.EVENT_BUS.post(new TotemPopEvent(entity));
			}
		}

	});

	public void onEnable(){
		GameSenseMod.EVENT_BUS.subscribe(this);
		popList = new HashMap<>();
		this.strengthedPlayers = new HashSet();
		this.renderPlayers = new HashSet();
	}

	public ChatFormatting getChatColor(){
		if (ChatColor.getValue().equalsIgnoreCase("Black")){
			return ChatFormatting.BLACK;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Dark Green")){
			return ChatFormatting.DARK_GREEN;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Dark Red")){
			return ChatFormatting.DARK_RED;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Gold")){
			return ChatFormatting.GOLD;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Dark Gray")){
			return ChatFormatting.DARK_GRAY;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Green")){
			return ChatFormatting.GREEN;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Red")){
			return ChatFormatting.RED;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Yellow")){
			return ChatFormatting.YELLOW;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Dark Blue")){
			return ChatFormatting.DARK_BLUE;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Dark Aqua")){
			return ChatFormatting.DARK_AQUA;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Dark Purple")){
			return ChatFormatting.DARK_PURPLE;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Gray")){
			return ChatFormatting.GRAY;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Blue")){
			return ChatFormatting.BLUE;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Light Purple")){
			return ChatFormatting.LIGHT_PURPLE;
		}
		if (ChatColor.getValue().equalsIgnoreCase("White")){
			return ChatFormatting.WHITE;
		}
		if (ChatColor.getValue().equalsIgnoreCase("Aqua")){
			return ChatFormatting.AQUA;
		}
		return null;
	}

	public ChatFormatting getPlayerColor(){
		if (PlayerColor.getValue().equalsIgnoreCase("Black")){
			return ChatFormatting.BLACK;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Dark Green")){
			return ChatFormatting.DARK_GREEN;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Dark Red")){
			return ChatFormatting.DARK_RED;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Gold")){
			return ChatFormatting.GOLD;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Dark Gray")){
			return ChatFormatting.DARK_GRAY;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Green")){
			return ChatFormatting.GREEN;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Red")){
			return ChatFormatting.RED;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Yellow")){
			return ChatFormatting.YELLOW;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Dark Blue")){
			return ChatFormatting.DARK_BLUE;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Dark Aqua")){
			return ChatFormatting.DARK_AQUA;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Dark Purple")){
			return ChatFormatting.DARK_PURPLE;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Gray")){
			return ChatFormatting.GRAY;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Blue")){
			return ChatFormatting.BLUE;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Light Purple")){
			return ChatFormatting.LIGHT_PURPLE;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("White")){
			return ChatFormatting.WHITE;
		}
		if (PlayerColor.getValue().equalsIgnoreCase("Aqua")){
			return ChatFormatting.AQUA;
		}
		return null;
	}

	public ChatFormatting getPopColor(){
		if (PopColor.getValue().equalsIgnoreCase("Black")){
			return ChatFormatting.BLACK;
		}
		if (PopColor.getValue().equalsIgnoreCase("Dark Green")){
			return ChatFormatting.DARK_GREEN;
		}
		if (PopColor.getValue().equalsIgnoreCase("Dark Red")){
			return ChatFormatting.DARK_RED;
		}
		if (PopColor.getValue().equalsIgnoreCase("Gold")){
			return ChatFormatting.GOLD;
		}
		if (PopColor.getValue().equalsIgnoreCase("Dark Gray")){
			return ChatFormatting.DARK_GRAY;
		}
		if (PopColor.getValue().equalsIgnoreCase("Green")){
			return ChatFormatting.GREEN;
		}
		if (PopColor.getValue().equalsIgnoreCase("Red")){
			return ChatFormatting.RED;
		}
		if (PopColor.getValue().equalsIgnoreCase("Yellow")){
			return ChatFormatting.YELLOW;
		}
		if (PopColor.getValue().equalsIgnoreCase("Dark Blue")){
			return ChatFormatting.DARK_BLUE;
		}
		if (PopColor.getValue().equalsIgnoreCase("Dark Aqua")){
			return ChatFormatting.DARK_AQUA;
		}
		if (PopColor.getValue().equalsIgnoreCase("Dark Purple")){
			return ChatFormatting.DARK_PURPLE;
		}
		if (PopColor.getValue().equalsIgnoreCase("Gray")){
			return ChatFormatting.GRAY;
		}
		if (PopColor.getValue().equalsIgnoreCase("Blue")){
			return ChatFormatting.BLUE;
		}
		if (PopColor.getValue().equalsIgnoreCase("Light Purple")){
			return ChatFormatting.LIGHT_PURPLE;
		}
		if (PopColor.getValue().equalsIgnoreCase("White")){
			return ChatFormatting.WHITE;
		}
		if (PopColor.getValue().equalsIgnoreCase("Aqua")){
			return ChatFormatting.AQUA;
		}
		return null;
	}

	public void onDisable(){
		knownPlayers.clear();
		GameSenseMod.EVENT_BUS.unsubscribe(this);
	}


}

