package com.gamesense.client.module.modules.misc;

import net.minecraft.network.play.client.*;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

// Made by Soulbond, 2/28/21

public class XCarry extends Module {

	public XCarry() {
		super("XCarry", Category.Misc);
	}

    @EventHandler
    private Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketCloseWindow){
            if(((CPacketCloseWindow)event.getPacket()).windowId == mc.player.inventoryContainer.windowId){
                event.cancel();
            }
        }
    });

    public void onEnable(){
        GameSense.EVENT_BUS.subscribe(this);
    }

    public void onDisable(){
        GameSense.EVENT_BUS.unsubscribe(this);
    }
}
