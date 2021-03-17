package com.gamesense.client.module.modules.misc;

import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Module.Declaration(name = "ChatModifier", category = Category.Misc)
public class ChatModifier extends Module {

    public BooleanSetting clearBkg = registerBoolean("Clear Chat", false);
    BooleanSetting greenText = registerBoolean("Green Text", false);
    BooleanSetting chatTimeStamps = registerBoolean("Chat Time Stamps", false);
    ModeSetting format = registerMode("Format", Arrays.asList("H24:mm", "H12:mm", "H12:mm a", "H24:mm:ss", "H12:mm:ss", "H12:mm:ss a"), "H24:mm");
    ModeSetting decoration = registerMode("Deco", Arrays.asList("< >", "[ ]", "{ }", " "), "[ ]");
    ModeSetting color = registerMode("Color", ColorUtil.colors, ChatFormatting.GRAY.getName());
    BooleanSetting space = registerBoolean("Space", false);

    @EventHandler
    private final Listener<ClientChatReceivedEvent> chatReceivedEventListener = new Listener<>(event -> {
        //Chat Time Stamps
        if (chatTimeStamps.getValue()) {
            String decoLeft = decoration.getValue().equalsIgnoreCase(" ") ? "" : decoration.getValue().split(" ")[0];
            String decoRight = decoration.getValue().equalsIgnoreCase(" ") ? "" : decoration.getValue().split(" ")[1];
            if (space.getValue()) decoRight += " ";
            String dateFormat = format.getValue().replace("H24", "k").replace("H12", "h");
            String date = new SimpleDateFormat(dateFormat).format(new Date());
            TextComponentString time = new TextComponentString(ChatFormatting.getByName(color.getValue()) + decoLeft + date + decoRight + ChatFormatting.RESET);
            event.setMessage(time.appendSibling(event.getMessage()));
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (greenText.getValue()) {
            if (event.getPacket() instanceof CPacketChatMessage) {
                if (((CPacketChatMessage) event.getPacket()).getMessage().startsWith("/") || ((CPacketChatMessage) event.getPacket()).getMessage().startsWith(CommandManager.getCommandPrefix()))
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
}