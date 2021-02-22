package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraftforge.client.event.GuiOpenEvent;

public class AutoRespawn extends Module {
    private static String AutoRespawnMessage = "/kit";

    Setting.Boolean respawnMessage;
    Setting.Integer respawnMessageDelay;

    private boolean isDead;
    private boolean sentRespawnMessage = true;
    long timeSinceRespawn;

    public AutoRespawn() {
        super("AutoRespawn", Category.Misc);
    }

    @Override
    public void setup() {
        respawnMessage = registerBoolean("Respawn Message", false);
        respawnMessageDelay = registerInteger("Msg Delay(ms)", 0, 0, 5000);
    }

    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener = new Listener<>(event -> {
        if (event.getGui() instanceof GuiGameOver) {
            event.setCanceled(true);
            isDead = true;
            sentRespawnMessage = true;
            mc.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
        }
    });

    @Override
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
