package com.hacked.controller;

import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.hacked.entity.Game;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.hacked.entity.Round;
import com.hacked.entity.Vote;
import com.hacked.reposetory.GameReposetory;
import com.hacked.reposetory.PlayerReposetory;
import com.hacked.reposetory.VoteReposetory;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * @author pd06286
 */
@SpringComponent
public class HackedService {
	@Autowired
	private PlayerReposetory playerReposetory;

	@Autowired
	private GameReposetory gameReposetory;

	@Autowired
	private VoteReposetory voteReposetory;

	public long addPlayerToGame(String playerName, String gameId) {
		Player player = new Player();
		player.setDeaktivatet(false);
		player.setHeacked(false);
		player.setReady(false);
		player.setGameId(gameId);
		player.setRole(Role.UNKNOWN);
		player.setName(playerName);
		long id = playerReposetory.save(player).getId();
		Game game = gameReposetory.findById(gameId).get();
		if (game.getMasterId() == 0L) {
			game.setMasterId(id);
			gameReposetory.save(game);
		}
		return id;
	}

	public boolean isPlayerMasterOfGame(long playerId, String gameId) {
		Game game = gameReposetory.findById(gameId).get();
		return game.getMasterId() == playerId;
	}

	public String generateGame() {
		Game game = new Game();
		game.setId(RandomStringUtils.randomAlphanumeric(5));
		game.setRunning(false);
		game.setMasterId(0L);
		game.setReady(false);
		return gameReposetory.save(game).getId();
	}

	public List<Player> getPlayerOfGame(String gameId) {
		return playerReposetory.findByGameId(gameId);
	}

	public Player getPlayer(long playerId) {
		return playerReposetory.findById(playerId).get();
	}

	public void setPlayerReady(long playerId) {
		Player player = playerReposetory.findById(playerId).get();
		player.setReady(true);
		playerReposetory.save(player);
	}

	/**
	 * @return
	 */
	public List<Player> getAllPlayer() {
		return playerReposetory.findAll();
	}

	/**
	 * @return
	 */
	public List<Game> getAllGames() {
		return gameReposetory.findAll();
	}

	/**
	 * @return
	 */
	public boolean playerOfGameReady(String gameId) {
		Game game = gameReposetory.findById(gameId).get();
		long countPlayer = playerReposetory.countByGameId(game.getId());
		long countReadyPlayer = playerReposetory.countByGameIdAndReady(game.getId(), true);
		if (countPlayer == countReadyPlayer) {
			game.setReady(true);
			gameReposetory.save(game);
			return true;
		}
		return false;
	}

	/**
	 * @param gameId
	 */
	public void startGame(String gameId) {
		List<Player> players = playerReposetory.findByGameId(gameId);
		int maxAnzAdmin = berechneMaxAnzAdmin(players.size());
		int maxAnzHacker = berechneMaxAnzHacker(players.size());
		int countAdmin = 0;
		int countHacker = 0;
		for (Player player : players) {
			player.setDeaktivatet(false);
			if (RandomUtils.nextBoolean() && countAdmin != maxAnzAdmin) {
				player.setRole(Role.ADMIN);
				countAdmin++;
			} else if (RandomUtils.nextBoolean() && countHacker != maxAnzHacker) {
				player.setRole(Role.HACKER);
				countHacker++;
			} else {
				player.setRole(Role.USER);
			}
			playerReposetory.save(player);
		}
		Game game = gameReposetory.findById(gameId).get();
		game.setReady(true);
		game.setRunning(true);
		gameReposetory.save(game);
	}

	private int berechneMaxAnzHacker(int size) {
		if (size > 10) {
			return 3;
		} else if (size > 5) {
			return 2;
		}
		return 1;
	}

	private int berechneMaxAnzAdmin(int size) {
		if (size > 10) {
			return 3;
		} else if (size > 5) {
			return 2;
		}
		return 1;
	}

	public boolean isGameReadyToStart(String gameId) {
		return gameReposetory.findById(gameId).get().isReady();
	}

	public boolean isGameReadyRunning(String gameId) {
		return gameReposetory.findById(gameId).get().isRunning();
	}

	public List<Player> getAktivPlayerOfGame(String gameId) {
		return playerReposetory.findByGameIdAndDeaktivatet(gameId, false);
	}

	public List<Player> getOnlyDeaktivatedPlayerOfGame(String gameId) {
		return playerReposetory.findByGameIdAndDeaktivatetAndHeacked(gameId, true, false);
	}

	public void hackPlayer(long id) {
		Round round = HackedSessionService.getRound();
		if (round == null) {
			round = new Round();
			round.setHackedPlayerId(0L);
			round.setVotedPlayerId(0L);
		}
		if (round.getHackedPlayerId() == 0L) {
			Player player = playerReposetory.findById(id).get();
			player.setHeacked(true);
			player.setDeaktivatet(false);
			playerReposetory.save(player);
			round.setHackedPlayerId(id);
			HackedSessionService.setRound(round);
		}
	}

	public boolean wurdeDieRundeGehackt() {
		Round round = HackedSessionService.getRound();
		if (round == null) {
			return false;
		} else if (round.getHackedPlayerId() == 0L) {
			return false;
		} else {
			return true;
		}
	}

	public void deaktivatePlayer(long id) {
		Player player = playerReposetory.findById(id).get();
		player.setDeaktivatet(true);
		playerReposetory.save(player);
	}

	public void voteForPlayer(long waehlerPlayerId, long gewaehlterPlayerId, String gameId) {
		Vote vote = new Vote();
		vote.setGameId(gameId);
		vote.setVoterId(waehlerPlayerId);
		vote.setNomineeId(gewaehlterPlayerId);
		voteReposetory.save(vote);
	}

	public boolean allPlayerVoted(String gameId) {
		long countVoter = voteReposetory.countByGameId(gameId);
		long countPlayer = playerReposetory.countByGameIdAndDeaktivatet(gameId, false);
		return countVoter == countPlayer;
	}

	public List<Vote> getAllVotes() {
		return voteReposetory.findAll();
	}

	public Player getPlayerOfVot(String gameId) {
		List<Vote> votes = voteReposetory.findAllByGameIdOrderByNomineeIdDesc(gameId);
		Round round = HackedSessionService.getRound();
		if (round == null) {
			round = new Round();
			round.setHackedPlayerId(0L);
			round.setVotedPlayerId(0L);
		}
		if (round.getVotedPlayerId() == 0L) {
			round.setVotedPlayerId(getPlayer(votes.get(0).getNomineeId()).getId());
		}
		HackedSessionService.setRound(round);
		return getPlayer(round.getVotedPlayerId());
	}

	public Player getHackedPlayer() {
		Round round = HackedSessionService.getRound();
		if (round != null && round.getHackedPlayerId() != 0L) {
			return getPlayer(round.getHackedPlayerId());
		}
		return null;
	}

	public void deleteVotes(String gameId) {
		voteReposetory.deleteByGameId(gameId);
	}

	public void gameOver(String gameId) {
		gameReposetory.deleteById(gameId);
		playerReposetory.deleteAllByGameId(gameId);
	}

	public Notification generateMeldung(String titel, String text) {
		Notification notif = new Notification(titel, Type.ASSISTIVE_NOTIFICATION);
		notif.setHtmlContentAllowed(true);
		notif.setPosition(Position.TOP_LEFT);
		notif.setDescription(text);
		return notif;
	}
	public Notification generateMeldung(String titel, String text, Position position) {
		Notification notif = new Notification(titel, Type.ASSISTIVE_NOTIFICATION);
		notif.setHtmlContentAllowed(true);
		notif.setPosition(position);
		notif.setDescription(text);
		return notif;
	}
}
