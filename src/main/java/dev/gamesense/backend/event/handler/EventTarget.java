package dev.gamesense.backend.event.handler;

import dev.gamesense.backend.event.handler.imp.Priority;

import java.lang.annotation.*;

/**
 * @author DarkMagician6
 * @since 07-30-2013
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventTarget {

    byte value() default Priority.MEDIUM;
}
