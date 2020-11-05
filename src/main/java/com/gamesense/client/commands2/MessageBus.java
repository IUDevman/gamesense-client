package com.gamesense.client.commands2;

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

    public static boolean cancelMessage = cancelMessage();

    protected static final Minecraft mc = Minecraft.getMinecraft();

    /** Sends a client-sided message WITH the client prefix **/
    public static void sendClientPrefixMessage(String message){
        TextComponentString string1 = new TextComponentString(watermark + messageFormatting + message);
        TextComponentString string2 = new TextComponentString(messageFormatting + message);

        Notifications.addMessage(string2);
        if (cancelMessage){
            return;
        }
        mc.player.sendMessage(string1);
    }

    /** Sends a client-sided message WITHOUT the client prefix **/
    public static void sendClientRawMessage(String message){
        TextComponentString string = new TextComponentString(messageFormatting + message);

        Notifications.addMessage(string);
        if (cancelMessage){
            return;
        }
        mc.player.sendMessage(string);
    }

    /** Sends a server-sided message **/
    public static void sendServerMessage(String message){
        mc.player.connection.sendPacket(new CPacketChatMessage(message));
    }

    /** Checks to see if Notifications module + "Cancel Messages" option is enabled **/
    public static boolean cancelMessage(){
        if (ModuleManager.isModuleEnabled("Notifications") && Notifications.disableChat.getValue()){
            return true;
        }
        else {
            return false;
        }
    }
}