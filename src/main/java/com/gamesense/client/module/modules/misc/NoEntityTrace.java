package com.gamesense.client.module.modules.misc;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;

/**
 * @author unkown
 * @author 0b00101010
 * @since 25/01/21
 */
public class NoEntityTrace extends Module {

	public NoEntityTrace() {
		super("NoEntityTrace", Category.Misc);
	}

	Setting.Boolean pickaxe;
	Setting.Boolean obsidian;
	Setting.Boolean eChest;
	Setting.Boolean block;
	Setting.Boolean all;

	public void setup() {
		pickaxe = registerBoolean("Pickaxe", true);
		obsidian = registerBoolean("Obsidian", false);
		eChest = registerBoolean("EnderChest", false);
		block = registerBoolean("Blocks", false);
		all = registerBoolean("All", false);
	}

	boolean isHoldingPickaxe = false;
	boolean isHoldingObsidian = false;
	boolean isHoldingEChest = false;
	boolean isHoldingBlock = false;

	public void onUpdate() {
		Item item = mc.player.getHeldItemMainhand().getItem();
		isHoldingPickaxe = item instanceof ItemPickaxe;
		isHoldingBlock = item instanceof ItemBlock;
		if (isHoldingBlock) {
			isHoldingObsidian = ((ItemBlock) item).getBlock() instanceof BlockObsidian;
			isHoldingEChest = ((ItemBlock) item).getBlock() instanceof BlockEnderChest;
		} else {
			isHoldingObsidian = false;
			isHoldingEChest = false;
		}
	}

	public boolean noTrace() {
		if (pickaxe.getValue() && isHoldingPickaxe) return isEnabled();
		if (obsidian.getValue() && isHoldingObsidian) return isEnabled();
		if (eChest.getValue() && isHoldingEChest) return isEnabled();
		if (block.getValue() && isHoldingBlock) return isEnabled();
		if (all.getValue()) return isEnabled();
		return false;
	}
}