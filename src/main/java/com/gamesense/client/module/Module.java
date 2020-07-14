package com.gamesense.client.module;

import com.gamesense.client.GameSenseMod;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.command.Command;
import com.gamesense.api.event.events.RenderEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    String name;
    Category category;
    int bind;
    boolean enabled;
    boolean drawn;

    public Module(String n, Category c) {
        name = n;
        category = c;
        bind = Keyboard.KEY_NONE;
        enabled = false;
        drawn = true;
        setup();
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    public Category getCategory(){
        return category;
    }

    public void setCategory(Category c){
        category = c;
    }

    public int getBind(){
        return bind;
    }

    public void setBind(int b){
        bind = b;
    }

    protected void onEnable(){
    }
    protected void onDisable(){
    }
    public void onUpdate(){}
    public void onRender(){}
    public void onWorldRender(RenderEvent event) {}

    public boolean isEnabled(){
        return enabled;
    }

    public void setEnabled(boolean e){
        enabled = e;
    }

    public void enable(){
        setEnabled(true);
        if(ModuleManager.isModuleEnabled("ToggleMsgs") && !getName().equalsIgnoreCase("DevGUI")) Command.sendRawMessage(getName() + ChatFormatting.GREEN + " enabled!");
        onEnable();
    }

    public void disable(){
        setEnabled(false);
        if(ModuleManager.isModuleEnabled("ToggleMsgs") && !getName().equalsIgnoreCase("DevGUI")) Command.sendRawMessage(getName() + ChatFormatting.RED + " disabled!");
        onDisable();
    }

    public void toggle(){
        if(isEnabled()) {
            disable();
        } else if(!isEnabled()){
            enable();
        }
    }

    public String getHudInfo(){
        return "";
    }

    public void setup(){}

    public boolean isDrawn(){
        return drawn;
    }

    public void setDrawn(boolean d){
        drawn = d;
    }

    protected Setting.i registerI(final String name, final int value, final int min, final int max) {
        final Setting.i s = new Setting.i(name, this, value, min, max);
        GameSenseMod.getInstance().settingsManager.addSetting(s);
        return s;
    }

    protected Setting.d registerD(final String name, final double value, final double min, final double max) {
        final Setting.d s = new Setting.d(name, this, value, min, max);
        GameSenseMod.getInstance().settingsManager.addSetting(s);
        return s;
    }

    protected Setting.b registerB(final String name, final boolean value) {
        final Setting.b s = new Setting.b(name, this, value);
        GameSenseMod.getInstance().settingsManager.addSetting(s);
        return s;
    }

    protected Setting.s registerS(final String name, final String value) {
        final Setting.s s = new Setting.s(name, this, value);
        GameSenseMod.getInstance().settingsManager.addSetting(s);
        return s;
    }

    protected Setting.mode registerMode(final String name, final List<String> modes, final String value) {
        final Setting.mode s = new Setting.mode(name, this, modes, value);
        GameSenseMod.getInstance().settingsManager.addSetting(s);
        return s;
    }

    public enum Category{
        Combat,
        Exploits,
        Movement,
        Misc,
        Render,
        HUD
    }
}
