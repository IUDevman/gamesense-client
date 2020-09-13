package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {
    private Setting.Boolean swordOnly;
    private Setting.Boolean caCheck;
    private Setting.Boolean criticals;
    private Setting.Double range;
    private boolean isAttacking = false;
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketUseEntity) {
            if (criticals.getValue() && ((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && mc.player.onGround && isAttacking) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1f, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    });

    public KillAura() {
        super("KillAura", Category.Combat);
    }

    public void setup() {
        range = registerDouble("Range", "Range", 5, 0, 10);
        swordOnly = registerBoolean("Sword Only", "SwordOnly", true);
        criticals = registerBoolean("Criticals", "Criticals", true);
        caCheck = registerBoolean("AC Check", "ACCheck", false);
    }

    public void onUpdate() {
        if (mc.player == null || mc.player.isDead) return;
        List<Entity> targets = mc.world.loadedEntityList.stream()
                .filter(entity -> entity != mc.player)
                .filter(entity -> mc.player.getDistance(entity) <= range.getValue())
                .filter(entity -> !entity.isDead)
                .filter(entity -> entity instanceof EntityPlayer)
                .filter(entity -> ((EntityPlayer) entity).getHealth() > 0)
                .filter(entity -> !Friends.isFriend(entity.getName()))
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e)))
                .collect(Collectors.toList());

        targets.forEach(target -> {
            if (swordOnly.getValue())
                if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) return;

            if (caCheck.getValue())
                if (((AutoCrystal) ModuleManager.getModuleByName("AutoCrystalGS")).isActive) return;

            attack(target);
        });
    }

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }

    public void attack(Entity e) {
        if (mc.player.getCooledAttackStrength(0) >= 1) {
            isAttacking = true;
            mc.playerController.attackEntity(mc.player, e);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            isAttacking = false;
        }
    }
}
