package com.gamesense.client.clickgui.buttons;

import java.math.RoundingMode;
import java.math.BigDecimal;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.GSColor;
import com.gamesense.client.clickgui.ClickGUI;
import com.gamesense.client.clickgui.frame.Buttons;
import com.gamesense.client.clickgui.frame.Component;
import com.gamesense.client.module.modules.hud.ClickGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;

public class ColorComponent extends Component {

    private boolean hovered;
    private final Setting.ColorSetting set;
    private final Buttons parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging;
    
    public ColorComponent(final Setting.ColorSetting value, final Buttons button, final int offset) {
        this.dragging = false;
        this.set = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }
    
    @Override
    public void renderComponent() {
        ClickGUI.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 80, ClickGUI.getTransColor(hovered));
		if (set.getRainbow()) ClickGUI.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1+16, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 32, set.getValue());
        ClickGUI.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 1, ClickGUI.getTransColor(false));
		FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.set.getName(), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4, ClickGUI.getFontColor());
		FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Rainbow", this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+16, ClickGUI.getFontColor());
		
		double renderWidthR,renderWidthG,renderWidthB;
		if (ColorMain.colorModel.getValue().equalsIgnoreCase("RGB")) {
			renderWidthR = 100 * set.getColor().getRed() / 255.0;
			renderWidthG = 100 * set.getColor().getGreen() / 255.0;
			renderWidthB = 100 * set.getColor().getBlue() / 255.0;
		} else {
			renderWidthR = 100 * set.getColor().getHue();
			renderWidthG = 100 * set.getColor().getSaturation();
			renderWidthB = 100 * set.getColor().getBrightness();
		}
		ClickGUI.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1+32, this.parent.parent.getX() + (int)renderWidthR, this.parent.parent.getY() + this.offset + 48, set.getValue());
		ClickGUI.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1+48, this.parent.parent.getX() + (int)renderWidthG, this.parent.parent.getY() + this.offset + 64, set.getValue());
		ClickGUI.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 1+64, this.parent.parent.getX() + (int)renderWidthB, this.parent.parent.getY() + this.offset + 80, set.getValue());
		
		if (ColorMain.colorModel.getValue().equalsIgnoreCase("RGB")) {
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Red: " + this.set.getColor().getRed(), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+32, ClickGUI.getFontColor());
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Green: " + this.set.getColor().getGreen(), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+48, ClickGUI.getFontColor());
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Blue: " + this.set.getColor().getBlue(), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+64, ClickGUI.getFontColor());
		} else {
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Hue: " + (int)(this.set.getColor().getHue()*360), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+32, ClickGUI.getFontColor());
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Saturation: " + (int)(this.set.getColor().getSaturation()*100), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+48, ClickGUI.getFontColor());
			FontUtils.drawStringWithShadow(HUD.customFont.getValue(), ChatFormatting.GRAY + "Brightness: " + (int)(this.set.getColor().getBrightness()*100), this.parent.parent.getX() + 2, this.parent.parent.getY() + this.offset + 4+64, ClickGUI.getFontColor());
		}
    }
    
    @Override
    public void setOff(final int newOff) {
        this.offset = newOff;
    }
    
    @Override
    public void updateComponent(final int mouseX, final int mouseY) {
        this.hovered = (this.isMouseOnButtonD(mouseX, mouseY) || this.isMouseOnButtonI(mouseX, mouseY));
        this.y = this.parent.parent.getY() + this.offset;
        this.x = this.parent.parent.getX();
        final double diff = Math.min(100, Math.max(0, mouseX - this.x));
        if (this.dragging) {
			GSColor c=set.getColor();
			if (ColorMain.colorModel.getValue().equalsIgnoreCase("RGB")) {
				int newValue;
				if (diff == 0.0) newValue=0;
				else newValue = (int)roundToPlace(diff/100.0*255, 2);
				if (mouseY-this.y>=32 && mouseY-this.y<48) {
					set.setValue(set.getRainbow(),new GSColor(newValue,c.getGreen(),c.getBlue()));
				} else if (mouseY-this.y>=48 && mouseY-this.y<64) {
					set.setValue(set.getRainbow(),new GSColor(c.getRed(),newValue,c.getBlue()));
				} else if (mouseY-this.y>=64 && mouseY-this.y<80) {
					set.setValue(set.getRainbow(),new GSColor(c.getRed(),c.getGreen(),newValue));
				}
			} else {
				float newValue=(float)(diff/100.0);
				if (mouseY-this.y>=32 && mouseY-this.y<48) {
					set.setValue(set.getRainbow(),GSColor.fromHSB(newValue,c.getSaturation(),c.getBrightness()));
				} else if (mouseY-this.y>=48 && mouseY-this.y<64) {
					set.setValue(set.getRainbow(),GSColor.fromHSB(c.getHue(),newValue,c.getBrightness()));
				} else if (mouseY-this.y>=64 && mouseY-this.y<80) {
					set.setValue(set.getRainbow(),GSColor.fromHSB(c.getHue(),c.getSaturation(),newValue));
				}
			}
        }
    }
    
    private static double roundToPlace(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        if ((isMouseOnButtonI(mouseX, mouseY)||isMouseOnButtonD(mouseX, mouseY)) && button == 0 && this.parent.open) {
            this.dragging = true;
			if (mouseY-this.y>=16 && mouseY-this.y<32) {
				set.setValue(!set.getRainbow(),set.getColor());
			}
        }
    }
    
    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int mouseButton) {
        this.dragging = false;
    }
    
    public boolean isMouseOnButtonD(final int x, final int y) {
        return x > this.x && x < this.x + (this.parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 80;
    }
    
    public boolean isMouseOnButtonI(final int x, final int y) {
        return x > this.x + this.parent.parent.getWidth() / 2 && x < this.x + this.parent.parent.getWidth() && y > this.y && y < this.y + 80;
    }
}
