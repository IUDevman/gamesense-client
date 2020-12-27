package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.enemy.Enemies;
import com.gamesense.client.command.Command;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class EnemyCommand extends Command {

    public EnemyCommand() {
        super("Enemy");

        setCommandSyntax(Command.getCommandPrefix() + "enemy list/add/del [player]");
        setCommandAlias(new String[]{
                "enemy", "enemies", "e"
        });
    }

    public void onCommand(String command, String[] message) throws Exception {
        String main = message[0];

        if (main.equalsIgnoreCase("list")) {
            MessageBus.sendClientPrefixMessage("Enemies: " + Enemies.getEnemiesByName() + "!");
            return;
        }

        String value = message[1];

        if (main.equalsIgnoreCase("add") && !Enemies.isEnemy(value)) {
            Enemies.addEnemy(value);
            MessageBus.sendCommandMessage("Added enemy: " + value.toUpperCase() + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && Enemies.isEnemy(value)) {
            Enemies.delEnemy(value);
            MessageBus.sendCommandMessage("Deleted enemy: " + value.toUpperCase() + "!", true);
        }
    }
}