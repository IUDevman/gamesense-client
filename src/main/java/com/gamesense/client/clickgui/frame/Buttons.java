package com.gamesense.client.clickgui.frame;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.color.Rainbow;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.clickgui.ClickGUI;
import com.gamesense.client.clickgui.buttons.*;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.hud.ClickGuiModule;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.hud.HUD;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;

public class Buttons extends Component {

    public Module mod;
    public Frames parent;
    public int offset;
    private boolean isHovered;
    private final ArrayList<Component> subcomponents;
    public boolean open;
    private final int height;

    private static final ResourceLocation opengui = new ResourceLocation("minecraft:opengui.png");
    private static final ResourceLocation closedgui = new ResourceLocation("minecraft:closedgui.png");

    public Buttons(final Module mod, final Frames parent, final int offset) {

        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.subcomponents = new ArrayList<Component>();
        this.open = false;
        this.height = 16;
        int opY = offset + 16;
        if (GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod) != null && !GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod).isEmpty()) {
            for (final Setting s : GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod)) {
                switch (s.getType()) {
                    case MODE: {
                        this.subcomponents.add(new ModeComponent((Setting.Mode)s, this, mod, opY));
                        opY += 16;
                        continue;
                    }
                    case BOOLEAN: {
                        this.subcomponents.add(new BooleanComponent((Setting.Boolean)s, this, opY));
                        opY += 16;
                        continue;
                    }
                    case DOUBLE: {
                        this.subcomponents.add(new DoubleComponent((Setting.Double)s, this, opY));
                        opY += 16;
                        continue;
                    }
                    case INT: {
                        this.subcomponents.add(new IntegerComponent((Setting.Integer)s, this, opY));
                        opY += 16;
                        continue;
                    }
                }
            }
        }
        this.subcomponents.add(new KeybindComponent(this, opY));
    }

    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
        int opY = this.offset + 16;
        for (final Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 16;
        }
    }

    @Override
    public void renderComponent() {
        if (ColorMain.rainbow.getValue()){
            ClickGUI.color = Rainbow.getColorWithOpacity(ClickGuiModule.opacity.getValue()).getRGB();
        }
        else {
            ClickGUI.color = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), ClickGuiModule.opacity.getValue()).getRGB();
        }
        Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset + 1, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + 16 + this.offset, this.isHovered ? (this.mod.isEnabled() ? ClickGUI.color : new Color(195, 195, 195, ClickGuiModule.opacity.getValue() -50).darker().darker().getRGB()) : (this.mod.isEnabled() ? ClickGUI.color : new Color(195, 195, 195, ClickGuiModule.opacity.getValue()-50).getRGB()));
        Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + this.offset + 1, new Color(195, 195, 195, ClickGuiModule.opacity.getValue()-50).getRGB());
        FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.mod.getName(), this.parent.getX() + 2, this.parent.getY() + this.offset + 2 + 2, -1);
        if (this.subcomponents.size() > 1) {
            if (ClickGuiModule.icon.getValue().equalsIgnoreCase("Image")) {
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.open ? "" : "", this.parent.getX() + this.parent.getWidth() - 10, this.parent.getY() + this.offset + 2 + 2, -1);
                if (this.open) {
                    //gif texture
                    drawOpenRender(this.parent.getX() + this.parent.getWidth() - 13, this.parent.getY() + this.offset + 2 + 2);
                } else {
                    //static texture
                    drawClosedRender(this.parent.getX() + this.parent.getWidth() - 13, this.parent.getY() + this.offset + 2 + 2);
                }
            }
            else {
                FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.open ? "~" : ">", this.parent.getX() + this.parent.getWidth() - 10, this.parent.getY() + this.offset + 2 + 2, -1);
            }
        }
        if (this.open && !this.subcomponents.isEmpty()) {
            for (final Component comp : this.subcomponents) {
                comp.renderComponent();
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            return 16 * (this.subcomponents.size() + 1);
        }
        return 16;
    }

    @Override
    public void updateComponent(final int mouseX, final int mouseY) {
        this.isHovered = this.isMouseOnButton(mouseX, mouseY);
        if (!this.subcomponents.isEmpty()) {
            for (final Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        if (this.isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (this.isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (final Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        for (final Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(final char typedChar, final int key) {
        for (final Component comp : this.subcomponents) {
            comp.keyTyped(typedChar, key);
        }
    }

    public boolean isMouseOnButton(final int x, final int y) {
        return x > this.parent.getX() && x < this.parent.getX() + this.parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 16 + this.offset;
    }

    public void drawOpenRender(int x, int y){
        GlStateManager.enableAlpha();
        this.mc.getTextureManager().bindTexture(opengui);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPushMatrix();
        Gui.drawScaledCustomSizeModalRect(x,y,0,0,256,256,10,10,256,256);
        GL11.glPopMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void drawClosedRender(int x, int y){
        GlStateManager.enableAlpha();
        this.mc.getTextureManager().bindTexture(closedgui);
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPushMatrix();
        Gui.drawScaledCustomSizeModalRect(x,y,0,0,256,256,10,10,256,256);
        GL11.glPopMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
    }
}
