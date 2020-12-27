package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.client.GameSense;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

public class MCF extends Module {

	public MCF() {
		super("MCF", Category.Misc);
	}

	@EventHandler
	private final Listener<InputEvent.MouseInputEvent> listener = new Listener<>(event -> {
		if (mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.ENTITY) && mc.objectMouseOver.entityHit instanceof EntityPlayer && Mouse.getEventButton() == 2) {
			if (Friends.isFriend(mc.objectMouseOver.entityHit.getName())) {
				GameSense.getInstance().friends.delFriend(mc.objectMouseOver.entityHit.getName());
				MessageBus.sendClientPrefixMessage(ChatFormatting.RED + "Removed " + mc.objectMouseOver.entityHit.getName() + " from friends list");
			}
			else {
				GameSense.getInstance().friends.addFriend(mc.objectMouseOver.entityHit.getName());
				MessageBus.sendClientPrefixMessage(ChatFormatting.GREEN + "Added " + mc.objectMouseOver.entityHit.getName() + " to friends list");
			}
		}
	});

	public void onEnable() {
		GameSense.EVENT_BUS.subscribe(this);
	}

	public void onDisable() {
		GameSense.EVENT_BUS.unsubscribe(this);
	}
}