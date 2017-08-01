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

	private Button readyButton;

	private Player player;

	@PostConstruct
	void init() {
		player = HackedSessionService.getPlayer();
		String gameId = player.getGameId();

		playerReady = new Label("Warte auf Spieler");
		if (!hackedService.isMinPlayerCountOfGame(gameId)) {
			playerReady.setValue("Zu wenig Spieler (Min. 3)");
		}
		
		myGrid = generatePlayerTable(gameId, player);
		
		startGame = new Button("Start Game");
		startGame.setVisible(hackedService.isPlayerMasterOfGame(player.getId(), gameId));
		startGame.setEnabled(hackedService.playerOfGameReady(gameId));
		startGame.addClickListener(e -> {
			reloadListe(myGrid, gameId);
			startGame(player.getId(), gameId);
		});

		readyButton = new Button("Bereit");
		readyButton.setEnabled(hackedService.isMinPlayerCountOfGame(gameId));
		readyButton.setDisableOnClick(true);
		readyButton.addClickListener(e -> {
			setPlayerReady(myGrid, player);
		});

		addComponents(playerReady, myGrid, new HorizontalLayout(readyButton, startGame));
		Broadcaster.register(gameId, this);
	}

	@Override
	public void detach() {
		Broadcaster.unregister(player.getGameId(), this);
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
		hackedService.generateMeldung("Deine Rolle", role.getIcon().getHtml() + " :: " + role.getTitel())
				.show(Page.getCurrent());
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
		// Auto-generated method stub
	}

	@Override
	public void receiveBroadcast(String message) {
		System.out.println(message);
		getUI().access(() -> {
			String gameId = player.getGameId();
			readyButton.setEnabled(hackedService.isMinPlayerCountOfGame(gameId));
			// hackedService.generateMeldung("",
			// message).show(Page.getCurrent());
			reloadListe(myGrid, gameId);
			if (!hackedService.isMinPlayerCountOfGame(gameId)) {
				playerReady.setValue("Zu wenig Spieler (Min. 3)");
			} else {
				playerReady.setValue("Warte auf Spieler");
			}
			if (hackedService.playerOfGameReady(gameId)) {
				startGame.setEnabled(true);
				playerReady.setValue("Alle Spieler Bereit");
			}
			startGame(player.getId(), gameId);
		});
	}
}
