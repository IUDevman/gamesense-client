package com.gamesense.client.clickgui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.ClickGUI;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.settings.BooleanComponent;
import com.lukflug.panelstudio.settings.EnumComponent;
import com.lukflug.panelstudio.settings.NumberComponent;
import com.lukflug.panelstudio.settings.SimpleToggleable;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.settings.ToggleableContainer;
import com.lukflug.panelstudio.tabgui.DefaultRenderer;
import com.lukflug.panelstudio.tabgui.TabGUI;
import com.lukflug.panelstudio.tabgui.TabGUIContainer;
import com.lukflug.panelstudio.tabgui.TabGUIItem;
import com.lukflug.panelstudio.tabgui.TabGUIRenderer;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.theme.Theme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GameSenseGUI extends GuiScreen implements Interface {
	public static final int WIDTH=100,HEIGHT=12,DISTANCE=10;
	private final Toggleable colorToggle;
	public final ClickGUI gui;
	private Theme theme;
	private Point mouse=new Point();
	private boolean lButton=false,rButton=false;
	
	// Stuff for glScissor to use
    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
	private static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer COORDS = GLAllocation.createDirectFloatBuffer(3);
    private Stack<Rectangle> clipRect=new Stack<Rectangle>();
	
	public GameSenseGUI() {
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
		ColorScheme scheme=new GameSenseScheme();
		theme=new GameSenseTheme(scheme,HEIGHT,2);
		
		Point pos=new Point(DISTANCE,DISTANCE);
		gui=new ClickGUI(this);
		TabGUIRenderer tabrenderer=new DefaultRenderer(new ColorScheme() {
			@Override
			public Color getActiveColor() {
				return ClickGuiModule.enabledColor.getValue();
			}

			@Override
			public Color getInactiveColor() {
				return ClickGuiModule.backgroundColor.getValue();
			}

			@Override
			public Color getBackgroundColor() {
				return ClickGuiModule.settingBackgroundColor.getValue();
			}

			@Override
			public Color getOutlineColor() {
				return ClickGuiModule.backgroundColor.getValue();
			}

			@Override
			public Color getFontColor() {
				return ClickGuiModule.fontColor.getValue();
			}

			@Override
			public int getOpacity() {
				return ClickGuiModule.opacity.getValue();
			}
		},HEIGHT,5,Keyboard.KEY_UP,Keyboard.KEY_DOWN,Keyboard.KEY_LEFT,Keyboard.KEY_RIGHT,Keyboard.KEY_RETURN);
		TabGUI tabgui=new TabGUI("TabGUI",tabrenderer,new GameSenseAnimation(),new Point(pos),75);
		gui.addComponent(tabgui);
		for (Module.Category category: Module.Category.values()) {
			DraggableContainer panel=new DraggableContainer(category.name(),theme.getPanelRenderer(),new SimpleToggleable(false),new GameSenseAnimation(),new Point(pos),WIDTH) {
				@Override
				protected int getScrollHeight (int childHeight) {
					if (ClickGuiModule.scrolling.getValue().equals("Screen")) {
						return childHeight;
					}
					return Math.min(childHeight,Math.max(HEIGHT*4,GameSenseGUI.this.height-getPosition(GameSenseGUI.this).y-renderer.getHeight()-HEIGHT));
				}
			};
			gui.addComponent(panel);
			TabGUIContainer tab=new TabGUIContainer(category.name(),tabrenderer,new GameSenseAnimation());
			tabgui.addComponent(tab);
			pos.translate(WIDTH+DISTANCE,0);
			for (Module module: ModuleManager.getModulesInCategory(category)) {
				addModule(panel,module);
				tab.addComponent(new TabGUIItem(module.getName(),module));
			}
		}
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX,MODELVIEW);
		GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX,PROJECTION);
		GlStateManager.glGetInteger(GL11.GL_VIEWPORT,VIEWPORT);
    	mouse=new Point(mouseX,mouseY);
    	begin();
        gui.render();
        end();
        int scroll=Mouse.getDWheel();
        if (scroll!=0) {
        	if (ClickGuiModule.scrolling.getValue().equals("Screen")) {
	        	for (FixedComponent component: gui.getComponents()) {
	        		Point p=component.getPosition(this);
	        		if (scroll>0) p.translate(0,ClickGuiModule.scrollSpeed.getValue());
	        		else p.translate(0,-ClickGuiModule.scrollSpeed.getValue());
	        		component.setPosition(this,p);
	        	}
        	}
        	if (scroll>0) gui.handleScroll(-ClickGuiModule.scrollSpeed.getValue());
        	else gui.handleScroll(ClickGuiModule.scrollSpeed.getValue());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
    	mouse=new Point(mouseX,mouseY);
    	switch (clickedButton) {
    	case Interface.LBUTTON:
    		lButton=true;
    		break;
    	case Interface.RBUTTON:
    		rButton=true;
    		break;
    	}
    	gui.handleButton(clickedButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    	mouse=new Point(mouseX,mouseY);
    	switch (releaseButton) {
    	case Interface.LBUTTON:
    		lButton=false;
    		break;
    	case Interface.RBUTTON:
    		rButton=false;
    		break;
    	}
    	gui.handleButton(releaseButton);
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) {
    	if (keyCode == 1) {
    		gui.exit();
    		this.mc.displayGuiScreen(null);
    	} else gui.handleKey(keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
	
	@Override
	public Point getMouse() {
		return new Point(mouse);
	}

	@Override
	public boolean getButton(int button) {
		switch (button) {
		case Interface.LBUTTON:
			return lButton;
		case Interface.RBUTTON:
			return rButton;
		}
		return false;
	}

	@Override
	public void drawString(Point pos, String s, Color c) {
		end();
		int x=pos.x+2, y=pos.y+1;
		if (!ColorMain.customFont.getValue()) {
			x+=1;
			y+=1;
		}
		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(),s,x,y,new GSColor(c));
		begin();
	}

	@Override
	public void fillTriangle(Point pos1, Point pos2, Point pos3, Color c1, Color c2, Color c3) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_TRIANGLES,DefaultVertexFormats.POSITION_COLOR);
        	bufferbuilder.pos(pos1.x,pos1.y,zLevel).color(c1.getRed()/255.0f,c1.getGreen()/255.0f,c1.getBlue()/255.0f,c1.getAlpha()/255.0f).endVertex();
        	bufferbuilder.pos(pos2.x,pos2.y,zLevel).color(c2.getRed()/255.0f,c2.getGreen()/255.0f,c2.getBlue()/255.0f,c2.getAlpha()/255.0f).endVertex();
        	bufferbuilder.pos(pos3.x,pos3.y,zLevel).color(c3.getRed()/255.0f,c3.getGreen()/255.0f,c3.getBlue()/255.0f,c3.getAlpha()/255.0f).endVertex();
        tessellator.draw();
	}

	@Override
	public void drawLine(Point a, Point b, Color c1, Color c2) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_LINES,DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(a.x,a.y,zLevel).color(c1.getRed()/255.0f,c1.getGreen()/255.0f,c1.getBlue()/255.0f,c1.getAlpha()/255.0f).endVertex();
			bufferbuilder.pos(b.x,b.y,zLevel).color(c2.getRed()/255.0f,c2.getGreen()/255.0f,c2.getBlue()/255.0f,c2.getAlpha()/255.0f).endVertex();
			tessellator.draw();
	}

	@Override
	public void fillRect(Rectangle r, Color c1, Color c2, Color c3, Color c4) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
	        bufferbuilder.pos(r.x,r.y+r.height,zLevel).color(c4.getRed()/255.0f,c4.getGreen()/255.0f,c4.getBlue()/255.0f,c4.getAlpha()/255.0f).endVertex();
	        bufferbuilder.pos(r.x+r.width,r.y+r.height,zLevel).color(c3.getRed()/255.0f,c3.getGreen()/255.0f,c3.getBlue()/255.0f,c3.getAlpha()/255.0f).endVertex();
	        bufferbuilder.pos(r.x+r.width,r.y,zLevel).color(c2.getRed()/255.0f,c2.getGreen()/255.0f,c2.getBlue()/255.0f,c2.getAlpha()/255.0f).endVertex();
	        bufferbuilder.pos(r.x,r.y,zLevel).color(c1.getRed()/255.0f,c1.getGreen()/255.0f,c1.getBlue()/255.0f,c1.getAlpha()/255.0f).endVertex();
        tessellator.draw();
	}

	@Override
	public void drawRect(Rectangle r, Color c1, Color c2, Color c3, Color c4) {
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
	        bufferbuilder.pos(r.x,r.y+r.height,zLevel).color(c4.getRed()/255.0f,c4.getGreen()/255.0f,c4.getBlue()/255.0f,c4.getAlpha()/255.0f).endVertex();
	        bufferbuilder.pos(r.x+r.width,r.y+r.height,zLevel).color(c3.getRed()/255.0f,c3.getGreen()/255.0f,c3.getBlue()/255.0f,c3.getAlpha()/255.0f).endVertex();
	        bufferbuilder.pos(r.x+r.width,r.y,zLevel).color(c2.getRed()/255.0f,c2.getGreen()/255.0f,c2.getBlue()/255.0f,c2.getAlpha()/255.0f).endVertex();
	        bufferbuilder.pos(r.x,r.y,zLevel).color(c1.getRed()/255.0f,c1.getGreen()/255.0f,c1.getBlue()/255.0f,c1.getAlpha()/255.0f).endVertex();
        tessellator.draw();
	}
	
	@Override
	public synchronized int loadImage(String name) {
		try {
			ResourceLocation rl=new ResourceLocation("gamesense:gui/"+name);
			InputStream stream=Minecraft.getMinecraft().resourceManager.getResource(rl).getInputStream();
			BufferedImage image=ImageIO.read(stream);
			int texture=TextureUtil.glGenTextures();
			TextureUtil.uploadTextureImage(texture,image);
			return texture;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void drawImage(Rectangle r, int rotation, boolean parity, int image) {
		if (image==0) return;
		int texCoords[][]={{0,1},{1,1},{1,0},{0,0}};
		for (int i=0;i<rotation%4;i++) {
			int temp1=texCoords[3][0],temp2=texCoords[3][1];
			texCoords[3][0]=texCoords[2][0];
			texCoords[3][1]=texCoords[2][1];
			texCoords[2][0]=texCoords[1][0];
			texCoords[2][1]=texCoords[1][1];
			texCoords[1][0]=texCoords[0][0];
			texCoords[1][1]=texCoords[0][1];
			texCoords[0][0]=temp1;
			texCoords[0][1]=temp2;
		}
		if (parity) {
			int temp1=texCoords[3][0],temp2=texCoords[3][1];
			texCoords[3][0]=texCoords[0][0];
			texCoords[3][1]=texCoords[0][1];
			texCoords[0][0]=temp1;
			texCoords[0][1]=temp2;
			temp1=texCoords[2][0];
			temp2=texCoords[2][1];
			texCoords[2][0]=texCoords[1][0];
			texCoords[2][1]=texCoords[1][1];
			texCoords[1][0]=temp1;
			texCoords[1][1]=temp2;
		}
		Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.bindTexture(image);
        GlStateManager.enableTexture2D();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        	bufferbuilder.pos(r.x,r.y+r.height,zLevel).tex(texCoords[0][0],texCoords[0][1]).endVertex();
        	bufferbuilder.pos(r.x+r.width,r.y+r.height,zLevel).tex(texCoords[1][0],texCoords[1][1]).endVertex();
        	bufferbuilder.pos(r.x+r.width,r.y,zLevel).tex(texCoords[2][0],texCoords[2][1]).endVertex();
        	bufferbuilder.pos(r.x,r.y,zLevel).tex(texCoords[3][0],texCoords[3][1]).endVertex();
        tessellator.draw();
        GlStateManager.disableTexture2D();
	}
	
	private void scissor (Rectangle r) {
		if (r==null) {
			GL11.glScissor(0,0,0,0);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			return;
		}
		float x1,y1,x2,y2;
		GLU.gluProject(r.x,r.y,zLevel,MODELVIEW,PROJECTION,VIEWPORT,COORDS);
		x1=COORDS.get(0);
		y1=COORDS.get(1);
		GLU.gluProject(r.x+r.width,r.y+r.height,zLevel,MODELVIEW,PROJECTION,VIEWPORT,COORDS);
		x2=COORDS.get(0);
		y2=COORDS.get(1);
		GL11.glScissor(Math.round(Math.min(x1,x2)),Math.round(Math.min(y1,y2)),Math.round(Math.abs(x2-x1)),Math.round(Math.abs(y2-y1)));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	@Override
	public void window (Rectangle r) {
		if (clipRect.isEmpty()) {
			scissor(r);
			clipRect.push(r);
		} else {
			Rectangle top=clipRect.peek();
			if (top==null) {
				scissor(null);
				clipRect.push(null);
			} else {
				int x1,y1,x2,y2;
				x1=Math.max(r.x,top.x);
				y1=Math.max(r.y,top.y);
				x2=Math.min(r.x+r.width,top.x+top.width);
				y2=Math.min(r.y+r.height,top.y+top.height);
				if (x2>x1 && y2>y1) {
					Rectangle rect=new Rectangle(x1,y1,x2-x1,y2-y1);
					scissor(rect);
					clipRect.push(rect);
				} else {
					scissor(null);
					clipRect.push(null);
				}
			}
		}
	}

	@Override
	public void restore() {
		if (!clipRect.isEmpty()) {
			clipRect.pop();
			if (clipRect.isEmpty()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
			else scissor(clipRect.peek());
		}
	}
	
	private void addModule (CollapsibleContainer panel, Module module) {
		CollapsibleContainer container;
		container=new ToggleableContainer(module.getName(),theme.getContainerRenderer(),new SimpleToggleable(false),new GameSenseAnimation(),module);
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
				container.addComponent(new SyncableColorComponent(theme,(Setting.ColorSetting)property,colorToggle,new GameSenseAnimation()));
			}
		}
		container.addComponent(new GameSenseKeybind(theme.getComponentRenderer(),module));
	}
	
	private void begin() {
		GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.glLineWidth(2);
	}
	
	private void end() {
		GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
	
	
	private static class GameSenseScheme implements ColorScheme {
		@Override
		public Color getActiveColor() {
			return ClickGuiModule.enabledColor.getValue();
		}

		@Override
		public Color getInactiveColor() {
			return ClickGuiModule.backgroundColor.getValue();
		}

		@Override
		public Color getBackgroundColor() {
			return ClickGuiModule.settingBackgroundColor.getValue();
		}

		@Override
		public Color getOutlineColor() {
			return ClickGuiModule.outlineColor.getValue();
		}

		@Override
		public Color getFontColor() {
			return ClickGuiModule.fontColor.getValue();
		}

		@Override
		public int getOpacity() {
			return ClickGuiModule.opacity.getValue();
		}		
	}
	
	
	private static class GameSenseAnimation extends Animation {
		@Override
		protected int getSpeed() {
			return ClickGuiModule.animationSpeed.getValue();
		}
	}
}
