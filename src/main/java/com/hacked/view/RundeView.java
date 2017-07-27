package com.hacked.view;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hacked.controller.HackedService;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.hacked.service.HackedSessionService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

/**
 * @author pd06286
 */
@SpringView(name = RundeView.VIEW_NAME)
public class RundeView extends VerticalLayout implements View {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "runde";

	@Autowired
	private HackedService hackedService;

	@PostConstruct
	void init() {
		long playerId = HackedSessionService.getPlayerId();
		Player player = hackedService.getPlayer(playerId);
		String gameId = HackedSessionService.getGameId();
		Grid<Player> myGrid = generateSpielerTabelle();

		Button wahlButton = new Button("Wählen");
		wahlButton.addClickListener(e -> {
			Player votedPlayer = (Player) myGrid.getSelectedItems().toArray()[0];
			hackedService.voteForPlayer(playerId, votedPlayer.getId(), gameId);
			System.out.println(player.getName() + " -> Wurde gewählt");
			wahlButton.setEnabled(false);
		});

		Button hackButton = new Button("Hacken");
		hackButton.setVisible(Role.HACKER == player.getRole());
		hackButton.addClickListener(e -> {
			Player hackedPlayer = (Player) myGrid.getSelectedItems().toArray()[0];
			hackedService.hackPlayer(hackedPlayer.getId());
			System.out.println(player.getName() + " -> Wurde gehackt");
			hackButton.setEnabled(false);
		});

		Button scannenButton = new Button("Scannen");
		scannenButton.setVisible(Role.ADMIN == player.getRole());
		scannenButton.addClickListener(e -> {
			Player scannedPlayer = (Player) myGrid.getSelectedItems().toArray()[0];
			generateScanMeldung(scannedPlayer);
			System.out.println(player.getName() + " -> Wurde gescannt");
			scannenButton.setEnabled(false);
		});

		Button aktualisieren = new Button("Aktualisieren");
		aktualisieren.addClickListener(e -> {
			if (isRundeEnde(gameId)) {
				scannenButton.setEnabled(true);
				hackButton.setEnabled(true);
				wahlButton.setEnabled(true);
				Player votedPlayer = hackedService.getPlayerOfVot(gameId);
				Player hackedPlayer = hackedService.getHackedPlayer();
				if (hackedPlayer != null) {
					hackedService.deaktivatePlayer(hackedPlayer.getId());
					System.out.println("Spieler " + hackedPlayer.getName() + " Wurde gehackt");
				}
				if (votedPlayer != null) {
					hackedService.deaktivatePlayer(votedPlayer.getId());
					System.out.println("Spieler " + votedPlayer.getName() + " Wurde rausgewaehlt");
				}
				generateRundeEndeMeldung(gameId, votedPlayer, hackedPlayer);
				HackedSessionService.setRound(null);
				hackedService.deleteVotes(gameId);
				myGrid.setItems(getPlayer());
				if (isSpielEnde()) {
					generateSpielEndeMeldung();
				}
			}
		});

		addComponents(new Label("Spieler: " + player.getName()), myGrid, wahlButton, aktualisieren, hackButton,
				scannenButton);
	}

	private void generateSpielEndeMeldung() {
		String meldungsText = "DAS Spiel ist zu Ende!";
		Notification scanNotif = new Notification("Was bisher geschah!");
		scanNotif.setDescription(meldungsText);
		scanNotif.setHtmlContentAllowed(true);
		scanNotif.setPosition(Position.TOP_LEFT);
		scanNotif.setDelayMsec(2000);
		scanNotif.show(Page.getCurrent());
	}

	private boolean isSpielEnde() {
		return false;
	}

	private void generateRundeEndeMeldung(String gameId, Player votedPlayer, Player hackedPlayer) {
		String meldungsText = votedPlayer.getName() + " wurde rausgewaehlt!" + "\n" + hackedPlayer.getName()
				+ " wurde gehackt!";
		Notification scanNotif = new Notification("Was bisher geschah!");
		scanNotif.setDescription(meldungsText);
		scanNotif.setHtmlContentAllowed(true);
		scanNotif.setPosition(Position.TOP_LEFT);
		scanNotif.setDelayMsec(2000);
		scanNotif.show(Page.getCurrent());
	}

	private Grid<Player> generateSpielerTabelle() {
		Grid<Player> myGrid = new Grid<>();
		myGrid.setSelectionMode(SelectionMode.SINGLE);
		myGrid.setHeight("300px");
		myGrid.setWidth("250px");
		myGrid.setItems(getPlayer());
		myGrid.addColumn(Player::getName).setCaption("Name");
		return myGrid;
	}

	private boolean isRundeEnde(String gameId) {
		return hackedService.allPlayerVoted(gameId);
	}

	private void generateScanMeldung(Player player) {
		Notification scanNotif = new Notification(player.getName());
		scanNotif.setDescription("" + player.getRole().name());
		scanNotif.setHtmlContentAllowed(true);
		scanNotif.setPosition(Position.TOP_LEFT);
		scanNotif.setDelayMsec(2000);
		scanNotif.show(Page.getCurrent());
	}

	private List<Player> getPlayer() {
		String gameId = HackedSessionService.getGameId();
		return hackedService.getAktivPlayerOfGame(gameId);
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
