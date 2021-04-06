package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import java.util.Arrays;

/**
 * @author GL_DONT_CARE (Viewmodel Transformations)
 * @author NekoPvP (Item FOV)
 */

@Module.Declaration(name = "ViewModel", category = Category.Render)
public class ViewModel extends Module {

    ModeSetting type = registerMode("Type", Arrays.asList("Value", "FOV", "Both"), "Value");
    public BooleanSetting cancelEating = registerBoolean("No Eat", false);
    DoubleSetting xLeft = registerDouble("Left X", 0.0, -2.0, 2.0);
    DoubleSetting yLeft = registerDouble("Left Y", 0.2, -2.0, 2.0);
    DoubleSetting zLeft = registerDouble("Left Z", -1.2, -2.0, 2.0);
    DoubleSetting xRight = registerDouble("Right X", 0.0, -2.0, 2.0);
    DoubleSetting yRight = registerDouble("Right Y", 0.2, -2.0, 2.0);
    DoubleSetting zRight = registerDouble("Right Z", -1.2, -2.0, 2.0);
    DoubleSetting fov = registerDouble("Item FOV", 130, 70, 200);

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<TransformSideFirstPersonEvent> eventListener = new Listener<>(event -> {
        if (type.getValue().equalsIgnoreCase("Value") || type.getValue().equalsIgnoreCase("Both")) {
            if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
                GlStateManager.translate(xRight.getValue(), yRight.getValue(), zRight.getValue());
            } else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
                GlStateManager.translate(xLeft.getValue(), yLeft.getValue(), zLeft.getValue());
            }
        }
    });

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<EntityViewRenderEvent.FOVModifier> fovModifierListener = new Listener<>(event -> {
        if (type.getValue().equalsIgnoreCase("FOV") || type.getValue().equalsIgnoreCase("Both")) {
            event.setFOV(fov.getValue().floatValue());
        }
    });
}