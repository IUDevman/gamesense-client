package dev.gamesense.backend.event.handler.imp;

/**
 * @author DarkMagician6
 * @since 08-03-2013
 */

public final class Priority {

    public static final byte HIGHEST = 0;
    public static final byte HIGH = 1;
    public static final byte MEDIUM = 2;
    public static final byte LOW = 3;
    public static final byte LOWEST = 4;

    public static final byte[] STANDARD_VALUES = new byte[]{
            HIGHEST,
            HIGH,
            MEDIUM,
            LOW,
            LOWEST
    };
}
