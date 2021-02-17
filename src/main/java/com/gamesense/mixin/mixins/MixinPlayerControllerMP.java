package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.DamageBlockEvent;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.exploits.PacketUse;
import com.gamesense.client.module.modules.exploits.Reach;
import com.gamesense.client.module.modules.misc.MultiTask;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

	@Shadow public abstract void syncCurrentPlayItem();

	@Inject(method = "onPlayerDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V"), cancellable = true)
	private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		GameSense.EVENT_BUS.post(new DestroyBlockEvent(pos));
	}

	@Inject(method = "onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", at = @At("HEAD"), cancellable = true)
	private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		DamageBlockEvent event = new DamageBlockEvent(posBlock, directionFacing);
		GameSense.EVENT_BUS.post(event);
		if (event.isCancelled()) {
			callbackInfoReturnable.setReturnValue(false);
		}
	}

	@Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
	private void getReachDistanceHook(final CallbackInfoReturnable<Float> distance) {
		if (ModuleManager.isModuleEnabled(Reach.class)) {
			distance.setReturnValue((float) Reach.distance.getValue());
		}
	}

	//author cookiedragon234
	@Inject(method = "resetBlockRemoving", at = @At("HEAD"), cancellable = true)
	private void resetBlock(CallbackInfo callbackInfo) {
		if (ModuleManager.isModuleEnabled(MultiTask.class)) {
			callbackInfo.cancel();
		}
	}

	@Inject(method = "onStoppedUsingItem", at = @At("HEAD"), cancellable = true)
	public void onStoppedUsingItem(EntityPlayer playerIn, CallbackInfo ci) {
		if (ModuleManager.isModuleEnabled(PacketUse.class)) {
			if ((PacketUse.food.getValue() && playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof ItemFood)
				|| (PacketUse.potion.getValue() && playerIn.getHeldItem(playerIn.getActiveHand()).getItem() instanceof ItemPotion)
				|| PacketUse.all.getValue()) {
				this.syncCurrentPlayItem();
				playerIn.stopActiveHand();
				ci.cancel();
			}
		}
	}
}