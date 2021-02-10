package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.combat.CrystalUtil;
import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.Pair;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.Timer;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.gamesense.api.util.player.RotationUtil.ROTATION_UTIL;

public class AutoCrystalRewrite extends Module {

    public AutoCrystalRewrite() {
        super("AutoCrystalRewrite", Category.Combat);
    }

    Setting.Boolean breakCrystal;
    Setting.Boolean antiWeakness;
    Setting.Boolean placeCrystal;
    Setting.Boolean autoSwitch;
    Setting.Boolean raytrace;
    Setting.Boolean rotate;
    Setting.Boolean spoofRotations;
    Setting.Boolean chat;
    Setting.Boolean showDamage;
    Setting.Boolean multiPlace;
    Setting.Boolean antiSuicide;
    Setting.Boolean showSelfDamage;
    public static Setting.Boolean endCrystalMode;
    Setting.Boolean cancelCrystal;
    Setting.Boolean noGapSwitch;
    Setting.Integer placeDelay;
    Setting.Integer facePlaceValue;
    Setting.Integer attackSpeed;
    Setting.Integer antiSuicideValue;
    Setting.Double maxSelfDmg;
    Setting.Double wallsRange;
    Setting.Double minDmg;
    Setting.Double minBreakDmg;
    Setting.Double enemyRange;
    public static Setting.Double placeRange;
    public static Setting.Double breakRange;
    Setting.Mode handBreak;
    Setting.Mode breakMode;
    Setting.Mode crystalPriority;
    Setting.Mode hudDisplay;
    Setting.Mode breakType;
    Setting.ColorSetting color;

    public void setup() {
        ArrayList<String> hands = new ArrayList<>();
        hands.add("Main");
        hands.add("Offhand");
        hands.add("Both");

        ArrayList<String> breakModes = new ArrayList<>();
        breakModes.add("All");
        breakModes.add("Smart");

        ArrayList<String> priority = new ArrayList<>();
        priority.add("Damage");
        priority.add("Closest");
        priority.add("Health");

        ArrayList<String> hudModes = new ArrayList<>();
        hudModes.add("Mode");
        hudModes.add("None");

        ArrayList<String> breakTypes = new ArrayList<>();
        breakTypes.add("Swing");
        breakTypes.add("Packet");

        breakMode = registerMode("Target", breakModes, "All");
        handBreak = registerMode("Hand", hands, "Main");
        crystalPriority = registerMode("Prioritise", priority, "Damage");
        breakType = registerMode("Type", breakTypes, "Swing");
        breakCrystal = registerBoolean("Break", true);
        placeCrystal = registerBoolean("Place", true);
        placeDelay = registerInteger("Place Delay", 1, 0, 10);
        attackSpeed = registerInteger("Attack Speed", 16, 0, 20);
        breakRange = registerDouble("Hit Range", 4.4, 0.0, 10.0);
        placeRange = registerDouble("Place Range", 4.4, 0.0, 6.0);
        wallsRange = registerDouble("Walls Range", 3.5, 0.0, 10.0);
        enemyRange = registerDouble("Enemy Range", 6.0, 0.0, 16.0);
        antiWeakness = registerBoolean("Anti Weakness", true);
        antiSuicide = registerBoolean("Anti Suicide", true);
        antiSuicideValue = registerInteger("Min Health", 14, 1, 36);
        autoSwitch = registerBoolean("Switch", true);
        noGapSwitch = registerBoolean("No Gap Switch", false);
        multiPlace = registerBoolean("Multi Place", false);
        endCrystalMode = registerBoolean("1.13 Place", false);
        cancelCrystal = registerBoolean("Cancel Crystal", false);
        minDmg = registerDouble("Min Damage", 5, 0, 36);
        minBreakDmg = registerDouble("Min Break Dmg", 5, 0,36.0);
        maxSelfDmg = registerDouble("Max Self Dmg", 10, 1.0, 36.0);
        facePlaceValue = registerInteger("FacePlace HP", 8, 0, 36);
        rotate = registerBoolean("Rotate", true);
        spoofRotations = registerBoolean("Spoof Angles", true);
        raytrace = registerBoolean("Raytrace", false);
        showDamage = registerBoolean("Render Dmg", true);
        showSelfDamage = registerBoolean("Render Self Dmg", false);
        chat = registerBoolean("Chat Msgs", true);
        hudDisplay = registerMode("HUD", hudModes, "Mode");
        color = registerColor("Color", new GSColor(0, 255, 0, 50));
    }

    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    public static boolean stopAC = false;
    private static boolean togglePitch = false;
    private int oldSlot = -1;
    private Entity renderEnt;
    private BlockPos render;
    Timer timer = new Timer();

    private final ConcurrentHashMap<Integer, Boolean> crystalIDs = new ConcurrentHashMap<>();
    private boolean everyOtherCycle = true;
    private int ticksSincePlace = 0;

    public void onUpdate() {
        // onUpdate gets called twice per tick
        // stops us from sending too many packets to the server
        everyOtherCycle = !everyOtherCycle;
        if (everyOtherCycle) {
            return;
        }
        if (placeCrystal.getValue()) {
            ticksSincePlace++;
        }

        if (mc.player == null || mc.world == null || mc.player.isDead) {
            disable();
            return;
        }

        if (stopAC) {
            return;
        }

        if (antiSuicide.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= antiSuicideValue.getValue()) {
            return;
        }

        ROTATION_UTIL.shouldSpoofAngles(spoofRotations.getValue());

        // entity range is the range from each crystal
        // so adding these together should solve problem
        // and reduce searching
        final double entityRangeSq = (enemyRange.getValue() * enemyRange.getValue()) + (placeRange.getValue() * placeRange.getValue());
        List<EntityPlayer> targets = mc.world.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer)
                .filter(entity -> mc.player.getDistanceSq(entity) <= entityRangeSq)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .sorted(Comparator.comparing(c -> mc.player.getDistanceSq(c)))
                .map(entity -> (EntityPlayer) entity)
                .collect(Collectors.toList());
        // no point continuing if there are no targets
        if (targets.size() == 0) {
            return;
        }

        EntityEnderCrystal crystal = getCrystalToBreak(targets);
        if (breakCrystal.getValue() && crystal != null) {
            if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > wallsRange.getValue()) {
                return;
            }

            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    // save initial player hand
                    oldSlot = mc.player.inventory.currentItem;
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

                if (rotate.getValue()) {
                    ROTATION_UTIL.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
                }

                if (breakType.getValue().equalsIgnoreCase("Swing")) {
                    mc.playerController.attackEntity(mc.player, crystal);
                    swingArm();
                }
                else if (breakType.getValue().equalsIgnoreCase("Packet")) {
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    swingArm();
                }

                if (cancelCrystal.getValue()) {
                    crystal.setDead();
                    mc.world.removeAllEntities();
                }
            }

            if (!multiPlace.getValue()) {
                return;
            }
        }
        else {
            ROTATION_UTIL.resetRotation();
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            crystalSlot = InventoryUtil.findFirstItemSlot(ItemEndCrystal.class, 0, 8);
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        if (this.placeCrystal.getValue() && ticksSincePlace >= placeDelay.getValue()) {
            BlockPos target = getPositionToPlace(targets);
            if (target == null) {
                this.render = null;
                this.renderEnt = null;
                ROTATION_UTIL.resetRotation();
                return;
            }

            this.ticksSincePlace = 0;
            this.render = target;

            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (this.autoSwitch.getValue()) {
                    if (!noGapSwitch.getValue() || !(mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)) {
                        mc.player.inventory.currentItem = crystalSlot;
                        ROTATION_UTIL.resetRotation();
                        this.switchCooldown = true;
                    }
                }
                return;
            }

            if (rotate.getValue()) {
                ROTATION_UTIL.lookAtPacket((double) target.getX() + 0.5D, target.getY() - 0.5D, (double) target.getZ() + 0.5D, mc.player);
            }

            EnumFacing enumFacing = null;
            if (raytrace.getValue()) {
                RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) target.getX() + 0.5D, (double) target.getY() - 0.5D, (double) target.getZ() + 0.5D));
                if (result == null || result.sideHit == null) {
                    render = null;
                    ROTATION_UTIL.resetRotation();
                    return;
                }
                else {
                    enumFacing = result.sideHit;
                }
            }

            if (this.switchCooldown) {
                this.switchCooldown = false;
                return;
            }

            if (mc.player != null) {
                if (raytrace.getValue() && enumFacing != null) {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                }
                else if (target.getY() == 255) {
                    // For Hoosiers. This is how we do buildheight. If the target block (q) is at Y 255. Then we send a placement packet to the bottom part of the block. Thus the EnumFacing.DOWN.
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                }
                else {
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                }
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (ModuleManager.isModuleEnabled("AutoGG")) {
                    AutoGG.INSTANCE.addTargetedPlayer(renderEnt.getName());
                }
            }

            if (ROTATION_UTIL.isSpoofingAngles()) {
                EntityPlayerSP player = mc.player;
                if (togglePitch) {
                    player.rotationPitch += 4.0E-4F;
                    togglePitch = false;
                }
                else {
                    player.rotationPitch -= 4.0E-4F;
                    togglePitch = true;
                }
            }
        }
    }

    public void onWorldRender(RenderEvent event) {
        // As far as I can tell, this code never gets executed, since render is always null :(
        if (this.render != null) {
            RenderUtil.drawBox(this.render,1, new GSColor(color.getValue(),50), 63);
            RenderUtil.drawBoundingBox(this.render, 1, 1.00f, new GSColor(color.getValue(),255));
        }

        if(showDamage.getValue()) {
            if (this.render != null && this.renderEnt != null) {
                double d = DamageUtil.calculateDamage((double) render.getX() + 0.5D, render.getY() + 1, (double) render.getZ() + 0.5D, renderEnt);
                String[] damageText = {(Math.floor(d) == d ? String.valueOf((int) d) : String.format("%.1f", d))};
                RenderUtil.drawNametag((double) render.getX() + 0.5D,(double) render.getY() + 0.5D,(double) render.getZ() + 0.5D, damageText, new GSColor(255,255,255),1);
            }
        }
        if (showSelfDamage.getValue()) {
            mc.world.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityEnderCrystal).forEach(entity -> {
                double d = DamageUtil.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player);
                String[] damageText = {(Math.floor(d) == d ? String.valueOf((int) d) : String.format("%.1f", d))};
                RenderUtil.drawNametag(entity.posX,entity.posY + 0.5D, entity.posZ, damageText, new GSColor(255,0,0),1);
            });
        }
    }

    private EntityEnderCrystal getCrystalToBreak(List<EntityPlayer> targets) {
        if (targets.size() == 0) {
            return null;
        }

        // get all current crystals
        List<EntityEnderCrystal> crystals = new ArrayList<>();
        for (Integer integer : crystalIDs.keySet()) {
            Entity crystal = mc.world.getEntityByID(integer);
            if (crystal != null) {
                if (crystal instanceof EntityEnderCrystal)
                    crystals.add((EntityEnderCrystal) crystal);
            }
        }

        final double breakRangeSq = breakRange.getValue() * breakRange.getValue();
        List<EntityEnderCrystal> crystalList = crystals.stream()
                .filter(entity -> mc.player.getDistanceSq(entity) <= breakRangeSq)
                .collect(Collectors.toList());
        // remove all crystals that deal more than max self damage
        // no point in checking these
        crystalList.removeIf(crystal -> DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player) > maxSelfDmg.getValue());
        if (antiSuicide.getValue()) {
            // remove all crystal that will cause suicide
            final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            crystalList.removeIf(crystal -> DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player) > playerHealth);
        }
        if (crystalList.size() == 0) {
            return null;
        }

        // will stop duplicates popping up
        TreeMap<Float, Pair<EntityEnderCrystal, EntityPlayer>> worthyCrystals = new TreeMap<>();
        // get the best crystal for each player
        // store in worthyCrystals
        for (EntityPlayer target : targets) {
            EntityEnderCrystal best = null;
            float bestDamage = 0f;
            for (EntityEnderCrystal crystal : crystalList) {
                float currentDamage = DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, target);
                if (currentDamage == bestDamage) {
                    // this new crystal is closer
                    // higher chance of being able to break it
                    if (best == null || mc.player.getDistanceSq(crystal) < mc.player.getDistanceSq(best)) {
                        bestDamage = currentDamage;
                        best = crystal;
                    }
                } else if (currentDamage > bestDamage) {
                    bestDamage = currentDamage;
                    best = crystal;
                }
            }
            // TODO: add own support
            if (best != null) {
                if (breakMode.getValue().equalsIgnoreCase("Smart")) {
                    if (bestDamage >= minBreakDmg.getValue() || target.getHealth() < facePlaceValue.getValue()) {
                        if (worthyCrystals.containsKey(bestDamage)) {
                            // have we found a closer crystal with equal damage
                            if (mc.player.getDistanceSq(worthyCrystals.get(bestDamage).getKey()) > mc.player.getDistanceSq(best)) {
                                worthyCrystals.replace(bestDamage, new Pair<>(best, target));
                            }
                        } else {
                            worthyCrystals.put(bestDamage, new Pair<>(best, target));
                        }
                    }
                } else {
                    if (worthyCrystals.containsKey(bestDamage)) {
                        // have we found a closer crystal with equal damage
                        if (mc.player.getDistanceSq(worthyCrystals.get(bestDamage).getKey()) > mc.player.getDistanceSq(best)) {
                            worthyCrystals.replace(bestDamage, new Pair<>(best, target));
                        }
                    } else {
                        worthyCrystals.put(bestDamage, new Pair<>(best, target));
                    }
                }
            }
        }

        if (crystalPriority.getValue().equalsIgnoreCase("Closest")) {
            Optional<Pair<EntityEnderCrystal, EntityPlayer>> out = worthyCrystals.values().stream().min(Comparator.comparing(pair -> mc.player.getDistanceSq(pair.getKey())));
            return out.map(Pair::getKey).orElse(null);
        } else if (crystalPriority.getValue().equalsIgnoreCase("Health")) {
            Optional<Pair<EntityEnderCrystal, EntityPlayer>> out = worthyCrystals.values().stream().min(Comparator.comparing(pair -> pair.getValue().getHealth() + pair.getValue().getAbsorptionAmount()));
            return out.map(Pair::getKey).orElse(null);
        }

        Map.Entry<Float, Pair<EntityEnderCrystal, EntityPlayer>> entry = worthyCrystals.lastEntry();
        if (entry == null) {
            return null;
        } else {
            return entry.getValue().getKey();
        }
    }

    private BlockPos getPositionToPlace(List<EntityPlayer> targets) {
        List<BlockPos> blockList = CrystalUtil.findCrystalBlocks((float) placeRange.getValue(), endCrystalMode.getValue());
        // remove all placements that deal more than max self damage
        // no point in checking these
        blockList.removeIf(crystal -> DamageUtil.calculateDamage((double) crystal.getX() + 0.5D, crystal.getY() + 1, (double) crystal.getZ() + 0.5D, mc.player) > maxSelfDmg.getValue());
        if (antiSuicide.getValue()) {
            // remove all crystal placements that will cause suicide
            final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            blockList.removeIf(crystal -> DamageUtil.calculateDamage((double) crystal.getX() + 0.5D,crystal.getY() + 1, (double) crystal.getZ() + 0.5D, mc.player) > playerHealth);
        }
        if (blockList.size() == 0) {
            return null;
        }


        final double enemyRangeSq = enemyRange.getValue() * enemyRange.getValue();
        // will stop duplicates popping up
        TreeMap<Float, Pair<BlockPos, EntityPlayer>> worthyPlacements = new TreeMap<>();
        // get the best crystal for each player
        // store in worthyPlacements
        for (EntityPlayer target : targets) {
            BlockPos best = null;
            float bestDamage = 0f;
            for (BlockPos crystal : blockList) {
                // if player is out of range of this crystal, do nothing
                if (target.getDistanceSq((double) crystal.getX() + 0.5D, crystal.getY() + 1, (double) crystal.getZ() + 0.5D) <= enemyRangeSq) {
                    float currentDamage = DamageUtil.calculateDamage((double) crystal.getX() + 0.5D, crystal.getY() + 1, (double) crystal.getZ() + 0.5D, target);
                    if (currentDamage == bestDamage) {
                        // this new crystal is closer
                        // higher chance of being able to break it
                        if (best == null || mc.player.getDistanceSq(crystal) < mc.player.getDistanceSq(best)) {
                            bestDamage = currentDamage;
                            best = crystal;
                        }
                    } else if (currentDamage > bestDamage) {
                        bestDamage = currentDamage;
                        best = crystal;
                    }
                }
            }
            // TODO: add own support
            if (best != null) {
                if (breakMode.getValue().equalsIgnoreCase("Smart")) {
                    if (bestDamage >= minDmg.getValue() || target.getHealth() < facePlaceValue.getValue()) {
                        if (worthyPlacements.containsKey(bestDamage)) {
                            // have we found a closer crystal with equal damage
                            if (mc.player.getDistanceSq(worthyPlacements.get(bestDamage).getKey()) > mc.player.getDistanceSq(best)) {
                                worthyPlacements.replace(bestDamage, new Pair<>(best, target));
                            }
                        } else {
                            worthyPlacements.put(bestDamage, new Pair<>(best, target));
                        }
                    }
                } else {
                    if (worthyPlacements.containsKey(bestDamage)) {
                        // have we found a closer crystal with equal damage
                        if (mc.player.getDistanceSq(worthyPlacements.get(bestDamage).getKey()) > mc.player.getDistanceSq(best)) {
                            worthyPlacements.replace(bestDamage, new Pair<>(best, target));
                        }
                    } else {
                        worthyPlacements.put(bestDamage, new Pair<>(best, target));
                    }
                }
            }
        }

        Optional<Pair<BlockPos, EntityPlayer>> out = Optional.empty();
        if (crystalPriority.getValue().equalsIgnoreCase("Closest")) {
            out = worthyPlacements.values().stream().min(Comparator.comparing(pair -> mc.player.getDistanceSq(pair.getKey())));
        } else if (crystalPriority.getValue().equalsIgnoreCase("Health")) {
            out = worthyPlacements.values().stream().min(Comparator.comparing(pair -> pair.getValue().getHealth() + pair.getValue().getAbsorptionAmount()));
        }

        if (out.isPresent()) {
            renderEnt = out.get().getValue();
            return out.map(Pair::getKey).orElse(null);
        }

        Map.Entry<Float, Pair<BlockPos, EntityPlayer>> entry = worthyPlacements.lastEntry();
        if (entry == null) {
            return null;
        } else {
            renderEnt = entry.getValue().getValue();
            return entry.getValue().getKey();
        }
    }

    private void swingArm() {
        if (handBreak.getValue().equalsIgnoreCase("Both")) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else if (handBreak.getValue().equalsIgnoreCase("Offhand")) {
            mc.player.swingArm(EnumHand.OFF_HAND);
        }
        else {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
        Packet<?> packet = event.getPacket();
        if (packet instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packetSpawnObject = (SPacketSpawnObject) event.getPacket();
            if (packetSpawnObject.getType() == 51) {
                crystalIDs.put(packetSpawnObject.getEntityID(), true);
            }
        } else if (packet instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packetDestroyEntities = (SPacketDestroyEntities) event.getPacket();
            for (int entityID : packetDestroyEntities.getEntityIDs()) {
                crystalIDs.remove(entityID);
            }
        }
    });

    public void onEnable() {
        ROTATION_UTIL.onEnable();
        GameSense.EVENT_BUS.subscribe(this);

        // get the ids of all currently loaded crystals
        List<EntityEnderCrystal> loadedCrystals = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity).collect(Collectors.toList());

        for (EntityEnderCrystal loadedCrystal : loadedCrystals) {
            crystalIDs.put(loadedCrystal.entityId, true);
        }

        if(chat.getValue() && mc.player != null) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoCrystal turned ON!");
        }
    }

    public void onDisable() {
        ROTATION_UTIL.onDisable();
        GameSense.EVENT_BUS.unsubscribe(this);
        render = null;
        renderEnt = null;
        ROTATION_UTIL.resetRotation();

        synchronized (crystalIDs) {
            crystalIDs.clear();
        }

        if(chat.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoCrystal turned OFF!");
        }
    }

    public String getHudInfo() {
        String t = "";
        if (hudDisplay.getValue().equalsIgnoreCase("Mode")){
            if (breakMode.getValue().equalsIgnoreCase("All")) {
                t = "[" + ChatFormatting.WHITE + "All" + ChatFormatting.GRAY + "]";
            }
            if (breakMode.getValue().equalsIgnoreCase("Smart")) {
                t = "[" + ChatFormatting.WHITE + "Smart" + ChatFormatting.GRAY + "]";
            }
            if (breakMode.getValue().equalsIgnoreCase("Own")) {
                t = "[" + ChatFormatting.WHITE + "Own" + ChatFormatting.GRAY + "]";
            }
        }
        if (hudDisplay.getValue().equalsIgnoreCase("None")) {
            t = "";
        }
        return t;
    }
}