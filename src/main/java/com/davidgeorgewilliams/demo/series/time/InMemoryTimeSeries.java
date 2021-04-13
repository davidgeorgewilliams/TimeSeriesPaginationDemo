package com.davidgeorgewilliams.demo.series.time;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.davidgeorgewilliams.demo.series.SeriesItem;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class InMemoryTimeSeries<V> implements TimeSeries<V> {
    TreeMap<Instant, V> treeMap;

    public static <V> InMemoryTimeSeries<V> of() {
        return new InMemoryTimeSeries<>(new TreeMap<>(Comparator.reverseOrder()));
    }

    public static InMemoryTimeSeries<Void> of(@NonNull final long[] timeMillis) {
        final InMemoryTimeSeries<Void> inMemoryTimeSeries = InMemoryTimeSeries.of();
        for (final long millis : timeMillis) {
            inMemoryTimeSeries.insert(SeriesItem.of(Instant.ofEpochMilli(millis), null));
        }
        return inMemoryTimeSeries;
    }

    @Override
    public List<SeriesItem<Instant, V>> lookup(@NonNull final Instant start,
                                               @NonNull final Instant end) {
        final SortedMap<Instant, V> sortedMap = treeMap().subMap(start, end);
        return sortedMap.entrySet().stream()
                .map(entry -> SeriesItem.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SeriesItem<Instant, V>> lookup(@NonNull final Instant start,
                                               final int howMany) {
        final SortedMap<Instant, V> sortedMap = treeMap().tailMap(start, true);
        return sortedMap.entrySet().stream()
                .limit(howMany)
                .map(entry -> SeriesItem.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public void insert(@NonNull final SeriesItem<Instant, V> seriesItem) {
        treeMap().put(seriesItem.key(), seriesItem.value());
    }
}
