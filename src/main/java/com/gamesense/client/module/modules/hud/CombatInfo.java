package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.players.friends.Friends;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystal;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CombatInfo extends Module {
    public CombatInfo(){
        super("CombatInfo", Category.HUD);
    }

    Setting.Integer posX;
    Setting.Integer posY;
    Setting.Mode infoType;
    Setting.ColorSetting color;

    public void setup(){
        ArrayList<String> infoTypes = new ArrayList<>();
        infoTypes.add("Cyber");
        infoTypes.add("Hoosiers");

        posX = registerInteger("X", "X", 0, 0, 1000);
        posY = registerInteger("Y", "Y", 150, 0, 1000);
        infoType = registerMode("Type", "Type", infoTypes, "Hoosiers");
        color = registerColor("Color","Color", new GSColor(0, 255, 0, 255));
    }

    private int totems;
    private BlockPos[] surroundOffset;


    public void onRender(){
        GSColor on = new GSColor(0, 255, 0);
        GSColor off = new GSColor(255, 0, 0);

        switch (infoType.getValue()){
            case "Cyber": {
                totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
                if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) totems++;

                EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityEnderCrystal)
                        .filter(e -> mc.player.getDistance(e) <= AutoCrystal.range.getValue())
                        .map(entity -> (EntityEnderCrystal) entity)
                        .min(Comparator.comparing(cl -> mc.player.getDistance(cl)))
                        .orElse(null);
                EntityOtherPlayerMP players = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityOtherPlayerMP)
                        .filter(entity -> !Friends.isFriend(entity.getName()))
                        .filter(e -> mc.player.getDistance(e) <= AutoCrystal.placeRange.getValue())
                        .map(entity -> (EntityOtherPlayerMP) entity)
                        .min(Comparator.comparing(cl -> mc.player.getDistance(cl)))
                        .orElse(null);

                final AutoCrystal a = (AutoCrystal) ModuleManager.getModuleByName("AutocrystalGS");
                this.surroundOffset = new BlockPos[]{new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0)};
                final List<EntityPlayer> entities = new ArrayList<EntityPlayer>(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));

                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "gamesense.cc", posX.getValue(), posY.getValue(), color.getValue());
                if (players != null && mc.player.getDistance(players) <= AutoCrystal.range.getValue()) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "HTR", posX.getValue(), posY.getValue() + 10, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "HTR", posX.getValue(), posY.getValue() + 10, off);
                }

                if (players != null && mc.player.getDistance(players) <= AutoCrystal.placeRange.getValue()) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "PLR", posX.getValue(), posY.getValue() + 20, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "PLR", posX.getValue(), posY.getValue() + 20, off);
                }

                if (totems > 0 && ModuleManager.isModuleEnabled("AutoTotem")) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), totems + "", posX.getValue(), posY.getValue() + 30, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), totems + "", posX.getValue(), posY.getValue() + 30, off);
                }

                if (getPing() > 100) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "PING " + getPing(), posX.getValue(), posY.getValue() + 40, off);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "PING " + getPing(), posX.getValue(), posY.getValue() + 40, on);

                }

                for (final EntityPlayer e : entities) {
                    int i = 0;
                    for (final BlockPos add : this.surroundOffset) {
                        ++i;
                        final BlockPos o = new BlockPos(e.getPositionVector().x, e.getPositionVector().y, e.getPositionVector().z).add(add.getX(), add.getY(), add.getZ());
                        if (mc.world.getBlockState(o).getBlock() == Blocks.OBSIDIAN) {
                            if (i == 1 && a.canPlaceCrystal(o.north(1).down())) {
                                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "LBY", posX.getValue(), posY.getValue() + 50, on);
                            }
                            if (i == 2 && a.canPlaceCrystal(o.east(1).down())) {
                                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "LBY", posX.getValue(), posY.getValue() + 50, on);
                            }
                            if (i == 3 && a.canPlaceCrystal(o.south(1).down())) {
                                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "LBY", posX.getValue(), posY.getValue() + 50, on);
                            }
                            if (i == 4 && a.canPlaceCrystal(o.west(1).down())) {
                                FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "LBY", posX.getValue(), posY.getValue() + 50, on);
                            }
                        } else
                            FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "LBY", posX.getValue(), posY.getValue() + 50, off);
                    }
                }
                break;
            }

            case "Hoosiers": {
                if (ModuleManager.isModuleEnabled("AutoCrystalGS")) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "AC: ENBL", posX.getValue(), posY.getValue(), on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "AC: DSBL", posX.getValue(), posY.getValue(), off);
                }

                if (ModuleManager.isModuleEnabled("KillAura")) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "KA: ENBL", posX.getValue(), posY.getValue() + 10, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "KA: DSBL", posX.getValue(), posY.getValue() + 10, off);
                }

                if (ModuleManager.isModuleEnabled("Surround")) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "SU: ENBL", posX.getValue(), posY.getValue() + 20, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "SU: DSBL", posX.getValue(), posY.getValue() + 20, off);
                }

                if (ModuleManager.isModuleEnabled("AutoTrap")) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "AT: ENBL", posX.getValue(), posY.getValue() + 30, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "AT: DSBL", posX.getValue(), posY.getValue() + 30, off);
                }

                if (ModuleManager.isModuleEnabled("SelfTrap")) {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "ST: ENBL", posX.getValue(), posY.getValue() + 40, on);
                } else {
                    FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), "ST: DSBL", posX.getValue(), posY.getValue() + 40, off);
                }
                break;
            }
        }
    }

    private int getPing () {
        int p = -1;
        if (mc.player == null || mc.getConnection() == null || mc.getConnection().getPlayerInfo(mc.player.getName()) == null) {
            p = -1;
        } else {
            p = mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime();
        }
        return p;
    }
}