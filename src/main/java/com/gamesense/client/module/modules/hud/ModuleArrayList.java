package com.gamesense.client.module.modules.hud;

import com.gamesense.api.settings.Setting;
import com.gamesense.api.util.font.FontUtils;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;

import java.util.Comparator;

public class ModuleArrayList extends Module {
    public ModuleArrayList(){
        super("ArrayList", Category.HUD);
    }

    Setting.Integer posX;
    Setting.Integer posY;
    Setting.Boolean sortUp;
    Setting.Boolean sortRight;
    Setting.ColorSetting color;

    public void setup(){
        posX = registerInteger("X", "X", 0, 0, 1000);
        posY = registerInteger("Y", "Y", 200, 0, 1000);
        sortUp = registerBoolean("Sort Up", "SortUp", true);
        sortRight = registerBoolean("Sort Right", "SortRight", false);
        color = registerColor("Color", "Color", new GSColor(255, 0, 0, 255));
    }

    private int count;
    private int sort;
    private GSColor adjColor;

    public void onRender(){
        if(sortUp.getValue()){
            sort = -1;
        }
        else {
            sort = 1;
        }
        count = 0;

        adjColor = new GSColor(color.getValue(), 255);

        ModuleManager.getModules()
                .stream()
                .filter(Module::isEnabled)
                .filter(Module::isDrawn)
                .sorted(Comparator.comparing(module -> FontUtils.getStringWidth(ColorMain.customFont.getValue(), module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo()) * (-1)))
                .forEach(module -> {
                    if(sortUp.getValue()) {
                        if (sortRight.getValue()) {
                            FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), module.getName() + ChatFormatting.GRAY  + module.getHudInfo(), posX.getValue() - FontUtils.getStringWidth(ColorMain.customFont.getValue(), module.getName() + ChatFormatting.GRAY + module.getHudInfo()), posY.getValue() + (count * 10), adjColor);
                        }
                        else {
                            FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), module.getName() + ChatFormatting.GRAY  + module.getHudInfo(), posX.getValue(), posY.getValue() + (count * 10), adjColor);
                        }
                        count++;
                    }
                    else {
                        if (sortRight.getValue()) {
                            FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), module.getName() + ChatFormatting.GRAY  + module.getHudInfo(), posX.getValue() - FontUtils.getStringWidth(ColorMain.customFont.getValue(),module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo()), posY.getValue() + (count * -10), adjColor);
                        }
                        else {
                            FontUtils.drawStringWithShadow(ColorMain.customFont.getValue(), module.getName() + ChatFormatting.GRAY  + module.getHudInfo(), posX.getValue(), posY.getValue() + (count * -10), adjColor);
                        }
                        count++;
                    }

                    if (color.getRainbow()) {
                        adjColor =GSColor.fromHSB(adjColor.getHue()+.02f, adjColor.getSaturation(), adjColor.getBrightness());
                    }
                });
    }
}