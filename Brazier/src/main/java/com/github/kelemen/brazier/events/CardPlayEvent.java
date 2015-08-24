package com.github.kelemen.brazier.event;

import com.github.kelemen.brazier.Keyword;
import com.github.kelemen.brazier.LabeledEntity;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.TargetRef;
import com.github.kelemen.brazier.TargetableCharacter;
import com.github.kelemen.brazier.actions.CardPlayArg;
import com.github.kelemen.brazier.actions.CardPlayRef;
import com.github.kelemen.brazier.actions.PlayTarget;
import com.github.kelemen.brazier.actions.UndoAction;
import com.github.kelemen.brazier.cards.Card;
import java.util.Set;
import org.jtrim.utils.ExceptionHelper;

public final class CardPlayEvent implements PlayerProperty, LabeledEntity, CardPlayRef, TargetRef {
    private CardPlayArg cardPlayArg;
    private final int manaCost;
    private boolean vetodPlay;

    public CardPlayEvent(CardPlayArg cardPlayArg, int manaCost) {
        ExceptionHelper.checkNotNullArgument(cardPlayArg, "cardPlayArg");

        this.cardPlayArg = cardPlayArg;
        this.manaCost = manaCost;
        this.vetodPlay = false;
    }

    public UndoAction replaceTarget(TargetableCharacter newTarget) {
        if (newTarget == getTarget()) {
            return UndoAction.DO_NOTHING;
        }

        return replaceTarget(new PlayTarget(getCastingPlayer(), newTarget));
    }

    public UndoAction replaceTarget(PlayTarget newTarget) {
        ExceptionHelper.checkNotNullArgument(newTarget, "newTarget");

        CardPlayArg prevArg = cardPlayArg;
        cardPlayArg = new CardPlayArg(getCard(), newTarget);
        return () -> cardPlayArg = prevArg;
    }

    public Player getCastingPlayer() {
        return cardPlayArg.getTarget().getCastingPlayer();
    }

    public CardPlayArg getCardPlayArg() {
        return cardPlayArg;
    }

    @Override
    public TargetableCharacter getTarget() {
        return cardPlayArg.getTarget().getTarget();
    }

    @Override
    public Set<Keyword> getKeywords() {
        return getCard().getKeywords();
    }

    public UndoAction vetoPlay() {
        if (vetodPlay) {
            return UndoAction.DO_NOTHING;
        }

        vetodPlay = true;
        return () -> vetodPlay = false;
    }

    public boolean isVetodPlay() {
        return vetodPlay;
    }

    @Override
    public Card getCard() {
        return cardPlayArg.getCard();
    }

    @Override
    public Player getOwner() {
        return getCard().getOwner();
    }

    @Override
    public int getManaCost() {
        return manaCost;
    }
}