package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @Author Hoosiers on 11/05/2020
 */

public class ModulesCommand extends Command {

    public ModulesCommand() {
        super("Modules");

        setCommandSyntax(Command.getCommandPrefix() + "modules (click to toggle)");
        setCommandAlias(new String[]{
                "modules", "module", "modulelist", "mod", "mods"
        });
    }

    /** ported from the old commands, @Author Seth for Seppuku **/
    public void onCommand(String command, String[] message) throws Exception {
        int size = ModuleManager.getModules().size();
        TextComponentString msg = new TextComponentString("\2477Modules: " + "\247f ");

        for (int i = 0; i < size; i++) {
            Module module = ModuleManager.getModules().get(i);
            if (module != null) {
                msg.appendSibling(new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "\2477" + ((i == size - 1) ? "" : ", "))
                        .setStyle(new Style()
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(module.getCategory().name())))
                                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Command.getCommandPrefix() + "toggle" + " " + module.getName()))));
            }
        }
        msg.appendSibling(new TextComponentString(ChatFormatting.GRAY + "!"));
        mc.ingameGUI.getChatGUI().printChatMessage(msg);
    }
}