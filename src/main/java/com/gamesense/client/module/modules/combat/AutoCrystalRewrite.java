package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.world.combat.ac.ACHelper;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import com.gamesense.client.GameSense;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Module.Declaration(name = "AutoCrystalRewrite", category = Category.Combat, priority = 100)
public class AutoCrystalRewrite extends Module {

    ModeSetting breakMode = registerMode("Target", Arrays.asList("All", "Smart", "Own"), "All");
    ModeSetting handBreak = registerMode("Hand", Arrays.asList("Main", "Offhand", "Both"), "Main");
    ModeSetting breakType = registerMode("Type", Arrays.asList("Swing", "Packet"), "Swing");
    ModeSetting crystalPriority = registerMode("Prioritise", Arrays.asList("Damage", "Closest", "Health"), "Damage");
    BooleanSetting breakCrystal = registerBoolean("Break", true);
    BooleanSetting placeCrystal = registerBoolean("Place", true);
    IntegerSetting attackSpeed = registerInteger("Attack Speed", 16, 0, 20);
    DoubleSetting breakRange = registerDouble("Hit Range", 4.4, 0.0, 10.0);
    DoubleSetting placeRange = registerDouble("Place Range", 4.4, 0.0, 6.0);
    DoubleSetting wallsRange = registerDouble("Walls Range", 3.5, 0.0, 10.0);
    DoubleSetting enemyRange = registerDouble("Enemy Range", 6.0, 0.0, 16.0);
    BooleanSetting antiWeakness = registerBoolean("Anti Weakness", true);
    BooleanSetting antiTotemPop = registerBoolean("Anti Totem Pop", true);
    BooleanSetting antiSuicide = registerBoolean("Anti Suicide", true);
    IntegerSetting antiSuicideValue = registerInteger("Min Health", 14, 1, 36);
    BooleanSetting autoSwitch = registerBoolean("Switch", true);
    BooleanSetting noGapSwitch = registerBoolean("No Gap Switch", false);
    BooleanSetting endCrystalMode = registerBoolean("1.13 Place", false);
    DoubleSetting minDmg = registerDouble("Min Damage", 5, 0, 36);
    DoubleSetting minBreakDmg = registerDouble("Min Break Dmg", 5, 0, 36.0);
    DoubleSetting maxSelfDmg = registerDouble("Max Self Dmg", 10, 1.0, 36.0);
    IntegerSetting facePlaceValue = registerInteger("FacePlace HP", 8, 0, 36);
    IntegerSetting armourFacePlace = registerInteger("Armour Health%", 20, 0, 100);
    DoubleSetting minFacePlaceDmg = registerDouble("FacePlace Dmg", 2, 0, 10);
    BooleanSetting rotate = registerBoolean("Rotate", true);
    BooleanSetting raytrace = registerBoolean("Raytrace", false);
    BooleanSetting showDamage = registerBoolean("Render Dmg", true);
    BooleanSetting showOwn = registerBoolean("Show Own", false);
    BooleanSetting chat = registerBoolean("Chat Msgs", true);
    ModeSetting hudDisplay = registerMode("HUD", Arrays.asList("Mode", "Target", "None"), "Mode");
    ColorSetting color = registerColor("Color", new GSColor(0, 255, 0, 50));

    BooleanSetting wait = registerBoolean("Force Wait", true);
    IntegerSetting timeout = registerInteger("Timeout (ms)", 5, 1, 10);

    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    public static boolean stopAC = false;
    private int oldSlot = -1;
    private Entity renderEntity;
    private BlockPos render;
    Timer timer = new Timer();

    private Vec3d lastHitVec = Vec3d.ZERO;
    private boolean rotating = false;

    // stores all know crystals
    private final Set<EntityEnderCrystal> allCrystals = Collections.synchronizedSet(new HashSet<>());
    // stores all the locations we have attempted to place crystals
    // and the corresponding crystal for that location (if there is any)
    private final Map<BlockPos, EntityEnderCrystal> placedCrystals = Collections.synchronizedMap(new HashMap<>());

    private List<CrystalInfo.BreakInfo> breakTargets = null;
    private List<CrystalInfo.PlaceInfo> placeTargets = null;

    @Override
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

        // entity range is the range from each crystal
        // so adding these together should solve problem
        // and reduce searching time
        double enemyDistance = enemyRange.getValue() + placeRange.getValue();
        final double entityRangeSq = (enemyDistance) * (enemyDistance);
        List<EntityPlayer> targets = mc.world.playerEntities.stream()
                .filter(entity -> mc.player.getDistanceSq(entity) <= entityRangeSq)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .filter(entity -> entity.getHealth() > 0.0f)
                .collect(Collectors.toList());
        // no point continuing if there are no targets
        if (targets.size() == 0) {
            return;
        }

        allCrystals.clear();
        allCrystals.addAll(mc.world.loadedEntityList.stream()
                            .filter(entity -> entity instanceof EntityEnderCrystal)
                            .map(entity -> (EntityEnderCrystal) entity).collect(Collectors.toSet()));

        final boolean own = breakMode.getValue().equalsIgnoreCase("Own");
        if (own) {
            // remove own crystals that have been destroyed
            placedCrystals.values().removeIf(crystal -> {
                if (crystal == null) {
                    return false;
                }
                return crystal.isDead;
            });
        }

        ACSettings settings = new ACSettings(breakCrystal.getValue(), placeCrystal.getValue(), enemyRange.getValue(), breakRange.getValue(), wallsRange.getValue(), minDmg.getValue(), minBreakDmg.getValue(), minFacePlaceDmg.getValue(), maxSelfDmg.getValue(), breakThreads.getValue(), facePlaceValue.getValue(), antiSuicide.getValue(), breakMode.getValue(), crystalPriority.getValue(), mc.player.getPositionVector());
        List<PlayerInfo> targetsInfo = new ArrayList<>();

        float armourPercent = armourFacePlace.getValue() / 100.0f;
        for (EntityPlayer target : targets) {
            targetsInfo.add(new PlayerInfo(target, armourPercent));
        }

        List<Future<CrystalInfo.PlaceInfo>> placeFutures = null;
        List<Future<List<CrystalInfo.BreakInfo>>> breakFutures = null;
        if (placeCrystal.getValue()) {
            placeFutures = startPlaceThreads(targetsInfo, settings);
        }
        if (breakCrystal.getValue()) {
            if (allCrystals.size() == 0) {
                placedCrystals.clear();
            } else {
                breakFutures = startBreakThreads(targetsInfo, settings);
            }
        }
        globalTimeoutTime = System.currentTimeMillis() + timeout.getValue();

        if (breakFutures != null) {
            EntityEnderCrystal crystal = getCrystalToBreak(breakFutures);
            if (crystal != null && (mc.player.canEntityBeSeen(crystal) || mc.player.getDistance(crystal) < wallsRange.getValue())) {
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

                    rotating = rotate.getValue();
                    lastHitVec = crystal.getPositionVector();

                    if (breakType.getValue().equalsIgnoreCase("Swing")) {
                        mc.playerController.attackEntity(mc.player, crystal);
                        swingArm();
                    } else if (breakType.getValue().equalsIgnoreCase("Packet")) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                        swingArm();
                    }
                }

                // return so we don't try to place if we just broke
                // stop place calculations as no point in continuing them
                if (placeFutures != null) {
                    for (Future<CrystalInfo.PlaceInfo> placeFuture : placeFutures) {
                        placeFuture.cancel(true);
                    }
                }
                return;
            }
        } else {
            rotating = false;
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
            // stop place calculations as no point in continuing them
            if (placeFutures != null) {
                for (Future<CrystalInfo.PlaceInfo> placeFuture : placeFutures) {
                    placeFuture.cancel(true);
                }
            }
            return;
        }

        if (placeFutures != null) {
            BlockPos target = getPositionToPlace(placeFutures);
            this.render = target;
            if (target == null) {
                rotating = false;
                return;
            }

            // autoSwitch stuff
            if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                if (this.autoSwitch.getValue()) {
                    if (!noGapSwitch.getValue() || !(mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)) {
                        mc.player.inventory.currentItem = crystalSlot;
                        rotating = false;
                        this.switchCooldown = true;
                    }
                }
                return;
            }

            EnumFacing enumFacing = null;
            if (raytrace.getValue()) {
                RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) target.getX() + 0.5d, (double) target.getY() - 0.5d, (double) target.getZ() + 0.5d));
                if (result == null || result.sideHit == null) {
                    render = null;
                    rotating = false;
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

            rotating = rotate.getValue();
            lastHitVec = new Vec3d(target).add(0.5, 0.5, 0.5);

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
        if (showOwn.getValue()) {
            placedCrystals.forEach(((blockPos, entityEnderCrystal) -> RenderUtil.drawBoundingBox(blockPos, 1, 1.00f, new GSColor(255, 255, 255,150))));
        }
    }

    private void startTargetFinder() {
        // entity range is the range from each crystal
        // so adding these together should solve problem
        // and reduce searching time
        double enemyDistance = enemyRange.getValue() + placeRange.getValue();
        final double entityRangeSq = (enemyDistance) * (enemyDistance);
        List<EntityPlayer> targets = mc.world.playerEntities.stream()
                .filter(entity -> mc.player.getDistanceSq(entity) <= entityRangeSq)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .filter(entity -> entity.getHealth() > 0.0f)
                .collect(Collectors.toList());
        // no point continuing if there are no targets
        if (targets.size() == 0) {
            return;
        }

        List<EntityEnderCrystal> allCrystals = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity).collect(Collectors.toList());

        final boolean own = breakMode.getValue().equalsIgnoreCase("Own");
        if (own) {
            // remove own crystals that have been destroyed
            allCrystals.removeIf(crystal -> !placedCrystals.containsKey(EntityUtil.getPosition(crystal)));
            placedCrystals.values().removeIf(crystal -> {
                if (crystal == null) {
                    return false;
                }
                return crystal.isDead;
            });
        }

        // remove all crystals that deal more than max self damage
        // no point in checking these
        final boolean antiSuicideValue = antiSuicide.getValue();
        final float maxSelfDamage = maxSelfDmg.getValue().floatValue();
        final float playerHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        allCrystals.removeIf(crystal -> {
            float damage = DamageUtil.calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player);
            if (damage > maxSelfDamage) {
                return true;
            } else return antiSuicideValue && damage > playerHealth;
        });

        List<BlockPos> blocks = CrystalUtil.findCrystalBlocks(placeRange.getValue().floatValue(), endCrystalMode.getValue());
        ACSettings settings = new ACSettings(breakCrystal.getValue(), placeCrystal.getValue(), enemyRange.getValue(), breakRange.getValue(), wallsRange.getValue(), minDmg.getValue(), minBreakDmg.getValue(), minFacePlaceDmg.getValue(), maxSelfDmg.getValue(), facePlaceValue.getValue(), antiSuicide.getValue(), breakMode.getValue(), crystalPriority.getValue(), mc.player.getPositionVector());

        List<PlayerInfo> targetsInfo = new ArrayList<>();
        float armourPercent = armourFacePlace.getValue() / 100.0f;
        for (EntityPlayer target : targets) {
            targetsInfo.add(new PlayerInfo(target, armourPercent));
        }

        long timeoutTime = System.currentTimeMillis() + timeout.getValue();
        ACHelper.INSTANCE.startCalculations(settings, targetsInfo, allCrystals, blocks, timeoutTime);
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
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.PRE || !rotating) return;

        Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
        Packet<?> packet = event.getPacket();
        if (packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect) packet;
            if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0f) {
                            entity.setDead();
                        }
                    }
                }
            }
        }
    });

    @EventHandler
    private final Listener<EntityJoinWorldEvent> entitySpawnListener = new Listener<>(event -> {
        Entity entity = event.getEntity();
        if (entity instanceof EntityEnderCrystal) {
            if (breakMode.getValue().equalsIgnoreCase("Own")) {
                EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
                BlockPos crystalPos = EntityUtil.getPosition(crystal);
                if (placedCrystals.containsKey(crystalPos)) {
                    placedCrystals.replace(crystalPos, crystal);
                }
            }
        }
    });

    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);

        // get all the currently loaded crystals
        List<EntityEnderCrystal> loadedCrystals = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity).collect(Collectors.toList());

        allCrystals.addAll(loadedCrystals);

        if(chat.getValue() && mc.player != null) {
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "AutoCrystal turned ON!");
        }
    }

    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        render = null;
        renderEntity = null;
        rotating = false;

        allCrystals.clear();
        placedCrystals.clear();

        if(chat.getValue()) {
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "AutoCrystal turned OFF!");
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