package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.*;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

@Module.Declaration(name = "AutoCrystal", category = Category.Combat, priority = 100)
public class AutoCrystal extends Module {

    ModeSetting breakMode = registerMode("Target", Arrays.asList("All", "Smart", "Own"), "All");
    ModeSetting handBreak = registerMode("Hand", Arrays.asList("Main", "Offhand", "Both"), "Main");
    ModeSetting breakType = registerMode("Type", Arrays.asList("Swing", "Packet"), "Swing");
    ModeSetting crystalPriority = registerMode("Prioritise", Arrays.asList("Damage", "Closest", "Health"), "Damage");
    BooleanSetting breakCrystal = registerBoolean("Break", true);
    BooleanSetting placeCrystal = registerBoolean("Place", true);
    IntegerSetting attackSpeed = registerInteger("Attack Speed", 16, 0, 20);
    public DoubleSetting breakRange = registerDouble("Hit Range", 4.4, 0.0, 10.0);
    public DoubleSetting placeRange = registerDouble("Place Range", 4.4, 0.0, 6.0);
    DoubleSetting wallsRange = registerDouble("Walls Range", 3.5, 0.0, 10.0);
    DoubleSetting enemyRange = registerDouble("Enemy Range", 6.0, 0.0, 16.0);
    BooleanSetting antiWeakness = registerBoolean("Anti Weakness", true);
    //BooleanSetting antiTotemPop = registerBoolean("Anti Totem Pop", true);
    BooleanSetting antiSuicide = registerBoolean("Anti Suicide", true);
    IntegerSetting antiSuicideValue = registerInteger("Min Health", 14, 1, 36);
    BooleanSetting autoSwitch = registerBoolean("Switch", true);
    BooleanSetting noGapSwitch = registerBoolean("No Gap Switch", false);
    public BooleanSetting endCrystalMode = registerBoolean("1.13 Place", false);
    BooleanSetting cancelCrystal = registerBoolean("Cancel Crystal", false);
    DoubleSetting minDmg = registerDouble("Min Damage", 5, 0, 36);
    DoubleSetting minBreakDmg = registerDouble("Min Break Dmg", 5, 0, 36.0);
    DoubleSetting maxSelfDmg = registerDouble("Max Self Dmg", 10, 1.0, 36.0);
    IntegerSetting facePlaceValue = registerInteger("FacePlace HP", 8, 0, 36);
    IntegerSetting armourFacePlace = registerInteger("Armour Health%", 20, 0, 100);
    DoubleSetting minFacePlaceDmg = registerDouble("FacePlace Dmg", 2, 0, 10);
    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting raytrace = registerBoolean("Raytrace", false);
    BooleanSetting showDamage = registerBoolean("Render Dmg", true);
    ModeSetting hudDisplay = registerMode("HUD", Arrays.asList("Mode", "Target", "None"), "Mode");
    ColorSetting color = registerColor("Color", new GSColor(0, 255, 0, 50));

    BooleanSetting wait = registerBoolean("Force Wait", true);
    IntegerSetting timeout = registerInteger("Timeout (ms)", 10, 1, 50);
    IntegerSetting maxTargets = registerInteger("Max Targets", 2, 1, 5);

    private boolean switchCooldown = false;
    public boolean isAttacking = false;
    public static boolean stopAC = false;
    private Entity renderEntity;
    private BlockPos render;
    Timer timer = new Timer();

    private Vec3d lastHitVec = Vec3d.ZERO;
    private boolean rotating = false;

    // Threading Stuff
    public List<CrystalInfo.PlaceInfo> targets = new ArrayList<>();
    private boolean finished = false;

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> onUpdate = new Listener<>(event -> {
        if (mc.player == null || mc.world == null || mc.player.isDead) {
            return;
        }

        if (stopAC) {
            return;
        }

        PlayerInfo player = new PlayerInfo(mc.player, false);
        if (antiSuicide.getValue() && player.health <= antiSuicideValue.getValue()) {
            return;
        }

        ACSettings settings = new ACSettings(breakCrystal.getValue(), placeCrystal.getValue(), enemyRange.getValue(), breakRange.getValue(), wallsRange.getValue(), placeRange.getValue(), minDmg.getValue(), minBreakDmg.getValue(), minFacePlaceDmg.getValue(), maxSelfDmg.getValue(), facePlaceValue.getValue(), antiSuicide.getValue(), endCrystalMode.getValue(), breakMode.getValue(), crystalPriority.getValue(), player, mc.player.getPositionVector());
        float armourPercent = armourFacePlace.getValue() / 100.0f;
        double enemyDistance = enemyRange.getValue() + placeRange.getValue();
        ACHelper.INSTANCE.recalculateValues(settings, player, armourPercent, enemyDistance);

        if (event.phase == TickEvent.Phase.START) {
            collectTargetFinder();
        } else {
            if (finished) {
                startTargetFinder();
                finished = false;
            }
        }

        // no longer target dead players
        targets.removeIf(placeInfo -> placeInfo.target.entity.isDead || placeInfo.target.entity.getHealth() == 0);
        if (!breakCrystal(settings)) {
            if (!placeCrystal(settings)) {
                rotating = false;
                isAttacking = false;
                render = null;
                renderEntity = null;
            }
        }
    });

    public boolean breakCrystal(ACSettings settings) {
        if (breakCrystal.getValue() && targets.size() > 0) {
            List<CrystalInfo.PlaceInfo> currentTargets;
            if (targets.size() < maxTargets.getValue()) {
                currentTargets = new ArrayList<>(targets);
            } else {
                currentTargets = new ArrayList<>(targets.subList(0, maxTargets.getValue()));
            }
            List<EntityEnderCrystal> crystals = ACHelper.INSTANCE.getTargetableCrystals();

            TreeSet<CrystalInfo.BreakInfo> possibleCrystals;
            String crystalPriorityValue = crystalPriority.getValue();
            if (crystalPriorityValue.equalsIgnoreCase("Health")) {
                possibleCrystals = new TreeSet<>(Comparator.comparingDouble((i) -> -i.target.health));
            } else if (crystalPriorityValue.equalsIgnoreCase("Closest")) {
                possibleCrystals = new TreeSet<>(Comparator.comparingDouble((i) -> -mc.player.getDistanceSq(i.target.entity)));
            } else {
                possibleCrystals = new TreeSet<>(Comparator.comparingDouble((i) -> i.damage));
            }

            for (CrystalInfo.PlaceInfo currentTarget : currentTargets) {
                CrystalInfo.BreakInfo breakInfo = ACUtil.calculateBestBreakable(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), crystals);
                if (breakInfo != null) {
                    possibleCrystals.add(breakInfo);
                }
            }
            if (possibleCrystals.size() != 0) {
                EntityEnderCrystal crystal = possibleCrystals.last().crystal;
                if (mc.player.canEntityBeSeen(crystal) || mc.player.getDistance(crystal) < wallsRange.getValue()) {
                    if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                        if (!isAttacking) {
                            isAttacking = true;
                        }
                        // search for sword and tools in hotbar
                        int newSlot = InventoryUtil.findFirstItemSlot(ItemSword.class, 0, 8);
                        if (newSlot == -1) {
                            InventoryUtil.findFirstItemSlot(ItemTool.class, 0, 8);
                        }
                        // check if any swords or tools were found
                        if (newSlot != -1) {
                            mc.player.inventory.currentItem = newSlot;
                            switchCooldown = true;
                        }
                    }

                    if (timer.getTimePassed() / 50L >= 20 - attackSpeed.getValue()) {
                        timer.reset();

                        rotating = rotate.getValue();
                        lastHitVec = crystal.getPositionVector();

                        swingArm();
                        if (breakType.getValue().equalsIgnoreCase("Swing")) {
                            mc.playerController.attackEntity(mc.player, crystal);
                        } else {
                            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                        }

                        if (cancelCrystal.getValue()) {
                            crystal.setDead();
                            mc.world.removeAllEntities();
                            mc.world.getLoadedEntityList();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean placeCrystal(ACSettings settings) {
        // check to see if we are holding crystals or not
        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            crystalSlot = InventoryUtil.findFirstItemSlot(ItemEndCrystal.class, 0, 8);
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return false;
        }

        if (placeCrystal.getValue()) {
            List<CrystalInfo.PlaceInfo> currentTargets;
            if (targets.size() < maxTargets.getValue()) {
                currentTargets = new ArrayList<>(targets);
            } else {
                currentTargets = targets.subList(0, maxTargets.getValue());
            }
            List<BlockPos> placements = ACHelper.INSTANCE.getPossiblePlacements();

            TreeSet<CrystalInfo.PlaceInfo> possiblePlacements;
            String crystalPriorityValue = crystalPriority.getValue();
            if (crystalPriorityValue.equalsIgnoreCase("Health")) {
                possiblePlacements = new TreeSet<>(Comparator.comparingDouble((i) -> -i.target.health));
            } else if (crystalPriorityValue.equalsIgnoreCase("Closest")) {
                possiblePlacements = new TreeSet<>(Comparator.comparingDouble((i) -> -mc.player.getDistanceSq(i.target.entity)));
            } else {
                possiblePlacements = new TreeSet<>(Comparator.comparingDouble((i) -> i.damage));
            }

            for (CrystalInfo.PlaceInfo currentTarget : currentTargets) {
                CrystalInfo.PlaceInfo placeInfo = ACUtil.calculateBestPlacement(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), placements);
                if (placeInfo != null) {
                    possiblePlacements.add(placeInfo);
                }
            }
            if (possiblePlacements.size() == 0) {
                return false;
            }

            CrystalInfo.PlaceInfo crystal = possiblePlacements.last();
            this.render = crystal.crystal;
            this.renderEntity = crystal.target.entity;

            // autoSwitch stuff
            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (this.autoSwitch.getValue()) {
                    if (!noGapSwitch.getValue() || !(mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)) {
                        mc.player.inventory.currentItem = crystalSlot;
                        rotating = false;
                        this.switchCooldown = true;
                    }
                }
                return false;
            }

            EnumFacing enumFacing = null;
            if (raytrace.getValue()) {
                RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) crystal.crystal.getX() + 0.5d, (double) crystal.crystal.getY() - 0.5d, (double) crystal.crystal.getZ() + 0.5d));
                if (result == null || result.sideHit == null) {
                    render = null;
                    return false;
                } else {
                    enumFacing = result.sideHit;
                }
            }

            if (this.switchCooldown) {
                this.switchCooldown = false;
                return false;
            }

            ACHelper.INSTANCE.onPlaceCrystal(crystal.crystal);

            rotating = rotate.getValue();
            lastHitVec = new Vec3d(crystal.crystal).add(0.5, 0.5, 0.5);

            mc.player.connection.sendPacket(new CPacketAnimation(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            if (raytrace.getValue() && enumFacing != null) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(crystal.crystal, enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            } else if (crystal.crystal.getY() == 255) {
                // For Hoosiers. This is how we do build height. If the target block (q) is at Y 255. Then we send a placement packet to the bottom part of the block. Thus the EnumFacing.DOWN.
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(crystal.crystal, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            } else {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(crystal.crystal, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            }

            if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                AutoGG.INSTANCE.addTargetedPlayer(renderEntity.getName());
            }

            return true;
        }
        return false;
    }

    public void onWorldRender(RenderEvent event) {
        if (this.render != null) {
            RenderUtil.drawBox(this.render,1, new GSColor(color.getValue(),50), 63);
            RenderUtil.drawBoundingBox(this.render, 1, 1.00f, new GSColor(color.getValue(),255));
        }

        if(showDamage.getValue()) {
            if (this.render != null && this.renderEntity != null) {
                String[] damageText = {String.format("%.1f", DamageUtil.calculateDamage((double) render.getX() + 0.5d, (double) render.getY() + 1.0d, (double) render.getZ() + 0.5d, renderEntity))};
                RenderUtil.drawNametag((double) render.getX() + 0.5d,(double) render.getY() + 0.5d,(double) render.getZ() + 0.5d, damageText, new GSColor(255,255,255),1);
            }
        }
    }

    private void startTargetFinder() {
        long timeoutTime = System.currentTimeMillis() + timeout.getValue();
        ACHelper.INSTANCE.startCalculations(timeoutTime);
    }

    private void collectTargetFinder() {
        List<CrystalInfo.PlaceInfo> output = ACHelper.INSTANCE.getOutput(wait.getValue());
        if (output != null) {
            finished = true;
            if (output.size() > 0) {
                targets = output;
            }
        } else {
            finished = false;
        }
    }

    private void swingArm() {
        switch (handBreak.getValue()) {
            case "Both" : {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.swingArm(EnumHand.OFF_HAND);
                break;
            }
            case "Offhand" : {
                mc.player.swingArm(EnumHand.OFF_HAND);
                break;
            }
            default: {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                break;
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.PRE || !rotating) return;

        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    });

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
        Packet<?> packet = event.getPacket();
        if (packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect) packet;
            if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0f) {
                            entity.setDead();
                        }
                    }
                }
            }
        }
    });

    public void onEnable() {
        ACHelper.INSTANCE.onEnable();
    }

    public void onDisable() {
        ACHelper.INSTANCE.onDisable();

        render = null;
        renderEntity = null;
        rotating = false;

        targets.clear();
    }

    public String getHudInfo() {
        String t = "";
        if (hudDisplay.getValue().equalsIgnoreCase("Mode")){
            t = "[" + ChatFormatting.WHITE + breakMode.getValue() + ChatFormatting.GRAY + "]";
        } else if (hudDisplay.getValue().equalsIgnoreCase("Target")) {
            if (renderEntity == null) {
                t = "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]";
            } else {
                t = "[" + ChatFormatting.WHITE + renderEntity.getName() + ChatFormatting.GRAY + "]";
            }
        }

        return t;
    }
}