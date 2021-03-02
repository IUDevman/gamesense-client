package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.friend.Friends;
import com.gamesense.client.command.Command;

/**
 * @Author Hoosiers on 11/05/2020
 */

@Command.Declaration(name = "Friend", syntax = "friend list/add/del [player]", alias = {"friend", "friends", "f"})
public class FriendCommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        if (main.equalsIgnoreCase("list")) {
            MessageBus.sendClientPrefixMessage("Friends: " + Friends.getFriendsByName() + "!");
            return;
        }

        String value = message[1];

        if (main.equalsIgnoreCase("add") && !Friends.isFriend(value)) {
            Friends.addFriend(value);
            MessageBus.sendCommandMessage("Added friend: " + value.toUpperCase() + "!", true);
        } else if (main.equalsIgnoreCase("del") && Friends.isFriend(value)) {
            Friends.delFriend(value);
            MessageBus.sendCommandMessage("Deleted friend: " + value.toUpperCase() + "!", true);
        }
    }
}