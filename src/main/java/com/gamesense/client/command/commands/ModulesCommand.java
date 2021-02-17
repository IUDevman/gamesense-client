package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collection;

/**
 * @author Hoosiers on 11/05/2020
 */

public class ModulesCommand extends Command {

    public ModulesCommand() {
        super("Modules");

        setCommandSyntax(Command.getCommandPrefix() + "modules (click to toggle)");
        setCommandAlias(new String[]{
            "modules", "module", "modulelist", "mod", "mods"
        });
    }

    /**
     * ported from the old commands, @Author Seth for Seppuku
     **/
    public void onCommand(String command, String[] message) throws Exception {
        TextComponentString msg = new TextComponentString("\2477Modules: " + "\247f ");

        Collection<Module> modules = ModuleManager.getModules();
        int size = modules.size();
        int index = 0;

        for (Module module : modules) {
            msg.appendSibling(new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "\2477" + ((index == size - 1) ? "" : ", "))
                .setStyle(new Style()
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(module.getCategory().name())))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Command.getCommandPrefix() + "toggle" + " " + module.getName()))));

            index++;
        }

        msg.appendSibling(new TextComponentString(ChatFormatting.GRAY + "!"));
        mc.ingameGUI.getChatGUI().printChatMessage(msg);
    }
}