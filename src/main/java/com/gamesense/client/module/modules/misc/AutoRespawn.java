package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraftforge.client.event.GuiOpenEvent;

@Module.Declaration(name = "AutoRespawn", category = Category.Misc)
public class AutoRespawn extends Module {

    BooleanSetting respawnMessage = registerBoolean("Respawn Message", false);
    IntegerSetting respawnMessageDelay = registerInteger("Msg Delay(ms)", 0, 0, 5000);

    private static String AutoRespawnMessage = "/kit";

    private boolean isDead;
    private boolean sentRespawnMessage = true;
    long timeSinceRespawn;

    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener = new Listener<>(event -> {
        if (event.getGui() instanceof GuiGameOver) {
            event.setCanceled(true);
            isDead = true;
            sentRespawnMessage = true;
            mc.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
        }
    });

    public void onUpdate() {
        if (mc.player == null)
            return;

        if (isDead && mc.player.isEntityAlive()) {
            if (respawnMessage.getValue()) {
                sentRespawnMessage = false;
                timeSinceRespawn = System.currentTimeMillis();
            }
            isDead = false;
        }

        if (!sentRespawnMessage) {
            if ((System.currentTimeMillis() - timeSinceRespawn) > respawnMessageDelay.getValue()) {
                mc.player.connection.sendPacket(new CPacketChatMessage(AutoRespawnMessage));
                sentRespawnMessage = true;
            }
        }
    }

    public static void setAutoRespawnMessage(String string) {
        AutoRespawnMessage = string;
    }

    public static String getAutoRespawnMessages() {
        return AutoRespawnMessage;
    }
}