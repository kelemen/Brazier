package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.actions.TargetedActionCondition;
import com.github.kelemen.brazier.actions.TargetedActionConditions;
import com.github.kelemen.brazier.minions.Minion;
import com.github.kelemen.brazier.parsing.NamedArg;

public final class MinionAbilities {
    public static ActivatableAbility<PlayerProperty> spellPower(@NamedArg("spellPower") int spellPower) {
        return (PlayerProperty self) -> {
            AuraAwareIntProperty playersSpellPower = self.getOwner().getSpellPower();
            return playersSpellPower.addExternalBuff(spellPower);
        };
    }

    public static ActivatableAbility<PlayerProperty> spellMultiplier(@NamedArg("mul") int mul) {
        return (PlayerProperty self) -> {
            AuraAwareIntProperty playersSpellPower = self.getOwner().getHeroDamageMultiplier();
            return playersSpellPower.addExternalBuff((prev) -> prev * mul);
        };
    }

    public static ActivatableAbility<Minion> neighboursAura(
            @NamedArg("aura") Aura<? super Minion, ? super Minion> aura) {
        return neighboursAura(TargetedActionCondition.ANY, aura);
    }

    public static ActivatableAbility<Minion> neighboursAura(
            @NamedArg("filter") TargetedActionCondition<? super Minion, ? super Minion> filter,
            @NamedArg("aura") Aura<? super Minion, ? super Minion> aura) {
        return Auras.aura(AuraTargets.NEIGHBOURS_MINION_PROVIDER, filter, aura);
    }

    public static ActivatableAbility<Minion> sameBoardOthersAura(
            @NamedArg("aura") Aura<? super Minion, ? super Minion> aura) {
        return sameBoardOthersAura(TargetedActionCondition.ANY, aura);
    }

    public static ActivatableAbility<Minion> sameBoardOthersAura(
            @NamedArg("filter") TargetedActionCondition<? super Minion, ? super Minion> filter,
            @NamedArg("aura") Aura<? super Minion, ? super Minion> aura) {
        return Auras.sameBoardAura(
                TargetedActionCondition.and(TargetedActionConditions.SAME_OWNER_OTHERS, filter),
                aura);
    }

    private MinionAbilities() {
        throw new AssertionError();
    }
}
