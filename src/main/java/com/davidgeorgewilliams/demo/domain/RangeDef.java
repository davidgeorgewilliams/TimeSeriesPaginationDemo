package com.davidgeorgewilliams.demo.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class RangeDef {
    int low;
    int high;
    int step;

    public static RangeDef of(final int low, final int high, final int step) {
        return new RangeDef(low, high, step);
    }

    public static RangeDef of(final int low, final int high) {
        return new RangeDef(low, high, 1);
    }

    public List<Integer> toList() {
        final List<Integer> range = new ArrayList<>();
        for (int i = low; i < high; i += step) {
            range.add(i);
        }
        return range;
    }
}
