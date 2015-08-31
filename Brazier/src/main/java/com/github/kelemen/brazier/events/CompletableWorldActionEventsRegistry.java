package com.github.kelemen.brazier.events;

import com.github.kelemen.brazier.Priority;

public interface CompletableWorldActionEventsRegistry<T> {
    public default UndoableUnregisterRef addListener(CompletableWorldObjectAction<? super T> listener) {
        return addListener(Priority.NORMAL_PRIORITY.getValue(), listener);
    }

    public UndoableUnregisterRef addListener(int priority, CompletableWorldObjectAction<? super T> listener);
}
