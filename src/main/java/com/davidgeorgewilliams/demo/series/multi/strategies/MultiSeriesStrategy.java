package com.davidgeorgewilliams.demo.series.multi.strategies;

import com.davidgeorgewilliams.demo.series.multi.MultiSeriesResult;
import lombok.NonNull;

import java.util.List;

public interface MultiSeriesStrategy {
    List<Integer> getRequestSizes(String state);
    void updateStrategy(@NonNull final String state, @NonNull final String nextState, @NonNull final MultiSeriesResult<?, ?> result);
}
