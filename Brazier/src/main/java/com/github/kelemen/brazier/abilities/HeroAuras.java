package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.Hero;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.PlayerProperty;
import com.github.kelemen.brazier.World;
import java.util.Arrays;
import java.util.Collections;

public final class HeroAuras {
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

    private HeroAuras() {
        throw new AssertionError();
    }
}
