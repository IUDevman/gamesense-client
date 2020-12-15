package com.gamesense.client.clickgui;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.gamesense.api.config.PositionConfig;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.modules.hud.HUDModule;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.hud.HUDClickGUI;
import com.lukflug.panelstudio.hud.HUDPanel;
import com.lukflug.panelstudio.mc.GLInterface;
import com.lukflug.panelstudio.mc.MinecraftHUDGUI;
import com.lukflug.panelstudio.settings.BooleanComponent;
import com.lukflug.panelstudio.settings.EnumComponent;
import com.lukflug.panelstudio.settings.NumberComponent;
import com.lukflug.panelstudio.settings.SimpleToggleable;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.settings.ToggleableContainer;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.theme.SettingsColorScheme;
import com.lukflug.panelstudio.theme.Theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class GameSenseGUI extends MinecraftHUDGUI {
	public static final int WIDTH=100,HEIGHT=12,DISTANCE=10,HUD_BORDER=2;
	private final Toggleable colorToggle;
	public final GUIInterface guiInterface;
	public final HUDClickGUI gui;
	private final Theme theme;
	
	public GameSenseGUI() {
		theme=new GameSenseTheme(new SettingsColorScheme(ClickGuiModule.enabledColor,ClickGuiModule.backgroundColor,ClickGuiModule.settingBackgroundColor,ClickGuiModule.outlineColor,ClickGuiModule.fontColor,ClickGuiModule.opacity),HEIGHT,2);
		colorToggle=new Toggleable() {
			@Override
			public void toggle() {
				ColorMain.colorModel.increment();
			}
			
			@Override
			public boolean isOn() {
				return ColorMain.colorModel.getValue().equals("HSB");
			}
		};
		guiInterface=new GUIInterface() {
			@Override
			public void drawString(Point pos, String s, Color c) {
				GLInterface.end();
				int x=pos.x+2, y=pos.y+1;
				if (!ColorMain.customFont.getValue()) {
					x+=1;
					y+=1;
				}
				FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(),s,x,y,new GSColor(c));
				GLInterface.begin();
			}
			
			@Override
			public int getFontWidth(String s) {
				return (int)Math.round(FontUtils.getStringWidth(ColorMain.customFont.getValue(),s))+4;
			}

			@Override
			public int getFontHeight() {
				return (int)Math.round(FontUtils.getFontHeight(ColorMain.customFont.getValue()))+2;
			}
			
			@Override
			public String getResourcePrefix() {
				return "gamesense:gui/";
			}
		};
		gui=new HUDClickGUI(guiInterface);
		
		for (Module module: ModuleManager.getModules()) {
			if (module instanceof HUDModule) {
				((HUDModule)module).populate(theme);
				gui.addHUDComponent(new GameSenseHUDPanel(((HUDModule)module).getComponent(),module));
			}
		}
		Point pos=new Point(DISTANCE,DISTANCE);
		for (Module.Category category: Module.Category.values()) {
			DraggableContainer panel=new DraggableContainer(category.name(),theme.getPanelRenderer(),new SimpleToggleable(false),new SettingsAnimation(ClickGuiModule.animationSpeed),new Point(pos),WIDTH) {
				@Override
				protected int getScrollHeight (int childHeight) {
					if (ClickGuiModule.scrolling.getValue().equals("Screen")) {
						return childHeight;
					}
					return Math.min(childHeight,Math.max(HEIGHT*4,GameSenseGUI.this.height-getPosition(guiInterface).y-renderer.getHeight()-HEIGHT));
				}
			};
			gui.addComponent(panel);
			pos.translate(WIDTH+DISTANCE,0);
			for (Module module: ModuleManager.getModulesInCategory(category)) {
				addModule(panel,module);
			}
		}
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX,mouseY,partialTicks);
        int scroll=Mouse.getDWheel();
        if (scroll!=0) {
        	if (ClickGuiModule.scrolling.getValue().equals("Screen")) {
	        	for (FixedComponent component: gui.getComponents()) {
	        		if (!(component instanceof HUDPanel)) {
		        		Point p=component.getPosition(guiInterface);
		        		if (scroll>0) p.translate(0,ClickGuiModule.scrollSpeed.getValue());
		        		else p.translate(0,-ClickGuiModule.scrollSpeed.getValue());
		        		component.setPosition(guiInterface,p);
	        		}
	        	}
        	}
        	if (scroll>0) gui.handleScroll(-ClickGuiModule.scrollSpeed.getValue());
        	else gui.handleScroll(ClickGuiModule.scrollSpeed.getValue());
        }
    }
	
	private void addModule (CollapsibleContainer panel, Module module) {
		CollapsibleContainer container;
		container=new ToggleableContainer(module.getName(),theme.getContainerRenderer(),new SimpleToggleable(false),new SettingsAnimation(ClickGuiModule.animationSpeed),module);
		panel.addComponent(container);
		for (Setting property: GameSenseMod.getInstance().settingsManager.getSettingsForMod(module)) {
			if (property instanceof Setting.Boolean) {
				container.addComponent(new BooleanComponent(property.getName(),theme.getComponentRenderer(),(Setting.Boolean)property));
			} else if (property instanceof Setting.Integer) {
				container.addComponent(new NumberComponent(property.getName(),theme.getComponentRenderer(),(Setting.Integer)property,((Setting.Integer)property).getMin(),((Setting.Integer)property).getMax()));
			} else if (property instanceof Setting.Double) {
				container.addComponent(new NumberComponent(property.getName(),theme.getComponentRenderer(),(Setting.Double)property,((Setting.Double)property).getMin(),((Setting.Double)property).getMax()));
			} else if (property instanceof Setting.Mode) {
				container.addComponent(new EnumComponent(property.getName(),theme.getComponentRenderer(),(Setting.Mode)property));
			} else if (property instanceof Setting.ColorSetting) {
				container.addComponent(new SyncableColorComponent(theme,(Setting.ColorSetting)property,colorToggle,new SettingsAnimation(ClickGuiModule.animationSpeed)));
			}
		}
		container.addComponent(new GameSenseKeybind(theme.getComponentRenderer(),module));
	}
	
	public static void renderItem (ItemStack item, Point pos) {
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
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item,pos.x,pos.y);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer,item,pos.x,pos.y);
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GLInterface.begin();
	}
	
	public static void renderEntity (EntityLivingBase entity, Point pos) {
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
		GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPopAttrib();
		GlStateManager.enableDepth();
		GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1,1,1,1);
        GuiInventory.drawEntityOnScreen(pos.x,pos.y,43,28,60,entity);
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
		return ClickGuiModule.scrollSpeed.getValue();
	}
	
	
	private class GameSenseHUDPanel extends HUDPanel implements PositionConfig {
		public GameSenseHUDPanel (FixedComponent component, Toggleable module) {
			super(component,theme.getPanelRenderer(),module,new SettingsAnimation(ClickGuiModule.animationSpeed),new Toggleable() {
				@Override
				public void toggle() {
				}
	
				@Override
				public boolean isOn() {
					return gui.isOn() && ClickGuiModule.showHUD.isOn();
				}
				
			},HUD_BORDER);
		}

		@Override
		public Point getConfigPos() {
			if (component instanceof PositionConfig) {
				return ((PositionConfig)component).getConfigPos();
			} else {
				return getPosition(guiInterface);
			}
		}

		@Override
		public void setConfigPos(Point pos) {
			if (component instanceof PositionConfig) {
				((PositionConfig)component).setConfigPos(pos);
			} else {
				setPosition(guiInterface,pos);
			}
		}
	}
}
