package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.BoardLocationRef;
import com.github.kelemen.brazier.BornEntity;
import com.github.kelemen.brazier.Keyword;
import com.github.kelemen.brazier.Keywords;
import com.github.kelemen.brazier.LabeledEntity;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.SummonLocationRef;
import com.github.kelemen.brazier.TargetableCharacter;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.actions.ActionUtils;
import com.github.kelemen.brazier.minions.Minion;
import com.github.kelemen.brazier.parsing.NamedArg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.jtrim.utils.ExceptionHelper;

public final class MinionAuras {
    public static final AuraFilter<Object, LabeledEntity> TARGET_IS_DEMON = Auras.targetHasKeyword(Keywords.RACE_DEMON);
    public static final AuraFilter<Object, LabeledEntity> TARGET_IS_MECH = Auras.targetHasKeyword(Keywords.RACE_MECH);
    public static final AuraFilter<Object, LabeledEntity> TARGET_IS_BEAST = Auras.targetHasKeyword(Keywords.RACE_BEAST);
    public static final AuraFilter<Object, LabeledEntity> TARGET_IS_DRAGON = Auras.targetHasKeyword(Keywords.RACE_DRAGON);
    public static final AuraFilter<Object, LabeledEntity> TARGET_IS_PIRATE = Auras.targetHasKeyword(Keywords.RACE_PIRATE);
    public static final AuraFilter<Object, LabeledEntity> TARGET_IS_MURLOC = Auras.targetHasKeyword(Keywords.RACE_MURLOC);

    public static final AuraFilter<PlayerProperty, Object> OWN_BOARD_HAS_DEMON = Auras.ownBoardHas(Keywords.RACE_DEMON);
    public static final AuraFilter<PlayerProperty, Object> OWN_BOARD_HAS_MECH = Auras.ownBoardHas(Keywords.RACE_MECH);
    public static final AuraFilter<PlayerProperty, Object> OWN_BOARD_HAS_BEAST = Auras.ownBoardHas(Keywords.RACE_BEAST);
    public static final AuraFilter<PlayerProperty, Object> OWN_BOARD_HAS_DRAGON = Auras.ownBoardHas(Keywords.RACE_DRAGON);

    public static final AuraFilter<TargetableCharacter, Object> SELF_DAMAGED = (world, source, target) -> {
        return source.isDamaged();
    };

    public static final AuraTargetProvider<Object, Minion> MINION_PROVIDER = (World world, Object source) -> {
        List<Minion> result = new ArrayList<>(2 * Player.MAX_BOARD_SIZE);
        world.getPlayer1().getBoard().collectMinions(result, Minion::notScheduledToDestroy);
        world.getPlayer2().getBoard().collectMinions(result, Minion::notScheduledToDestroy);
        BornEntity.sortEntities(result);
        return result;
    };

    public static final AuraTargetProvider<PlayerProperty, Minion> SAME_BOARD_MINION_PROVIDER = (world, source) -> {
        return source.getOwner().getBoard().getAllMinions();
    };

    public static final AuraTargetProvider<Minion, Minion> NEIGHBOURS_MINION_PROVIDER = (world, source) -> {
        SummonLocationRef locationRef = source.getLocationRef();

        BoardLocationRef left = locationRef.tryGetLeft();
        BoardLocationRef right = locationRef.tryGetRight();
        if (left == null) {
            if (right == null) {
                return Collections.emptyList();
            }
            else {
                return Collections.singletonList(right.getMinion());
            }
        }
        else {
            if (right == null) {
                return Collections.singletonList(left.getMinion());
            }
            else {
                return Arrays.asList(left.getMinion(), right.getMinion());
            }
        }
    };

    public static final Aura<Object, Minion> UNTARGETABLE = (World world, Object source, Minion target) -> {
        return target.getProperties().getBody().getUntargetableProperty().setValueToExternal(true);
    };

    public static final AuraFilter<PlayerProperty, PlayerProperty> SAME_OWNER = (world, source, target) -> {
        return source.getOwner() == target.getOwner();
    };

    public static final AuraFilter<PlayerProperty, PlayerProperty> SAME_OWNER_OTHERS = (world, source, target) -> {
        return source.getOwner() == target.getOwner() && source != target;
    };

    public static final AuraFilter<Minion, Minion> NEXT_MINION = (world, source, target) -> {
        return target == tryGetLeft(source) || target == tryGetRight(source);
    };

    public static final Aura<Object, Minion> GRANT_IMMUNITY = (world, source, target) -> {
        return target.getBody().getImmuneProperty().setValueToExternal(true);
    };

    public static AuraFilter<Object, Minion> minionTargetNameIs(@NamedArg("name") String name) {
        ExceptionHelper.checkNotNullArgument(name, "name");
        return (World world, Object owner, Minion target) -> {
            return name.equals(target.getBaseDescr().getId().getName());
        };
    }

    private static Minion tryGetLeft(Minion minion) {
        BoardLocationRef leftRef = minion.getLocationRef().tryGetLeft();
        return leftRef != null ? leftRef.getMinion() : null;
    }

    private static Minion tryGetRight(Minion minion) {
        BoardLocationRef leftRef = minion.getLocationRef().tryGetRight();
        return leftRef != null ? leftRef.getMinion() : null;
    }

    public static Aura<Object, Minion> attackForOtherMinionsBuff(
            @NamedArg("attack") int attack,
            @NamedArg("keywords") Keyword[] keywords) {
        Predicate<LabeledEntity> keywordFilter = ActionUtils.includedKeywordsFilter(keywords);
        return (World world, Object source, Minion target) -> {
            Predicate<Minion> filter = (minion) -> {
                return target != minion && keywordFilter.test(minion);
            };

            return target.getBuffableAttack().addExternalBuff((prev) -> {
                int count1 = world.getPlayer1().getBoard().countMinions(filter);
                int count2 = world.getPlayer2().getBoard().countMinions(filter);

                return prev + attack * (count1 + count2);
            });
        };
    }

    public static Aura<Object, Minion> minHp(@NamedArg("hp") int hp) {
        return (World world, Object source, Minion target) -> {
            return target.getBody().getMinHpProperty().addExternalBuff((prev) -> Math.max(prev, hp));
        };
    }
}
