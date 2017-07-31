package com.hacked.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hacked.controller.Broadcaster;
import com.hacked.controller.Broadcaster.BroadcastListener;
import com.hacked.controller.HackedService;
import com.hacked.controller.HackedSessionService;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.vaadin.annotations.Push;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

/**
 * @author pd06286
 */
@SpringView(name = LobbyView.VIEW_NAME)
@Push
public class LobbyView extends VerticalLayout implements View, BroadcastListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private HackedService hackedService;

	public static final String VIEW_NAME = "lobby";

	private Grid<Player> myGrid;

	private Button startGame;

	private Label playerReady;

	private String gameId;

	private Player player;

	@PostConstruct
	void init() {
		playerReady = new Label("Warte auf Spieler");
		gameId = HackedSessionService.getGameId();
		player = HackedSessionService.getPlayer();
		myGrid = generatePlayerTable(gameId, player);
		reloadListe(myGrid, gameId);

		startGame = new Button("Start Game");
		startGame.setVisible(false);
		startGame.setEnabled(false);
		if (hackedService.isPlayerMasterOfGame(player.getId(), gameId)) {
			startGame.setVisible(true);
		}
		if (hackedService.playerOfGameReady(gameId)) {
			startGame.setEnabled(true);
		}
		startGame.addClickListener(e -> {
			reloadListe(myGrid, gameId);
			startGame(player.getId(), gameId);
		});

		Button readyButton = new Button("Bereit");
		readyButton.addClickListener(e -> {
			setPlayerReady(myGrid, player);
			readyButton.setEnabled(false);
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(readyButton, startGame);

		addComponents(playerReady, myGrid, buttons);
		Broadcaster.register(gameId, this);
	}

	@Override
	public void detach() {
		Broadcaster.unregister(gameId, this);
		super.detach();
	}

	private Grid<Player> generatePlayerTable(String gameId, Player player) {
		Grid<Player> myGrid = new Grid<>();
		myGrid.setHeight("300px");
		myGrid.setWidth("250px");
		myGrid.addColumn(p -> ladeReadyImage(p), new HtmlRenderer()).setCaption(" ").setMaximumWidth(50);
		myGrid.addColumn(Player::getName).setCaption("Name");
		myGrid.addColumn(p -> ladeMasterImage(p, gameId), new HtmlRenderer()).setCaption(" ").setMaximumWidth(50);
		myGrid.select(player);
		return myGrid;
	}

	private String ladeMasterImage(Player p, String gameId) {
		if (hackedService.isPlayerMasterOfGame(p.getId(), gameId)) {
			return VaadinIcons.KEY.getHtml();
		}
		return "";
	}

	/**
	 * @param allPlayerReady
	 */
	private void startGame(long playerId, String gameId) {
		if (!hackedService.playerOfGameReady(gameId)) {
			return;
		}
		if (!hackedService.isGameReadyRunning(gameId)) {
			hackedService.startGame(gameId);
		}
		generateRoleMeldung(playerId);
		UI.getCurrent().getNavigator().navigateTo(RundeView.VIEW_NAME);
		Broadcaster.broadcast(player.getGameId(), "Spiel Startet");
	}

	private void generateRoleMeldung(long playerId) {
		Role role = hackedService.getPlayer(playerId).getRole();
		hackedService.generateMeldung("Deine Rolle",  role.getIcon().getHtml() + " :: " + role.getTitel()).show(Page.getCurrent());
	}

	private void setPlayerReady(Grid<Player> myGrid, Player player) {
		hackedService.setPlayerReady(player.getId());
		Broadcaster.broadcast(player.getGameId(), "Player " + player.getName() + " ist Bereit");
		myGrid.setItems(hackedService.getPlayerOfGame(player.getGameId()));
	}

	private void reloadListe(Grid<Player> myGrid, String gameId) {
		myGrid.setItems(hackedService.getPlayerOfGame(gameId));
	}

	private String ladeReadyImage(Player p) {
		if (p.isReady()) {
			return VaadinIcons.CHECK_CIRCLE_O.getHtml();
		}
		return VaadinIcons.CIRCLE_THIN.getHtml();
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

	@Override
	public void receiveBroadcast(String message) {
		getUI().access(() -> {
			hackedService.generateMeldung("", message).show(Page.getCurrent());
			reloadListe(myGrid, gameId);

			if (hackedService.playerOfGameReady(gameId)) {
				startGame.setEnabled(true);
				playerReady.setValue("Alle Spieler Bereit");
			}
			if (hackedService.isGameReadyRunning(gameId)) {
				generateRoleMeldung(player.getId());
				UI.getCurrent().getNavigator().navigateTo(RundeView.VIEW_NAME);
			}
		});
	}
}
