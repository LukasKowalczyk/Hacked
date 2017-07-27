package com.hacked.entity;

public class Round {
	private long hackedPlayerId;
	
	private long votedPlayerId;

	
	public long getHackedPlayerId() {
		return hackedPlayerId;
	}

	public void setHackedPlayerId(long hackedPlayerId) {
		this.hackedPlayerId = hackedPlayerId;
	}

	public long getVotedPlayerId() {
		return votedPlayerId;
	}

	public void setVotedPlayerId(long votedPlayerId) {
		this.votedPlayerId = votedPlayerId;
	}

}