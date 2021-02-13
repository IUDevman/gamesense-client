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
import io.netty.util.internal.ConcurrentSet;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.*;
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
    Setting.Boolean antiSuicide;
    Setting.Boolean showSelfDamage;
    Setting.Boolean showOwn;
    public static Setting.Boolean endCrystalMode;
    Setting.Boolean cancelCrystal;
    Setting.Boolean noGapSwitch;
    Setting.Integer facePlaceValue;
    Setting.Integer attackSpeed;
    Setting.Integer antiSuicideValue;
    Setting.Double maxSelfDmg;
    Setting.Double wallsRange;
    Setting.Double minDmg;
    Setting.Double minBreakDmg;
    Setting.Double minFacePlaceDmg;
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
        breakModes.add("Own");

        ArrayList<String> priority = new ArrayList<>();
        priority.add("Damage");
        priority.add("Closest");
        priority.add("Health");

        ArrayList<String> hudModes = new ArrayList<>();
        hudModes.add("Mode");
        hudModes.add("Target");
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
        endCrystalMode = registerBoolean("1.13 Place", false);
        cancelCrystal = registerBoolean("Cancel Crystal", false);
        minDmg = registerDouble("Min Damage", 5, 0, 36);
        minBreakDmg = registerDouble("Min Break Dmg", 5, 0,36.0);
        maxSelfDmg = registerDouble("Max Self Dmg", 10, 1.0, 36.0);
        facePlaceValue = registerInteger("FacePlace HP", 8, 0, 36);
        minFacePlaceDmg = registerDouble("FacePlace Min Dmg", 2.0d, 0, 10);
        rotate = registerBoolean("Rotate", true);
        spoofRotations = registerBoolean("Spoof Angles", true);
        raytrace = registerBoolean("Raytrace", false);
        showDamage = registerBoolean("Render Dmg", true);
        showSelfDamage = registerBoolean("Render Self Dmg", false);
        showOwn = registerBoolean("Debug Own", false);
        chat = registerBoolean("Chat Msgs", true);
        hudDisplay = registerMode("HUD", hudModes, "Mode");
        color = registerColor("Color", new GSColor(0, 255, 0, 50));
    }

    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    public static boolean stopAC = false;
    private static boolean togglePitch = false;
    private int oldSlot = -1;
    private Entity renderEntity;
    private BlockPos render;
    Timer timer = new Timer();

    // stores all know crystals
    private final ConcurrentSet<EntityEnderCrystal> allCrystals = new ConcurrentSet<>();
    // stores ID's of all new crystals that have been made, but not added to AllCrystals
    private final ConcurrentSet<Integer> newCrystals = new ConcurrentSet<>();
    // stores all the locations we have attempted to place crystals
    // and the corresponding crystal for that location (if there is any)
    private final HashMap<BlockPos, EntityEnderCrystal> placedCrystals = new HashMap<>();

    public void onUpdate() {
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
        // and reduce searching time
        final double entityRangeSq = (enemyRange.getValue() + placeRange.getValue()) * (placeRange.getValue() + enemyRange.getValue());
        List<EntityPlayer> targets = mc.world.playerEntities.stream()
                .filter(entity -> mc.player.getDistanceSq(entity) <= entityRangeSq)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .filter(entity -> entity.getHealth() > 0.0f)
                .collect(Collectors.toList());
        // no point continuing if there are no targets
        if (targets.size() == 0) {
            return;
        }

        newCrystals.removeIf(entityID -> {
           Entity entity = mc.world.getEntityByID(entityID);
           if (entity == null) {
               return true;
           }
           if (entity instanceof EntityEnderCrystal) {
               allCrystals.add((EntityEnderCrystal) entity);
               return true;
           } else {
               return false;
           }
        });
        allCrystals.removeIf(crystal -> crystal.isDead);

        final boolean own = breakMode.getValue().equalsIgnoreCase("Own");
        if (own) {
            // remove own crystals that have been destroyed
            placedCrystals.entrySet().removeIf(entry -> {
                EntityEnderCrystal crystal = entry.getValue();
                if (crystal != null) {
                    return crystal.isDead;
                } else {
                    return false;
                }
            });
        }

        EntityEnderCrystal crystal = getCrystalToBreak(targets);
        if (breakCrystal.getValue() && crystal != null) {
            if (mc.player.canEntityBeSeen(crystal) || mc.player.getDistance(crystal) < wallsRange.getValue()) {
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

                    // return so we don't try to place if we just broke
                    return;
                }
            }
        } else {
            ROTATION_UTIL.resetRotation();
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
        }

        // check to see if we are holding crystals or not
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

        if (this.placeCrystal.getValue()) {
            BlockPos target = getPositionToPlace(targets);
            this.render = target;
            if (target == null) {
                ROTATION_UTIL.resetRotation();
                return;
            }

            // autoSwitch stuff
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
                ROTATION_UTIL.lookAtPacket((double) target.getX() + 0.5d, target.getY() - 0.5d, (double) target.getZ() + 0.5d, mc.player);
            }

            EnumFacing enumFacing = null;
            if (raytrace.getValue()) {
                RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) target.getX() + 0.5d, (double) target.getY() - 0.5d, (double) target.getZ() + 0.5d));
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

            if (own) {
                BlockPos up = target.up();
                if (!placedCrystals.containsKey(up)) {
                    placedCrystals.put(up, null);
                }
            }

            if (raytrace.getValue() && enumFacing != null) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            }
            else if (target.getY() == 255) {
                // For Hoosiers. This is how we do build height. If the target block (q) is at Y 255. Then we send a placement packet to the bottom part of the block. Thus the EnumFacing.DOWN.
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            }
            else {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(target, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
            }
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

            if (ModuleManager.isModuleEnabled("AutoGG")) {
                AutoGG.INSTANCE.addTargetedPlayer(renderEntity.getName());
            }

            if (ROTATION_UTIL.isSpoofingAngles()) {
                if (togglePitch) {
                    mc.player.rotationPitch += 4.0E-4F;
                    togglePitch = false;
                }
                else {
                    mc.player.rotationPitch -= 4.0E-4F;
                    togglePitch = true;
                }
            }
        }
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
        if (showSelfDamage.getValue()) {
            mc.world.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityEnderCrystal).forEach(entity -> {
                String[] damageText = {String.format("%.1f", DamageUtil.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player))};
                RenderUtil.drawNametag(entity.posX,entity.posY + 0.5d, entity.posZ, damageText, new GSColor(255,0,0),1);
            });
        }
        if (showOwn.getValue()) {
            placedCrystals.forEach(((blockPos, entityEnderCrystal) -> RenderUtil.drawBoundingBox(blockPos, 1, 1.00f, new GSColor(255, 255, 255,150))));
        }
    }

    private EntityEnderCrystal getCrystalToBreak(List<EntityPlayer> targets) {
        // get all current crystals
        List<EntityEnderCrystal> crystals = new ArrayList<>(allCrystals);
        if (crystals.size() == 0) {
            placedCrystals.clear();
            return null;
        }

        final double breakRangeSq = breakRange.getValue() * breakRange.getValue();
        List<EntityEnderCrystal> crystalList = crystals.stream()
                .filter(entity -> mc.player.getDistanceSq(entity) <= breakRangeSq)
                .collect(Collectors.toList());
        if (breakMode.getValue().equalsIgnoreCase("Own")) {
            // no point in checking crystals that arent ours
            crystalList.retainAll(placedCrystals.values());
        }
        // remove all crystals that deal more than max self damage
        // no point in checking these
        crystalList.removeIf(crystal -> DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player) > maxSelfDmg.getValue());
        if (antiSuicide.getValue()) {
            // remove all crystals that will cause suicide
            final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            crystalList.removeIf(crystal -> DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player) > playerHealth);
        }
        if (crystalList.size() == 0) {
            return null;
        }

        final boolean smart = breakMode.getValue().equalsIgnoreCase("Smart");
        final float minBreakDamage = (float) minBreakDmg.getValue();
        final float facePlace = (float) facePlaceValue.getValue();
        final float minFacePlace = (float) minFacePlaceDmg.getValue();
        // will stop duplicate damage amounts popping up
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

            if (best != null) {
                boolean shouldAdd = false;
                if (smart) {
                    if ((double) bestDamage >= minBreakDamage || (target.getHealth() + target.getAbsorptionAmount() <= facePlace && bestDamage > minFacePlace)) {
                        shouldAdd = true;
                    }
                } else {
                    shouldAdd = true;
                }

                if (shouldAdd) {
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

        // get the best crystal based on our needs
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
        blockList.removeIf(crystal -> DamageUtil.calculateDamage((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, mc.player) > maxSelfDmg.getValue());
        if (antiSuicide.getValue()) {
            // remove all crystal placements that will cause suicide
            final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            blockList.removeIf(crystal -> DamageUtil.calculateDamage((double) crystal.getX() + 0.5d,(double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, mc.player) > playerHealth);
        }
        if (blockList.size() == 0) {
            return null;
        }

        final double enemyRangeSq = enemyRange.getValue() * enemyRange.getValue();
        final float minDamage = (float) minDmg.getValue();
        final float facePlace = (float) facePlaceValue.getValue();
        final float minFacePlace = (float) minFacePlaceDmg.getValue();
        // will stop duplicates popping up
        TreeMap<Float, Pair<BlockPos, EntityPlayer>> worthyPlacements = new TreeMap<>();
        // get the best crystal for each player
        // store in worthyPlacements
        for (EntityPlayer target : targets) {
            BlockPos best = null;
            float bestDamage = 0f;
            for (BlockPos crystal : blockList) {
                // if player is out of range of this crystal, do nothing
                if (target.getDistanceSq((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d) <= enemyRangeSq) {
                    float currentDamage = DamageUtil.calculateDamage((double) crystal.getX() + 0.5d, (double) crystal.getY() + 1.0d, (double) crystal.getZ() + 0.5d, target);
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

            if (best != null) {
                if (bestDamage >= minDamage || (target.getHealth() + target.getAbsorptionAmount() <= facePlace && bestDamage > minFacePlace)) {
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

        // get the best crystal based on our needs
        // done slightly differently here to enable use to update renderEnt
        Optional<Pair<BlockPos, EntityPlayer>> out = Optional.empty();
        if (crystalPriority.getValue().equalsIgnoreCase("Closest")) {
            out = worthyPlacements.values().stream().min(Comparator.comparing(pair -> mc.player.getDistanceSq(pair.getKey())));
        } else if (crystalPriority.getValue().equalsIgnoreCase("Health")) {
            out = worthyPlacements.values().stream().min(Comparator.comparing(pair -> pair.getValue().getHealth() + pair.getValue().getAbsorptionAmount()));
        }
        if (out.isPresent()) {
            renderEntity = out.get().getValue();
            return out.get().getKey();
        }

        Map.Entry<Float, Pair<BlockPos, EntityPlayer>> entry = worthyPlacements.lastEntry();
        if (entry == null) {
            renderEntity = null;
            return null;
        } else {
            renderEntity = entry.getValue().getValue();
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
            // 51 is the id for EnderCrystals
            if (packetSpawnObject.getType() == 51) {
                // entity is not initialised yet so we have save the id for later
                newCrystals.add(packetSpawnObject.getEntityID());
            }
        } else if (packet instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packetDestroyEntities = (SPacketDestroyEntities) event.getPacket();
            for (int entityID : packetDestroyEntities.getEntityIDs()) {
                Entity entity = mc.world.getEntityByID(entityID);
                if (entity instanceof EntityEnderCrystal) {
                    allCrystals.remove(entity);
                }
            }
        }
    });

    @EventHandler
    private final Listener<EntityJoinWorldEvent> entitySpawnListener = new Listener<>(event -> {
        Entity entity = event.getEntity();
        if (entity instanceof EntityEnderCrystal) {
            EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
            allCrystals.add(crystal);
            // already added, no need to try to add twice
            newCrystals.remove(crystal.getEntityId());
            if (breakMode.getValue().equalsIgnoreCase("Own")) {
                BlockPos crystalPos = EntityUtil.getPosition(crystal);
                if (placedCrystals.containsKey(crystalPos)) {
                    placedCrystals.replace(crystalPos, crystal);
                }
            }
        }
    });

    public void onEnable() {
        ROTATION_UTIL.onEnable();
        GameSense.EVENT_BUS.subscribe(this);

        // get all the currently loaded crystals
        List<EntityEnderCrystal> loadedCrystals = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity).collect(Collectors.toList());

        allCrystals.addAll(loadedCrystals);

        if(chat.getValue() && mc.player != null) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoCrystal turned ON!");
        }
    }

    public void onDisable() {
        ROTATION_UTIL.onDisable();
        GameSense.EVENT_BUS.unsubscribe(this);
        render = null;
        renderEntity = null;
        ROTATION_UTIL.resetRotation();

        allCrystals.clear();
        placedCrystals.clear();
        newCrystals.clear();

        if(chat.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoCrystal turned OFF!");
        }
    }

    private static final String stringAll = "[" + ChatFormatting.WHITE + "All" + ChatFormatting.GRAY + "]";
    private static final String stringSmart = "[" + ChatFormatting.WHITE + "Smart" + ChatFormatting.GRAY + "]";
    private static final String stringOwn = "[" + ChatFormatting.WHITE + "Own" + ChatFormatting.GRAY + "]";
    private static final String stringNone = "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]";

    public String getHudInfo() {
        String t = "";
        if (hudDisplay.getValue().equalsIgnoreCase("Mode")){
            if (breakMode.getValue().equalsIgnoreCase("All")) {
                t = stringAll;
            }
            if (breakMode.getValue().equalsIgnoreCase("Smart")) {
                t = stringSmart;
            }
            if (breakMode.getValue().equalsIgnoreCase("Own")) {
                t = stringOwn;
            }
        } else if (hudDisplay.getValue().equalsIgnoreCase("Target")) {
            if (renderEntity == null) {
                t = stringNone;
            } else {
                t = "[" + ChatFormatting.WHITE + renderEntity.getName() + ChatFormatting.GRAY + "]";
            }
        }
        return t;
    }
}