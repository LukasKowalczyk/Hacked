package com.hacked.reposetory;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.hacked.entity.Player;
import com.hacked.entity.Role;

/**
 * @author pd06286
 */
public interface PlayerReposetory extends CrudRepository<Player, Long> {
	List<Player> findAll();

	List<Player> findByGameId(String gameId);

	List<Player> findByGameIdAndRole(String gameId, Role role);

	List<Player> findByGameIdAndDeaktivatet(String gameId, boolean deaktivated);

	long countByGameId(String gameId);

	long countByGameIdAndReady(String gameId, boolean ready);
	
	long countByGameIdAndDeaktivatet(String gameId, boolean deaktivated);

	List<Player> findByGameIdAndDeaktivatetAndHeacked(String gameId, boolean deaktiviert, boolean hacked);

	@Transactional
	void deleteAllByGameId(String gameId);

}