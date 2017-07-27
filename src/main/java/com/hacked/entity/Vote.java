package com.hacked.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Vote {
	@Id
	private long voterId;
	
	private String gameId;

	private long nomineeId;

	public long getVoterId() {
		return voterId;
	}

	public void setVoterId(long voterId) {
		this.voterId = voterId;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public long getNomineeId() {
		return nomineeId;
	}

	public void setNomineeId(long nomineeId) {
		this.nomineeId = nomineeId;
	}

	@Override
	public String toString() {
		return gameId + " -> Voter:" + voterId + " Nominee:" + nomineeId;
	}
}
