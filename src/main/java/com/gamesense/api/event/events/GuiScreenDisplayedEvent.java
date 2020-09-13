package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenDisplayedEvent extends GameSenseEvent {
    private final GuiScreen guiScreen;

    public GuiScreenDisplayedEvent(GuiScreen screen) {
        super();
        guiScreen = screen;
    }

    public GuiScreen getScreen() {
        return guiScreen;
    }
}