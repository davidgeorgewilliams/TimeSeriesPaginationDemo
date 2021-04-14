package com.davidgeorgewilliams.demo.series.multi.strategies;

import com.davidgeorgewilliams.demo.domain.ItemCombinator;
import com.davidgeorgewilliams.demo.domain.RangeDef;
import com.davidgeorgewilliams.demo.learning.QLearning;
import com.davidgeorgewilliams.demo.series.multi.MultiSeriesResult;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class MultiSeriesQLearningStrategy implements MultiSeriesStrategy {
    QLearning qLearning;
    List<List<Integer>> requestSizes;
    @NonFinal @Setter int lastAction;

    public static MultiSeriesQLearningStrategy of(@NonNull final List<RangeDef> rangeDefs) {
        final List<List<Integer>> rangeItems = new ArrayList<>();
        for (final RangeDef rangeDef : rangeDefs) {
            final List<Integer> range = rangeDef.toList();
            rangeItems.add(range);
        }
        final ItemCombinator<Integer> itemCombinator = ItemCombinator.of(rangeItems);
        final List<List<Integer>> requestSizes = new ArrayList<>();
        while (true) {
            final List<Integer> items = itemCombinator.next();
            if (items == null) {
                break;
            }
            requestSizes.add(items);
        }
        final QLearning qLearning = QLearning.of(requestSizes.size());
        return new MultiSeriesQLearningStrategy(qLearning, requestSizes);
    }

    @Override
    public List<Integer> getRequestSizes(@NonNull final String state) {
        final int action = qLearning.nextAction(state);
        lastAction(action);
        return requestSizes().get(action);
    }

    @Override
    public void updateStrategy(@NonNull final String state,
                               @NonNull final String nextState,
                               @NonNull final MultiSeriesResult<?, ?> result) {
        final int delivered = result.seriesItems().size();
        int discarded = 0;
        for (final int truncatedValue : result.truncatedCount().values()) {
            discarded += truncatedValue;
        }
        final int score = delivered - discarded;
        final boolean done = delivered == 0;
        qLearning().learn(state, lastAction(), 1. * score, nextState, done);
    }
}
