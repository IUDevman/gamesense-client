package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;

public class ViewModel extends Module {
    public ViewModel() {super("ViewModel", Category.Render);}

    Setting.Double xr;
    Setting.Double yr;
    Setting.Double zr;
    Setting.Double xl;
    Setting.Double yl;
    Setting.Double zl;

    public void setup() {
        xl = registerDouble("Left X", "LeftX", 0.0, -2.0, 2.0);
        yl = registerDouble("Left Y", "LeftY", 0.2, -2.0, 2.0);
        zl = registerDouble("Left Z", "LeftZ", -1.2, -2.0, 2.0);
        xr = registerDouble("Right X", "RightX", 0.0, -2.0, 2.0);
        yr = registerDouble("Right Y", "RightY", 0.2, -2.0, 2.0);
        zr = registerDouble("Right Z", "RightZ", -1.2, -2.0, 2.0);
    }

    @EventHandler
    private Listener<TransformSideFirstPersonEvent> eventListener = new Listener<>(event -> {
       if (event.getHandSide() == EnumHandSide.RIGHT) {
           GlStateManager.translate(xr.getValue(), yr.getValue(), zr.getValue());
       } else if (event.getHandSide() == EnumHandSide.LEFT) {
           GlStateManager.translate(xl.getValue(), yl.getValue(), zl.getValue());
       }
    });

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }
}
