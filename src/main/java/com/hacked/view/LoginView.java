package com.hacked.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hacked.controller.Broadcaster;
import com.hacked.controller.HackedService;
import com.hacked.controller.HackedSessionService;
import com.hacked.controller.SessionKonstanten;
import com.hacked.entity.Player;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author pd06286
 */
@SpringView(name = LoginView.VIEW_NAME)
@UIScope
public class LoginView extends VerticalLayout implements View {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private HackedService hackedService;

	public static final String VIEW_NAME = "login";

	@PostConstruct
	void init() {

		String gameIdText = (String) UI.getCurrent().getSession().getAttribute(SessionKonstanten.GAME_ID);
		TextField textFieldgameId = new TextField("Spiel-ID");
		textFieldgameId.setValue(gameIdText);
		TextField textFieldplayerName = new TextField("Spielername");
		Button buttonAnmelden = new Button("Anmelden");
		buttonAnmelden.addStyleName(ValoTheme.BUTTON_SMALL);
		buttonAnmelden
				.addClickListener(e -> addPlayerToGame(textFieldgameId.getValue(), textFieldplayerName.getValue()));
		addComponents(textFieldgameId, textFieldplayerName, buttonAnmelden);

	}

	/**
	 * @param textFieldgameId
	 * @param textFieldplayerName
	 */
	private void addPlayerToGame(String gameId, String playerName) {
		long id = hackedService.addPlayerToGame(playerName, gameId);
		Player player = hackedService.getPlayer(id);
		HackedSessionService.setGameId(gameId);
		HackedSessionService.setPlayerId(player.getId());
		HackedSessionService.setPlayer(player);
		Broadcaster.broadcast(gameId, "Spieler " + playerName + " tritt dem Spiel bei.");
		getUI().getNavigator().navigateTo(LobbyView.VIEW_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.
	 * ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
