package com.gamesense.client.module.modules.combat;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.GameSenseTessellator;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.misc.AutoGG;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

//Fuck me in the ASS A2H ~ CyberTF2

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystalGS", Category.Combat);
    }

    private BlockPos render;
    private Entity renderEnt;
    // we need this cooldown to not place from old hotbar slot, before we have switched to crystals
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int newSlot;
    private int waitCounter;
    EnumFacing f;

    Setting.b explode;
    Setting.i waitTick;
    public static Setting.d range;
    Setting.d walls;
    Setting.b antiWeakness;
    Setting.b place;
    Setting.b autoSwitch;
    public static Setting.d placeRange;
    Setting.d minDmg;
    Setting.i facePlace;
    Setting.b raytrace;
    Setting.b rotate;
    Setting.b spoofRotations;
    Setting.b chat;
    Setting.d maxSelfDmg;
    Setting.b noGappleSwitch;
    Setting.b showDamage;

    public boolean isActive = false;


    public void setup() {
        explode = this.registerB("Break", true);
        waitTick = this.registerI("HitDelay",  1, 0, 20);
        range = this.registerD("HitRange",  5.0, 0.0, 10.0);
        walls = this.registerD("WallsRange", 3.5, 0.0, 10.0);
        antiWeakness = this.registerB("AntiWeakness", true);
        showDamage = this.registerB("ShowDamage", false);
        place = this.registerB("Place", true);
        autoSwitch = this.registerB("AutoSwitch", true);
        placeRange = this.registerD("PlaceRange", 5.0, 0.0, 10.0);
        minDmg = this.registerD("MinDamage", 5, 0, 36);
        maxSelfDmg = this.registerD("MaxSelfDmg", 10, 1, 36);
        noGappleSwitch = this.registerB("NoGapSwitch", false);
        facePlace = this.registerI("FacePlaceHP", 8, 0, 36);
        raytrace = this.registerB("Raytrace", false);
        rotate = this.registerB("Rotate", true);
        spoofRotations = this.registerB("SpoofAngles", true);
        chat = this.registerB("ToggleMsg", true);


    }

    public void onUpdate() {
        isActive = false;
        if (mc.player == null || mc.player.isDead) return; // bruh
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .filter(e -> mc.player.getDistance(e) <= range.getValue())
                .map(entity -> (EntityEnderCrystal) entity)
                .min(Comparator.comparing(c -> mc.player.getDistance(c)))
                .orElse(null);
        if (explode.getValue() && crystal != null) {
            if (!mc.player.canEntityBeSeen(crystal) && mc.player.getDistance(crystal) > walls.getValue()) return;


            //TODO: Add Smart break sometime. Only attack crystals that will hurt targets. Not Friends nor ourselves.

            // Hit delay in Ticks. :O (Skidded from Heph but after searching this is how most people do it)
            if (waitTick.getValue() > 0) {
                if (waitCounter < waitTick.getValue()) {
                    waitCounter++;
                    return;
                } else {
                    waitCounter = 0;
                }
            }

            if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    // save initial player hand
                    oldSlot = mc.player.inventory.currentItem;
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                newSlot = -1;
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

            isActive = true;
            if (rotate.getValue()) {
                lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
            }
            mc.playerController.attackEntity(mc.player, crystal);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            isActive = false;
            return;
        } else {
            resetRotation();
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
                    crystalSlot = l;
                    break;
                }
            }
        }
        boolean offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return;
        }

        List<BlockPos> blocks = findCrystalBlocks();


        List<Entity> entities = new ArrayList<>();
        entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList()));

        BlockPos q = null;
        double damage = .5;
        // TODO: Switch Mode AutoCrystal. The AutoCrystal doesn't properly switch targets now. Have to fix that
        for (Entity entity : entities) {
            //ignore self
            if (entity == mc.player) {
                continue;
            }

            // Only Place on Player Entitites
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }

            if (((EntityLivingBase) entity).getHealth() <= 0 || entity.isDead || mc.player == null) {
                continue;
            }


            for (BlockPos blockPos : blocks) {

                double b = entity.getDistanceSq(blockPos);
                if (b >= 169.0) {
                    continue;
                }

                double selfDamage = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, (Entity) mc.player);
                double targetDamage = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity);
                float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                float targetHealth = ((EntityPlayer) entity).getHealth() + ((EntityPlayer) entity).getAbsorptionAmount();

                // skip any blockpos that will do damage below our mindmg value.
                if (targetDamage < minDmg.getValue() && targetHealth > facePlace.getValue()) {
                    continue;
                }

                if (targetDamage <= damage) {
                    continue;
                }

                // Dont Suicide dumbass
                if (selfDamage >= selfHealth - 0.5) {
                    continue;
                }

                // If this deals more damage to ourselves than it does to our target, continue. This is only ignored if the crystal is sure to kill our target but not us.
                // Also continue if our crystal is going to hurt us.. alot
                if ((selfDamage > targetDamage && !(targetDamage < ((EntityLivingBase) entity).getHealth())) || selfDamage - .5 > mc.player.getHealth()) {
                    continue;
                }

                // Guess what this one does.
                if (selfDamage > maxSelfDmg.getValue()) {
                    continue;
                }
                damage = targetDamage;
                q = blockPos;
                this.renderEnt = entity;

            }
            if (damage == 0.5) {
                this.render = null;
                this.renderEnt = null;
                resetRotation();
                return;
            }
            render = q;
            if (mc.player == null) {
                return;

            }

            if (renderEnt == null) {
                render = null;
                resetRotation();
                return;
            }

            //}
            render = q;

            if (place.getValue()) {
                if (mc.player == null) return;
                isActive = true;
                if (rotate.getValue() && q != null) {
                    lookAtPacket(q.getX() + .5, q.getY() - .5, q.getZ() + .5, mc.player);
                }
                if (q != null) {
                    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(q.getX() + .5, q.getY() - .5d, q.getZ() + .5));
                    if (raytrace.getValue()) {
                        if (result == null || result.sideHit == null) {
                            q = null;
                            f = null;
                            render = null;
                            resetRotation();
                            isActive = false;
                            return;
                        } else {
                            f = result.sideHit;
                        }
                    }
                }

                if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
                    if (autoSwitch.getValue()) {
                        if (noGappleSwitch.getValue() && isEatingGap()) {
                            isActive = false;
                            resetRotation();
                            return;
                        } else {
                            isActive = true;
                            mc.player.inventory.currentItem = crystalSlot;
                            resetRotation();
                            switchCooldown = true;
                        }
                    }
                    return;
                }
                // return after we did an autoswitch
                if (switchCooldown) {
                    switchCooldown = false;
                    return;
                }

                //TODO: find better way of doing PlaceDelay as our previous method fucked up placements.
                //mc.playerController.processRightClickBlock(mc.player, mc.world, q, f, new Vec3d(0, 0, 0), EnumHand.MAIN_HAND);
                if (q != null && mc.player != null) {
                    isActive = true;
                    if (raytrace.getValue() && f != null) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    } else if (q.getY() == 255) {
                        // For Hoosiers. This is how we do buildheight. If the target block (q) is at Y 255. Then we send a placement packet to the bottom part of the block. Thus the EnumFacing.DOWN.
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    } else {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                    }
                    if (ModuleManager.isModuleEnabled("AutoGG"))
                        AutoGG.INSTANCE.addTargetedPlayer(renderEnt.getName());
                }
                isActive = false;
            }
        }
    }


    public void onWorldRender (RenderEvent event){
        if (this.render != null) {
            final float[] hue = {(System.currentTimeMillis() % (360 * 32)) / (360f * 32)};
            int rgb = Color.HSBtoRGB(hue[0], 1, 1);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            hue[0] +=.02f;

            if (ColorMain.Rainbow.getValue()) {
                GameSenseTessellator.prepare(7);
                GameSenseTessellator.drawBox(this.render, r, g, b, 50, 63);
                GameSenseTessellator.release();
                GameSenseTessellator.prepare(7);
                GameSenseTessellator.drawBoundingBoxBlockPos(this.render, 1.00f, r, g, b, 255);
            } else {
                GameSenseTessellator.prepare(7);
                GameSenseTessellator.drawBox(this.render, ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), 50, 63);
                GameSenseTessellator.release();
                GameSenseTessellator.prepare(7);
                GameSenseTessellator.drawBoundingBoxBlockPos(this.render, 1.00f, ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), 255);
            }
            GameSenseTessellator.release();
        }

        if (showDamage.getValue()) {
            if (this.render != null && this.renderEnt != null) {
                GlStateManager.pushMatrix();
                GameSenseTessellator.glBillboardDistanceScaled((float) render.getX() + 0.5f, (float) render.getY() + 0.5f, (float) render.getZ() + 0.5f, mc.player, 1);
                double d = calculateDamage(render.getX() + .5, render.getY() + 1, render.getZ() + .5, renderEnt);
                final String damageText = (Math.floor(d) == d ? (int) d : String.format("%.1f", d)) + "";
                GlStateManager.disableDepth();
                GlStateManager.translate(-(mc.fontRenderer.getStringWidth(damageText) / 2.0d), 0, 0);
                mc.fontRenderer.drawStringWithShadow(damageText, 0, 0, 0xFFffffff);
                GlStateManager.popMatrix();
            }
        }
    }

    private boolean isEatingGap(){
        return mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && mc.player.isHandActive();
    }




    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    public boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
                && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    private List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getPlayerPos(), (float)placeRange.getValue(), (int)placeRange.getValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        /*if (entity instanceof EntityLivingBase)
            finald = getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));*/
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            //damage = damage * (1.0F - f / 25.0F);

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            //   damage = Math.max(damage - ep.getAbsorptionAmount(), 0.0F);
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    //Better Rotation Spoofing System:

    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;

    //this modifies packets being sent so no extra ones are made. NCP used to flag with "too many packets"
    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx*dirx + diry*diry + dirz*dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        //to degree
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw,pitch};
    }

    @EventHandler
    private Listener<PacketEvent.Send> packetSendListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer && spoofRotations.getValue()) {
            if (isSpoofingAngles) {
                ((CPacketPlayer) packet).yaw = (float) yaw;
                ((CPacketPlayer) packet).pitch = (float) pitch;
            }
        }
    });

    @EventHandler
    private Listener<PacketEvent.Receive> packetReceiveListener = new Listener<>(event -> {
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

    @Override
    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
        isActive = false;
        if(chat.getValue() && mc.player != null) {
            Command.sendRawMessage("\u00A7aAutoCrystal turned ON!");
        }
    }

    @Override
    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
        render = null;
        renderEnt = null;
        resetRotation();
        isActive = false;
        if(chat.getValue()) {
            Command.sendRawMessage("\u00A7cAutoCrystal turned OFF!");
        }
    }
}

