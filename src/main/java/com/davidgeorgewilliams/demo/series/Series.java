package com.davidgeorgewilliams.demo.series;

import java.util.List;

public interface Series<K extends Comparable<K>, V> {
    List<SeriesItem<K, V>> lookup(K start, K end);
    List<SeriesItem<K, V>> lookup(K start, int howMany);
    void insert(SeriesItem<K, V> seriesItem);
}
