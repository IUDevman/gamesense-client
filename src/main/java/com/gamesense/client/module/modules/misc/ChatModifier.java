package com.gamesense.client.module.modules.misc;


import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.settings.Setting;
import com.gamesense.client.GameSenseMod;
import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatModifier extends Module {
    public Setting.Boolean clearBkg;
    Setting.Boolean chattimestamps;
    Setting.Mode format;
    Setting.Mode color;
    Setting.Mode decoration;
    Setting.Boolean space;
    @EventHandler
    private final Listener<ClientChatReceivedEvent> chatReceivedEventListener = new Listener<>(event -> {
        //Chat Time Stamps
        if (chattimestamps.getValue()) {
            String decoLeft = decoration.getValue().equalsIgnoreCase(" ") ? "" : decoration.getValue().split(" ")[0];
            String decoRight = decoration.getValue().equalsIgnoreCase(" ") ? "" : decoration.getValue().split(" ")[1];
            if (space.getValue()) decoRight += " ";
            String dateFormat = format.getValue().replace("H24", "k").replace("H12", "h");
            String date = new SimpleDateFormat(dateFormat).format(new Date());
            TextComponentString time = new TextComponentString(ChatFormatting.getByName(color.getValue()) + decoLeft + date + decoRight + ChatFormatting.RESET);
            event.setMessage(time.appendSibling(event.getMessage()));
        }
    });
    Setting.Boolean greentext;
    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (greentext.getValue()) {
            if (event.getPacket() instanceof CPacketChatMessage) {
                if (((CPacketChatMessage) event.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith(Command.getPrefix()))
                    return;
                String message = ((CPacketChatMessage) event.getPacket()).getMessage();
                String prefix = "";
                prefix = ">";
                String s = prefix + message;
                if (s.length() > 255) return;
                ((CPacketChatMessage) event.getPacket()).message = s;
            }
        }
    });

    public ChatModifier() {
        super("ChatModifier", Category.Misc);
    }

    public void setup() {

        ArrayList<String> formats = new ArrayList<>();
        formats.add("H24:mm");
        formats.add("H12:mm");
        formats.add("H12:mm a");
        formats.add("H24:mm:ss");
        formats.add("H12:mm:ss");
        formats.add("H12:mm:ss a");
        ArrayList<String> deco = new ArrayList<>();
        deco.add("< >");
        deco.add("[ ]");
        deco.add("{}");
        deco.add(" ");
        ArrayList<String> colors = new ArrayList<>();
        for (ChatFormatting cf : ChatFormatting.values()) {
            colors.add(cf.getName());
        }

        clearBkg = registerBoolean("Clear Chat", "ClearChat", false);
        greentext = registerBoolean("Green Text", "GreenText", false);
        chattimestamps = registerBoolean("Chat Time Stamps", "ChatTimeStamps", false);
        format = registerMode("Format", "Format", formats, "H24:mm");
        decoration = registerMode("Deco", "Deco", deco, "[ ]");
        color = registerMode("Color", "Colors", colors, ChatFormatting.GRAY.getName());
        space = registerBoolean("Space", "Space", false);

    }

    public void onEnable() {
        GameSenseMod.EVENT_BUS.subscribe(this);
    }

    public void onDisable() {
        GameSenseMod.EVENT_BUS.unsubscribe(this);
    }
}
