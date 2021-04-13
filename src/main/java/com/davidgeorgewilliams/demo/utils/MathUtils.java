package com.davidgeorgewilliams.demo.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.random.Well512a;

@UtilityClass
public class MathUtils {

    public static long[] randomBetween(final long low,
                                       final long high,
                                       final int howMany) {
        final long delta = high - low;
        final long[] values = new long[howMany];
        final byte[] seed = new byte[256];
        new Random().nextBytes(seed);
        final Random random = new SecureRandom(seed);
        for (int i = 0; i < howMany; i++) {
            final double nextDouble = random.nextDouble();
            values[i] = low + (long) (nextDouble * delta);
        }
        return values;
    }

    public static int sample(@NonNull final int[] items) {
        final double[] weights = new double[items.length];
        Arrays.fill(weights, 1.0 / weights.length);
        return new EnumeratedIntegerDistribution(new Well512a(), items, weights).sample();
    }

    public static int sample(@NonNull final int[] items, @NonNull final double[] weights) {
        return new EnumeratedIntegerDistribution(new Well512a(), items, weights).sample();
    }

    public static int[] argMax(@NonNull final double[] values) {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (double value : values) {
            if (value >= maxValue) {
                maxValue = value;
            }
        }
        final List<Integer> argMaxList = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == maxValue) {
                argMaxList.add(i);
            }
        }
        final int[] argMaxArray = new int[argMaxList.size()];
        for (int i = 0; i < argMaxList.size(); i++) {
            argMaxArray[i] = argMaxList.get(i);
        }
        return argMaxArray;
    }
}
