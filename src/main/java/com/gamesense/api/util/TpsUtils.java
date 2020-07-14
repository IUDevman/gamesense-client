package com.gamesense.api.util;

import com.gamesense.client.GameSenseMod;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

/*
 * @author 086
 */
public class TpsUtils {
    private static float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate;

    public TpsUtils(){
        nextIndex = 0;
        timeLastTimeUpdate = -1L;
        Arrays.fill(tickRates, 0.0F);
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public static float getTickRate() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks += 1.0F;
            }
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.0F, 20.0F);
    }

    private void onTimeUpdate() {
        if (this.timeLastTimeUpdate != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0F;
            tickRates[(this.nextIndex % tickRates.length)] = MathHelper.clamp(20.0F / timeElapsed, 0.0F, 20.0F);
            this.nextIndex += 1;
        }
        this.timeLastTimeUpdate = System.currentTimeMillis();
    }

    @EventHandler
    Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            onTimeUpdate();
        }
    });
}
