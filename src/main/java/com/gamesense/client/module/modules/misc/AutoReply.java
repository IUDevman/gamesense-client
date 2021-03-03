package com.gamesense.client.module.modules.misc;

import com.gamesense.client.GameSense;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class AutoReply extends Module {

	public AutoReply() {
		super("AutoReply", Category.Misc);
	}

	private static String reply = "I don't speak to newfags!";

	@EventHandler
	private final Listener<ClientChatReceivedEvent> listener = new Listener<>(event -> {
		if (event.getMessage().getUnformattedText().contains("whispers: ") && !event.getMessage().getUnformattedText().startsWith(mc.player.getName())) {
			if (event.getMessage().getUnformattedText().contains("I don't speak to newfags!")) {
				return;
			}

			MessageBus.sendServerMessage("/r " + reply);
		}
	});

	public static String getReply() {
		return reply;
	}

	public static void setReply(String r) {
		reply = r;
	}
}