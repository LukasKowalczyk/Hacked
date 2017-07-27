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
	@Value("${server.address}")
	private String serverAddress;
	@Value("${server.port}")
	private String serverPort;

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

	@ResponseBody
	@RequestMapping(value = "/getQR", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getQR(String gameId) {
		return QRCode.from("http://"+serverAddress+":" + serverPort + "/?gameId=" + gameId).withCharset("UTF-8").withSize(250, 250)
				.to(ImageType.PNG).stream().toByteArray();
	}

}
