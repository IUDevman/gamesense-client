package com.gamesense.api.util.config;

import com.gamesense.api.players.enemy.Enemies;
import com.gamesense.api.players.friends.Friends;
import com.gamesense.api.util.font.CFontRenderer;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.devgui.DevFrame;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.macro.Macro;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.mojang.realmsclient.gui.ChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.*;
import java.util.Iterator;

/**
 * Made by Hoosiers on 08/02/20 for GameSense, some functions were modified and ported from the original ConfigUtils
 */

public class LoadConfiguration {

    public LoadConfiguration(){

        //loads functions on client startup
        loadAutoGG();
        loadAutoReply();
        loadBinds();
        loadDrawn();
        loadEnabled();
        loadEnemies();
        loadFont();
        loadFriends();
        loadGUI();
        loadMacros();
        loadMessages();
        loadPrefix();
    }

    //loads gui settings
    public void loadGUI(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "DevGUI.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null){
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String x = curLine.split(":")[1];
                String y = curLine.split(":")[2];
                String e = curLine.split(":")[3];
                int x1 = Integer.parseInt(x);
                int y1 = Integer.parseInt(y);
                boolean open = Boolean.parseBoolean(e);
                DevFrame devFrame = DevGUI.getFrameByName(name);
                if (devFrame != null) {
                    devFrame.x = x1;
                    devFrame.y = y1;
                    devFrame.open = open;
                }
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveGUI();
        }
    }

    //loads client macros
    public void loadMacros(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "ClientMacros.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                String curLine = line.trim();
                String bind = curLine.split(":")[0];
                String value = curLine.split(":")[1];
                GameSenseMod.getInstance().macroManager.addMacro(new Macro(Keyboard.getKeyIndex(bind), value.replace("_", " ")));
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveMacros();
        }
    }

    //loads friends
    public void loadFriends(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "Friends.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Friends.friends.clear();
            String line;
            while((line = br.readLine()) != null) {
                GameSenseMod.getInstance().friends.addFriend(line);
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveFriends();
        }
    }

    //loads enemies
    public void loadEnemies(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "Enemies.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Enemies.enemies.clear();
            String line;
            while((line = br.readLine()) != null) {
                Enemies.addEnemy(line);
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveEnemies();
        }
    }

    //loads client command prefix
    public void loadPrefix(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "CommandPrefix.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                Command.setPrefix(line);
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.savePrefix();
        }
    }

    //loads custom font
    public void loadFont(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "CustomFont.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                String name = line.split(":")[0];
                String size = line.split(":")[1];
                int sizeInt = Integer.parseInt(size);
                GameSenseMod.fontRenderer = new CFontRenderer(new Font(name, Font.PLAIN, sizeInt), true, false);
                GameSenseMod.fontRenderer.setFont(new Font(name, Font.PLAIN, sizeInt));
                GameSenseMod.fontRenderer.setAntiAlias(true);
                GameSenseMod.fontRenderer.setFractionalMetrics(false);
                GameSenseMod.fontRenderer.setFontName(name);
                GameSenseMod.fontRenderer.setFontSize(sizeInt);
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveFont();
        }
    }

    //loads AutoGG message
    public void loadAutoGG(){
        try {
            File file = new File(SaveConfiguration.Messages.getAbsolutePath(), "AutoGG.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                AutoGG.addAutoGgMessage(line);
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveAutoGG();
        }
    }

    //loads AutoReply message
    public void loadAutoReply(){
        try {
            File file = new File(SaveConfiguration.Messages.getAbsolutePath(), "AutoReply.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                AutoReply.setReply(line);
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveAutoReply();
        }
    }

    //loads client messages such as the watermark
    public void loadMessages(){
        try {
            File file = new File(SaveConfiguration.Messages.getAbsolutePath(), "ClientMessages.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                String curLine = line.trim();
                String watermark = curLine.split(",")[0];
                String color = curLine.split(",")[1];
                boolean w = Boolean.parseBoolean(watermark);
                ChatFormatting c = ChatFormatting.getByName(color);
                Command.cf = c;
                Command.MsgWaterMark = w;
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveMessages();
        }
    }

    //loads drawn modules
    public void loadDrawn(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "DrawnModules.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String isOn = curLine.split(":")[1];
                boolean drawn = Boolean.parseBoolean(isOn);
                for(Module m : GameSenseMod.getInstance().moduleManager.getModules()) {
                    if (m.getName().equalsIgnoreCase(name)) {
                        m.setDrawn(drawn);
                    }
                }
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveDrawn();
        }
    }

    //loads enabled/disabled modules
    public void loadEnabled(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "EnabledModules.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                Iterator var6 = GameSenseMod.getInstance().moduleManager.getModules().iterator();
                while(var6.hasNext()) {
                    Module m = (Module)var6.next();
                    if (m.getName().equals(line)) {
                        m.enable();
                    }
                }
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveEnabled();
        }
    }

    //loads module binds
    public void loadBinds(){
        try {
            File file = new File(SaveConfiguration.Miscellaneous.getAbsolutePath(), "ModuleBinds.json");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String bind = curLine.split(":")[1];
                for(Module m : GameSenseMod.getInstance().moduleManager.getModules()) {
                    if (m != null && m.getName().equalsIgnoreCase(name)) {
                        m.setBind(Keyboard.getKeyIndex(bind));
                    }
                }
            }
            br.close();
        }
        catch (Exception var6){
            var6.printStackTrace();
            SaveConfiguration.saveBinds();
        }
    }
}