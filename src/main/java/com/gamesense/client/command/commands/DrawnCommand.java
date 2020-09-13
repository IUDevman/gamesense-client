package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;

public class DrawnCommand extends Command {
    boolean found;

    @Override
    public String[] getAlias() {
        return new String[]{"drawn", "visible", "d"};
    }

    @Override
    public String getSyntax() {
        return "drawn <Module>";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        found = false;
        ModuleManager.getModules().forEach(m -> {
            if (m.getName().equalsIgnoreCase(args[0])) {
                if (m.isDrawn()) {
                    m.setDrawn(false);
                    found = true;
                    Command.sendClientMessage(m.getName() + ChatFormatting.RED + " drawn = false");
                } else if (!m.isDrawn()) {
                    m.setDrawn(true);
                    found = true;
                    Command.sendClientMessage(m.getName() + ChatFormatting.GREEN + " drawn = true");
                }
            }
        });
        if (!found && args.length == 1) Command.sendClientMessage(ChatFormatting.GRAY + "Module not found!");
    }
}
