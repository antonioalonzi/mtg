package com.aa.mtg.game.turn.action.leave;

import com.aa.TestUtils;
import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.cards.sets.CoreSet2019;
import com.aa.mtg.cards.sets.CoreSet2020;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.attach.AttachService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LeaveTestConfiguration.class)
public class LeaveBattlefieldServiceTest {

    @Autowired
    private LeaveBattlefieldService leaveBattlefieldService;

    @Autowired
    private AttachService attachService;

    @Test
    public void testLeaveBattlefield() {
        // Given
        GameStatus gameStatus = TestUtils.testGameStatus();
        CardInstance cardInstance = new CardInstance(gameStatus, 61, CoreSet2020.CANOPY_SPIDER, "player-name", "player-name");
        cardInstance.getModifiers().tap();
        gameStatus.getPlayer1().getBattlefield().addCard(cardInstance);

        // When
        CardInstance cardLeft = leaveBattlefieldService.leaveTheBattlefield(gameStatus, 61);

        // Then
        assertThat(gameStatus.getPlayer1().getBattlefield().size()).isEqualTo(0);
        assertThat(cardLeft).isSameAs(cardInstance);
        assertThat(cardLeft.getModifiers().isTapped()).isFalse();
    }

    @Test
    public void testLeaveBattlefieldWithAttachments() {
        // Given
        GameStatus gameStatus = TestUtils.testGameStatus();
        CardInstance creature = new CardInstance(gameStatus, 61, CoreSet2020.CANOPY_SPIDER, "player-name", "player-name");
        gameStatus.getPlayer1().getBattlefield().addCard(creature);

        CardInstance enchantment1 = new CardInstance(gameStatus, 62, CoreSet2019.KNIGHTS_PLEDGE, "player-name", "player-name");
        gameStatus.getPlayer1().getBattlefield().addCard(enchantment1);
        attachService.attach(gameStatus, enchantment1, creature.getId());

        CardInstance enchantment2 = new CardInstance(gameStatus, 63, CoreSet2019.KNIGHTS_PLEDGE, "opponent-name", "opponent-name");
        gameStatus.getPlayer2().getBattlefield().addCard(enchantment2);
        attachService.attach(gameStatus, enchantment2, creature.getId());

        CardInstance equipment = new CardInstance(gameStatus, 64, CoreSet2019.MARAUDERS_AXE, "player-name", "player-name");
        gameStatus.getPlayer1().getBattlefield().addCard(equipment);
        attachService.attach(gameStatus, equipment, creature.getId());

        // When
        CardInstance cardLeft = leaveBattlefieldService.leaveTheBattlefield(gameStatus, 61);

        // Then
        assertThat(gameStatus.getPlayer1().getBattlefield().size()).isEqualTo(1);
        assertThat(gameStatus.getPlayer1().getBattlefield().getCards()).contains(equipment);
        assertThat(gameStatus.getPlayer1().getGraveyard().getCards()).contains(enchantment1);
        assertThat(gameStatus.getPlayer2().getGraveyard().getCards()).contains(enchantment2);
        assertThat(cardLeft).isSameAs(creature);
    }

    @Test
    public void testLeaveBattlefieldCardNotFound() {
        // Given
        GameStatus gameStatus = TestUtils.testGameStatus();

        // When
        CardInstance cardLeft = leaveBattlefieldService.leaveTheBattlefield(gameStatus, 61);

        // Then
        assertThat(cardLeft).isNull();
    }
}