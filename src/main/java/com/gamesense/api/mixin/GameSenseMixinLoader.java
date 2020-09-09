package com.gamesense.api.mixin;

import com.gamesense.client.GameSenseMod;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

public class GameSenseMixinLoader implements IFMLLoadingPlugin{

	private static boolean isObfuscatedEnvironment = false;

	public GameSenseMixinLoader(){
		GameSenseMod.log.info("GameSense mixins initialized");
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.gamesense.json");
		MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
		GameSenseMod.log.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
	}

	@Override
	public String[] getASMTransformerClass(){
		return new String[0];
	}

	@Override
	public String getModContainerClass(){
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass(){
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data){
		isObfuscatedEnvironment = (boolean) (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass(){
		return null;
	}
}
