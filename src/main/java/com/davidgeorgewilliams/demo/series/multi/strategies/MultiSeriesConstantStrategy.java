package com.davidgeorgewilliams.demo.series.multi.strategies;

import com.davidgeorgewilliams.demo.series.multi.MultiSeriesResult;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class MultiSeriesConstantStrategy implements MultiSeriesStrategy {
    int value;
    int howMany;

    public static MultiSeriesConstantStrategy of(final int value, final int howMany) {
        return new MultiSeriesConstantStrategy(value, howMany);
    }

    @Override
    public List<Integer> getRequestSizes(@NonNull final String state) {
        return Collections.nCopies(howMany, value);
    }

    @Override
    public void updateStrategy(@NonNull final String state,
                               @NonNull final String nextState,
                               @NonNull final MultiSeriesResult<?, ?> result) {
    }
}
