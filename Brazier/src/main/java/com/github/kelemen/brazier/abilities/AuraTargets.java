package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.BoardLocationRef;
import com.github.kelemen.brazier.BornEntity;
import com.github.kelemen.brazier.Hero;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.SummonLocationRef;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.cards.Card;
import com.github.kelemen.brazier.minions.Minion;
import com.github.kelemen.brazier.weapons.Weapon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class AuraTargets {
    public static final AuraTargetProvider<Object, Hero> HERO_PROVIDER = (World world, Object source) -> {
        return Arrays.asList(world.getPlayer1().getHero(), world.getPlayer2().getHero());
    };

    public static final AuraTargetProvider<PlayerProperty, Hero> OWN_HERO_PROVIDER = (World world, PlayerProperty source) -> {
        return Collections.singletonList(source.getOwner().getHero());
    };

    public static final AuraTargetProvider<Object, Player> PLAYER_PROVIDER = (World world, Object source) -> {
        return Arrays.asList(world.getPlayer1(), world.getPlayer2());
    };

    public static final AuraTargetProvider<PlayerProperty, Player> OWN_PLAYER_PROVIDER = (World world, PlayerProperty source) -> {
        return Collections.singletonList(source.getOwner());
    };

    public static final AuraTargetProvider<Object, Weapon> WEAPON_PROVIDER = (World world, Object source) -> {
        Weapon weapon1 = world.getPlayer1().tryGetWeapon();
        Weapon weapon2 = world.getPlayer2().tryGetWeapon();
        if (weapon1 == null) {
            return weapon2 != null ? Collections.singletonList(weapon2) : Collections.emptyList();
        }
        else {
            return weapon2 != null ? Arrays.asList(weapon1, weapon2) : Collections.singletonList(weapon1);
        }
    };

    public static final AuraTargetProvider<PlayerProperty, Weapon> OWN_WEAPON_PROVIDER = (World world, PlayerProperty source) -> {
        Weapon weapon = source.getOwner().tryGetWeapon();
        return weapon != null ? Collections.singletonList(weapon) : Collections.emptyList();
    };

    public static final AuraTargetProvider<Object, Card> CARD_PROVIDER = (World world, Object source) -> {
        List<Card> result = new ArrayList<>(2 * Player.MAX_HAND_SIZE);
        world.getPlayer1().getHand().collectCards(result);
        world.getPlayer2().getHand().collectCards(result);
        return result;
    };

    public static final AuraTargetProvider<PlayerProperty, Card> OWN_CARD_PROVIDER = (World world, PlayerProperty source) -> {
        return source.getOwner().getHand().getCards();
    };

    public static final AuraTargetProvider<PlayerProperty, Card> OPPONENT_CARD_PROVIDER = (World world, PlayerProperty source) -> {
        return source.getOwner().getOpponent().getHand().getCards();
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

    private AuraTargets() {
        throw new AssertionError();
    }
}
