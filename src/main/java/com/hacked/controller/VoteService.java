package com.hacked.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.hacked.entity.Player;
import com.hacked.entity.Round;
import com.hacked.entity.Vote;
import com.hacked.reposetory.VoteReposetory;
import com.vaadin.spring.annotation.SpringComponent;


@SpringComponent
public class VoteService {

	@Autowired
	private VoteReposetory voteReposetory;

	@Autowired
	private PlayerService playerService;

	public void voteForPlayer(long waehlerPlayerId, long gewaehlterPlayerId, String gameId) {
		Vote vote = new Vote();
		vote.setGameId(gameId);
		vote.setVoterId(waehlerPlayerId);
		vote.setNomineeId(gewaehlterPlayerId);
		voteReposetory.save(vote);
	}

	public boolean allPlayerVoted(String gameId) {
		long countVoter = voteReposetory.countByGameId(gameId);
		long countPlayer =playerService.countDeaktivatetPlayerOfGame(gameId);
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
			round.setVotedPlayerId(playerService.getPlayer(votes.get(0).getNomineeId()).getId());
		}
		HackedSessionService.setRound(round);
		return playerService.getPlayer(round.getVotedPlayerId());
	}



	public void deleteVotes(String gameId) {
		voteReposetory.deleteByGameId(gameId);
	}
}
