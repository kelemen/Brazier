package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.Hero;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.Priority;
import com.github.kelemen.brazier.WorldProperty;
import com.github.kelemen.brazier.actions.TargetedActionCondition;
import com.github.kelemen.brazier.cards.Card;
import com.github.kelemen.brazier.minions.Minion;
import com.github.kelemen.brazier.parsing.NamedArg;
import com.github.kelemen.brazier.weapons.Weapon;
import java.util.Collections;
import org.jtrim.utils.ExceptionHelper;

public final class Auras {
    private static <Self> AuraTargetProvider<Self, Self> selfProvider() {
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
