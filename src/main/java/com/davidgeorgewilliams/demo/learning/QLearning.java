package com.davidgeorgewilliams.demo.learning;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.Setter;
import lombok.Synchronized;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import static com.davidgeorgewilliams.demo.utils.MathUtils.argMax;
import static com.davidgeorgewilliams.demo.utils.MathUtils.sample;

@Accessors(fluent = true, chain = true)
@Value
public class QLearning {
    @NonFinal @Setter double discountRate = 1.0;
    @NonFinal @Setter double explorationRate = 1.0;
    @NonFinal @Setter double explorationRateDecay = 0.999;
    @NonFinal @Setter double learningRate = 0.01;
    @NonFinal @Setter double learningRateDecay = 1.0;
    Map<String, Double> stateExplorationRates = new HashMap<>();
    Map<String, double[]> qTable = new HashMap<>();
    int[] actions;

    private QLearning(final int numActions) {
        final int[] actions = new int[numActions];
        for (int i = 0; i < numActions; i++) {
            actions[i] = i;
        }
        this.actions = actions;
    }

    public static QLearning of(final int numActions) {
        return new QLearning(numActions);
    }

    private double bellmanUpdate(final double reward, final double qValue, final double nextQValue) {
        return (1.0 - learningRate()) * qValue + learningRate() * (reward + discountRate() * nextQValue);
    }

    private double[] epsilonGreedyWeights(@NonNull final String state) {
        double explorationRate = stateExplorationRates().get(state);
        double[] samplingWeights = new double[actions().length];
        Arrays.fill(samplingWeights, explorationRate / actions().length);
        samplingWeights[sample(argMax(qTable().get(state)))] = 1.0 - explorationRate + explorationRate / actions().length;
        return samplingWeights;
    }

    @Synchronized
    public int nextAction(@NonNull final String state) {
        if (!qTable().containsKey(state)) {
            qTable().put(state, new double[actions().length]);
        }
        if (!stateExplorationRates().containsKey(state)) {
            stateExplorationRates().put(state, explorationRate());
        }
        final int action = sample(actions(), epsilonGreedyWeights(state));
        stateExplorationRates().put(state, stateExplorationRates().get(state) * explorationRateDecay());
        return action;
    }

    @Synchronized
    public QLearning learn(@NonNull final String state,
                           @NonNull final Integer action,
                           @NonNull final Double reward,
                           @NonNull final String nextState,
                           @NonNull final Boolean done) {
        if (!qTable().containsKey(state)) {
            qTable().put(state, new double[actions().length]);
        }
        if (done) {
            final double qValue = qTable().get(state)[action];
            final double nextQValue = 0.0;
            qTable().get(state)[action] = bellmanUpdate(reward, qValue, nextQValue);
        } else {
            final int nextGreedyAction;
            if (qTable().containsKey(nextState)) {
                nextGreedyAction = sample(argMax(qTable().get(nextState)));
            } else {
                nextGreedyAction = sample(actions());
                qTable().put(nextState, new double[actions().length]);
            }
            final double qValue = qTable().get(state)[action];
            final double nextQValue = qTable().get(nextState)[nextGreedyAction];
            qTable().get(state)[action] = bellmanUpdate(reward, qValue, nextQValue);
        }
        return learningRate(learningRate() * learningRateDecay());
    }
}
