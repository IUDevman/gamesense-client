package com.gamesense.client.clickgui;

import com.gamesense.api.setting.Setting;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.settings.SimpleToggleable;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.theme.Theme;
import org.lwjgl.input.Keyboard;

/**
 * @author 0b00101010
 */
public class TextComponent extends CollapsibleContainer {
    public TextComponent(Theme theme, Setting.Text stringHolder, Animation animation) {
        super(stringHolder.getName(), null, theme.getContainerRenderer(), new SimpleToggleable(false), animation, null);

        this.addComponent(new TextBox(theme.getComponentRenderer(), stringHolder));
    }

    private static class TextBox extends FocusableComponent {
        private boolean attached = false;

        private StringBuilder input = new StringBuilder();
        private final Setting.Text textHolder;

        public TextBox(Renderer renderer, Setting.Text textHolder) {
            super("", "Input", renderer);
            this.textHolder = textHolder;
        }

        public void render(Context context) {
            super.render(context);

            StringBuilder toRender;
            if (attached)
                toRender = new StringBuilder(input);
            else
                toRender = new StringBuilder(textHolder.getValue());

            // shifts text along so we can see what we are writing
            int maxWidth = context.getRect().width;
            int textWidth = context.getInterface().getFontWidth(toRender.toString());

            while (textWidth > maxWidth) {
                toRender.deleteCharAt(0);
                textWidth = context.getInterface().getFontWidth(toRender.toString());
            }

            this.renderer.renderTitle(context, toRender.toString(), true, attached);
        }

        public void handleButton(Context context, int button) {
            super.handleButton(context, button);
            if (button == 0 && context.isClicked()) {
                this.attached = !attached;
                if (attached)
                    this.input = new StringBuilder(textHolder.getValue());
            }
        }

        @Override
        public void handleKey(Context context, int scancode) {
            super.handleKey(context, scancode);
            if (attached) {
                // didn't add all characters, just the ones necessary
                // to get it working
                switch (scancode) {
                    case Keyboard.KEY_DELETE:
                        input = new StringBuilder();
                    case Keyboard.KEY_BACK:
                        if (input.length() == 0)
                            break;
                        input.deleteCharAt(input.length() - 1);
                    case Keyboard.CHAR_NONE:
                    case Keyboard.KEY_LSHIFT:
                    case Keyboard.KEY_RSHIFT:
                    case Keyboard.KEY_LCONTROL:
                    case Keyboard.KEY_RCONTROL:
                        break;
                    case Keyboard.KEY_RETURN:
                        textHolder.setValue(input.toString());
                        this.attached = false;
                        break;
                    default:
                        input.append(Keyboard.getEventCharacter());
                }
            }
        }
    }
}
