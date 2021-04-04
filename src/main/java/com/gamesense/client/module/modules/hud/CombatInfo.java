package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystal;
import com.gamesense.client.module.modules.combat.OffHand;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Module.Declaration(name = "CombatInfo", category = Category.HUD)
@HUDModule.Declaration(posX = 0, posZ = 150)
public class CombatInfo extends HUDModule {

    ModeSetting infoType = registerMode("Type", Arrays.asList("Cyber", "Hoosiers"), "Hoosiers");
    ColorSetting color1 = registerColor("On", new GSColor(0, 255, 0, 255));
    ColorSetting color2 = registerColor("Off", new GSColor(255, 0, 0, 255));

    private InfoList list = new InfoList();
    private static final BlockPos[] surroundOffset = new BlockPos[]{new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0)};
    private static final String[] hoosiersModules = {"AutoCrystal", "KillAura", "Surround", "AutoTrap", "SelfTrap"};
    private static final String[] hoosiersNames = {"AC", "KA", "SU", "AT", "ST"};

    @Override
    public void populate(Theme theme) {
        component = new ListComponent(getName(), theme.getPanelRenderer(), position, list);
    }

    public void onRender() {
        AutoCrystal autoCrystal = ModuleManager.getModule(AutoCrystal.class);
        list.totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) ? 1 : 0);
        list.players = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityOtherPlayerMP)
                .filter(entity -> !SocialManager.isFriend(entity.getName()))
                .filter(e -> mc.player.getDistance(e) <= autoCrystal.placeRange.getValue())
                .map(entity -> (EntityOtherPlayerMP) entity)
                .min(Comparator.comparing(cl -> mc.player.getDistance(cl)))
                .orElse(null);
        list.renderLby = false;
        List<EntityPlayer> entities = new ArrayList<EntityPlayer>(mc.world.playerEntities.stream().filter(entityPlayer -> !SocialManager.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
        for (EntityPlayer e : entities) {
            int i = 0;
            for (BlockPos add : surroundOffset) {
                ++i;
                BlockPos o = new BlockPos(e.getPositionVector().x, e.getPositionVector().y, e.getPositionVector().z).add(add.getX(), add.getY(), add.getZ());
                if (mc.world.getBlockState(o).getBlock() == Blocks.OBSIDIAN) {
                    if (i == 1 && CrystalUtil.canPlaceCrystal(o.north(1).down(), autoCrystal.endCrystalMode.getValue())) {
                        list.lby = true;
                        list.renderLby = true;
                    } else if (i == 2 && CrystalUtil.canPlaceCrystal(o.east(1).down(), autoCrystal.endCrystalMode.getValue())) {
                        list.lby = true;
                        list.renderLby = true;
                    } else if (i == 3 && CrystalUtil.canPlaceCrystal(o.south(1).down(), autoCrystal.endCrystalMode.getValue())) {
                        list.lby = true;
                        list.renderLby = true;
                    } else if (i == 4 && CrystalUtil.canPlaceCrystal(o.west(1).down(), autoCrystal.endCrystalMode.getValue())) {
                        list.lby = true;
                        list.renderLby = true;
                    }
                } else {
                    list.lby = false;
                    list.renderLby = true;
                }

            }
        }
    }

    private static int getPing() {
        int p = -1;
        if (mc.player == null || mc.getConnection() == null || mc.getConnection().getPlayerInfo(mc.player.getName()) == null) {
            p = -1;
        } else {
            p = mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime();
        }
        return p;
    }

    private class InfoList implements HUDList {

        public int totems = 0;
        public EntityOtherPlayerMP players = null;
        public boolean renderLby = false;
        public boolean lby = false;

        @Override
        public int getSize() {
            if (infoType.getValue().equals("Hoosiers")) {
                return hoosiersModules.length;
            } else if (infoType.getValue().equals("Cyber")) {
                return renderLby ? 6 : 5;
            } else {
                return 0;
            }
        }

        @Override
        public String getItem(int index) {
            if (infoType.getValue().equals("Hoosiers")) {
                if (ModuleManager.isModuleEnabled(hoosiersModules[index])) return hoosiersNames[index] + ": ON";
                else return hoosiersNames[index] + ": OFF";
            } else if (infoType.getValue().equals("Cyber")) {
                if (index == 0) return "gamesense.cc";
                else if (index == 1) return "HTR";
                else if (index == 2) return "PLR";
                else if (index == 3) return "" + totems;
                else if (index == 4) return "PING " + getPing();
                else return "LBY";
            } else {
                return "";
            }
        }

        @Override
        public Color getItemColor(int index) {
            AutoCrystal autoCrystal = ModuleManager.getModule(AutoCrystal.class);

            if (infoType.getValue().equals("Hoosiers")) {
                if (ModuleManager.isModuleEnabled(hoosiersModules[index])) return color1.getValue();
                else return color2.getValue();
            } else if (infoType.getValue().equals("Cyber")) {
                boolean on = false;
                if (index == 0) {
                    on = true;
                } else if (index == 1) {
                    if (players != null) {
                        on = mc.player.getDistance(players) <= autoCrystal.breakRange.getValue();
                    }
                } else if (index == 2) {
                    if (players != null) {
                        on = mc.player.getDistance(players) <= autoCrystal.placeRange.getValue();
                    }
                } else if (index == 3) {
                    on = totems > 0 && ModuleManager.isModuleEnabled(OffHand.class);
                } else if (index == 4) {
                    on = getPing() <= 100;
                } else {
                    on = lby;
                }
                if (on) return color1.getValue();
                else return color2.getValue();
            } else {
                return new Color(255, 255, 255);
            }
        }

        @Override
        public boolean sortUp() {
            return false;
        }

        @Override
        public boolean sortRight() {
            return false;
        }
    }
}