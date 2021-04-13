package com.davidgeorgewilliams.demo.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class ItemCombinator<T> {
    int[] state;
    List<List<T>> items;

    public static <T> ItemCombinator<T> of(@NonNull final List<List<T>> items) {
        int[] state = new int[items.size()];
        Arrays.fill(state, 0);
        return new ItemCombinator<>(state, items);
    }

    public List<T> next() {
        if (state[state.length - 1] == items.get(state.length - 1).size()) {
            return null;
        }

        final List<T> combination = new ArrayList<>();

        for (int i = 0; i < state.length; i++) {
            combination.add(items.get(i).get(state[i]));
        }

        for (int i = 0; i < state.length; i++) {
            if (i == state.length - 1 && state[i] == items.get(i).size() - 1) {
                state[i] += 1;
                break;
            }
            if (state[i] < items.get(i).size() - 1) {
                state[i] += 1;
                break;
            }
            state[i] = 0;
        }

        return combination;
    }
}
