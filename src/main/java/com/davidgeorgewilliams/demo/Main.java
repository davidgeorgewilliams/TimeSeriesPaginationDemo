package com.davidgeorgewilliams.demo;

import com.davidgeorgewilliams.demo.domain.RangeDef;
import com.davidgeorgewilliams.demo.series.SeriesItem;
import com.davidgeorgewilliams.demo.series.multi.MultiSeries;
import com.davidgeorgewilliams.demo.series.multi.MultiSeriesResult;
import com.davidgeorgewilliams.demo.series.multi.strategies.MultiSeriesConstantStrategy;
import com.davidgeorgewilliams.demo.series.multi.strategies.MultiSeriesQLearningStrategy;
import com.davidgeorgewilliams.demo.series.multi.strategies.MultiSeriesStrategy;
import com.davidgeorgewilliams.demo.series.time.InMemoryTimeSeries;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import static com.davidgeorgewilliams.demo.utils.MathUtils.randomBetween;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Slf4j
public class Main {
    private static final String SINGLETON_STATE = "singletonState";

    public static void main(String[] args) throws Exception {

        final MultiSeriesConstantStrategy multiSeriesConstantStrategy = MultiSeriesConstantStrategy.of(100, 2);

        processMultiSeriesStrategy(multiSeriesConstantStrategy, "results/data/constantStrategy.csv");

        final List<RangeDef> rangeDefs = List.of(RangeDef.of(5, 100), RangeDef.of(5, 100));
        final MultiSeriesQLearningStrategy multiSeriesQLearningStrategy = MultiSeriesQLearningStrategy.of(rangeDefs);

        processMultiSeriesStrategy(multiSeriesQLearningStrategy, "results/data/qLearningStrategy.csv");
    }

    private static MultiSeries<Instant, Void> createMultiSeries() {
        final long toMillis = System.currentTimeMillis();
        final long fromMillis = toMillis - 1_000_000;

        final long[] seriesAMillis = randomBetween(fromMillis, toMillis, 10_000);
        final long[] seriesBMillis = randomBetween(fromMillis, toMillis, 100_000);

        final InMemoryTimeSeries<Void> inMemoryTimeSeriesA = InMemoryTimeSeries.of(seriesAMillis);
        final InMemoryTimeSeries<Void> inMemoryTimeSeriesB = InMemoryTimeSeries.of(seriesBMillis);

        return MultiSeries.of(List.of(inMemoryTimeSeriesA, inMemoryTimeSeriesB));
    }

    private static void processMultiSeriesStrategy(@NonNull final MultiSeriesStrategy multiSeriesStrategy,
                                                   @NonNull final String pathToReport) throws IOException {
        @Cleanup BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(pathToReport), StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING, WRITE);
        final String headerString = "iteration,reward\n";
        bufferedWriter.write(headerString, 0, headerString.length());
        MultiSeries<Instant, Void> multiSeries = createMultiSeries();
        Instant start = Instant.now();
        for (int i = 0; i < 10000; i++) {
            final List<Integer> requestSizes = multiSeriesStrategy.getRequestSizes(SINGLETON_STATE);
            final MultiSeriesResult<Instant, Void> result = multiSeries.lookup(start, requestSizes);
            multiSeriesStrategy.updateStrategy(SINGLETON_STATE, SINGLETON_STATE, result);
            final int delivered = result.seriesItems().size();
            int discarded = 0;
            for (final int truncatedValue : result.truncatedCount().values()) {
                discarded += truncatedValue;
            }
            final boolean done = delivered == 0;
            if (done) {
                multiSeries = createMultiSeries();
                start = Instant.now();
            } else {
                final List<SeriesItem<Instant, Void>> seriesItems = result.seriesItems();
                start = seriesItems.get(seriesItems.size() - 1).key().minusMillis(1);
            }
            final String reportString = String.format("%s,%s\n", i, delivered - discarded);
            bufferedWriter.write(reportString, 0, reportString.length());
            if (i % 100 == 0) {
                log.info(String.format("%s %s %s %s %s", i, start, delivered, discarded, requestSizes));
            }
        }
    }
}
