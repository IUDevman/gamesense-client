package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameType;

import java.util.UUID;

/*
    @Author TechAle
    @Since 23/03/21
 */

@Module.Declaration(name = "FakePlayer", category = Category.Misc)
public class FakePlayer extends Module {

    final private ItemStack[] armors = new ItemStack[]{
            new ItemStack(Items.DIAMOND_BOOTS),
            new ItemStack(Items.DIAMOND_LEGGINGS),
            new ItemStack(Items.DIAMOND_CHESTPLATE),
            new ItemStack(Items.DIAMOND_HELMET)
    };

    BooleanSetting playerStacked = registerBoolean("Player Stacked", false);

    public void onEnable() {
        if (mc.player == null || mc.player.isDead) {
            disable();
            return;
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), "Fit"));
        clonedPlayer.copyLocationAndAnglesFrom(mc.player);
        clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
        clonedPlayer.rotationYaw = mc.player.rotationYaw;
        clonedPlayer.rotationPitch = mc.player.rotationPitch;
        clonedPlayer.setGameType(GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.world.addEntityToWorld(-1234, clonedPlayer);
        // If enchants
        if (playerStacked.getValue()) {
            // ITerate
            for(int i = 0; i < 4; i++) {
                // Create base
                ItemStack item = armors[i];
                // Add enchants
                item.addEnchantment(
                        i == 2 ? Enchantments.BLAST_PROTECTION : Enchantments.PROTECTION,
                        4);
                // Add it to the player
                clonedPlayer.inventory.armorInventory.set(i, item);
            }
        }
        clonedPlayer.onLivingUpdate();
    }
    public void onDisable() {
        if (mc.world != null) {
            mc.world.removeEntityFromWorld(-1234);
        }
    }
}
