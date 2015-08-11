package com.github.kelemen.hearthstone.emulator.abilities;

import com.github.kelemen.hearthstone.emulator.Hero;
import com.github.kelemen.hearthstone.emulator.Keyword;
import com.github.kelemen.hearthstone.emulator.Player;
import com.github.kelemen.hearthstone.emulator.PlayerProperty;
import com.github.kelemen.hearthstone.emulator.World;
import com.github.kelemen.hearthstone.emulator.parsing.NamedArg;
import java.util.Arrays;
import java.util.Collections;
import org.jtrim.utils.ExceptionHelper;

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

    public static final Aura<Object, Hero> GRANT_IMMUNITY = (world, source, target) -> {
        return target.getImmuneProperty().addAuraBuff(true);
    };

    public static final Aura<Object, Player> DUPLICATE_DEATH_RATTLE = (world, source, target) -> {
        return target.getDeathRattleTriggerCount().addAuraBuff((int prev) -> Math.max(prev, 2));
    };

    public static Aura<Object, Player> playerFlag(@NamedArg("flag") Keyword flag) {
        ExceptionHelper.checkNotNullArgument(flag, "flag");

        return (World world, Object source, Player target) -> {
            return target.getAuraFlags().registerFlag(flag);
        };
    }

    private HeroAuras() {
        throw new AssertionError();
    }
}