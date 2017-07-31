package com.hacked.controller;

import com.hacked.entity.Game;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.hacked.entity.Round;
import com.vaadin.ui.UI;

public class HackedSessionService {
	public static Role getWinner() {
		return (Role) UI.getCurrent().getSession().getAttribute(SessionKonstanten.WINNER);
	}

	public static Player getPlayer() {
		return (Player) UI.getCurrent().getSession().getAttribute(SessionKonstanten.PLAYER);
	}

	public static Game getGame() {
		return (Game) UI.getCurrent().getSession().getAttribute(SessionKonstanten.GAME);
	}

	public static void setGame(Game game) {
		UI.getCurrent().getSession().setAttribute(SessionKonstanten.GAME, game);
	}

	public static void setPlayer(Player player) {
		UI.getCurrent().getSession().setAttribute(SessionKonstanten.PLAYER, player);
	}

	public static long getPlayerId() {
		return (Long) UI.getCurrent().getSession().getAttribute(SessionKonstanten.PLAYER_ID);
	}

	public static String getGameId() {
		return (String) UI.getCurrent().getSession().getAttribute(SessionKonstanten.GAME_ID);
	}

	public static void setGameId(String gameId) {
		UI.getCurrent().getSession().setAttribute(SessionKonstanten.GAME_ID, gameId);
	}

	public static void setPlayerId(long playerId) {
		UI.getCurrent().getSession().setAttribute(SessionKonstanten.PLAYER_ID, playerId);
	}

	public static Round getRound() {
		return (Round) UI.getCurrent().getSession().getAttribute(SessionKonstanten.ROUND);
	}

	public static void setRound(Round runde) {
		UI.getCurrent().getSession().setAttribute(SessionKonstanten.ROUND, runde);
	}

	public static void setWinner(Role winner) {
		UI.getCurrent().getSession().setAttribute(SessionKonstanten.WINNER, winner);
	}

}
