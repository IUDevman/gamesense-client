package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.Phase;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.combat.CrystalUtil;
import com.gamesense.api.util.combat.DamageUtil;
import com.gamesense.api.util.math.RotationUtils;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author CyberTF2 and Hoosiers
 */

@Module.Declaration(name = "AutoCrystalGS", category = Category.Combat, priority = 100)
public class AutoCrystalGS extends Module {

    BooleanSetting breakCrystal;
    BooleanSetting antiWeakness;
    BooleanSetting placeCrystal;
    BooleanSetting autoSwitch;
    BooleanSetting raytrace;
    BooleanSetting rotate;
    BooleanSetting chat;
    BooleanSetting showDamage;
    BooleanSetting antiSuicide;
    BooleanSetting multiPlace;
    public static BooleanSetting endCrystalMode;
    BooleanSetting cancelCrystal;
    BooleanSetting noGapSwitch;
    BooleanSetting refresh;
    IntegerSetting facePlaceValue;
    IntegerSetting attackSpeed;
    IntegerSetting antiSuicideValue;
    IntegerSetting attackValue;
    DoubleSetting maxSelfDmg;
    DoubleSetting wallsRange;
    DoubleSetting minDmg;
    DoubleSetting minBreakDmg;
    DoubleSetting enemyRange;
    public static DoubleSetting placeRange;
    public static DoubleSetting breakRange;
    ModeSetting handBreak;
    ModeSetting breakMode;
    ModeSetting hudDisplay;
    ModeSetting breakType;
    ColorSetting color;

    public void setup() {
        ArrayList<String> hands = new ArrayList<>();
        hands.add("Main");
        hands.add("Offhand");
        hands.add("Both");

        ArrayList<String> breakModes = new ArrayList<>();
        breakModes.add("All");
        breakModes.add("Smart");
        breakModes.add("Own");

        ArrayList<String> hudModes = new ArrayList<>();
        hudModes.add("Mode");
        hudModes.add("None");

        ArrayList<String> breakTypes = new ArrayList<>();
        breakTypes.add("Swing");
        breakTypes.add("Packet");

        breakMode = registerMode("Target", breakModes, "All");
        handBreak = registerMode("Hand", hands, "Main");
        breakType = registerMode("Type", breakTypes, "Swing");
        breakCrystal = registerBoolean("Break", true);
        placeCrystal = registerBoolean("Place", true);
        attackSpeed = registerInteger("Attack Speed", 16, 0, 20);
        attackValue = registerInteger("Hit Amount", 1, 1, 10);
        breakRange = registerDouble("Hit Range", 4.4, 0.0, 10.0);
        placeRange = registerDouble("Place Range", 4.4, 0.0, 6.0);
        wallsRange = registerDouble("Walls Range", 3.5, 0.0, 10.0);
        enemyRange = registerDouble("Enemy Range", 6.0, 0.0, 16.0);
        refresh = registerBoolean("Refresh", true);
        antiWeakness = registerBoolean("Anti Weakness", true);
        antiSuicide = registerBoolean("Anti Suicide", true);
        antiSuicideValue = registerInteger("Min Health", 14, 1, 36);
        autoSwitch = registerBoolean("Switch", true);
        noGapSwitch = registerBoolean("No Gap Switch", false);
        multiPlace = registerBoolean("Multi Place", false);
        endCrystalMode = registerBoolean("1.13 Place", false);
        cancelCrystal = registerBoolean("Cancel Crystal", false);
        minDmg = registerDouble("Min Damage", 5, 0, 36);
        minBreakDmg = registerDouble("Min Break Dmg", 5, 0, 36.0);
        maxSelfDmg = registerDouble("Max Self Dmg", 10, 1.0, 36.0);
        facePlaceValue = registerInteger("FacePlace HP", 8, 0, 36);
        rotate = registerBoolean("Rotate", true);
        raytrace = registerBoolean("Raytrace", false);
        showDamage = registerBoolean("Render Dmg", true);
        chat = registerBoolean("Chat Msgs", true);
        hudDisplay = registerMode("HUD", hudModes, "Mode");
        color = registerColor("Color", new GSColor(0, 255, 0, 50));
    }

    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    public boolean isActive = false;
    public static boolean stopAC = false;
    private int oldSlot = -1;
    private Entity renderEnt;
    private BlockPos render;
    public static final ArrayList<BlockPos> PlacedCrystals = new ArrayList<>();
    private EnumFacing enumFacing;
    Timer timer = new Timer();
    Timer stuckTimer = new Timer();

    private Vec3d lastHitVec = Vec3d.ZERO;
    private boolean rotating = false;

    @SuppressWarnings("unused")
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener<>(event -> {
        if (event.getPhase() != Phase.PRE || !rotating) return;

        Vec2f rotation = RotationUtils.getRotationTo(lastHitVec);
        PlayerPacket packet = new PlayerPacket(this, rotation);
        PlayerPacketManager.INSTANCE.addPacket(packet);
    });

    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.isDead) {
            disable();
            return;
        }

        if (stopAC) {
            return;
        }

        if (refresh.getValue() && stuckTimer.getTimePassed() / 1000L >= 2) {
            stuckTimer.reset();
            PlacedCrystals.clear();
        }

        if (antiSuicide.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) <= antiSuicideValue.getValue()) {
            return;
        }

        isActive = false;

        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .filter(e -> mc.player.getDistance(e) <= breakRange.getValue())
                .filter(this::crystalCheck)
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);

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
                int newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if ((stack.getItem() instanceof ItemSword)) {
                        newSlot = i;
                        break;
                    }
                    if ((stack.getItem() instanceof ItemTool)) {
                        newSlot = i;
                        break;
                    }
                }
                // check if any swords or tools were found
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }

            if (timer.getTimePassed() / 50L >= 20 - attackSpeed.getValue()) {
                timer.reset();

                isActive = true;

                rotating = rotate.getValue();
                lastHitVec = crystal.getPositionVector();

                IntStream.range(0, attackValue.getValue()).forEach(i -> {
                    if (breakType.getValue().equalsIgnoreCase("Swing")) {
                        breakCrystal(crystal);
                    } else if (breakType.getValue().equalsIgnoreCase("Packet")) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                        swingArm();
                    }
                });

                if (cancelCrystal.getValue()) {
                    crystal.setDead();
                    mc.world.removeAllEntities();
                    mc.world.getLoadedEntityList();
                }

                isActive = false;
            }

            if (!multiPlace.getValue()) {
                return;
            }
        } else {
            rotating = false;
            if (oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
            isActive = false;
        }

        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    if (mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() != Items.END_CRYSTAL) {
                        crystalSlot = l;
                        break;
                    }
                }
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = CrystalUtil.findCrystalBlocks(placeRange.getValue().floatValue(), endCrystalMode.getValue());

        List<Entity> entities = mc.world.playerEntities.stream().filter(entityPlayer -> !EntityUtil.basicChecksEntity(entityPlayer)).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList());

        BlockPos q = null;
        double damage = 0.5D;
        Iterator<Entity> var9 = entities.iterator();

        label164:
        while (true) {
            EntityPlayer entity;
            do {
                do {
                    if (!var9.hasNext()) {
                        if (damage == 0.5D) {
                            this.render = null;
                            this.renderEnt = null;
                            rotating = false;
                            return;
                        }

                        this.render = q;
                        if (this.placeCrystal.getValue()) {

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

                            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) q.getX() + 0.5D, (double) q.getY() - 0.5D, (double) q.getZ() + 0.5D));
                            if (raytrace.getValue()) {
                                if (result == null || result.sideHit == null) {
                                    enumFacing = null;
                                    render = null;
                                    rotating = false;
                                    isActive = false;
                                    return;
                                } else {
                                    enumFacing = result.sideHit;
                                }
                            }

                            if (this.switchCooldown) {
                                this.switchCooldown = false;
                                return;
                            }

                            if (mc.player != null) {
                                rotating = rotate.getValue();
                                lastHitVec = new Vec3d(q).add(0.5, 0.5, 0.5);

                                isActive = true;

                                if (raytrace.getValue() && enumFacing != null) {
                                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                                } else if (q.getY() == 255) {
                                    // For Hoosiers. This is how we do buildheight. If the target block (q) is at Y 255. Then we send a placement packet to the bottom part of the block. Thus the EnumFacing.DOWN.
                                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                                } else {
                                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                                }
                                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                                //Cache the crystals we've placed
                                PlacedCrystals.add(q);
                                if (ModuleManager.isModuleEnabled(AutoGG.class))
                                    AutoGG.INSTANCE.addTargetedPlayer(renderEnt.getName());
                            }

                            return;
                        }
                    }

                    entity = (EntityPlayer) var9.next();
                } while (entity == mc.player);
            } while (entity.getHealth() <= 0.0F);

            Iterator var11 = blocks.iterator();

            while (true) {
                BlockPos blockPos;
                double d;
                double self;
                double targetDamage;
                float targetHealth;
                double x;
                double y;
                double z;
                do {
                    do {
                        do {
                            do {
                                do {
                                    if (!var11.hasNext()) {
                                        continue label164;
                                    }

                                    blockPos = (BlockPos) var11.next();
                                    // Better method for doing EnemyRange
                                    // @author Cyber
                                    x = blockPos.getX() + 0.0;
                                    y = blockPos.getY() + 1.0;
                                    z = blockPos.getZ() + 0.0;
                                    // } while (b >= 169.0D);
                                } while (entity.getDistanceSq(x, y, z) >= enemyRange.getValue() * enemyRange.getValue());

                                d = DamageUtil.calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, entity);
                            } while (d <= damage);
                            targetDamage = DamageUtil.calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity);
                            targetHealth = entity.getHealth() + entity.getAbsorptionAmount();
                        } while (targetDamage < minDmg.getValue() && targetHealth > facePlaceValue.getValue());
                        self = DamageUtil.calculateDamage((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, mc.player);
                    } while (self >= maxSelfDmg.getValue());
                } while (self >= mc.player.getHealth() + mc.player.getAbsorptionAmount());

                damage = d;
                q = blockPos;
                renderEnt = entity;
            }
        }
    }

    public void onWorldRender(RenderEvent event) {
        // As far as I can tell, this code never gets executed, since render is always null :(
        if (this.render != null) {
            RenderUtil.drawBox(this.render, 1, new GSColor(color.getValue(), 50), 63);
            RenderUtil.drawBoundingBox(this.render, 1, 1.00f, new GSColor(color.getValue(), 255));
        }

        if (showDamage.getValue()) {
            if (this.render != null && this.renderEnt != null) {
                double d = DamageUtil.calculateDamage(render.getX() + .5, render.getY() + 1, render.getZ() + .5, renderEnt);
                String[] damageText = new String[1];
                damageText[0] = (Math.floor(d) == d ? (int) d : String.format("%.1f", d)) + "";
                RenderUtil.drawNametag(render.getX() + 0.5, render.getY() + 0.5, render.getZ() + 0.5, damageText, new GSColor(255, 255, 255), 1);
            }
        }
    }

    private boolean crystalCheck(Entity crystal) {

        if (!(crystal instanceof EntityEnderCrystal)) {
            return false;
        }

        if (breakMode.getValue().equalsIgnoreCase("All")) {
            return true;
        } else if (breakMode.getValue().equalsIgnoreCase("Own")) {
            for (BlockPos pos : new ArrayList<>(PlacedCrystals)) {
                if (pos != null && pos.getDistance((int) crystal.posX, (int) crystal.posY, (int) crystal.posZ) <= 3.0) {
                    return true;
                }
            }
        } else if (breakMode.getValue().equalsIgnoreCase("Smart")) {
            EntityLivingBase target = renderEnt != null ? (EntityLivingBase) renderEnt : GetNearTarget(crystal);

            if (target == null || target == mc.player) {
                return false;
            }

            float targetDmg = DamageUtil.calculateDamage(crystal.posX + 0.5, crystal.posY + 1, crystal.posZ + 0.5, target);

            return targetDmg >= minBreakDmg.getValue() || (targetDmg > minBreakDmg.getValue()) && target.getHealth() > facePlaceValue.getValue();
        }

        return false;
    }

    // 0b00101010: replaced getDistance with getDistanceSq as speeds up calculation
    private EntityLivingBase GetNearTarget(Entity distanceTarget) {
        return mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .map(entity -> (EntityLivingBase) entity)
                .min(Comparator.comparing(distanceTarget::getDistanceSq))
                .orElse(null);
    }

    private void breakCrystal(EntityEnderCrystal crystal) {
        mc.playerController.attackEntity(mc.player, crystal);

        swingArm();
    }

    private void swingArm() {
        if (handBreak.getValue().equalsIgnoreCase("Both")) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.OFF_HAND);
        } else if (handBreak.getValue().equalsIgnoreCase("Offhand")) {
            mc.player.swingArm(EnumHand.OFF_HAND);
        } else {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : Minecraft.getMinecraft().world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            e.setDead();
                        }
                    }
                }
            }
        }
    });

    public void onEnable() {
        PlacedCrystals.clear();
        isActive = false;
        if (chat.getValue() && mc.player != null) {
            MessageBus.sendClientPrefixMessage(ColorMain.getEnabledColor() + "AutoCrystalGS turned ON!");
        }
    }

    public void onDisable() {
        render = null;
        renderEnt = null;
        rotating = false;
        PlacedCrystals.clear();
        isActive = false;
        if (chat.getValue()) {
            MessageBus.sendClientPrefixMessage(ColorMain.getDisabledColor() + "AutoCrystalGS turned OFF!");
        }
    }

    public String getHudInfo() {
        String t = "";
        if (hudDisplay.getValue().equalsIgnoreCase("Mode")) {
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
