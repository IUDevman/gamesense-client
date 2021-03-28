package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Collection;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author Hoosiers on 11/05/2020
 * @author Seth for Seppuku
 */

@Command.Declaration(name = "Modules", syntax = "modules (click to toggle)", alias = {"modules", "module", "modulelist", "mod", "mods"})
public class ModulesCommand extends Command {

    public void onCommand(String command, String[] message) throws Exception {
        TextComponentString msg = new TextComponentString("\2477Modules: " + "\247f ");

        Collection<Module> modules = ModuleManager.getModules();
        int size = modules.size();
        int index = 0;

        for (Module module : modules) {
            msg.appendSibling(new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "\2477" + ((index == size - 1) ? "" : ", "))
                    .setStyle(new Style()
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(module.getCategory().name())))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandManager.getCommandPrefix() + "toggle" + " " + module.getName()))));

            index++;
        }

        msg.appendSibling(new TextComponentString(ChatFormatting.GRAY + "!"));
        mc.ingameGUI.getChatGUI().printChatMessage(msg);
    }
}