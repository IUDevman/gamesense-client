package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import org.lwjgl.input.Keyboard;

public class DevGuiModule extends Module {
    public DevGuiModule INSTANCE;
    public DevGuiModule(){
        super("DevGUI", Category.HUD);
        setBind(Keyboard.KEY_O);
        setDrawn(false);
        INSTANCE = this;
    }

    public Setting.b customFont;
    public static Setting.i opacity;

    public void setup(){
        opacity = this.registerI("Opacity",200,50,255);
    }

    public void onEnable(){
        mc.displayGuiScreen(GameSenseMod.getInstance().devGUI);
        if(((Announcer) ModuleManager.getModuleByName("Announcer")).clickGui.getValue() && ModuleManager.isModuleEnabled("Announcer") && mc.player != null)
            if(((Announcer)ModuleManager.getModuleByName("Announcer")).clientSide.getValue()){
                Command.sendClientMessage(Announcer.guiMessage);
            } else {
                mc.player.sendChatMessage(Announcer.guiMessage);
            }
    }


    private void drawStringWithShadow(String text, int x, int y, int color){
        if(customFont.getValue())
            GameSenseMod.fontRenderer.drawStringWithShadow(text, x, y, color);
        else
            mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void onDisable(){
        this.enable();
    }
}
