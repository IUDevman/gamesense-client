package com.gamesense.client.module.modules.misc;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class AutoReply extends Module{
	public AutoReply(){
		super("AutoReply", Category.Misc);
	}

	private static String reply = "I don't speak to newfags!";

	@EventHandler
	private final Listener<ClientChatReceivedEvent> listener = new Listener<>(event ->{
		if (event.getMessage().getUnformattedText().contains("whispers: ") && !event.getMessage().getUnformattedText().startsWith(mc.player.getName())){
			mc.player.sendChatMessage("/r " + reply);
		}
		else if (event.getMessage().getUnformattedText().contains("whispers: I don't speak to newfags!") && !event.getMessage().getUnformattedText().startsWith(mc.player.getName())){
			return; //should prevent most instances of users spam replying back to each other in a loop, this is for you Mini :P
		}
	});

	public static String getReply(){
		return reply;
	}

	public static void setReply(String r){
		reply = r;
	}

	public void onEnable(){
		GameSenseMod.EVENT_BUS.subscribe(this);
	}

	public void onDisable(){
		GameSenseMod.EVENT_BUS.unsubscribe(this);
	}
}