package com.github.kelemen.brazier.abilities;

import com.github.kelemen.brazier.BoardSide;
import com.github.kelemen.brazier.Hero;
import com.github.kelemen.brazier.Keyword;
import com.github.kelemen.brazier.LabeledEntity;
import com.github.kelemen.brazier.Player;
import com.github.kelemen.brazier.Priority;
import com.github.kelemen.brazier.TargetableCharacter;
import com.github.kelemen.brazier.World;
import com.github.kelemen.brazier.actions.ActionUtils;
import com.github.kelemen.brazier.actions.UndoAction;
import com.github.kelemen.brazier.cards.Card;
import com.github.kelemen.brazier.events.UndoableUnregisterRef;
import com.github.kelemen.brazier.events.UndoableUnregisterRefBuilder;
import com.github.kelemen.brazier.minions.Minion;
import com.github.kelemen.brazier.parsing.NamedArg;
import com.github.kelemen.brazier.weapons.Weapon;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jtrim.utils.ExceptionHelper;

public final class Buffs {
    public static final Buff<Player> DUPLICATE_DEATH_RATTLE = (world, target, arg) -> {
        return target.getDeathRattleTriggerCount().addRemovableBuff(arg, (int prev) -> Math.max(prev, 2));
    };

    public static final Buff<Player> DAMAGING_HEAL = (world, target, arg) -> {
        return target.getDamagingHealAura().setValueTo(arg, true);
    };

    public static Buff<TargetableCharacter> IMMUNE = (World world, TargetableCharacter target, BuffArg arg) -> {
        if (target instanceof Minion) {
            Minion minion = (Minion)target;
            return minion.getProperties().getBody().getImmuneProperty().setValueTo(arg, true);
        }
        else if (target instanceof Hero) {
            Hero hero = (Hero)target;
            return hero.getImmuneProperty().setValueTo(true);
        }
        else {
            return UndoableUnregisterRef.UNREGISTERED_REF;
        }
    };

    public static final Buff<Minion> UNTARGETABLE = (World world, Minion target, BuffArg arg) -> {
        return target.getProperties().getBody().getUntargetableProperty().setValueTo(arg, true);
    };

    public static final Buff<Minion> DOUBLE_ATTACK = (world, target, arg) -> {
        return target.getProperties().getBuffableAttack().addRemovableBuff(arg, (prev) -> 2 * prev);
    };

    public static final Buff<Minion> WEAPON_ATTACK_BUFF = weaponAttackBuff(1);

    public static final PermanentBuff<Minion> TWILIGHT_BUFF = (world, target, arg) -> {
        return buff(target, arg, 0, target.getOwner().getHand().getCardCount());
    };

    public static final Buff<Minion> INNER_FIRE = (world, target, arg) -> {
        int hp = target.getBody().getCurrentHp();
        return target.getProperties().getBuffableAttack().setValueTo(arg, hp);
    };

    public static final PermanentBuff<Minion> EXORCIST_BUFF = (World world, Minion target, BuffArg arg) -> {
        BoardSide opponentBoard = target.getOwner().getOpponent().getBoard();
        int buff = opponentBoard.countMinions((opponentMinion) -> opponentMinion.getProperties().isDeathRattle());

        UndoAction attackBuffUndo = target.getBuffableAttack().addBuff(arg, buff);
        UndoAction hpBuffUndo = target.getBody().getHp().buffHp(arg, buff);
        return () -> {
            hpBuffUndo.undo();
            attackBuffUndo.undo();
        };
    };

    public static final PermanentBuff<Minion> WARLORD_BUFF = minionLeaderBuff(1, 1);

    public static final Buff<Minion> WIND_FURY = windFury(2);

    public static final Buff<Minion> CHARGE = (World world, Minion target, BuffArg arg) -> {
        AuraAwareBoolProperty charge = target.getProperties().getChargeProperty();
        return charge.setValueTo(arg, true);
    };

    public static Buff<Minion> windFury(@NamedArg("attackCount") int attackCount) {
        return (World world, Minion target, BuffArg arg) -> {
            AuraAwareIntProperty maxAttackCount = target.getProperties().getMaxAttackCountProperty();
            return maxAttackCount.addRemovableBuff(arg, (prev) -> Math.max(prev, attackCount));
        };
    }

    public static Buff<Minion> setAttack(@NamedArg("attack") int attack) {
        return (World world, Minion target, BuffArg arg) -> {
            return target.getBuffableAttack().setValueTo(arg, attack);
        };
    }

    private static PermanentBuff<TargetableCharacter> adjustHp(Function<HpProperty, UndoAction> action) {
        return (World world, TargetableCharacter target, BuffArg arg) -> {
            arg.checkNormalBuff();
            return ActionUtils.adjustHp(target, action);
        };
    }

    public static PermanentBuff<TargetableCharacter> setCurrentHp(@NamedArg("hp") int hp) {
        return adjustHp((hpProperty) -> {
            if (hpProperty.getMaxHp() >= hp) {
                return hpProperty.setCurrentHp(hp);
            }
            else {
                return hpProperty.setMaxAndCurrentHp(hp);
            }
        });
    }

    public static PermanentBuff<TargetableCharacter> setMaxHp(@NamedArg("hp") int hp) {
        return adjustHp((hpProperty) -> {
            return hpProperty.setMaxHp(hp);
        });
    }

    public static Buff<TargetableCharacter> buffAttack(
            @NamedArg("minAttack") int minAttack,
            @NamedArg("maxAttack") int maxAttack) {
        return (World world, TargetableCharacter target, BuffArg arg) -> {
            int buff = world.getRandomProvider().roll(minAttack, maxAttack);
            return buffAttack(arg, target, buff);
        };
    }

    public static PermanentBuff<TargetableCharacter> buffHp(@NamedArg("hp") int hp) {
        return buff(0, hp);
    }

    public static Buff<TargetableCharacter> buffAttack(@NamedArg("attack") int attack) {
        return buffAttack(attack, attack);
    }

    public static PermanentBuff<TargetableCharacter> buff(
            @NamedArg("attack") int attack,
            @NamedArg("hp") int hp) {
        return (World world, TargetableCharacter target, BuffArg arg) -> {
            return buff(target, arg, attack, hp);
        };
    }

    private static UndoAction buff(TargetableCharacter target, BuffArg arg, int attack, int hp) {
        if (target instanceof Minion) {
            return buffMinion((Minion)target, arg, attack, hp);
        }
        if (target instanceof Hero) {
            return buffHero((Hero)target, arg, attack, hp);
        }
        return UndoAction.DO_NOTHING;
    }

    private static UndoAction buffMinion(Minion minion, BuffArg arg, int attack, int hp) {
        if (attack == 0) {
            return minion.getBody().getHp().buffHp(arg, hp);
        }
        if (hp == 0) {
            return minion.getBuffableAttack().addBuff(arg, attack);
        }

        UndoAction attackBuffUndo = minion.getBuffableAttack().addBuff(arg, attack);
        UndoAction hpBuffUndo = minion.getBody().getHp().buffHp(hp);
        return () -> {
            hpBuffUndo.undo();
            attackBuffUndo.undo();
        };
    }

    private static UndoAction buffHero(Hero hero, BuffArg arg, int attack, int hp) {
        // FIXME: Attack buff is only OK because everything buffing a hero's
        //        attack only lasts until the end of turn.

        if (attack == 0) {
            return hero.getHp().buffHp(arg, hp);
        }
        if (hp == 0) {
            return hero.addExtraAttackForThisTurn(attack);
        }

        UndoAction attackBuffUndo = hero.addExtraAttackForThisTurn(attack);
        UndoAction hpBuffUndo = hero.getHp().buffHp(arg, hp);
        return () -> {
            hpBuffUndo.undo();
            attackBuffUndo.undo();
        };
    }

    private static UndoableUnregisterRef buffHp(BuffArg arg, TargetableCharacter target, int hp) {
        if (hp != 0 && (arg.getPriority() != Priority.HIGH_PRIORITY.getValue() || !arg.isExternal())) {
            throw new UnsupportedOperationException("Temporary health buffs are only supported standard auras.");
        }

        HpProperty hpProperty = ActionUtils.tryGetHp(target);
        if (hpProperty == null) {
            return UndoableUnregisterRef.UNREGISTERED_REF;
        }

        return hpProperty.addAuraBuff(hp);
    }

    private static UndoableUnregisterRef buffAttack(BuffArg arg, TargetableCharacter target, int attack) {
        if (target instanceof Minion) {
            return ((Minion)target).getBuffableAttack().addRemovableBuff(arg, attack);
        }
        if (target instanceof Hero) {
            // FIXME: This is only OK because everything buffing a hero's
            //        attack only lasts until the end of turn.
            return ((Hero)target).addExtraAttackForThisTurn(attack);
        }
        return UndoableUnregisterRef.UNREGISTERED_REF;
    }

    public static Buff<TargetableCharacter> buffRemovable(
            @NamedArg("attack") int attack,
            @NamedArg("hp") int hp) {
        if (hp == 0) {
            return buffAttack(attack);
        }
        if (attack == 0) {
            return (world, target, arg) -> buffHp(arg, target, hp);
        }

        return (World world, TargetableCharacter target, BuffArg arg) -> {
            UndoableUnregisterRefBuilder result = new UndoableUnregisterRefBuilder(2);
            result.addRef(buffAttack(arg, target, attack));
            result.addRef(buffHp(arg, target, hp));
            return result;
        };
    }

    public static Buff<Minion> weaponAttackBuff(
            @NamedArg("buffPerAttack") int buffPerAttack) {

        return (World world, Minion target, BuffArg arg) -> {
            Weapon weapon = target.getOwner().tryGetWeapon();
            if (weapon == null) {
                return UndoableUnregisterRef.UNREGISTERED_REF;
            }

            int buff = weapon.getAttack();
            return target.getBuffableAttack().addRemovableBuff(arg, buffPerAttack * buff);
        };
    }

    private static Buff<Weapon> buffWeaponAttack(@NamedArg("attack") int attack) {
        return (world, target, arg) -> target.getBuffableAttack().addRemovableBuff(arg, attack);
    }

    public static Buff<Weapon> buffWeapon(@NamedArg("attack") int attack) {
        return buffWeaponAttack(attack);
    }

    public static PermanentBuff<Weapon> buffWeapon(
            @NamedArg("attack") int attack,
            @NamedArg("charges") int charges) {
        if (charges == 0) {
            return buffWeaponAttack(attack).toPermanent();
        }

        return (world, target, arg) -> {
            arg.checkNormalBuff();
            UndoAction attackBuffUndo = target.getBuffableAttack().addBuff(arg, attack);
            UndoAction incChargesUndo = target.increaseCharges(charges);

            return () -> {
                incChargesUndo.undo();
                attackBuffUndo.undo();
            };
        };
    }

    public static PermanentBuff<Minion> vancleefBuff(
            @NamedArg("attack") int attack,
            @NamedArg("hp") int hp) {
        return (World world, Minion minion, BuffArg arg) -> {
            int mul = minion.getOwner().getCardsPlayedThisTurn() - 1;
            if (mul <= 0) {
                return UndoAction.DO_NOTHING;
            }

            return buff(minion, arg, attack * mul, hp * mul);
        };
    }

    public static PermanentBuff<Minion> minionLeaderBuff(
            @NamedArg("attack") int attack,
            @NamedArg("hp") int hp,
            @NamedArg("keywords") Keyword... keywords) {

        Predicate<LabeledEntity> minionFilter = ActionUtils.includedKeywordsFilter(keywords);
        return (World world, Minion target, BuffArg arg) -> {
            Predicate<LabeledEntity> appliedFilter = minionFilter.and((otherMinion) -> target != otherMinion);
            int buff = target.getOwner().getBoard().countMinions(appliedFilter);
            if (buff <= 0) {
                return UndoAction.DO_NOTHING;
            }

            UndoAction attackBuffUndo = target.getBuffableAttack().addRemovableBuff(arg, attack * buff);
            UndoAction hpBuffUndo = target.getBody().getHp().buffHp(hp * buff);

            return () -> {
                hpBuffUndo.undo();
                attackBuffUndo.undo();
            };
        };
    }

    public static Buff<Minion> attackForOtherMinionsBuff(
            @NamedArg("attack") int attack,
            @NamedArg("keywords") Keyword[] keywords) {
        Predicate<LabeledEntity> keywordFilter = ActionUtils.includedKeywordsFilter(keywords);
        return (World world, Minion target, BuffArg arg) -> {
            Predicate<Minion> filter = (minion) -> {
                return target != minion && keywordFilter.test(minion);
            };

            return target.getBuffableAttack().addRemovableBuff(arg, (prev) -> {
                int count1 = world.getPlayer1().getBoard().countMinions(filter);
                int count2 = world.getPlayer2().getBoard().countMinions(filter);

                return prev + attack * (count1 + count2);
            });
        };
    }

    public static Buff<Minion> minHp(@NamedArg("hp") int hp) {
        return (World world, Minion target, BuffArg arg) -> {
            return target.getBody().getMinHpProperty().addRemovableBuff(arg, (prev) -> Math.max(prev, hp));
        };
    }

    public static Buff<Player> playerFlag(@NamedArg("flag") Keyword flag) {
        ExceptionHelper.checkNotNullArgument(flag, "flag");

        return (World world, Player target, BuffArg arg) -> {
            return target.getAuraFlags().registerFlag(flag);
        };
    }

    public static Buff<Card> increaseManaCost(@NamedArg("amount") int amount) {
        return (World world, Card target, BuffArg arg) -> {
            return target.getRawManaCost().addRemovableBuff(amount);
        };
    }

    public static Buff<Card> decreaseManaCostWithLimit(@NamedArg("amount") int amount) {
        return decreaseManaCostWithLimit(amount, 1);
    }

    public static Buff<Card> decreaseManaCostWithLimit(
            @NamedArg("amount") int amount,
            @NamedArg("limit") int limit) {
        return (World world, Card target, BuffArg arg) -> {
            return target.getRawManaCost().addRemovableBuff(arg, (prevValue) -> {
                return prevValue > limit
                        ? Math.max(limit, prevValue - amount)
                        : prevValue;
            });
        };
    }

    public static Buff<Card> setManaCost(@NamedArg("manaCost") int manaCost) {
        return (World world, Card target, BuffArg arg) -> {
            return target.getRawManaCost().setValueTo(arg, 0);
        };
    }

    private Buffs() {
        throw new AssertionError();
    }
}
