package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;

@Module.Declaration(name = "Speedometer", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 70)
public class Speedometer extends HUDModule {

    private static final String MPS = "m/s";
    private static final String KMH = "km/h";
    private static final String MPH = "mph";

    ModeSetting speedUnit = registerMode("Unit", Arrays.asList(MPS, KMH, MPH), KMH);
    BooleanSetting averageSpeed = registerBoolean("Average Speed", true);
    IntegerSetting averageSpeedTicks = registerInteger("Average Time", 20, 5, 100);

    private final ArrayDeque<Double> speedDeque = new ArrayDeque<>();
    private String speedString = "";

    protected void onDisable() {
        speedDeque.clear();
        speedString = "";
    }

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener = new Listener<>(event -> {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayerSP player = mc.player;
        if (player == null) return;

        String unit = speedUnit.getValue();
        double speed = calcSpeed(player, unit);
        double displaySpeed = speed;

        if (averageSpeed.getValue()) {
            if (speed > 0.0 || player.ticksExisted % 4 == 0) {
                speedDeque.add(speed); // Only adding it every 4 ticks if speed is 0
            } else {
                speedDeque.pollFirst();
            }

            while (!speedDeque.isEmpty() && speedDeque.size() > averageSpeedTicks.getValue()) {
                speedDeque.poll();
            }

            displaySpeed = average(speedDeque);
        }

        speedString = String.format("%.2f", displaySpeed) + ' ' + unit;
    });

    private double calcSpeed(EntityPlayerSP player, String unit) {
        double tps = 1000.0 / mc.timer.tickLength;
        double xDiff = player.posX - player.prevPosX;
        double zDiff = player.posZ - player.prevPosZ;

        double speed = Math.hypot(xDiff, zDiff) * tps;

        // Fast memory address comparison
        switch (unit) {
            case KMH:
                speed *= 3.6;
                break;
            case MPH:
                speed *= 2.237;
                break;
            default:
                break;
        }

        return speed;
    }

    private double average(Collection<Double> collection) {
        if (collection.isEmpty()) return 0.0;

        double sum = 0.0;
        int size = 0;

        for (double element : collection) {
            sum += element;
            size++;
        }

        return sum / size;
    }

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, new SpeedLabel());
    }

    private class SpeedLabel implements HUDList {
        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public String getItem(int index) {
            return speedString;
        }

        @Override
        public Color getItemColor(int index) {
            return new Color(255, 255, 255);
        }

        @Override
        public boolean sortUp() {
            return false;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}