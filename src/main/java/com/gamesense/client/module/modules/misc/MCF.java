package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.client.GameSense;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
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
		if (mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.ENTITY) && mc.objectMouseOver.entityHit instanceof EntityPlayer && Mouse.isButtonDown(2)) {
			if (Friends.isFriend(mc.objectMouseOver.entityHit.getName())) {
				Friends.delFriend(mc.objectMouseOver.entityHit.getName());
				MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "Removed " + mc.objectMouseOver.entityHit.getName() + " from friends list");
			}
			else {
				Friends.addFriend(mc.objectMouseOver.entityHit.getName());
				MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "Added " + mc.objectMouseOver.entityHit.getName() + " to friends list");
			}
		}
	});
}