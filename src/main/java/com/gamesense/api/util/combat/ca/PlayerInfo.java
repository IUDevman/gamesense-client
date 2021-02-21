package com.gamesense.api.util.combat.ca;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class PlayerInfo {
    private static final Potion RESISTANCE = Potion.getPotionById(11);
    private static final DamageSource EXPLOSION_SOURCE = (new DamageSource("explosion")).setDifficultyScaled().setExplosion();

    public final EntityPlayer entity;

    public final float totalArmourValue;
    public final float armourToughness;
    public final int enchantModifier;

    public final boolean hasResistance;

    public PlayerInfo(EntityPlayer entity) {
        this.entity = entity;

        this.totalArmourValue = entity.getTotalArmorValue();
        this.armourToughness = (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        this.enchantModifier = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EXPLOSION_SOURCE);

        this.hasResistance = entity.isPotionActive(RESISTANCE);
    }

}
