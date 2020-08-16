package com.gamesense.client.clickgui;

import com.gamesense.api.util.color.Rainbow;
import com.gamesense.client.clickgui.frame.Component;
import com.gamesense.client.clickgui.frame.Frames;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.hud.ClickGuiModule;
import com.gamesense.client.module.modules.hud.ColorMain;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {
    public static ArrayList<Frames> frames;
    public static int color;

    public ClickGUI(){
        ClickGUI.frames = new ArrayList<Frames>();
        int DevFrameX = 5;
        for (final Module.Category category : Module.Category.values()){
            final Frames devframe = new Frames(category);
           devframe.setX(DevFrameX);
           ClickGUI.frames.add(devframe);
           DevFrameX += devframe.getWidth() + 10;
        }
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks){
        if (ColorMain.Rainbow.getValue()){
            ClickGUI.color = Rainbow.getColorWithOpacity(ClickGuiModule.opacity.getValue()).getRGB();
        }
        else {
            ClickGUI.color = new Color(ColorMain.Red.getValue(), ColorMain.Green.getValue(), ColorMain.Blue.getValue(), ClickGuiModule.opacity.getValue()).getRGB();
        }
        for (final Frames frames : ClickGUI.frames){
            frames.renderGUIFrame(this.fontRenderer);
            frames.updatePosition(mouseX, mouseY);
            for (final Component comp : frames.getComponents()){
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException{
        for (final Frames frames : ClickGUI.frames){
            if (frames.isWithinHeader(mouseX, mouseY) && mouseButton == 0){
                frames.setDrag(true);
                frames.dragX = mouseX - frames.getX();
                frames.dragY = mouseY - frames.getY();
            }
            if (frames.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                frames.setOpen(!frames.isOpen());
            }
            if (frames.isOpen() && !frames.getComponents().isEmpty()){
                for (final Component component : frames.getComponents()){
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    protected void mouseReleased(final int mouseX, final int mouseY, final int state){
        for (final Frames frames : ClickGUI.frames){
            frames.setDrag(false);
        }
        for (final Frames frames : ClickGUI.frames){
            if (frames.isOpen() && !frames.getComponents().isEmpty()){
                for (final Component component : frames.getComponents()){
                    component.mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

    protected void keyTyped(final char typedChar, final int keyCode){
        for (final Frames frames : ClickGUI.frames){
            if (frames.isOpen() && !frames.getComponents().isEmpty()){
                for (final Component component : frames.getComponents()){
                    component.keyTyped(typedChar, keyCode);
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }

    public boolean doesGuiPauseGame(){
        return false;
    }

    public void initGui(){

    }

    public static Frames getFrameByName(String name){
        Frames pa = null;

        for(Frames frames : getFrames()){
            if(name.equalsIgnoreCase(String.valueOf(frames.category))) pa = frames;
        }
        return pa;
    }

    public static ArrayList<Frames> getFrames(){
        return frames;
    }

    static{
        ClickGUI.color = -1;
    }
}
