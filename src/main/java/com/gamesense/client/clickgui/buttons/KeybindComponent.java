package com.gamesense.client.clickgui.buttons;

import com.gamesense.api.util.font.FontUtils;
import com.gamesense.client.clickgui.frame.Buttons;
import com.gamesense.client.clickgui.frame.Component;
import com.gamesense.client.clickgui.frame.Frames;
import com.gamesense.client.clickgui.frame.Renderer;
import com.gamesense.client.module.modules.hud.ClickGuiModule;
import com.gamesense.client.module.modules.hud.HUD;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import com.mojang.realmsclient.gui.ChatFormatting;

public class KeybindComponent extends Component {
	private boolean hovered;
	private boolean binding;
	private final Buttons parent;
	private int offset;
	private int x;
	private int y;
	
	public KeybindComponent(final Buttons button, final int offset){
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}
	
	@Override
	public void setOff(final int newOff){
		this.offset = newOff;
	}
	
	@Override
	public void renderComponent(){
		Renderer.drawModuleBox(this.parent.parent.getX(), this.parent.parent.getY() + 1 + this.offset, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 15, Renderer.getSettingColor(hovered));
		Gui.drawRect(this.parent.parent.getX(), this.parent.parent.getY() + this.offset + 15, this.parent.parent.getX() + this.parent.parent.getWidth(), this.parent.parent.getY() + this.offset + 16, ClickGuiModule.outlineColor.getValue().getRGB());
		FontUtils.drawStringWithShadow(HUD.customFont.getValue(), this.binding ? "Key..." : ("Key: " + ChatFormatting.GRAY + Keyboard.getKeyName(this.parent.mod.getBind())), (this.parent.parent.getX() + 2), (this.parent.parent.getY() + this.offset + 4), Renderer.getFontColor());
	}
	
	@Override
	public void updateComponent(final int mouseX, final int mouseY){
		this.hovered = this.isMouseOnButton(mouseX, mouseY);
		this.y = this.parent.parent.getY() + this.offset;
		this.x = this.parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(final int mouseX, final int mouseY, final int button){
		if (this.isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open){
			this.binding = !this.binding;
		}
	}
	
	@Override
	public void keyTyped(final char typedChar, final int key){
		if (this.binding){
			if (key == 211){
				this.parent.mod.setBind(0);
			}
			else{
				if (key == Keyboard.KEY_ESCAPE){
					this.binding = false;
				}
				else{
					this.parent.mod.setBind(key);
				}
			}
			this.binding = false;
		}
	}
	
	public boolean isMouseOnButton(final int x, final int y){
		return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 16;
	}
}
