package dev.gamesense.misc.setting;

/**
 * @author IUDevman
 * @since 02-13-2022
 */

public interface Setting<T> {

    String getName();

    T getDefaultValue();

    T getValue();

    void setValue(T value);

    default void reset() {
        setValue(getDefaultValue());
    }
}
