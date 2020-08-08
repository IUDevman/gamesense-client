package com.gamesense.api.util.config;

import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.enemy.Enemy;
import com.gamesense.api.players.friends.Friend;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.devgui.DevFrame;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.macro.Macro;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

/**
 * Made by Hoosiers on 08/02/20 for GameSense, some functions were modified and ported from the original ConfigUtils
 */

public class SaveConfiguration {

    Minecraft mc = Minecraft.getMinecraft();

    //File Structure
    public static File GameSenseDev;
        //Main file, %appdata%/.minecraft
    public static File Modules;
        //Inside main file, houses settings for modules
    public static File Messages;
        //Inside main file, houses settings for client messages such as AutoGG
    public static File Miscellaneous;
        //Inside main file, houses settings for client settings such as Font

    public static File Combat;
    public static File Exploits;
    public static File Hud;
    public static File Misc;
    public static File Movement;
    public static File Render;
        //Files inside the modules folder, houses module configs per category

    public SaveConfiguration(){
        this.GameSenseDev = new File(mc.gameDir + File.separator + "GameSense");
        if (!this.GameSenseDev.exists()){
            this.GameSenseDev.mkdirs();
        }
        this.Modules = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules");
        if (!this.Modules.exists()){
            this.Modules.mkdirs();
        }
        this.Messages = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Messages");
        if (!this.Messages.exists()){
            this.Messages.mkdirs();
        }
        this.Miscellaneous = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Miscellaneous");
        if (!this.Miscellaneous.exists()){
            this.Miscellaneous.mkdirs();
        }
        this.Combat = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules" + File.separator + "Combat");
        if (!this.Combat.exists()){
            this.Combat.mkdirs();
        }
        this.Exploits = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules" + File.separator + "Exploits");
        if (!this.Exploits.exists()){
            this.Exploits.mkdirs();
        }
        this.Hud = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules" + File.separator + "Hud");
        if (!this.Hud.exists()){
            this.Hud.mkdirs();
        }
        this.Misc = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules" + File.separator + "Misc");
        if (!this.Misc.exists()){
            this.Misc.mkdirs();
        }
        this.Movement = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules" + File.separator + "Movement");
        if (!this.Movement.exists()){
            this.Movement.mkdirs();
        }
        this.Render = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Modules" + File.separator + "Render");
        if (!this.Render.exists()){
            this.Render.mkdirs();
        }
    }

    //saves gui settings
    public static void saveGUI(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(),"DevGUI.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = DevGUI.devframes.iterator();
            while (var3.hasNext()){
                DevFrame devFrame = (DevFrame)var3.next();
                out.write(devFrame.category + ":" + devFrame.getX() + ":" + devFrame.getY() + ":" + devFrame.isOpen());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves macros
    public static void saveMacros(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "ClientMacros.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().macroManager.getMacros().iterator();
            while(var3.hasNext()) {
                Macro m = (Macro) var3.next();
                out.write(Keyboard.getKeyName(m.getKey()) + ":" + m.getValue().replace(" ", "_"));
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves friends
    public static void saveFriends(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "Friends.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = Friends.getFriends().iterator();
            while(var3.hasNext()) {
                Friend f = (Friend)var3.next();
                out.write(f.getName());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves enemies
    public static void saveEnemies(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "Enemies.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = Enemies.getEnemies().iterator();
            while(var3.hasNext()) {
                Enemy e = (Enemy)var3.next();
                out.write(e.getName());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves prefix
    public static void savePrefix(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "CommandPrefix.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Command.getPrefix());
            out.write("\r\n");
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves font
    public static void saveFont(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "CustomFont.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(GameSenseMod.fontRenderer.getFontName()+ ":" + GameSenseMod.fontRenderer.getFontSize());
            out.write("\r\n");
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves AutoGG
    public static void saveAutoGG(){
        try {
            File file = new File(Messages.getAbsolutePath(), "AutoGG.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(String s : AutoGG.getAutoGgMessages()) {
                out.write(s);
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves AutoReply
    public static void saveAutoReply(){
        try {
            File file = new File(Messages.getAbsolutePath(), "AutoReply.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(AutoReply.getReply());
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves client messages such as the watermark
    public static void saveMessages(){
        try {
            File file = new File(Messages.getAbsolutePath(), "ClientMessages.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Command.MsgWaterMark + "");
            out.write(",");
            out.write(Command.cf.getName());
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves drawn modules
    public static void saveDrawn(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "DrawnModules.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().moduleManager.getModules().iterator();
            while(var3.hasNext()) {
                Module module = (Module)var3.next();
                out.write(module.getName() + ":" + module.isDrawn());
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves enabled/disabled modules
    public static void saveEnabled(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "EnabledModules.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().moduleManager.getModules().iterator();
            while(var3.hasNext()) {
                Module module = (Module)var3.next();
                if (module.isEnabled()) {
                    out.write(module.getName());
                    out.write("\r\n");
                }
            }
            out.close();
        }
        catch (Exception var5){
        }
    }

    //saves module binds
    public static void saveBinds(){
        try {
            File file = new File(Miscellaneous.getAbsolutePath(), "ModuleBinds.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().moduleManager.getModules().iterator();
            while(var3.hasNext()) {
                Module module = (Module)var3.next();
                out.write(module.getName() + ":" + Keyboard.getKeyName(module.getBind()));
                out.write("\r\n");
            }
            out.close();
        }
        catch (Exception var5){
        }
    }
}