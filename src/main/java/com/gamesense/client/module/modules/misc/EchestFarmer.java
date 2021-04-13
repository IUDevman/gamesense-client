package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Module.Declaration(name = "EchestFarmer", category = Category.Misc)
public class EchestFarmer extends Module {

    IntegerSetting stackCount = registerInteger("N^Stack", 0, 0, 64);
    BooleanSetting info = registerBoolean("Info", true);

    private int obbyNeeded;

    @Override
    public void onEnable() {
        initValues();
    }

    private void initValues() {
        int obbyCount = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.OBSIDIAN).mapToInt(ItemStack::getCount).sum();
        int stackWanted = ( stackCount.getValue() == 0 ?  9999 : stackCount.getValue() * 64 );
        obbyNeeded = stackWanted - obbyCount;
        if (info.getValue())
            PistonCrystal.printDebug(String.format("N^obby: %d, N^stack: %d, needed: %d", obbyCount, stackWanted, obbyNeeded), false);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUpdate() {

    }

}
