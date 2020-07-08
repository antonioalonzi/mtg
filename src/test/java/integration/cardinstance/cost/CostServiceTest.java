package integration.cardinstance.cost;

import com.matag.cards.Cards;
import com.matag.cards.CardsConfiguration;
import com.matag.cards.properties.Cost;
import com.matag.game.cardinstance.CardInstance;
import com.matag.game.cardinstance.CardInstanceFactory;
import com.matag.game.cardinstance.cost.CostService;
import com.matag.game.status.GameStatus;
import integration.TestUtils;
import integration.TestUtilsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;

import static com.matag.cards.properties.Cost.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@Import({CardsConfiguration.class, TestUtilsConfiguration.class})
public class CostServiceTest {
  private int cardInstanceId = 60;

  @Autowired
  private TestUtils testUtils;

  @Autowired
  private Cards cards;

  @Autowired
  private CardInstanceFactory cardInstanceFactory;

  @Autowired
  private CostService costService;

  @Test
  public void isCastingCostFulfilledFeralMaakaCorrectCosts() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Feral Maaka");
    var manaPaid = asList(GREEN, RED);

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, null, manaPaid);

    // Then
    assertThat(fulfilled).isTrue();
  }

  @Test
  public void isCastingCostFulfilledFeralMaakaNoMana() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Feral Maaka");
    var manaPaid = new ArrayList<Cost>();

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, null, manaPaid);

    // Then
    assertThat(fulfilled).isFalse();
  }

  @Test
  public void isCastingCostFulfilledFeralMaakaWrongCost() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Feral Maaka");
    var manaPaid = asList(WHITE, GREEN);

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, null, manaPaid);

    // Then
    assertThat(fulfilled).isFalse();
  }

  @Test
  public void isCastingCostFulfilledFeralMaakaOneLessMana() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Feral Maaka");
    var manaPaid = Collections.singletonList(RED);

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, null, manaPaid);

    // Then
    assertThat(fulfilled).isFalse();
  }

  @Test
  public void isCastingCostFulfilledAxebaneBeastCorrectCosts() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Axebane Beast");
    var manaPaid = asList(GREEN, GREEN, RED, RED);

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, null, manaPaid);

    // Then
    assertThat(fulfilled).isTrue();
  }

  @Test
  public void isCastingCostFulfilledCheckpointOfficerTapAbility() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Checkpoint Officer");
    var manaPaid = asList(WHITE, WHITE);

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, "THAT_TARGETS_GET", manaPaid);

    // Then
    assertThat(fulfilled).isTrue();
  }

  @Test
  public void isCastingCostFulfilledCheckpointOfficerTapAbilityOfTappedCreature() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Checkpoint Officer");
    cardInstance.getModifiers().setTapped(true);
    var manaPaid = asList(WHITE, WHITE);

    // When
    var fulfilled = costService.isCastingCostFulfilled(cardInstance, "THAT_TARGETS_GET", manaPaid);

    // Then
    assertThat(fulfilled).isFalse();
  }

  @Test
  public void canAffordReturnsFalseIfNoLandsAvailable() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    var cardInstance = createCardInstance(gameStatus, "Axebane Beast");

    // When
    boolean result = costService.canAfford(cardInstance, null, gameStatus);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  public void canAffordReturnsTrueIfEnoughMana() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Forest"));
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Forest"));
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Forest"));
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Forest"));
    var cardInstance = createCardInstance(gameStatus, "Axebane Beast");

    // When
    boolean result = costService.canAfford(cardInstance, null, gameStatus);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  public void canAffordReturnsTrueIfCorrectManaDualLands() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Selesnya Guildgate")); // GREEN, WHITE
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Llanowar Elves")); // GREEN
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Leyline Prowler")); // any color
    var cardInstance = createCardInstance(gameStatus, "Skyknight Legionnaire"); // "RED", "WHITE", "COLORLESS"

    // When
    boolean result = costService.canAfford(cardInstance, null, gameStatus);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  public void canAffordReturnsFalseIfWrongManaDualLands() {
    // Given
    var gameStatus = testUtils.testGameStatus();
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Selesnya Guildgate")); // GREEN, WHITE
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Llanowar Elves")); // GREEN
    gameStatus.getPlayer1().getBattlefield().addCard(createCardInstance(gameStatus, "Dimir Guildgate")); // BLUE, BLACK
    var cardInstance = createCardInstance(gameStatus, "Skyknight Legionnaire"); // "RED", "WHITE", "COLORLESS"

    // When
    boolean result = costService.canAfford(cardInstance, null, gameStatus);

    // Then
    assertThat(result).isFalse();
  }

  private CardInstance createCardInstance(GameStatus gameStatus, String cardName) {
    var card = cards.get(cardName);
    var cardInstance = cardInstanceFactory.create(gameStatus, ++cardInstanceId, card, "player-name", "player-name");
    gameStatus.getActivePlayer().getBattlefield().addCard(cardInstance);
    return cardInstance;
  }
}