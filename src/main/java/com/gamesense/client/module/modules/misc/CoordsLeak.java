package com.gamesense.client.module.modules.misc;

//very useful module 

import com.gamesense.client.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Random;

public class CoordsLeak extends Module {

    public CoordsLeak() {
        super("CoordsLeak", Category.Misc);
    }

    public void onEnable() {
        if (mc.player != null) {
            mc.player.sendChatMessage(">I just used the CoordsLeak module! Come grief my stash at" + (mc.player.posX) + ", " + (mc.player.posZ) + " thanks to GameSense!");
            disable();
        }
    }
}
