package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.BoardLocationRef;
import com.github.kelemen.brazier.BornEntity;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.SummonLocationRef;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.minions.Minion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MinionAuras {
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
}
