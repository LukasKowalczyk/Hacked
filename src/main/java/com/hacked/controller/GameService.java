package com.hacked.controller;

import java.util.List;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.hacked.entity.Game;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.hacked.entity.Round;
import com.hacked.reposetory.GameReposetory;
import com.vaadin.spring.annotation.SpringComponent;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

@SpringComponent
public class GameService {
    @Value("${qrcode.server.address}")
    private String serverAddress;

    @Value("${qrcode.server.port}")
    private String serverPort;

    @Autowired
    private GameReposetory gameReposetory;

    @Autowired
    private PlayerService playerService;

    public byte[] getQR(String gameId) {
        return QRCode.from("http://" + serverAddress + ":" + serverPort + "/?gameId=" + gameId).withCharset("UTF-8")
            .withSize(250, 250).to(ImageType.PNG).stream().toByteArray();
    }

    public boolean isPlayerMasterOfGame(long playerId, String gameId) {
        Game game = gameReposetory.findOne(gameId);
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
        Game game = gameReposetory.findOne(gameId);
        long countPlayer = playerService.countPlayerOfGame(game.getId());
        long countReadyPlayer = playerService.countPlayerReadyOfGame(game.getId());
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
        List<Player> players = playerService.getPlayerOfGame(gameId);
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
            playerService.updatePlayer(player);
        }
        Game game = gameReposetory.findOne(gameId);
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
        return gameReposetory.findOne(gameId).isReady();
    }

    public boolean isGameReadyRunning(String gameId) {
        return gameReposetory.findOne(gameId).isRunning();
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

    public void gameOver(String gameId) {
        gameReposetory.delete(gameId);
        playerService.deleteAllPlayerOfGame(gameId);
    }

    public Game getGame(String gameId) {
        return gameReposetory.findOne(gameId);
    }

    public void updateGame(Game game) {
        gameReposetory.save(game);
    }
}
