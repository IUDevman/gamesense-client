package com.gamesense.client.clickgui.frame;

import com.gamesense.client.module.modules.hud.ClickGuiModule;
import net.minecraft.client.gui.Gui;

public class FrameCap extends Component {

    private final Frames parent1;
    private int x1;
    private int y1;
    private int offset1;

    public FrameCap(Frames frames, int tY) {
        this.parent1 = frames;
        this.x1 = frames.getX() + frames.getWidth();
        this.y1 = frames.getY() + tY;
        this.offset1 = tY;
    }

    @Override
    public void renderComponent(){
        Gui.drawRect(this.parent1.getX(), this.parent1.getY() + this.offset1, this.parent1.getX() + this.parent1.getWidth(), this.parent1.getY() + this.offset1 + 1, ClickGuiModule.outlineColor.getValue().getRGB());
    }
}