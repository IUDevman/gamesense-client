package com.gamesense.client.module.modules.hud;

import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.world.TpsUtils;
import com.gamesense.api.util.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystal;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {

    public HUD() {
        super("HUD", Category.HUD);
        setDrawn(false);

        resource = new ResourceLocation("minecraft:inventory_viewer.png");
    }

    public static Setting.Boolean customFont;
    Setting.Boolean PotionEffects;
    Setting.Boolean Watermark;
    Setting.Boolean Welcomer;
    Setting.Boolean Inventory;
    Setting.Integer inventoryX;
    Setting.Integer inventoryY;
    Setting.Boolean GameSenseInfo;
    Setting.Mode Type;
    Setting.Boolean ArrayList;
    Setting.Boolean ArmorHud;
    Setting.Integer potionx;
    Setting.Integer potiony;
    Setting.Integer welcomex;
    Setting.Integer welcomey;
    Setting.Integer infox;
    Setting.Integer infoy;
    Setting.Boolean sortUp;
    Setting.Boolean right;
    Setting.Boolean psortUp;
    Setting.Boolean pright;
    Setting.Integer arrayx;
    Setting.Integer arrayy;
	Setting.ColorSetting color;
    private BlockPos[] surroundOffset;
    ResourceLocation resource;
    int sort;
    int modCount;
    int count;
    DecimalFormat format1 = new DecimalFormat("0");
    DecimalFormat format2 = new DecimalFormat("00");

    private static final RenderItem itemRender = Minecraft.getMinecraft()
            .getRenderItem();
    int totems;

    public void setup() {
        ArrayList<String> Modes = new ArrayList<>();
        Modes.add("PvP");
        Modes.add("Combat");
        Type = registerMode("Info Type", "InfoType", Modes, "PvP");
        infox = registerInteger("Information X", "InformationX", 0, 0, 1000);
        infoy = registerInteger("Information Y", "InformationY",  0, 0, 1000);
        GameSenseInfo = registerBoolean("Information", "Information", false);
        ArmorHud = registerBoolean("Armor Hud", "ArmorHud", false);
        ArrayList = registerBoolean("ArrayList", "ArrayList",  false);
        sortUp = registerBoolean("Array Sort Up", "ArraySortUp", false);
        right = registerBoolean("Array Right", "ArrayRight", false);
        arrayx = registerInteger("Array X", "ArrayX", 0, 0, 1000);
        arrayy = registerInteger("Array Y", "ArrayY",0 , 0 ,1000);
        Inventory = registerBoolean("Inventory", "Inventory", false);
        inventoryX = registerInteger("Inventory X", "InventoryX", 0,0,1000);
        inventoryY = registerInteger("Inventory Y", "InventoryY", 12,0,1000);
        PotionEffects = registerBoolean("Potion Effects", "PotionEffects",false);
        potionx = registerInteger("Potion X", "PotionX", 0, 0, 1000);
        potiony = registerInteger("Potion Y", "PotionY", 0, 0, 1000);
        psortUp = registerBoolean("Potion Sort Up", "PotionSortUp", false);
        pright = registerBoolean("Potion Right", "PotionRight", false);
        Watermark = registerBoolean("Watermark", "Watermark", false);
        Welcomer = registerBoolean("Welcomer", "Welcomer", false);
        welcomex = registerInteger("Welcomer X", "WelcomerX", 0, 0, 1000);
        welcomey = registerInteger("Welcomer Y", "WelcomerY", 0, 0, 1000);
        customFont = registerBoolean("Custom Font", "CustomFont", false);
		color=registerColor("Color","Color");
    }

    public void onRender() {
		GSColor c=color.getValue();
        if (PotionEffects.getValue()){
            count = 0;
            try {
                mc.player.getActivePotionEffects().forEach(effect -> {
                    String name = I18n.format(effect.getPotion().getName());
                    double duration = effect.getDuration() / TpsUtils.getTickRate();
                    int amplifier = effect.getAmplifier() + 1;
                    double p1 = duration % 60;
                    double p2 = duration / 60;
                    double p3 = p2 % 60;
                    String minutes = format1.format(p3);
                    String seconds = format2.format(p1);
                    String s = name + " " + amplifier + ChatFormatting.GRAY + " " + minutes + ":" + seconds;
                    if (psortUp.getValue()) {
                        if (pright.getValue()) {
                            FontUtils.drawStringWithShadow(customFont.getValue(), s, potionx.getValue() - FontUtils.getStringWidth(customFont.getValue(),s),potiony.getValue() + (count * 10), c);
                        } else {
                            FontUtils.drawStringWithShadow(customFont.getValue(), s, potionx.getValue(), potiony.getValue() + (count * 10), c);
                        }
                        count++;
                    } else {
                        if (pright.getValue()) {
                            FontUtils.drawStringWithShadow(customFont.getValue(), s, potionx.getValue() - FontUtils.getStringWidth(customFont.getValue(),s),  potiony.getValue() + (count * -10), c);
                        } else {
                            FontUtils.drawStringWithShadow(customFont.getValue(), s, potionx.getValue(), potiony.getValue() + (count * -10), c);
                        }
                        count++;
                    }
                });
            } catch(NullPointerException e){e.printStackTrace();}
        }

        if (Watermark.getValue()) {
            FontUtils.drawStringWithShadow(customFont.getValue(), "GameSense " + GameSenseMod.MODVER, 0, 0, c);
        }

        if (Welcomer.getValue()) {
            FontUtils.drawStringWithShadow(customFont.getValue(), "Hello " + mc.player.getName() + " :^)", welcomex.getValue(), welcomey.getValue(), c);
        }

        if (Inventory.getValue()) {
            drawInventory(inventoryX.getValue(), inventoryY.getValue());
        }

		GSColor on = new GSColor(0, 255, 0);
        GSColor off = new GSColor(255, 0, 0);
        if (GameSenseInfo.getValue()) {
            if (Type.getValue().equalsIgnoreCase("PvP")) {
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
                if (Type.getValue().equalsIgnoreCase("PvP")) {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "gamesense.cc", infox.getValue(), infoy.getValue(), c);
                    if (players != null && mc.player.getDistance(players) <= AutoCrystal.range.getValue()) {
                        FontUtils.drawStringWithShadow(customFont.getValue(), "HTR", infox.getValue(), infoy.getValue() + 10, on);
                    } else {
                        FontUtils.drawStringWithShadow(customFont.getValue(), "HTR", infox.getValue(), infoy.getValue() + 10, off);
                    }
                    if (players != null && mc.player.getDistance(players) <= AutoCrystal.placeRange.getValue()) {
                        FontUtils.drawStringWithShadow(customFont.getValue(), "PLR", infox.getValue(), infoy.getValue() + 20, on);
                    } else {
                        FontUtils.drawStringWithShadow(customFont.getValue(), "PLR", infox.getValue(), infoy.getValue() + 20, off);
                    }
                    if (totems > 0 && ModuleManager.isModuleEnabled("AutoTotem")) {
                        FontUtils.drawStringWithShadow(customFont.getValue(), totems + "", infox.getValue(), infoy.getValue() + 30, on);
                    } else {
                        FontUtils.drawStringWithShadow(customFont.getValue(), totems + "", infox.getValue(), infoy.getValue() + 30, off);
                    }

                    if (getPing() > 100) {
                        FontUtils.drawStringWithShadow(customFont.getValue(), "PING " + getPing(), infox.getValue(), infoy.getValue() + 40, off);
                    } else {
                        FontUtils.drawStringWithShadow(customFont.getValue(), "PING " + getPing(), infox.getValue(), infoy.getValue() + 40, on);

                    }
                    for (final EntityPlayer e : entities) {
                        int i = 0;
                        for (final BlockPos add : this.surroundOffset) {
                            ++i;
                            final BlockPos o = new BlockPos(e.getPositionVector().x, e.getPositionVector().y, e.getPositionVector().z).add(add.getX(), add.getY(), add.getZ());
                            if (mc.world.getBlockState(o).getBlock() == Blocks.OBSIDIAN) {
                                if (i == 1 && a.canPlaceCrystal(o.north(1).down())) {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), "LBY", infox.getValue(), infoy.getValue() + 50, on);
                                }
                                if (i == 2 && a.canPlaceCrystal(o.east(1).down())) {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), "LBY", infox.getValue(), infoy.getValue() + 50, on);
                                }
                                if (i == 3 && a.canPlaceCrystal(o.south(1).down())) {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), "LBY", infox.getValue(), infoy.getValue() + 50, on);
                                }
                                if (i == 4 && a.canPlaceCrystal(o.west(1).down())) {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), "LBY", infox.getValue(), infoy.getValue() + 50, on);
                                }
                            } else
                                FontUtils.drawStringWithShadow(customFont.getValue(), "LBY", infox.getValue(), infoy.getValue() + 50, off);
                        }
                    }
                }
            } else if (Type.getValue().equalsIgnoreCase("Combat")) {
                FontUtils.drawStringWithShadow(customFont.getValue(), " ", infox.getValue(), infoy.getValue(), c);
                if (ModuleManager.isModuleEnabled("AutoCrystalGS")) {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "AC: ENBL", infox.getValue(), infoy.getValue(), on);
                } else {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "AC: DSBL", infox.getValue(), infoy.getValue(), off);
                }
                if (ModuleManager.isModuleEnabled("KillAura")) {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "KA: ENBL", infox.getValue(), infoy.getValue() + 10, on);
                } else {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "KA: DSBL", infox.getValue(), infoy.getValue() + 10, off);
                }
                if (ModuleManager.isModuleEnabled("AutoFeetPlace")) {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "FP: ENBL", infox.getValue(), infoy.getValue() + 20, on);
                } else {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "FP: DSBL", infox.getValue(), infoy.getValue() + 20, off);
                }
                if (ModuleManager.isModuleEnabled("AutoTrap")) {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "AT: ENBL", infox.getValue(), infoy.getValue() + 30, on);
                } else {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "AT: DSBL", infox.getValue(), infoy.getValue() + 30, off);
                }
                if (ModuleManager.isModuleEnabled("SelfTrap")) {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "ST: ENBL", infox.getValue(), infoy.getValue() + 40, on);
                } else {
                    FontUtils.drawStringWithShadow(customFont.getValue(), "ST: DSBL", infox.getValue(), infoy.getValue() + 40, off);
                }
            }
        }

        if (ArrayList.getValue()) {

                if(sortUp.getValue()){ sort = -1;
                } else { sort = 1; }
                modCount = 0;
                ModuleManager.getModules()
                        .stream()
                        .filter(Module::isEnabled)
                        .filter(Module::isDrawn)
                        .sorted(Comparator.comparing(module -> FontUtils.getStringWidth(customFont.getValue(), module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo()) * (-1)))
                        .forEach(m -> {
							GSColor col=c;
                            if(sortUp.getValue()) {
                                if (right.getValue()) {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), m.getName() + ChatFormatting.GRAY  + m.getHudInfo(), arrayx.getValue() - FontUtils.getStringWidth(customFont.getValue(), m.getName() + ChatFormatting.GRAY + m.getHudInfo()), arrayy.getValue() + (modCount * 10), col);
                                } else {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), m.getName() + ChatFormatting.GRAY  + m.getHudInfo(), arrayx.getValue(), arrayy.getValue() + (modCount * 10), col);
                                }
                                modCount++;
                            } else {
                                if (right.getValue()) {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), m.getName() + ChatFormatting.GRAY  + m.getHudInfo(), arrayx.getValue() - FontUtils.getStringWidth(customFont.getValue(),m.getName() + ChatFormatting.GRAY + " " + m.getHudInfo()), arrayy.getValue() + (modCount * -10), col);
                                } else {
                                    FontUtils.drawStringWithShadow(customFont.getValue(), m.getName() + ChatFormatting.GRAY  + m.getHudInfo(), arrayx.getValue(), arrayy.getValue() + (modCount * -10), col);
                                }
                                modCount++;
                            }
							if (color.getRainbow()) col=GSColor.fromHSB(col.getHue()+.02f,col.getSaturation(),col.getBrightness());
                        });
            }

            if (ArmorHud.getValue()) {

                GlStateManager.enableTexture2D();

                ScaledResolution resolution = new ScaledResolution(mc);
                int i = resolution.getScaledWidth() / 2;
                int iteration = 0;
                int y = resolution.getScaledHeight() - 55 - (mc.player.isInWater() ? 10 : 0);
                for (ItemStack is : mc.player.inventory.armorInventory) {
                    iteration++;
                    if (is.isEmpty()) continue;
                    int x = i - 90 + (9 - iteration) * 20 + 2;
                    GlStateManager.enableDepth();

                    itemRender.zLevel = 200F;
                    itemRender.renderItemAndEffectIntoGUI(is, x, y);
                    itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
                    itemRender.zLevel = 0F;

                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();

                    String s = is.getCount() > 1 ? is.getCount() + "" : "";
                    mc.fontRenderer.drawStringWithShadow(s, x + 19 - 2 - mc.fontRenderer.getStringWidth(s), y + 9, new GSColor(255,255,255).getRGB());
                        float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                        float red = 1 - green;
                        int dmg = 100 - (int) (red * 100);
                        FontUtils.drawStringWithShadow(customFont.getValue(), dmg + "", x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2, y - 11, new GSColor((int) (red * 255), (int) (green * 255), 0));
                }

                GlStateManager.enableDepth();
                GlStateManager.disableLighting();
            }

        }

        public void drawInventory ( int x, int y){
            if (Inventory.getValue()) {
                GlStateManager.enableAlpha();
                mc.renderEngine.bindTexture(resource);
                GlStateManager.color(1, 1, 1, 1);
                mc.ingameGUI.drawTexturedModalRect(x, y, 7, 17, 162, 54);
                GlStateManager.disableAlpha();

                GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
                NonNullList<ItemStack> items = Minecraft.getMinecraft().player.inventory.mainInventory;
                for (int size = items.size(), item = 9; item < size; ++item) {
                    final int slotX = x + 1 + item % 9 * 18;
                    final int slotY = y + 1 + (item / 9 - 1) * 18;
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(items.get(item), slotX, slotY);
                    mc.getRenderItem().renderItemOverlays(mc.fontRenderer, items.get(item), slotX, slotY);
                    RenderHelper.disableStandardItemLighting();
                }
            }
        }

        public int getPing () {
            int p = -1;
            if (mc.player == null || mc.getConnection() == null || mc.getConnection().getPlayerInfo(mc.player.getName()) == null) {
                p = -1;
            } else {
                p = mc.getConnection().getPlayerInfo(mc.player.getName()).getResponseTime();
            }
            return p;
        }
}
