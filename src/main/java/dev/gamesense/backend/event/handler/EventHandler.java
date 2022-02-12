package dev.gamesense.backend.event.handler;

import dev.gamesense.GameSense;
import dev.gamesense.backend.event.handler.imp.Event;
import dev.gamesense.backend.event.handler.imp.Priority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author DarkMagician6
 * @since 02-02-2014
 */

public final class EventHandler {

    private final HashMap<Class<? extends Event>, List<MethodData>> REGISTRY_MAP = new HashMap<>();

    public EventHandler() {
        GameSense.INSTANCE.LOGGER.info("EventHandler");
    }

    public void register(Object object) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            if (!isMethodBad(method)) {
                register(method, object);
            }
        }
    }

    public void unregister(Object object) {
        for (final List<MethodData> dataList : this.REGISTRY_MAP.values()) {
            dataList.removeIf(data -> data.getSource().equals(object));
        }

        cleanMap(true);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Event call(final Event event) {
        List<MethodData> dataList = this.REGISTRY_MAP.get(event.getClass());

        if (dataList != null) {
            for (final MethodData data : dataList) {
                invoke(data, event);
            }
        }

        return event;
    }

    @SuppressWarnings("unchecked")
    private void register(Method method, Object object) {
        Class<? extends Event> indexClass = (Class<? extends Event>) method.getParameterTypes()[0];

        final MethodData data = new MethodData(object, method, method.getAnnotation(EventTarget.class).value());

        if (!data.getTarget().isAccessible()) {
            data.getTarget().setAccessible(true);
        }

        if (this.REGISTRY_MAP.containsKey(indexClass)) {
            if (!this.REGISTRY_MAP.get(indexClass).contains(data)) {
                this.REGISTRY_MAP.get(indexClass).add(data);
                sortListValue(indexClass);
            }
        } else {
            this.REGISTRY_MAP.put(indexClass, new CopyOnWriteArrayList<MethodData>() {
                private static final long serialVersionUID = 666L;

                {
                    add(data);
                }
            });
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void cleanMap(boolean onlyEmptyEntries) {
        Iterator<Map.Entry<Class<? extends Event>, List<MethodData>>> mapIterator = this.REGISTRY_MAP.entrySet().iterator();

        while (mapIterator.hasNext()) {
            if (!onlyEmptyEntries || mapIterator.next().getValue().isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    private void invoke(MethodData data, Event argument) {
        try {
            data.getTarget().invoke(data.getSource(), argument);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
            GameSense.INSTANCE.LOGGER.warn("Failed to invoke event!");
        }
    }

    private void sortListValue(Class<? extends Event> indexClass) {
        List<MethodData> sortedList = new CopyOnWriteArrayList<>();

        for (final byte priority : Priority.STANDARD_VALUES) {
            for (final MethodData data : this.REGISTRY_MAP.get(indexClass)) {
                if (data.getPriority() == priority) {
                    sortedList.add(data);
                }
            }
        }

        this.REGISTRY_MAP.put(indexClass, sortedList);
    }

    private boolean isMethodBad(Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventTarget.class);
    }

    private static final class MethodData {

        private final Object source;
        private final Method target;
        private final byte priority;

        public MethodData(Object source, Method target, byte priority) {
            this.source = source;
            this.target = target;
            this.priority = priority;
        }

        public Object getSource() {
            return this.source;
        }

        public Method getTarget() {
            return this.target;
        }

        public byte getPriority() {
            return this.priority;
        }
    }
}
