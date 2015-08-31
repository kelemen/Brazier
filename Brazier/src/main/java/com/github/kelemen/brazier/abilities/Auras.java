package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.BoardSide;
import com.github.kelemen.brazier.Hero;
import com.github.kelemen.brazier.Keyword;
import com.github.kelemen.brazier.LabeledEntity;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.Priority;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.WorldProperty;
import com.github.kelemen.brazier.actions.ActionUtils;
import com.github.kelemen.brazier.actions.TargetedActionCondition;
import com.github.kelemen.brazier.cards.Card;
import com.github.kelemen.brazier.minions.Minion;
import com.github.kelemen.brazier.parsing.NamedArg;
import com.github.kelemen.brazier.weapons.Weapon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.jtrim.utils.ExceptionHelper;

public final class Auras {
    public static final TargetedActionCondition<PlayerProperty, PlayerProperty> SAME_OWNER = (world, source, target) -> {
        return source.getOwner() == target.getOwner();
    };

    public static final TargetedActionCondition<PlayerProperty, PlayerProperty> SAME_OWNER_OTHERS = (world, source, target) -> {
        return source.getOwner() == target.getOwner() && source != target;
    };

    public static final TargetedActionCondition<PlayerProperty, Object> NOT_PLAYED_MINION_THIS_TURN = (world, source, target) -> {
        return source.getOwner().getMinionsPlayedThisTurn() == 0;
    };

    public static final TargetedActionCondition<PlayerProperty, Object> OWNER_HAS_WEAPON = (world, source, target) -> {
        return source.getOwner().tryGetWeapon() != null;
    };

    public static final TargetedActionCondition<PlayerProperty, PlayerProperty> NOT_SELF = (world, source, target) -> {
        return source != target;
    };

    public static TargetedActionCondition<Object, LabeledEntity> targetHasKeyword(@NamedArg("keywords") Keyword... keywords) {
        List<Keyword> keywordsCopy = new ArrayList<>(Arrays.asList(keywords));
        ExceptionHelper.checkNotNullElements(keywordsCopy, "keywords");

        return (World world, Object source, LabeledEntity target) -> {
            return target.getKeywords().containsAll(keywordsCopy);
        };
    }

    public static TargetedActionCondition<Object, LabeledEntity> targetDoesntHaveKeyword(@NamedArg("keywords") Keyword... keywords) {
        Predicate<LabeledEntity> targetFilter = ActionUtils.excludedKeywordsFilter(keywords);

        return (World world, Object source, LabeledEntity target) -> {
            return targetFilter.test(target);
        };
    }

    public static TargetedActionCondition<PlayerProperty, Object> ownBoardHas(@NamedArg("keywords") Keyword... keywords) {
        Predicate<LabeledEntity> minionFilter = ActionUtils.includedKeywordsFilter(keywords);

        return (World world, PlayerProperty source, Object target) -> {
            BoardSide board = source.getOwner().getBoard();
            return board.findMinion(minionFilter) != null;
        };
    }

    public static TargetedActionCondition<PlayerProperty, Object> opponentsHandLarger(@NamedArg("limit") int limit) {
        return (World world, PlayerProperty source, Object target) -> {
            return source.getOwner().getOpponent().getHand().getCardCount() > limit;
        };
    }

    public static <Self> AuraTargetProvider<Self, Self> selfProvider() {
        return (world, source) -> {
            return Collections.singletonList(source);
        };
    };

    public static <Source, Target> Aura<Source, Target> buffAura(
            @NamedArg("buff") Buff<Target> buff) {
        return buffAura(Priority.HIGH_PRIORITY, buff);
    }

    public static <Source, Target> Aura<Source, Target> buffAura(
            @NamedArg("priority") Priority priority,
            @NamedArg("buff") Buff<? super Target> buff) {
        ExceptionHelper.checkNotNullArgument(buff, "buff");
        BuffArg buffArg = BuffArg.externalBuff(priority.getValue());
        return (world, source, target) -> buff.buff(world, target, buffArg);
    };

    public static <Self extends WorldProperty> ActivatableAbility<Self> selfAura(
            @NamedArg("aura") Aura<? super Self, ? super Self> aura) {
        return selfAura(TargetedActionCondition.ANY, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> selfAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Self> filter,
            @NamedArg("aura") Aura<? super Self, ? super Self> aura) {
        return aura(selfProvider(), filter, aura);
    }


    public static <Self extends PlayerProperty> ActivatableAbility<Self> sameBoardAura(
            @NamedArg("aura") Aura<? super Self, ? super Minion> aura) {
        return aura(AuraTargets.SAME_BOARD_MINION_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> sameBoardAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Minion> filter,
            @NamedArg("aura") Aura<? super Self, ? super Minion> aura) {
        return aura(AuraTargets.SAME_BOARD_MINION_PROVIDER, filter, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> boardAura(
            @NamedArg("aura") Aura<? super Self, ? super Minion> aura) {
        return aura(AuraTargets.MINION_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> boardAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Minion> filter,
            @NamedArg("aura") Aura<? super Self, ? super Minion> aura) {
        return aura(AuraTargets.MINION_PROVIDER, filter, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownCardAura(
            @NamedArg("aura") Aura<? super Self, ? super Card> aura) {
        return aura(AuraTargets.OWN_CARD_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownCardAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Card> filter,
            @NamedArg("aura") Aura<? super Self, ? super Card> aura) {
        return aura(AuraTargets.OWN_CARD_PROVIDER, filter, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> cardAura(
            @NamedArg("aura") Aura<? super Self, ? super Card> aura) {
        return aura(AuraTargets.CARD_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> cardAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Card> filter,
            @NamedArg("aura") Aura<? super Self, ? super Card> aura) {
        return aura(AuraTargets.CARD_PROVIDER, filter, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownHeroAura(
            @NamedArg("aura") Aura<? super Self, ? super Hero> aura) {
        return aura(AuraTargets.OWN_HERO_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownHeroAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Hero> filter,
            @NamedArg("aura") Aura<? super Self, ? super Hero> aura) {
        return aura(AuraTargets.OWN_HERO_PROVIDER, filter, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> heroAura(
            @NamedArg("aura") Aura<? super Self, ? super Hero> aura) {
        return aura(AuraTargets.HERO_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> heroAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Hero> filter,
            @NamedArg("aura") Aura<? super Self, ? super Hero> aura) {
        return aura(AuraTargets.HERO_PROVIDER, filter, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownPlayerAura(
            @NamedArg("aura") Aura<? super Self, ? super Player> aura) {
        return aura(AuraTargets.OWN_PLAYER_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownPlayerAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Player> filter,
            @NamedArg("aura") Aura<? super Self, ? super Player> aura) {
        return aura(AuraTargets.OWN_PLAYER_PROVIDER, filter, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> playerAura(
            @NamedArg("aura") Aura<? super Self, ? super Player> aura) {
        return aura(AuraTargets.PLAYER_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> playerAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Player> filter,
            @NamedArg("aura") Aura<? super Self, ? super Player> aura) {
        return aura(AuraTargets.PLAYER_PROVIDER, filter, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownWeaponAura(
            @NamedArg("aura") Aura<? super Self, ? super Weapon> aura) {
        return aura(AuraTargets.OWN_WEAPON_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends PlayerProperty> ActivatableAbility<Self> ownWeaponAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Weapon> filter,
            @NamedArg("aura") Aura<? super Self, ? super Weapon> aura) {
        return aura(AuraTargets.OWN_WEAPON_PROVIDER, filter, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> weaponAura(
            @NamedArg("aura") Aura<? super Self, ? super Weapon> aura) {
        return aura(AuraTargets.WEAPON_PROVIDER, TargetedActionCondition.ANY, aura);
    }

    public static <Self extends WorldProperty> ActivatableAbility<Self> weaponAura(
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Weapon> filter,
            @NamedArg("aura") Aura<? super Self, ? super Weapon> aura) {
        return aura(AuraTargets.WEAPON_PROVIDER, filter, aura);
    }

    public static <Self extends WorldProperty, Target> ActivatableAbility<Self> aura(
            @NamedArg("target") AuraTargetProvider<? super Self, ? extends Target> target,
            @NamedArg("filter") TargetedActionCondition<? super Self, ? super Target> filter,
            @NamedArg("aura") Aura<? super Self, ? super Target> aura) {

        ExceptionHelper.checkNotNullArgument(target, "target");
        ExceptionHelper.checkNotNullArgument(filter, "filter");
        ExceptionHelper.checkNotNullArgument(aura, "aura");

        return (Self self) -> self.getWorld().addAura(new TargetedActiveAura<>(self, target, filter, aura));
    }

    private Auras() {
        throw new AssertionError();
    }
}
