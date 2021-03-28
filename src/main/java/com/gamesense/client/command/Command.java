package com.gamesense.client.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minecraft.client.Minecraft;

/**
 * @Author Hoosiers on 11/04/2020
 */

public abstract class Command {

    protected static final Minecraft mc = Minecraft.getMinecraft();
    private final String name = getDeclaration().name();
    private final String[] alias = getDeclaration().alias();
    private final String syntax = getDeclaration().syntax();

    private Declaration getDeclaration() {
        return getClass().getAnnotation(Declaration.class);
    }

    public String getName() {
        return this.name;
    }

    public String getSyntax() {
        return CommandManager.getCommandPrefix() + this.syntax;
    }

    public String[] getAlias() {
        return this.alias;
    }

    public abstract void onCommand(String command, String[] message) throws Exception;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Declaration {
        String name();

        String syntax();

        String[] alias();
    }
}