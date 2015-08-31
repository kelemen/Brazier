package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.cards.Card;
import java.util.ArrayList;
import java.util.List;

public final class CardAuras {
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

    private CardAuras() {
        throw new AssertionError();
    }
}
