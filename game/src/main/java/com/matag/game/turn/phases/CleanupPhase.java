package com.matag.game.turn.phases;

import com.matag.cardinstance.CardInstance;
import com.matag.game.status.GameStatus;
import org.springframework.stereotype.Component;

import static com.matag.game.turn.phases.UntapPhase.UT;

@Component
public class CleanupPhase implements Phase {
  public static final String CL = "CL";

  private final UntapPhase untapPhase;

  public CleanupPhase(UntapPhase untapPhase) {
    this.untapPhase = untapPhase;
  }

  @Override
  public void apply(GameStatus gameStatus) {
    cleanup(gameStatus);
    moveToNextPlayer(gameStatus);
    untapPhase.apply(gameStatus);
  }

  private void cleanup(GameStatus gameStatus) {
    gameStatus.getTurn().getCardsPlayedWithinTurn().clear();
    gameStatus.getCurrentPlayer().getBattlefield().getCards().forEach(CardInstance::cleanup);
    gameStatus.getNonCurrentPlayer().getBattlefield().getCards().forEach(CardInstance::cleanup);
  }

  private void moveToNextPlayer(GameStatus gameStatus) {
    gameStatus.getTurn().increaseTurnNumber();
    gameStatus.getTurn().setCurrentPhase(UT);
    String nextCurrentPlayer = gameStatus.getNonCurrentPlayer().getName();
    gameStatus.getTurn().setCurrentTurnPlayer(nextCurrentPlayer);
    gameStatus.getTurn().setCurrentPhaseActivePlayer(nextCurrentPlayer);
  }
}