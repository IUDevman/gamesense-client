package com.gamesense.client.clickgui.components;

import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.theme.Renderer;

public class GameSenseToggleMessage extends FocusableComponent {

    protected final Module module;

    public GameSenseToggleMessage(Renderer renderer, Module module) {
        super("Toggle Msgs", null, renderer);
        this.module = module;
    }

    @Override
    public void render(Context context) {
        super.render(context);

        renderer.renderTitle(context, title, hasFocus(context), module.isToggleMsg());
    }

    @Override
    public void handleButton(Context context, int button) {
        super.handleButton(context, button);

        if (button == Interface.LBUTTON && context.isClicked()) {
            module.setToggleMsg(!module.isToggleMsg());
        }
    }
}