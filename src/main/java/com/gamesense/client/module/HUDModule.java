package com.gamesense.client.module;

import java.awt.Point;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gamesense.client.GameSense;
import com.lukflug.panelstudio.component.IFixedComponent;
import com.lukflug.panelstudio.theme.ITheme;

/**
 * @author lukflug
 */

public abstract class HUDModule extends Module {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Declaration {
        int posX();

        int posZ();
    }

    private Declaration getDeclaration() {
        return getClass().getAnnotation(Declaration.class);
    }

    public static final int LIST_BORDER=1;
    protected IFixedComponent component;
    protected Point position = new Point(getDeclaration().posX(), getDeclaration().posZ());

    public abstract void populate(ITheme theme);

    public IFixedComponent getComponent() {
        return component;
    }

    public void resetPosition() {
        component.setPosition(GameSense.INSTANCE.gameSenseGUI.guiInterface, position);
    }
}