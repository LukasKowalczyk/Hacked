package com.hacked.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.hacked.entity.Game;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.hacked.entity.Round;
import com.hacked.reposetory.PlayerReposetory;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class PlayerService {
    @Value("${min.player.count}")
    private String minPlayerCount;

    @Autowired
    private PlayerReposetory playerReposetory;

    @Autowired
    private GameService gameService;

    public long addPlayerToGame(String playerName, String gameId) {
        Player player = new Player();
        player.setDeaktivatet(false);
        player.setHeacked(false);
        player.setReady(false);
        player.setGameId(gameId);
        player.setRole(Role.UNKNOWN);
        player.setName(playerName);
        long id = playerReposetory.save(player).getId();
        Game game = gameService.getGame(gameId);
        if (game.getMasterId() == 0L) {
            game.setMasterId(id);
            gameService.updateGame(game);
        }
        return id;
    }

    public long countPlayerOfGame(String gameId) {
        return playerReposetory.countByGameId(gameId);
    }

    public long countPlayerReadyOfGame(String gameId) {
        return playerReposetory.countByGameIdAndReady(gameId, true);
    }

    public boolean isPlayerInGame(String playerName, String gameId) {
        return playerReposetory.existsByGameIdAndName(gameId, playerName);
    }

    public List<Player> getPlayerOfGame(String gameId) {
        return playerReposetory.findByGameId(gameId);
    }

    public Player getPlayer(long playerId) {
        return playerReposetory.findOne(playerId);
    }

    public void setPlayerReady(long playerId) {
        Player player = playerReposetory.findOne(playerId);
        player.setReady(true);
        playerReposetory.save(player);
    }

    public boolean isPlayerMasterOfGame(long playerId, String gameId) {
        Game game = gameService.getGame(gameId);
        return game.getMasterId() == playerId;
    }

    /**
     * @return
     */
    public List<Player> getAllPlayer() {
        return playerReposetory.findAll();
    }

    public boolean isMinPlayerCountOfGame(String gameId) {
        return playerReposetory.countByGameId(gameId) >= Integer.valueOf(minPlayerCount);
    }

    public Player getHackedPlayer() {
        Round round = HackedSessionService.getRound();
        if (round != null && round.getHackedPlayerId() != 0L) {
            return playerReposetory.findOne(round.getHackedPlayerId());
        }
        return null;
    }

    /**
     * @return
     */
    public boolean playerOfGameReady(String gameId) {
        Game game = gameService.getGame(gameId);
        long countPlayer = playerReposetory.countByGameId(game.getId());
        long countReadyPlayer = playerReposetory.countByGameIdAndReady(game.getId(), true);
        if (countPlayer == countReadyPlayer) {
            game.setReady(true);
            gameService.updateGame(game);
            return true;
        }
        return false;
    }

    public List<Player> getOtherAktivPlayerOfGame(String gameId, long playerId) {
        return playerReposetory.findByGameIdAndDeaktivatetAndIdNot(gameId, false, playerId);
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
            Player player = playerReposetory.findOne(id);
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
        Player player = playerReposetory.findOne(id);
        player.setDeaktivatet(true);
        playerReposetory.save(player);
    }

    public void updatePlayer(Player player) {
        playerReposetory.save(player);
    }

    public void deleteAllPlayerOfGame(String gameId) {
        playerReposetory.deleteAllByGameId(gameId);
    }

    public long countDeaktivatetPlayerOfGame(String gameId) {
        return playerReposetory.countByGameIdAndDeaktivatet(gameId, false);
    }
}
