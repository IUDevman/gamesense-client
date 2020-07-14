package com.gamesense.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.Command;

public class ClientMsgsCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{"messages", "clientmessages"};
    }

    @Override
    public String getSyntax() {
        return "messages <color | watermark> <(color) | (true | false)>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args[0].equalsIgnoreCase("color")){
            if(ChatFormatting.getByName(args[1]) != null) {
                Command.cf = ChatFormatting.getByName(args[1]);
                Command.sendClientMessage("Message color set to " + args[1]);
            } else Command.sendClientMessage(ChatFormatting.RED + getSyntax());
        } else if(args[0].equalsIgnoreCase("watermark")){
            Command.MsgWaterMark = Boolean.parseBoolean(args[1]);
            Command.sendClientMessage("Message watermark = " + args[1]);
        } else {
            Command.sendClientMessage(ChatFormatting.RED + getSyntax());
        }
    }
}
