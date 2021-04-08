package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

@Module.Declaration(name = "AutoReply", category = Category.Misc)
public class AutoReply extends Module {

    private static String reply = "I don't speak to newfags!";

    @SuppressWarnings("unused")
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