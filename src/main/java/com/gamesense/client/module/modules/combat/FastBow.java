package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

public class FastBow extends Module {

	Setting.Integer drawLength;

	public FastBow() {
		super("FastBow", Category.Combat);
	}

	@Override
	public void setup() {
		// https://minecraft.gamepedia.com/Bow#Weapon
		drawLength = registerInteger("Draw Length", 3, 3, 21);
	}

	public void onUpdate() {
		if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= drawLength.getValue()) {
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
			mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
			mc.player.stopActiveHand();
		}
	}
}