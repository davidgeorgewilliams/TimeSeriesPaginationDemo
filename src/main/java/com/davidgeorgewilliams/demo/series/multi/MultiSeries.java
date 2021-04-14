package com.davidgeorgewilliams.demo.series.multi;

import com.davidgeorgewilliams.demo.series.Series;
import com.davidgeorgewilliams.demo.series.SeriesItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import static java.util.Collections.reverseOrder;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class MultiSeries<K extends Comparable<K>, V> {
    List<Series<K, V>> seriesList;

    public static <K extends Comparable<K>, V> MultiSeries<K, V> of(@NonNull final List<Series<K, V>> seriesList) {
        return new MultiSeries<>(seriesList);
    }

    public MultiSeriesResult<K, V> lookup(@NonNull final K start, @NonNull final List<Integer> howMany) {
        if (howMany.size() != seriesList().size()) {
            throw new IllegalArgumentException("Must specify how many from each series");
        }

        final List<List<SeriesItem<K, V>>> seriesItemsList = new ArrayList<>();

        for (int i = 0; i < howMany.size(); i++) {
            seriesItemsList.add(seriesList().get(i).lookup(start, howMany.get(i)));
        }

        SeriesItem<K, V> maxMinSeriesItem = null;

        for (final List<SeriesItem<K, V>> seriesItems : seriesItemsList) {
            if (seriesItems.size() > 0) {
                final SeriesItem<K, V> lastSeriesItem = seriesItems.get(seriesItems.size() - 1);
                if (maxMinSeriesItem == null || lastSeriesItem.key().compareTo(maxMinSeriesItem.key()) > 0) {
                    maxMinSeriesItem = lastSeriesItem;
                }
            }
        }

        final SeriesItem<K, V> finalMaxMinSeriesItem = maxMinSeriesItem;

        final List<SeriesItem<K, V>> resultSeriesItems = new ArrayList<>();

        final Map<Integer, Integer> truncatedCount = new HashMap<>();

        for (int i = 0; i < seriesItemsList.size(); i++) {
            final List<SeriesItem<K, V>> seriesItems = seriesItemsList.get(i);

            final List<SeriesItem<K, V>> filteredSeriesItems = seriesItems.stream().filter(seriesItem ->
                    seriesItem.key().compareTo(finalMaxMinSeriesItem.key()) >= 0).collect(Collectors.toList());

            truncatedCount.put(i, seriesItems.size() - filteredSeriesItems.size());

            resultSeriesItems.addAll(filteredSeriesItems);
        }

        final List<SeriesItem<K, V>> sortedResultSeriesItems = resultSeriesItems.stream()
                .sorted(Comparator.comparing(SeriesItem::key, reverseOrder())).collect(Collectors.toList());

        return MultiSeriesResult.of(sortedResultSeriesItems, truncatedCount);
    }
}
