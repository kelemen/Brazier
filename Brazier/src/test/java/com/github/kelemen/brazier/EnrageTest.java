package com.github.kelemen.brazier;

import org.junit.Test;

import static com.github.kelemen.brazier.TestCards.*;

public final class EnrageTest {
    @Test
    public void testHumilityAfterEnrage() {
        PlayScript.testScript((script) -> {
            script.setMana("p1", 10);

            script.playMinionCard("p1", AMANI_BERSERKER, 0);

            script.expectBoard("p1",
                    expectedMinion(AMANI_BERSERKER, 2, 3));

            script.playCard("p1", MOONFIRE, "p1:0");

            script.expectBoard("p1",
                    expectedMinion(AMANI_BERSERKER, 5, 2));

            script.playMinionCard("p1", HUMILITY, 1, "p1:0");

            script.expectBoard("p1",
                    expectedMinion(AMANI_BERSERKER, 1, 2));

            script.playCard("p1", HOLY_LIGHT, "p1:0");

            script.expectBoard("p1",
                    expectedMinion(AMANI_BERSERKER, 1, 3));

            script.playCard("p1", MOONFIRE, "p1:0");

            script.expectBoard("p1",
                    expectedMinion(AMANI_BERSERKER, 4, 2));
        });
    }
}
