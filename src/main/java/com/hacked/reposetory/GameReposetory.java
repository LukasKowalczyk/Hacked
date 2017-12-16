package com.hacked.reposetory;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

import com.hacked.entity.Game;

/**
 * @author  
 */
public interface GameReposetory extends CrudRepository<Game, String> {
    List<Game> findAll();
}
