package com.github.kelemen.brazier.actions;

import com.github.kelemen.brazier.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jtrim.utils.ExceptionHelper;

public interface TargetedActionCondition<Actor, Target> {
    public static final TargetedActionCondition<Object, Object> ANY = (world, source, target) -> true;

    public boolean applies(World world, Actor actor, Target target);

    public static <Source, Target> TargetedActionCondition<Source, Target> and(
            TargetedActionCondition<? super Source, ? super Target> filter1,
            TargetedActionCondition<? super Source, ? super Target> filter2) {
        return (World world, Source source, Target target) -> {
            return filter1.applies(world, source, target) && filter2.applies(world, source, target);
        };
    }

    public static <Actor, Target> TargetedActionCondition<Actor, Target> merge(
            Collection<? extends TargetedActionCondition<Actor, Target>> filters) {
        List<TargetedActionCondition<Actor, Target>> filtersCopy = new ArrayList<>(filters);
        ExceptionHelper.checkNotNullElements(filtersCopy, "filters");

        int count = filtersCopy.size();
        if (count == 0) {
            return (world, actor, target) -> true;
        }
        if (count == 1) {
            return filtersCopy.get(0);
        }

        return (World world, Actor actor, Target target) -> {
            for (TargetedActionCondition<Actor, Target> filter: filtersCopy) {
                if (!filter.applies(world, actor, target)) {
                    return false;
                }
            }
            return true;
        };
    }
}
