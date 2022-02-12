package dev.gamesense.client.module;

import dev.gamesense.GameSense;
import dev.gamesense.misc.Global;
import org.lwjgl.input.Keyboard;

import java.lang.annotation.*;

/**
 * @author IUDevman
 * @since 02-11-2022
 */

@SuppressWarnings("EmptyMethod")
public abstract class Module implements Global {

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Info {
        String name();

        Category category();

        int bind() default Keyboard.KEY_NONE;

        boolean drawn() default true;

        boolean messages() default false;

        boolean enabled() default false;
    }

    private Info getInfo() {
        return getClass().getAnnotation(Info.class);
    }

    private final String name = getInfo().name();
    private final Category category = getInfo().category();
    private int bind = getInfo().bind();
    private boolean drawn = getInfo().drawn();
    private boolean messages = getInfo().messages();
    private boolean enabled = getInfo().enabled();

    public void reset() {
        this.bind = getInfo().bind();
        this.drawn = getInfo().drawn();
        this.messages = getInfo().messages();
        this.enabled = getInfo().enabled();
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public int getBind() {
        return this.bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean isDrawn() {
        return this.drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public boolean isMessages() {
        return this.messages;
    }

    public void setMessages(boolean messages) {
        this.messages = messages;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) enable();
        else disable();
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }

    protected void enable() {
        this.enabled = true;
        GameSense.INSTANCE.EVENT_HANDLER.register(this);
        if (!this.isNull()) {
            onEnable();
            //todo: send message
        }
    }

    protected void disable() {
        this.enabled = false;
        GameSense.INSTANCE.EVENT_HANDLER.unregister(this);
        if (!this.isNull()) {
            onDisable();
            //todo: send message
        }
    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    public void onTick() {

    }

    public void onRender3D() {

    }
}
