package com.davidgeorgewilliams.demo.series;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class SeriesItem<K extends Comparable<K>, V> {
    K key;
    V value;

    public static <K extends Comparable<K>, V> SeriesItem<K, V> of(@NonNull final K key,
                                                                   final V value) {
        return new SeriesItem<>(key, value);
    }
}
