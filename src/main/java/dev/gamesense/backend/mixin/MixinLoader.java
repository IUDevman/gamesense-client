package dev.gamesense.backend.mixin;

import dev.gamesense.GameSense;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author IUDevman
 * @since 02-09-2022
 */

@IFMLLoadingPlugin.Name(GameSense.MOD_NAME)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public final class MixinLoader implements IFMLLoadingPlugin {

    public MixinLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.gamesense.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
