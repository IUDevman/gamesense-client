package com.gamesense.client.command.commands;

import com.gamesense.client.command.Command;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

//Credit: Seth - SeppukuDevelopment
public class ModsCommand extends Command {
    @Override
    public String[] getAlias() {
        return new String[]{"modules", "mods"};
    }

    @Override
    public String getSyntax() {
        return "modules";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        final int size = ModuleManager.getModules().size();

        final TextComponentString msg = new TextComponentString("\2477Modules: " + "\247f ");

        for (int i = 0; i < size; i++) {
            final Module mod = ModuleManager.getModules().get(i);
            if (mod != null) {
                msg.appendSibling(new TextComponentString((mod.isEnabled() ? "\247a" : "\247c") + mod.getName() + "\2477" + ((i == size - 1) ? "" : ", "))
                        .setStyle(new Style()
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(mod.getCategory().name())))
                                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Command.getPrefix() + "toggle" + " " + mod.getName()))));
            }
        }
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
    }
}
