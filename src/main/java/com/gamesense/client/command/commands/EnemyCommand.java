package com.gamesense.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.command.Command;
import com.gamesense.api.players.enemy.Enemies;

public class EnemyCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{
                "enemy", "enemies", "e"
        };
    }

    @Override
    public String getSyntax() {
        return "enemy <add | del> <name>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if(args[0].equalsIgnoreCase("add")) {
            if (!Enemies.getEnemies().contains(Enemies.getEnemyByName(args[1]))){
                Enemies.addEnemy(args[1]);
                sendClientMessage(ChatFormatting.GRAY + "Added enemy with name " + args[1]);
            } else {
                sendClientMessage(ChatFormatting.GRAY + args[1] + " is already an enemy!");
            }
        } else if(args[0].equalsIgnoreCase("del") || (args[0].equalsIgnoreCase("remove"))){
            Enemies.delEnemy(args[1]);
            sendClientMessage(ChatFormatting.GRAY + "Removed enemy with name " + args[1]);
        } else {
            sendClientMessage(getSyntax());
        }
    }
}
