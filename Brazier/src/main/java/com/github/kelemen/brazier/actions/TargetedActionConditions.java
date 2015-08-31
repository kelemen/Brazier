package com.github.kelemen.brazier.actions;

import com.github.kelemen.brazier.Keywords;
import com.github.kelemen.brazier.LabeledEntity;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.TargetableCharacter;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.parsing.NamedArg;
import java.util.function.Predicate;
import org.jtrim.utils.ExceptionHelper;

public final class TargetedActionConditions {
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_DEMON = forTarget(EntityFilters.isDemon());
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_MECH = forTarget(EntityFilters.isMech());
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_BEAST = forTarget(EntityFilters.isBeast());
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_DRAGON = forTarget(EntityFilters.isDragon());
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_PIRATE = forTarget(EntityFilters.isPirate());
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_MURLOC = forTarget(EntityFilters.isMurloc());

    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_MINION = forTarget(EntityFilters.withKeywords(Keywords.MINION));
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_IS_SPELL = forTarget(EntityFilters.withKeywords(Keywords.SPELL));
    public static final TargetedActionCondition<Object, LabeledEntity> TARGET_HAS_BATTLE_CRY = forTarget(EntityFilters.withKeywords(Keywords.BATTLE_CRY));

    public static final TargetedActionCondition<PlayerProperty, Object> OWN_BOARD_HAS_MECH = forActor(EntityFilters.ownBoardHas(EntityFilters.isMech()));

    public static final TargetedActionCondition<TargetableCharacter, Object> SELF_DAMAGED = (world, source, target) -> {
        return source.isDamaged();
    };

    public static <Actor, Target> TargetedActionCondition<Actor, Target> forActor(
            @NamedArg("filter") Predicate<? super Actor> filter) {
        ExceptionHelper.checkNotNullArgument(filter, "filter");

        return (World world, Actor actor, Target target) -> {
            return filter.test(actor);
        };
    }

    public static <Actor, Target> TargetedActionCondition<Actor, Target> forTarget(
            @NamedArg("filter") Predicate<? super Target> filter) {
        ExceptionHelper.checkNotNullArgument(filter, "filter");

        return (World world, Actor actor, Target target) -> {
            return filter.test(target);
        };
    }

    public static <Actor extends PlayerProperty, Target extends PlayerProperty> TargetedActionCondition<Actor, Target> sameOwner() {
        return (World world, Actor actor, Target target) -> {
            return actor.getOwner() == target.getOwner();
        };
    }

    private TargetedActionConditions() {
        throw new AssertionError();
    }
}
