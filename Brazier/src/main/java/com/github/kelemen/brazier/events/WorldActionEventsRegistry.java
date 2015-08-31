package com.github.kelemen.brazier.events;

import com.github.kelemen.brazier.Priority;
import com.github.kelemen.brazier.actions.WorldObjectAction;
import java.util.function.Predicate;

public interface WorldActionEventsRegistry<T> {
    public default UndoableUnregisterRef addAction(WorldObjectAction<T> action) {
        return addAction(Priority.NORMAL_PRIORITY.getValue(), action);
    }

    public default UndoableUnregisterRef addAction(
            int priority,
            WorldObjectAction<T> action) {
        return addAction(priority, (arg) -> true, action);
    }

    public UndoableUnregisterRef addAction(
            int priority,
            Predicate<? super T> condition,
            WorldObjectAction<? super T> action);
}
