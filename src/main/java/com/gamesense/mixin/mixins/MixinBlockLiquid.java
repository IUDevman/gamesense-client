package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.exploits.LiquidInteract;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {

	@Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
	public void canCollideCheck(final IBlockState blockState, final boolean b, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		callbackInfoReturnable.setReturnValue(ModuleManager.isModuleEnabled(LiquidInteract.class) || (b && blockState.getValue(BlockLiquid.LEVEL) == 0));
	}
}