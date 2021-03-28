package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

/**
 * @see com.gamesense.mixin.mixins.MixinNetworkManager
 */

@Module.Declaration(name = "NoKick", category = Category.Misc)
public class NoKick extends Module {

    public BooleanSetting noPacketKick = registerBoolean("Packet", true);
    BooleanSetting noSlimeCrash = registerBoolean("Slime", false);
    BooleanSetting noOffhandCrash = registerBoolean("Offhand", false);
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (noOffhandCrash.getValue()) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                if (((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                    event.cancel();
                }
            }
        }
    });

    public void onUpdate() {
        if (mc.world != null && noSlimeCrash.getValue()) {
            mc.world.loadedEntityList.forEach(entity -> {
                if (entity instanceof EntitySlime) {
                    EntitySlime slime = (EntitySlime) entity;
                    if (slime.getSlimeSize() > 4) {
                        mc.world.removeEntity(entity);
                    }
                }
            });
        }
    }
}