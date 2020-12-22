package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.friends.Friends;
import com.gamesense.client.command.Command;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class FriendCommand extends Command {

    public FriendCommand() {
        super("Friend");

        setCommandSyntax(Command.getCommandPrefix() + "friend add/del [player]");
        setCommandAlias(new String[]{
                "friend", "friends", "f"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];
        String value = message[1];

        if (main.equalsIgnoreCase("add") && !Friends.isFriend(value)) {
            Friends.addFriend(value);
            MessageBus.sendCommandMessage("Added friend: " + value.toUpperCase() + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && Friends.isFriend(value)) {
            Friends.delFriend(value);
            MessageBus.sendCommandMessage("Deleted friend: " + value.toUpperCase() + "!", true);
        }
    }
}