package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;

public class RespawnCommand extends Module {

    Setting.Text stringHolder;

    private boolean isDead = true;

    public RespawnCommand() {
        super("RespawnCommand", Category.Misc);
    }

    @Override
    public void setup() {
        stringHolder = registerText("Command", "Command", "/kit");
    }

    @Override
    public void onUpdate() {
        if (mc.player == null)
            return;

        if (!mc.player.isEntityAlive())
            isDead = true;
        else if (isDead) {
            mc.player.connection.sendPacket(new CPacketChatMessage(stringHolder.getValue()));
            isDead = false;
        }
    }

    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
    }
}
