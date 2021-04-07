package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Burrow extends Module {
  
   public Burrow() {
        super("Burrow", Category.Combat);
    }

    Setting.Boolean chatMsg,
                    autoswitch;
    Setting.Integer height;

    
    @Override
    public void setup() {
      chatMsg = registerBoolean("Chat Msgs", true);
      height = registerInteger("height", 1, 1.4, 1.13);
 
    }

    private BlockPos playerPos;

    @Override
    public void onEnable() {
        playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        if (mc.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN) {
            toggle();
            return;
        }

        mc.player.jump();
    }

    public void onUpdate() {
        if (nullCheck()) return;

        int oldSlot = -1;

        if (mc.player.posY > playerPos.getY() + height.getValueDouble()) {

            if (autoswitch.getValBoolean()) {
                oldSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = PlayerUtil.getBlockInHotbar(Blocks.OBSIDIAN);
            }

            PlayerUtil.placeBlock(playerPos);

            if (autoswitch.getValBoolean()) {
                mc.player.inventory.currentItem = oldSlot;
            }

            mc.player.jump();
            toggle();
        }
    }
}
