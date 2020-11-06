package com.gamesense.api.util.misc;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.hud.Notifications;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;

/**
 * @Author Hoosiers on 11/04/2020
 */

public class MessageBus {

    public static String watermark = ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "Game" + ChatFormatting.GREEN + "Sense" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET;
    public static ChatFormatting messageFormatting = ChatFormatting.GRAY;

    protected static final Minecraft mc = Minecraft.getMinecraft();

    /** Sends a client-sided message WITH the client prefix **/
    public static void sendClientPrefixMessage(String message){
        TextComponentString string1 = new TextComponentString(watermark + messageFormatting + message);
        TextComponentString string2 = new TextComponentString(messageFormatting + message);

        Notifications.addMessage(string2);
        if (ModuleManager.isModuleEnabled("Notifications") && Notifications.disableChat.getValue()){
            return;
        }
        mc.player.sendMessage(string1);
    }

    /** @Unused Sends a client-sided message WITHOUT the client prefix **/
    public static void sendClientRawMessage(String message){
        TextComponentString string = new TextComponentString(messageFormatting + message);

        Notifications.addMessage(string);
        if (ModuleManager.isModuleEnabled("Notifications") && Notifications.disableChat.getValue()){
            return;
        }
        mc.player.sendMessage(string);
    }

    /** Sends a server-sided message **/
    public static void sendServerMessage(String message){
        mc.player.connection.sendPacket(new CPacketChatMessage(message));
    }
}