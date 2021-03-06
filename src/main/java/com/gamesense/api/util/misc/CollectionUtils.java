package com.gamesense.api.util.misc;

import java.util.function.ToIntFunction;

public class CollectionUtils {
    public static <T> T maxOrNull(Iterable<T> iterable, ToIntFunction<T> block) {
        int value = Integer.MIN_VALUE;
        T maxElement = null;

        for (T element : iterable) {
            int newValue = block.applyAsInt(element);

            if (newValue > value) {
                value = newValue;
                maxElement = element;
            }
        }

        return maxElement;
    }
}