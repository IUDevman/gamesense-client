package com.gamesense.api.util.world.combat.ac;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class PlayerInfo {
    private static final Potion RESISTANCE = Potion.getPotionById(11);
    private static final DamageSource EXPLOSION_SOURCE = (new DamageSource("explosion")).setDifficultyScaled().setExplosion();

    public final EntityPlayer entity;

    public final float totalArmourValue;
    public final float armourToughness;
    public final float health;
    public final int enchantModifier;

    public final boolean hasResistance;
    public final boolean lowArmour;

    public PlayerInfo(EntityPlayer entity, float armorPercent) {
        this.entity = entity;

        this.totalArmourValue = entity.getTotalArmorValue();
        this.armourToughness = (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        this.health = entity.getHealth() + entity.getAbsorptionAmount();
        this.enchantModifier = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EXPLOSION_SOURCE);

        this.hasResistance = entity.isPotionActive(RESISTANCE);

        boolean i = false;
        for (ItemStack stack : entity.getArmorInventoryList()) {
            if ((1.0f - ((float) stack.getItemDamage() / (float) stack.getMaxDamage())) < armorPercent) {
                i = true;
                break;
            }
        }
        this.lowArmour = i;
    }

    public PlayerInfo(EntityPlayer entity, boolean lowArmour) {
        this.entity = entity;

        this.totalArmourValue = entity.getTotalArmorValue();
        this.armourToughness = (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        this.health = entity.getHealth() + entity.getAbsorptionAmount();
        this.enchantModifier = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EXPLOSION_SOURCE);

        this.hasResistance = entity.isPotionActive(RESISTANCE);

        this.lowArmour = lowArmour;
    }
}
