package com.gamesense.client.clickgui;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.*;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.hud.HUDClickGUI;
import com.lukflug.panelstudio.hud.HUDPanel;
import com.lukflug.panelstudio.mc12.GLInterface;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;
import com.lukflug.panelstudio.settings.*;
import com.lukflug.panelstudio.theme.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GameSenseGUI extends MinecraftHUDGUI {

    public static final int WIDTH = 100, HEIGHT = 12, DISTANCE = 10, HUD_BORDER = 2;
    private final Toggleable colorToggle;
    public final GUIInterface guiInterface;
    public final HUDClickGUI gui;
    private final Theme theme, gameSenseTheme, clearTheme, clearGradientTheme;

    public GameSenseGUI() {
        ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        ColorScheme scheme = new SettingsColorScheme(clickGuiModule.enabledColor, clickGuiModule.backgroundColor, clickGuiModule.settingBackgroundColor, clickGuiModule.outlineColor, clickGuiModule.fontColor, clickGuiModule.opacity);
        gameSenseTheme = new GameSenseTheme(scheme, HEIGHT, 2, 5);
        clearTheme = new ClearTheme(scheme, false, HEIGHT, 1);
        clearGradientTheme = new ClearTheme(scheme, true, HEIGHT, 1);
        theme = new ThemeMultiplexer() {
            @Override
            protected Theme getTheme() {
                if (clickGuiModule.theme.getValue().equals("2.0")) return clearTheme;
                else if (clickGuiModule.theme.getValue().equals("2.1.2")) return clearGradientTheme;
                else return gameSenseTheme;
            }
        };
        colorToggle = new Toggleable() {
            @Override
            public void toggle() {
                colorMain.colorModel.increment();
            }

            @Override
            public boolean isOn() {
                return colorMain.colorModel.getValue().equals("HSB");
            }
        };
        guiInterface = new GUIInterface(true) {
            @Override
            public void drawString(Point pos, String s, Color c) {
                GLInterface.end();
                int x = pos.x + 2, y = pos.y + 1;
                if (!colorMain.customFont.getValue()) {
                    x += 1;
                    y += 1;
                }
                FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), s, x, y, new GSColor(c));
                GLInterface.begin();
            }

            @Override
            public int getFontWidth(String s) {
                return Math.round(FontUtil.getStringWidth(colorMain.customFont.getValue(), s)) + 4;
            }

            @Override
            public int getFontHeight() {
                return Math.round(FontUtil.getFontHeight(colorMain.customFont.getValue())) + 2;
            }

            @Override
            public String getResourcePrefix() {
                return "gamesense:gui/";
            }
        };
        gui = new HUDClickGUI(guiInterface, null) {
            @Override
            public void handleScroll(int diff) {
                super.handleScroll(diff);
                if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                    for (FixedComponent component : components) {
                        if (!hudComponents.contains(component)) {
                            Point p = component.getPosition(guiInterface);
                            p.translate(0, -diff);
                            component.setPosition(guiInterface, p);
                        }
                    }
                }
            }
        };
        Toggleable hudToggle = new Toggleable() {
            @Override
            public void toggle() {
            }

            @Override
            public boolean isOn() {
                return gui.isOn() && clickGuiModule.showHUD.isOn() || hudEditor;
            }
        };

        for (Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule) module).populate(theme);
                gui.addHUDComponent(new HUDPanel(((HUDModule) module).getComponent(), theme.getPanelRenderer(), module, new SettingsAnimation(clickGuiModule.animationSpeed), hudToggle, HUD_BORDER));
            }
        }
        Point pos = new Point(DISTANCE, DISTANCE);
        for (Category category : Category.values()) {
            DraggableContainer panel = new DraggableContainer(category.name(), null, theme.getPanelRenderer(), new SimpleToggleable(false), new SettingsAnimation(clickGuiModule.animationSpeed), null, new Point(pos), WIDTH) {

                @Override
                protected int getScrollHeight(int childHeight) {
                    if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                        return childHeight;
                    }
                    return Math.min(childHeight, Math.max(HEIGHT * 4, GameSenseGUI.this.height - getPosition(guiInterface).y - renderer.getHeight(open.getValue() != 0) - HEIGHT));
                }
            };
            gui.addComponent(panel);
            pos.translate(WIDTH + DISTANCE, 0);
            for (Module module : ModuleManager.getModulesInCategory(category)) {
                addModule(panel, module);
            }
        }
    }

    private void addModule(CollapsibleContainer panel, Module module) {
        ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        CollapsibleContainer container = new CollapsibleContainer(module.getName(), null, theme.getContainerRenderer(), new SimpleToggleable(false), new SettingsAnimation(clickGuiModule.animationSpeed), module);
        panel.addComponent(container);
        for (Setting property : SettingsManager.getSettingsForModule(module)) {
            if (property instanceof BooleanSetting) {
                container.addComponent(new BooleanComponent(property.getName(), null, theme.getComponentRenderer(), (BooleanSetting) property));
            } else if (property instanceof IntegerSetting) {
                container.addComponent(new NumberComponent(property.getName(), null, theme.getComponentRenderer(), (IntegerSetting) property, ((IntegerSetting) property).getMin(), ((IntegerSetting) property).getMax()));
            } else if (property instanceof DoubleSetting) {
                container.addComponent(new NumberComponent(property.getName(), null, theme.getComponentRenderer(), (DoubleSetting) property, ((DoubleSetting) property).getMin(), ((DoubleSetting) property).getMax()));
            } else if (property instanceof ModeSetting) {
                container.addComponent(new EnumComponent(property.getName(), null, theme.getComponentRenderer(), (ModeSetting) property));
            } else if (property instanceof ColorSetting) {
                container.addComponent(new SyncableColorComponent(theme, (ColorSetting) property, colorToggle, new SettingsAnimation(clickGuiModule.animationSpeed)));
            }
        }
        container.addComponent(new GameSenseKeybind(theme.getComponentRenderer(), module));
    }

    public static void renderItem(ItemStack item, Point pos) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getRenderItem().zLevel = -150.0f;
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, pos.x, pos.y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, item, pos.x, pos.y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GLInterface.begin();
    }

    public static void renderEntity(EntityLivingBase entity, Point pos, int scale) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GuiInventory.drawEntityOnScreen(pos.x, pos.y, scale, 28, 60, entity);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GLInterface.begin();
    }

    @Override
    protected HUDClickGUI getHUDGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return guiInterface;
    }

    @Override
    protected int getScrollSpeed() {
        return ModuleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue();
    }
}