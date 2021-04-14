package com.davidgeorgewilliams.demo.series.multi;

import com.davidgeorgewilliams.demo.series.SeriesItem;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class MultiSeriesResult<K extends Comparable<K>, V> {
    List<SeriesItem<K, V>> seriesItems;
    Map<Integer, Integer> truncatedCount;

    public static <K extends Comparable<K>, V> MultiSeriesResult<K, V> of(
            @NonNull final List<SeriesItem<K, V>> seriesItems,
            @NonNull final Map<Integer, Integer> truncatedCount) {
        return new MultiSeriesResult<>(seriesItems, truncatedCount);
    }
}
