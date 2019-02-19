package com.aa.mtg.game.status;

import com.aa.mtg.game.player.Player;
import com.aa.mtg.game.turn.Turn;
import com.aa.mtg.game.turn.phases.Phase;

public class GameStatus {

    private String gameId;
    private Player player1;
    private Player player2;
    private Turn turn;

    public GameStatus(String gameId) {
        this.gameId = gameId;
        this.turn = new Turn();
    }

    public String getGameId() {
        return gameId;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Turn getTurn() {
        return turn;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
    
    public Player getPlayer(String sessionId) {
        if (this.getPlayer1().getSessionId().equals(sessionId)) {
            return this.getPlayer1();
        } else if (this.getPlayer2().getSessionId().equals(sessionId)) {
            return this.getPlayer2();
        }
        throw new RuntimeException("SessionId " + sessionId + " is not linked to game " + gameId + " .");
    }

    public void passPriority() {
        String currentPhaseActivePlayer = turn.getCurrentPhaseActivePlayer();

        if (currentPhaseActivePlayer.equals(player1.getName())) {
            turn.setCurrentPhaseActivePlayer(player2.getName());
        } else {
            turn.setCurrentPhase(Phase.nextPhase(turn.getCurrentPhase()));
            turn.setCurrentPhaseActivePlayer(player1.getName());
        }
    }
}