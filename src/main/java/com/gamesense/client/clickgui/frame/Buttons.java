package com.gamesense.client.clickgui.frame;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.clickgui.buttons.*;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

public class Buttons extends Component {
	public Module mod;
	public Frames parent;
	public int offset;
	private boolean isHovered;
	private final ArrayList<Component> subcomponents;
	public boolean open;
	private final int height;

	public Buttons(final Module mod, final Frames parent, final int offset) {

		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<Component>();
		this.open = false;
		int opY = offset + 16;
		if (GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod) != null && !GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod).isEmpty()) {
			for (final Setting s : GameSenseMod.getInstance().settingsManager.getSettingsForMod(mod)) {
				switch (s.getType()) {
					case MODE:
						this.subcomponents.add(new ModeComponent((Setting.Mode)s, this, mod, opY));
						break;
					case BOOLEAN:
						this.subcomponents.add(new BooleanComponent((Setting.Boolean)s, this, opY));
						break;
					case DOUBLE:
						this.subcomponents.add(new DoubleComponent((Setting.Double)s, this, opY));
						break;
					case INT:
						this.subcomponents.add(new IntegerComponent((Setting.Integer)s, this, opY));
						break;
					case COLOR:
						this.subcomponents.add(new ColorComponent((Setting.ColorSetting)s, this, opY));
						opY+=80;
						break;
				}
				opY += 16;
			}
		}
		this.height=opY+16-offset;
		this.subcomponents.add(new KeybindComponent(this, this.height));
	}

	@Override
	public void setOff(final int newOff) {
		this.offset = newOff;
		int opY = this.offset + 16;
		for (final Component comp : this.subcomponents) {
			comp.setOff(opY);
			if (comp instanceof ColorComponent) opY+=80;
			opY += 16;
		}
	}

	@Override
	public void renderComponent() {
		Renderer.drawModuleBox(this.parent.getX(), this.parent.getY() + this.offset, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + 16 + this.offset, mod.isEnabled()?Renderer.getEnabledColor(isHovered):Renderer.getBackgroundColor(isHovered));
		FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), this.mod.getName(), this.parent.getX() + 2, this.parent.getY() + this.offset + 2 + 2, Renderer.getFontColor());
		if (this.open && !this.subcomponents.isEmpty()) {
			Gui.drawRect(this.parent.getX(), this.parent.getY() + this.offset + 16, this.parent.getX() + this.parent.getWidth(), this.parent.getY() + this.offset + 17, ClickGuiModule.outlineColor.getValue().getRGB());
			for (final Component comp : this.subcomponents) {
				comp.renderComponent();
			}
		}
	}

	@Override
	public int getHeight() {
		if (this.open) {
			return height;
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
}