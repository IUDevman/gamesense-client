package com.gamesense.client.devgui;

import com.gamesense.api.util.Rainbow;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.ColorMain;
import com.gamesense.client.module.modules.hud.DevGuiModule;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class DevGUI extends GuiScreen {
    public static ArrayList<DevFrame> devframes;
    public static int color;

    public DevGUI(){
        DevGUI.devframes = new ArrayList<DevFrame>();
        int DevFrameX = 5;
        for (final Module.Category category : Module.Category.values()){
            final DevFrame devframe = new DevFrame(category);
           devframe.setX(DevFrameX);
           DevGUI.devframes.add(devframe);
           DevFrameX += devframe.getWidth() + 10;
        }
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks){
        DevGuiModule devGuiModule = ((DevGuiModule) ModuleManager.getModuleByName("DevGUI"));
        ColorMain colorMain = ((ColorMain) ModuleManager.getModuleByName("Colors"));
        if (colorMain.Rainbow.getValue()){
            DevGUI.color = Rainbow.getColorWithOpacity(devGuiModule.opacity.getValue()).getRGB();
        }
        else {
            DevGUI.color = new Color(colorMain.Red.getValue(), colorMain.Green.getValue(), colorMain.Blue.getValue(), devGuiModule.opacity.getValue()).getRGB();
        }
        for (final DevFrame devFrame : DevGUI.devframes){
            devFrame.renderDevFrame(this.fontRenderer);
            devFrame.updatePosition(mouseX, mouseY);
            for (final DevComponent comp : devFrame.getComponents()){
                comp.updateComponent(mouseX, mouseY);
            }
        }
    }

    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException{
        for (final DevFrame devFrame : DevGUI.devframes){
            if (devFrame.isWithinHeader(mouseX, mouseY) && mouseButton == 0){
                devFrame.setDrag(true);
                devFrame.dragX = mouseX - devFrame.getX();
                devFrame.dragY = mouseY - devFrame.getY();
            }
            if (devFrame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                devFrame.setOpen(!devFrame.isOpen());
            }
            if (devFrame.isOpen() && !devFrame.getComponents().isEmpty()){
                for (final DevComponent devComponent : devFrame.getComponents()){
                    devComponent.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    protected void mouseReleased(final int mouseX, final int mouseY, final int state){
        for (final DevFrame devFrame : DevGUI.devframes){
            devFrame.setDrag(false);
        }
        for (final DevFrame devFrame : DevGUI.devframes){
            if (devFrame.isOpen() && !devFrame.getComponents().isEmpty()){
                for (final DevComponent devComponent : devFrame.getComponents()){
                    devComponent.mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

    protected void keyTyped(final char typedChar, final int keyCode){
        for (final DevFrame devFrame : DevGUI.devframes){
            if (devFrame.isOpen() && !devFrame.getComponents().isEmpty()){
                for (final DevComponent devComponent : devFrame.getComponents()){
                    devComponent.keyTyped(typedChar, keyCode);
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen)null);
        }
    }

    public boolean doesGuiPauseGame(){
        return false;
    }

    public void initGui(){

    }

    public static DevFrame getFrameByName(String name){
        DevFrame pa = null;

        for(DevFrame devFrame : getFrames()){
            if(name.equalsIgnoreCase(String.valueOf(devFrame.category))) pa = devFrame;
        }
        return pa;
    }

    public static ArrayList<DevFrame> getFrames(){
        return devframes;
    }

    static{
        DevGUI.color = -1;
    }
}
