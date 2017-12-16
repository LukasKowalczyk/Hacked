package com.hacked.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hacked.entity.Game;
import com.hacked.entity.Player;
import com.hacked.entity.Vote;

/**
 * @author pd06286
 */
@RestController
public class GameController {
	@Autowired
	private GameService gameService;
	
	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private VoteService voteService;

	@RequestMapping("/generateGame")
	public String greeting() {
		return gameService.generateGame();
	}

	@RequestMapping("/allGames")
	public List<Game> games() {
		return gameService.getAllGames();
	}

	@RequestMapping("/allPlayer")
	public List<Player> player() {
		return playerService.getAllPlayer();
	}

	@RequestMapping("/allVotes")
	public List<Vote> votes() {
		return voteService.getAllVotes();
	}

}
