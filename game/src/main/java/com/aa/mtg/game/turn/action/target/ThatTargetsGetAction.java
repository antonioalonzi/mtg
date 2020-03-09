package com.aa.mtg.game.turn.action.target;

import com.aa.mtg.cardinstance.CardInstance;
import com.aa.mtg.cardinstance.ability.CardInstanceAbility;
import com.aa.mtg.cards.ability.AbilityService;
import com.aa.mtg.cardinstance.ability.AbilityAction;
import com.aa.mtg.game.player.Player;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.damage.DealDamageToPlayerService;
import com.aa.mtg.game.turn.action.draw.DrawXCardsService;
import com.aa.mtg.game.turn.action.life.LifeService;
import com.aa.mtg.game.turn.action.permanent.PermanentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ThatTargetsGetAction implements AbilityAction {
  private static final Logger LOGGER = LoggerFactory.getLogger(ThatTargetsGetAction.class);

  private final LifeService lifeService;
  private final DrawXCardsService drawXCardsService;
  private final PermanentService permanentService;
  private final DealDamageToPlayerService dealDamageToPlayerService;
  private final AbilityService abilityService;

  public ThatTargetsGetAction(LifeService lifeService, DrawXCardsService drawXCardsService, PermanentService permanentService, DealDamageToPlayerService dealDamageToPlayerService, AbilityService abilityService) {
    this.lifeService = lifeService;
    this.drawXCardsService = drawXCardsService;
    this.permanentService = permanentService;
    this.dealDamageToPlayerService = dealDamageToPlayerService;
    this.abilityService = abilityService;
  }

  @Override
  public void perform(CardInstance cardInstance, GameStatus gameStatus, CardInstanceAbility ability) {
    for (Object targetId : cardInstance.getModifiers().getTargets()) {
      if (targetId instanceof String) {
        String targetPlayerName = (String) targetId;
        thatTargetPlayerGet(cardInstance, gameStatus, ability.getParameters(), targetPlayerName);

      } else {
        int targetCardId = (int) targetId;

        Optional<CardInstance> targetOptional = gameStatus.getAllBattlefieldCards().withId(targetCardId);

        if (targetOptional.isPresent()) {
          CardInstance target = targetOptional.get();
          permanentService.thatPermanentGets(cardInstance, gameStatus, ability.getParameters(), target);

        } else {
          LOGGER.info("target {} is not anymore valid.", targetCardId);
        }
      }
    }
  }

  private void thatTargetPlayerGet(CardInstance cardInstance, GameStatus gameStatus, List<String> parameters, String targetPlayerName) {
    for (String parameter : parameters) {
      thatTargetPlayerGet(cardInstance, gameStatus, parameter, targetPlayerName);
    }
  }

  private void thatTargetPlayerGet(CardInstance cardInstance, GameStatus gameStatus, String parameter, String targetPlayerName) {
    Player player = gameStatus.getPlayerByName(targetPlayerName);
    int damage = abilityService.damageFromParameter(parameter);
    dealDamageToPlayerService.dealDamageToPlayer(gameStatus, damage, player);

    int life = abilityService.lifeFromParameter(parameter);
    if (life != 0) {
      lifeService.add(player, damage, gameStatus);
      LOGGER.info("AbilityActionExecuted: {} add {} life to {}", cardInstance.getIdAndName(), damage, player.getName());
    }

    int cardsToDraw = abilityService.drawFromParameter(parameter);
    if (cardsToDraw > 0) {
      drawXCardsService.drawXCards(player, cardsToDraw);
    }
  }
}
