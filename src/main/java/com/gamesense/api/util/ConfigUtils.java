package com.gamesense.api.util;

import com.gamesense.client.GameSenseMod;
import com.gamesense.client.devgui.DevFrame;
import com.gamesense.client.devgui.DevGUI;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.Command;
import com.gamesense.api.friends.Friend;
import com.gamesense.api.friends.Friends;
import com.gamesense.client.waypoint.Waypoint;
import com.gamesense.api.enemy.Enemies;
import com.gamesense.api.enemy.Enemy;
import com.gamesense.api.settings.Setting;
import com.gamesense.api.event.EventProcessor;
import com.gamesense.client.macro.Macro;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.modules.misc.Announcer;
import com.gamesense.api.util.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ConfigUtils {
    Minecraft mc = Minecraft.getMinecraft();
    public File GameSense;
    public File Settings;

    public ConfigUtils() {
        this.GameSense = new File(mc.gameDir + File.separator + "GameSense");
        if (!this.GameSense.exists()) {
            this.GameSense.mkdirs();
        }
        this.Settings = new File(mc.gameDir + File.separator + "GameSense" + File.separator + "Settings");
        if (!this.Settings.exists()) {
            this.Settings.mkdirs();
        }

        loadMods();
        loadDrawn();
        loadSettingsList();
        loadBinds();
        loadFriends();
        loadDevGUI();
        loadPrefix();
        loadRainbow();
        loadMacros();
        loadMsgs();
        loadAutoGG();
        loadAutoReply();
        loadWaypoints();
        loadFont();
        loadEnemies();
    }

    public void saveDevGUI(){
        try {
            File file = new File(this.GameSense.getAbsolutePath(),"DevGUI.txt");
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

    public void loadDevGUI(){
        try {
            File file = new File(this.GameSense.getAbsolutePath(),"DevGUI.txt");
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
        catch (Exception var17) {
            var17.printStackTrace();
            this.saveDevGUI();
        }
    }

    public void saveBinds() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Binds.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().moduleManager.getModules().iterator();

            while(var3.hasNext()) {
                Module module = (Module)var3.next();
                out.write(module.getName() + ":" + Keyboard.getKeyName(module.getBind()));
                out.write("\r\n");
            }
            out.close();
        } catch (Exception var5) {
        }

    }

    public void loadBinds() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Binds.txt");
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
        } catch (Exception var11) {
            var11.printStackTrace();
            saveBinds();
        }
    }

    public void saveMacros() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Macros.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().macroManager.getMacros().iterator();

            while(var3.hasNext()) {
                Macro m = (Macro) var3.next();
                out.write(Keyboard.getKeyName(m.getKey()) + ":" + m.getValue().replace(" ", "_"));
                out.write("\r\n");
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadMacros() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Macros.txt");
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
        } catch (Exception var11) {
            var11.printStackTrace();
            saveMacros();
        }
    }

    public void saveWaypoints() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Waypoints.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().waypointManager.getWaypoints().iterator();

            while(var3.hasNext()) {
                Waypoint w = (Waypoint) var3.next();
                out.write(w.getName() + ":" + (int)w.getX() + ":" + (int)w.getY() + ":" + (int)w.getZ() + ":" + w.getColor());
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadWaypoints() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Waypoints.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = br.readLine()) != null) {
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String x = curLine.split(":")[1];
                int xx = Integer.parseInt(x);
                String y = curLine.split(":")[2];
                int yy = Integer.parseInt(y);
                String z = curLine.split(":")[3];
                int zz = Integer.parseInt(z);
                String color = curLine.split(":")[4];
                int c = Integer.parseInt(color);
                GameSenseMod.getInstance().waypointManager.addWaypoint(new Waypoint(name, xx, yy, zz, c));
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
            saveWaypoints();
        }
    }

    public void saveAnnouncer() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Announcer.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write("Walk:" + Announcer.walkMessage);
                out.write("\r\n");
                out.write("Place:" + Announcer.placeMessage);
                out.write("\r\n");
                out.write("Jump:" + Announcer.jumpMessage);
                out.write("\r\n");
                out.write("Break:" + Announcer.breakMessage);
                out.write("\r\n");
                out.write("Attack:" + Announcer.attackMessage);
                out.write("\r\n");
                out.write("Eat:" + Announcer.eatMessage);
                out.write("\r\n");
                out.write("ClickGUI:" + Announcer.guiMessage);
                out.write("\r\n");

            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadAnnouncer() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Announcer.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = br.readLine()) != null) {
                String curLine = line.trim();
                String name = curLine.split(":")[0];
                String message = curLine.split(":")[1];
                if(name.equalsIgnoreCase("Walk")) Announcer.walkMessage = message;
                if(name.equalsIgnoreCase("Place")) Announcer.placeMessage = message;
                if(name.equalsIgnoreCase("Jump")) Announcer.jumpMessage = message;
                if(name.equalsIgnoreCase("Break")) Announcer.breakMessage = message;
                if(name.equalsIgnoreCase("Attack")) Announcer.attackMessage = message;
                if(name.equalsIgnoreCase("Eat")) Announcer.eatMessage = message;
                if(name.equalsIgnoreCase("ClickGUI")) Announcer.guiMessage = message;
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
            saveAnnouncer();
        }
    }

    public void saveMods() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "EnabledModules.txt");
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
        } catch (Exception var5) {
        }
    }

    public void saveFriends() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Friends.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = Friends.getFriends().iterator();

            while(var3.hasNext()) {
                Friend f = (Friend)var3.next();
                out.write(f.getName());
                out.write("\r\n");
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadFriends() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Friends.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            Friends.friends.clear();
            String line;
            while((line = br.readLine()) != null) {
                GameSenseMod.getInstance().friends.addFriend(line);
            }
            br.close();
        } catch (Exception var6) {
            var6.printStackTrace();
            saveFriends();
        }
    }

    public void saveEnemies() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Enemies.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = Enemies.getEnemies().iterator();

            while(var3.hasNext()) {
                Enemy e = (Enemy)var3.next();
                out.write(e.getName());
                out.write("\r\n");
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadEnemies() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Enemies.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            Enemies.enemies.clear();
            String line;
            while((line = br.readLine()) != null) {
                Enemies.addEnemy(line);
            }
            br.close();
        } catch (Exception var6) {
            var6.printStackTrace();
            saveEnemies();
        }
    }

    public void savePrefix() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "CommandPrefix.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Command.getPrefix());
            out.write("\r\n");
            out.close();
        } catch (Exception var3) {
        }
    }

    public void loadPrefix() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "CommandPrefix.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = br.readLine()) != null) {
                Command.setPrefix(line);
            }
            br.close();
        } catch (Exception var6) {
            var6.printStackTrace();
            this.savePrefix();
        }
    }

    public void saveFont() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Font.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(GameSenseMod.fontRenderer.getFontName()+ ":" + GameSenseMod.fontRenderer.getFontSize());
            out.write("\r\n");
            out.close();
        } catch (Exception var3) {
        }
    }

    public void loadFont() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Font.txt");
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
        } catch (Exception var6) {
            var6.printStackTrace();
            this.saveFont();
        }
    }

    public void saveAutoGG() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "AutoGgMessage.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
              for(String s : AutoGG.getAutoGgMessages()) {
                  out.write(s);
                  out.write("\r\n");
               }
            out.close();
        } catch (Exception var3) {
        }
    }

    public void loadAutoGG() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "AutoGgMessage.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = br.readLine()) != null) {
                    AutoGG.addAutoGgMessage(line);
            }
            br.close();
        } catch (Exception var6) {
            var6.printStackTrace();
            this.saveAutoGG();
        }
    }

    public void saveAutoReply() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "AutoReplyMessage.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(AutoReply.getReply());
            out.close();
        } catch (Exception var3) {
        }
    }

    public void loadAutoReply() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "AutoReplyMessage.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = br.readLine()) != null) {
                      AutoReply.setReply(line);
            }
            br.close();
        } catch (Exception var6) {
            var6.printStackTrace();
            this.saveAutoReply();
        }
    }

    public void saveRainbow() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "RainbowSpeed.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(EventProcessor.INSTANCE.getRainbowSpeed() + "");
            //out.write("\r\n");
            out.close();
        } catch (Exception var3) {
        }
    }

    public void loadRainbow() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "RainbowSpeed.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null) {
                EventProcessor.INSTANCE.setRainbowSpeed(Integer.parseInt(line));
            }
            br.close();
        } catch (Exception var6) {
            var6.printStackTrace();
            saveRainbow();
        }
    }

    public void saveMsgs() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "ClientMessages.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(Command.MsgWaterMark + "");
            out.write(",");
            out.write(Command.cf.getName());
            out.close();
        } catch (Exception var3) {
        }
    }

    public void loadMsgs() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "ClientMessages.txt");
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
        } catch (Exception var11) {
            var11.printStackTrace();
            this.saveMsgs();
        }
    }

    public void saveDrawn() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Drawn.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            Iterator var3 = GameSenseMod.getInstance().moduleManager.getModules().iterator();

            while(var3.hasNext()) {
                Module module = (Module)var3.next();
                out.write(module.getName() + ":" + module.isDrawn());
                out.write("\r\n");
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadDrawn() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "Drawn.txt");
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
        } catch (Exception var11) {
            var11.printStackTrace();
            this.saveDrawn();
        }
    }

    public void loadMods() {
        try {
            File file = new File(this.GameSense.getAbsolutePath(), "EnabledModules.txt");
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
        } catch (Exception var8) {
            var8.printStackTrace();
            this.saveMods();
        }
    }

    public void saveSettingsList() {
        File file;
        BufferedWriter out;
        Iterator var3;
        Setting i;
        try {
            file = new File(Settings.getAbsolutePath(), "Number.txt");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettings().iterator();

            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.DOUBLE) {
                    out.write(i.getName() + ":" +((Setting.d) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
                if (i.getType() == Setting.Type.INT) {
                    out.write(i.getName() + ":" +((Setting.i) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var7) {
        }
        try {
            file = new File(Settings.getAbsolutePath(), "Boolean.txt");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettings().iterator();

            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.BOOLEAN) {
                    out.write(i.getName() + ":" + ((Setting.b) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var6) {
        }
        try {
            file = new File(Settings.getAbsolutePath(), "String.txt");
            out = new BufferedWriter(new FileWriter(file));
            var3 = GameSenseMod.getInstance().settingsManager.getSettings().iterator();

            while(var3.hasNext()) {
                i = (Setting)var3.next();
                if (i.getType() == Setting.Type.MODE) {
                    out.write(i.getName() + ":" + ((Setting.mode) i).getValue() + ":" + i.getParent().getName() + "\r\n");
                }
            }
            out.close();
        } catch (Exception var5) {
        }
    }

    public void loadSettingsList() {
        File file;
        FileInputStream fstream;
        DataInputStream in;
        BufferedReader br;
        String line;
        String curLine;
        String name;
        String isOn;
        String m;
        Setting mod;
        int color;
        try {
            file = new File(Settings.getAbsolutePath(), "Number.txt");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                name = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModules()) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(name, mm);

                        if (mod instanceof Setting.i) {
                            ((Setting.i) mod).setValue(Integer.parseInt(isOn));
                        } else if (mod instanceof Setting.d){
                            ((Setting.d) mod).setValue(Double.parseDouble(isOn));
                        }
                    }
                }
            }
            br.close();
        } catch (Exception var13) {
            var13.printStackTrace();
            //saveSettingsList();
        }
        try {
            file = new File(Settings.getAbsolutePath(), "Boolean.txt");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                name = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModules()) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(name, mm);
                        ((Setting.b) mod).setValue(Boolean.parseBoolean(isOn));
                    }
                }
            }
            br.close();
        } catch (Exception var12) {
            var12.printStackTrace();
            //saveSettingsList();
        }
        try {
            file = new File(Settings.getAbsolutePath(), "String.txt");
            fstream = new FileInputStream(file.getAbsolutePath());
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));

            while((line = br.readLine()) != null) {
                curLine = line.trim();
                name = curLine.split(":")[0];
                isOn = curLine.split(":")[1];
                m = curLine.split(":")[2];
                for(Module mm : ModuleManager.getModules()) {
                    if (mm != null && mm.getName().equalsIgnoreCase(m)) {
                        mod = GameSenseMod.getInstance().settingsManager.getSettingByNameAndMod(name, mm);
                        ((Setting.mode) mod).setValue(isOn);
                    }
                }
            }
            br.close();
        } catch (Exception var11) {
            var11.printStackTrace();
            //aveSettingsList();
        }
    }
}