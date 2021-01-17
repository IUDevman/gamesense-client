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

public class AutoRespawn extends Module{
    public static AutoRespawn INSTANCE;
    static String AutoRespawnMessage = "/kit";

    Setting.Boolean respawnMessage;

    public boolean isDead;

    public AutoRespawn() {
        super("AutoRespawn", Module.Category.Misc);
        INSTANCE = this;
    }

    @Override
    public void setup() {
        respawnMessage = registerBoolean("Respawn Message", "RespawnMessage", false);
    }

    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener = new Listener<>(event -> {
        if (event.getGui() instanceof GuiGameOver) {
            event.setCanceled(true);
            isDead = true;
            mc.player.connection.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
        }
    });

    @Override
    public void onUpdate() {
        if (mc.player == null)
            return;

        if (isDead && mc.player.isEntityAlive()) {
            if (respawnMessage.getValue())
                mc.player.connection.sendPacket(new CPacketChatMessage(AutoRespawnMessage));
            isDead = false;
        }
    }

    @Override
    protected void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);
    }

    @Override
    protected void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
    }

    public static void setAutoRespawnMessage(String string) {
        AutoRespawnMessage = string;
    }

    public static String getAutoRespawnMessages() {
        return AutoRespawnMessage;
    }
}
