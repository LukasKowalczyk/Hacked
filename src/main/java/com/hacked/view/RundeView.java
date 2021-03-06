package com.hacked.view;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.appreciated.material.MaterialTheme;
import com.hacked.controller.Broadcaster;
import com.hacked.controller.Broadcaster.BroadcastListener;
import com.hacked.controller.HackedSessionService;
import com.hacked.controller.PlayerService;
import com.hacked.controller.UtilityService;
import com.hacked.controller.VoteService;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.jarektoro.responsivelayout.ResponsiveRow.SpacingSize;
import com.vaadin.annotations.Push;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author
 */
@SpringView(name = RundeView.VIEW_NAME)
@Push
public class RundeView extends VerticalLayout implements View, BroadcastListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String VIEW_NAME = "runde";

    private String gameId;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private UtilityService utilityService;

    private Button scannenButton;

    private Button hackButton;

    private Button wahlButton;

    private Grid<Player> myGrid;

    private Player player;

    @PostConstruct
    void init() {
        setSizeFull();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        addComponent(responsiveLayout);
        ResponsiveRow rootRow = responsiveLayout.addRow();
        rootRow.setHeight("100%");
        rootRow.setHorizontalSpacing(SpacingSize.NORMAL, true);
        rootRow.setVerticalSpacing(SpacingSize.SMALL, false);
        rootRow.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        player = playerService.getPlayer(HackedSessionService.getPlayerId());

        ResponsiveColumn nameOutput = new ResponsiveColumn();
        nameOutput.setComponent(new Label("Spieler: " + player.getName()));
        rootRow.addColumn(nameOutput);

        gameId = HackedSessionService.getGameId();
        myGrid = generateSpielerTabelle();
        ResponsiveColumn gridOutput = new ResponsiveColumn();
        gridOutput.setComponent(myGrid);
        rootRow.addColumn(gridOutput);

        wahlButton = new Button("Wählen");
        wahlButton.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        wahlButton.addClickListener(e -> {
            Player votedPlayer = getSelectedPlayer();
            if (votedPlayer != null) {
                voteService.voteForPlayer(player.getId(), votedPlayer.getId(), gameId);
                System.out.println(player.getName() + " -> Wurde gewählt");
                wahlButton.setEnabled(false);
                myGrid.deselectAll();
                Broadcaster.broadcast(gameId, player.getName() + " hat gewählt");
            }
        });
        ResponsiveColumn wahlButtonInput = new ResponsiveColumn();
        wahlButtonInput.setComponent(wahlButton);
        rootRow.addColumn(wahlButtonInput);

        hackButton = new Button("Hacken");
        hackButton.setVisible(Role.HACKER == player.getRole());
        hackButton.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        hackButton.addClickListener(e -> {
            Player hackedPlayer = getSelectedPlayer();
            if (hackedPlayer != null) {
                playerService.hackPlayer(hackedPlayer.getId());
                System.out.println(player.getName() + " -> Wurde gehackt");
                hackButton.setEnabled(false);
                myGrid.deselectAll();
            }
        });
        ResponsiveColumn hackButtonInput = new ResponsiveColumn();
        hackButtonInput.setComponent(hackButton);
        rootRow.addColumn(hackButtonInput);

        scannenButton = new Button("Scannen");
        scannenButton.setVisible(Role.ADMIN == player.getRole());
        scannenButton.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        scannenButton.addClickListener(e -> {
            Player scannedPlayer = getSelectedPlayer();
            if (scannedPlayer != null) {
                generateScanMeldung(scannedPlayer);
                System.out.println(player.getName() + " -> Wurde gescannt");
                scannenButton.setEnabled(false);
                myGrid.deselectAll();
            }
        });

        ResponsiveColumn scannenButtonInput = new ResponsiveColumn();
        scannenButtonInput.setComponent(scannenButton);
        rootRow.addColumn(scannenButtonInput);

        Broadcaster.register(gameId, this);
    }

    private Player getSelectedPlayer() {
        Object[] array = myGrid.getSelectedItems().toArray();
        if (array.length > 0) {
            Player hackedPlayer = (Player) array[0];
            if (hackedPlayer.getId() != player.getId()) {
                return hackedPlayer;
            }
        }
        return null;
    }

    private void generateSpielEndeMeldung() {
        String meldungsText = "Das Spiel ist zu Ende!";
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
        String hackedMeldungsText = "";
        if (hackedPlayer != null) {
            hackedMeldungsText = "\n" + hackedPlayer.getName() + " wurde gehackt!";
        }

        String meldungsText = votedPlayer.getName() + " wurde rausgewaehlt!" + hackedMeldungsText;
        utilityService.generateInfoMeldung("Was bisher geschah!", meldungsText);
    }

    private Grid<Player> generateSpielerTabelle() {
        Grid<Player> myGrid = new Grid<>();
        myGrid.setSelectionMode(SelectionMode.SINGLE);
        myGrid.removeHeaderRow(0);
        myGrid.setResponsive(true);
        myGrid.setHeightByRows(5);
        myGrid.setWidth("100%");
        myGrid.setStyleName(MaterialTheme.TABLE_COMPACT);
        myGrid.setItems(getPlayer());
        myGrid.addColumn(Player::getName).setCaption("Name");
        return myGrid;
    }

    private boolean isRundeEnde(String gameId) {
        return voteService.allPlayerVoted(gameId);
    }

    private void generateScanMeldung(Player player) {
        utilityService.generateInfoMeldung(player.getName(), player.getRole().getIcon().getHtml());
    }

    private List<Player> getPlayer() {
        String gameId = HackedSessionService.getGameId();
        return playerService.getOtherAktivPlayerOfGame(gameId, player.getId());
    }

    @Override
    public void detach() {
        Broadcaster.unregister(gameId, this);
        super.detach();
    }

    @Override
    public void receiveBroadcast(String message) {
        getUI().access(() -> {
            // hackedService.generateMeldung("",
            // message).show(Page.getCurrent());
            if (isRundeEnde(gameId)) {
                scannenButton.setEnabled(true);
                hackButton.setEnabled(true);
                wahlButton.setEnabled(true);
                Player votedPlayer = voteService.getPlayerOfVot(gameId);
                Player hackedPlayer = playerService.getHackedPlayer();
                if (hackedPlayer != null) {
                    playerService.deaktivatePlayer(hackedPlayer.getId());
                    if (hackedPlayer.getId() == player.getId()) {
                        utilityService.generateInfoMeldung("", "Du Wurdes gehackt!");
                        UI.getCurrent().getNavigator().navigateTo(EndeView.VIEW_NAME);
                    }
                }
                if (votedPlayer != null) {
                    playerService.deaktivatePlayer(votedPlayer.getId());
                    if (votedPlayer.getId() == player.getId()) {
                        utilityService.generateInfoMeldung("", "Du Wurdes rausgewählt!");
                        UI.getCurrent().getNavigator().navigateTo(EndeView.VIEW_NAME);
                    }
                }
                generateRundeEndeMeldung(gameId, votedPlayer, hackedPlayer);
                HackedSessionService.setRound(null);
                voteService.deleteVotes(gameId);
                myGrid.setItems(getPlayer());
                if (isSpielEnde()) {
                    generateSpielEndeMeldung();
                    HackedSessionService.setWinner(ermittleGewinner());
                    UI.getCurrent().getNavigator().navigateTo(EndeView.VIEW_NAME);
                }
            }
        });
    }

    private Role ermittleGewinner() {
        return Role.USER;
    }

    /*
     * (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener. ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
