package com.gamesense.client.commands2;

import net.minecraft.client.Minecraft;

/**
 * @Author Hoosiers on 11/04/2020
 */

public abstract class Command {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    public static String commandPrefix = "-";

    String commandName;
    String[] commandAlias;
    String commandSyntax;

    public Command(String commandName){
        this.commandName = commandName;
    }

    public static String getCommandPrefix(){
        return commandPrefix;
    }

    public String getCommandName(){
        return this.commandName;
    }

    public String getCommandSyntax(){
        return this.commandSyntax;
    }

    public String[] getCommandAlias(){
        return this.commandAlias;
    }

    public static void setCommandPrefix(String prefix){
        commandPrefix = prefix;
    }

    public void setCommandSyntax(String syntax){
        this.commandSyntax = syntax;
    }

    public void setCommandAlias(String[] alias){
        this.commandAlias = alias;
    }

    public void onCommand(String command, String[] message) throws Exception{

    }
}