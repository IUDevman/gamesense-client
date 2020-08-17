package com.gamesense.client.module.modules.render;

import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;

public class ViewmodelChanger extends Module {
    public ViewmodelChanger() {super("ViewmodelChanger", Category.Render);}

    Setting.Double xr;
    Setting.Double yr;
    Setting.Double zr;
    Setting.Double xl;
    Setting.Double yl;
    Setting.Double zl;

    public void setup() {
        xr = registerDouble("x right", "xright", 0.0, -2.0, 2.0);
        yr = registerDouble("y right", "yright", 0.0, -2.0, 2.0);
        zr = registerDouble("z right", "zright", 0.0, -2.0, 2.0);
        xl = registerDouble("x left", "xleft", 0.0, -2.0, 2.0);
        yl = registerDouble("y left", "yleft", 0.0, -2.0, 2.0);
        zl = registerDouble("z left", "zleft", 0.0, -2.0, 2.0);
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
