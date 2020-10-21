package com.gamesense.api.mixin.mixins;

import net.minecraft.client.renderer.entity.RenderLivingBase;

import org.spongepowered.asm.mixin.Mixin;

//NOTE: this is for my sanity, doesn't really do anything but is for any future target hud entity render stuff because it will mess with this

@Mixin(RenderLivingBase.class)
public class MixinRenderLivingBase {}