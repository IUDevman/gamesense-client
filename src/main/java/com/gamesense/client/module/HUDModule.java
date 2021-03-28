package com.gamesense.client.module;

import com.gamesense.client.GameSense;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.theme.Theme;
import java.awt.Point;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lukflug
 */

public abstract class HUDModule extends Module {

    protected FixedComponent component;
    protected Point position = new Point(getDeclaration().posX(), getDeclaration().posZ());

    private Declaration getDeclaration() {
        return getClass().getAnnotation(Declaration.class);
    }

    public abstract void populate(Theme theme);

    public FixedComponent getComponent() {
        return component;
    }

    public void resetPosition() {
        component.setPosition(GameSense.INSTANCE.gameSenseGUI.guiInterface, position);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Declaration {
        int posX();

        int posZ();
    }
}