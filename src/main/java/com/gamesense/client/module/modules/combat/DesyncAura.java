package com.gamesense.client.module.modules.combat;

import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;

import java.util.Iterator;

public class DesyncAura extends Module{
	public DesyncAura(){super ("DesyncAura", Category.Combat);
	}

	Setting.Double hitRange;
	Setting.Integer delay;
	Setting.Boolean switchTo32k;
	Setting.Boolean onlyUse32k;

	public void setup(){

		hitRange = registerDouble("Hit Range", "HitRange", 4, 1, 6);
		delay = registerInteger("Delay", "Delay", 6, 0 , 10);
		switchTo32k = registerBoolean("Switch To 32k", "SwitchTo32k", true);
		onlyUse32k = registerBoolean("Only 32k", "Only32k", true);
	}
	private int hasWaited = 0;

	public void onUpdate(){
		if (this.isEnabled() && !mc.player.isDead && mc.world != null){
			if (this.hasWaited < this.delay.getValue()){
				this.hasWaited++;
			} else{
				this.hasWaited = 0;
				Iterator var1 = mc.world.loadedEntityList.iterator();

				while(true){
					Entity entity;
					do{
						do{
							do{
								do{
									do{
										do{
											do{
												if (!var1.hasNext()){
													return;
												}

												entity = (Entity)var1.next();
											} while(!(entity instanceof EntityLivingBase));
										} while(entity == mc.player);
									} while((double)mc.player.getDistance(entity) > this.hitRange.getValue());
								} while(((EntityLivingBase)entity).getHealth() <= 0.0F);
							} while(!(entity instanceof EntityPlayer));
						} while(entity instanceof EntityPlayer && Friends.isFriend(entity.getName()));
					} while(!this.checkSharpness(mc.player.getHeldItemMainhand()) && this.onlyUse32k.getValue());

					this.attack(entity);
				}
			}
		}
	}

	private boolean checkSharpness(ItemStack item){
		if (item.getTagCompound() == null){
			return false;
		} else{
			NBTTagList enchants = (NBTTagList)item.getTagCompound().getTag("ench");
			if (enchants == null){
				return false;
			} else{
				for(int i = 0; i < enchants.tagCount(); i++){
					NBTTagCompound enchant = enchants.getCompoundTagAt(i);
					if (enchant.getInteger("id") == 16){
						int lvl = enchant.getInteger("lvl");
						if (lvl >= 42){
							return true;
						}
						break;
					}
				}

				return false;
			}
		}
	}

	public void attack(Entity e){
		boolean holding32k = false;
		if (this.checkSharpness(mc.player.getHeldItemMainhand())){
			holding32k = true;
		}

		if (this.switchTo32k.getValue() && !holding32k){
			int newSlot = -1;

			for(int i = 0; i < 9; i++){
				ItemStack stack = mc.player.inventory.getStackInSlot(i);
				if (stack != ItemStack.EMPTY && this.checkSharpness(stack)){
					newSlot = i;
					break;
				}
			}

			if (newSlot != -1){
				mc.player.inventory.currentItem = newSlot;
				holding32k = true;
			}
		}

		if (!this.onlyUse32k.getValue() || holding32k){
			mc.playerController.attackEntity(mc.player, e);
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	}
}