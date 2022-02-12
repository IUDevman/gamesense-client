package dev.gamesense.backend.event.handler.imp;

/**
 * @author DarkMagician6
 * @since 08-27-2013
 */

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
