package com.hacked.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.hacked.entity.Game;
import com.hacked.entity.Player;
import com.hacked.entity.Vote;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

/**
 * @author pd06286
 */
@RestController
public class GameController {
	@Autowired
	private HackedService hackedService;

	@RequestMapping("/generateGame")
	public String greeting() {
		return hackedService.generateGame();
	}

	@RequestMapping("/allGames")
	public List<Game> games() {
		return hackedService.getAllGames();
	}

	@RequestMapping("/allPlayer")
	public List<Player> player() {
		return hackedService.getAllPlayer();
	}

	@RequestMapping("/allVotes")
	public List<Vote> votes() {
		return hackedService.getAllVotes();
	}


}
