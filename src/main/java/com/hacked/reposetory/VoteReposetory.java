package com.hacked.reposetory;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.hacked.entity.Vote;

/**
 * @author pd06286
 */
public interface VoteReposetory extends CrudRepository<Vote, Long> {
	List<Vote> findAll();

	List<Vote> findAllByGameId(String gameId);

	long countByGameId(String gameId);
	
	List<Vote> findAllByGameIdOrderByNomineeIdDesc(String gameId);

	@Transactional
	long deleteByGameId(String gameId);
}
