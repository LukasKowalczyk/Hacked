package com.hacked.view;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.appreciated.material.MaterialTheme;
import com.hacked.controller.Broadcaster;
import com.hacked.controller.Broadcaster.BroadcastListener;
import com.hacked.controller.GameService;
import com.hacked.controller.HackedSessionService;
import com.hacked.controller.PlayerService;
import com.hacked.controller.UtilityService;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.vaadin.annotations.Push;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

/**
 * @author
 */
@SpringView(name = LobbyView.VIEW_NAME)
@Push
public class LobbyView extends VerticalLayout implements View, BroadcastListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    private GameService gameService;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private PlayerService playerService;

    public static final String VIEW_NAME = "lobby";

    private Grid<Player> myGrid;

    private Button startGame;

    private Label playerReady;

    private Button readyButton;

    private Player player;

    @PostConstruct
    void init() {
        this.setSizeUndefined();
        player = HackedSessionService.getPlayer();
        String gameId = player.getGameId();

        playerReady = new Label("Warte auf Spieler");
        playerReady.setWidth("100%");
        if (!playerService.isMinPlayerCountOfGame(gameId)) {
            playerReady.setValue("Zu wenig Spieler (Min. 3)");
        }

        myGrid = generatePlayerTable(gameId, player);
        reloadListe(myGrid, gameId);
        myGrid.setWidth("100%");
        myGrid.setHeightByRows(5);

        startGame = new Button("Start Game");
        startGame.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        startGame.setVisible(playerService.isPlayerMasterOfGame(player.getId(), gameId));
        startGame.setEnabled(playerService.playerOfGameReady(gameId));
        startGame.addClickListener(e -> {
            reloadListe(myGrid, gameId);
            startGame(player.getId(), gameId);
        });

        readyButton = new Button("Bereit");
        readyButton.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        readyButton.setEnabled(playerService.isMinPlayerCountOfGame(gameId));
        readyButton.setDisableOnClick(true);
        readyButton.addClickListener(e -> {
            setPlayerReady(myGrid, player);
        });

        HorizontalLayout buttonBar = new HorizontalLayout(readyButton, startGame);
        buttonBar.setWidth("100%");
        buttonBar.setComponentAlignment(readyButton, Alignment.MIDDLE_CENTER);
        buttonBar.setComponentAlignment(startGame, Alignment.MIDDLE_CENTER);
        buttonBar.setSpacing(true);
        buttonBar.setMargin(true);

        VerticalLayout table = new VerticalLayout(playerReady, myGrid, buttonBar);
        table.setMargin(true);

        Panel containerTable = new Panel();
        containerTable.setContent(table);
        addComponents(containerTable);

        Broadcaster.register(gameId, this);
        this.setSizeFull();
    }

    @Override
    public void detach() {
        Broadcaster.unregister(player.getGameId(), this);
        super.detach();
    }

    private Grid<Player> generatePlayerTable(String gameId, Player player) {

        Grid<Player> myGrid = new Grid<>();
        myGrid.removeHeaderRow(0);
        myGrid.setStyleName(MaterialTheme.TABLE_BORDERLESS);
        myGrid.addColumn(p -> ladeReadyImage(p), new HtmlRenderer()).setMaximumWidth(50);
        myGrid.addColumn(Player::getName);
        myGrid.addColumn(p -> ladeMasterImage(p, gameId), new HtmlRenderer()).setMaximumWidth(50);
        myGrid.select(player);
        return myGrid;
    }

    private String ladeMasterImage(Player p, String gameId) {
        if (playerService.isPlayerMasterOfGame(p.getId(), gameId)) {
            return VaadinIcons.KEY.getHtml();
        }
        return "";
    }

    /**
     * @param allPlayerReady
     */
    private void startGame(long playerId, String gameId) {
        if (!playerService.playerOfGameReady(gameId)) {
            return;
        }
        if (!gameService.isGameReadyRunning(gameId)) {
            gameService.startGame(gameId);
        }
        generateRoleMeldung(playerId);
        UI.getCurrent().getNavigator().navigateTo(RundeView.VIEW_NAME);
        Broadcaster.broadcast(player.getGameId(), "Spiel Startet");
    }

    private void generateRoleMeldung(long playerId) {
        Role role = playerService.getPlayer(playerId).getRole();
        utilityService.generateMeldung("Deine Rolle", role.getIcon().getHtml() + " :: " + role.getTitel())
            .show(Page.getCurrent());
    }

    private void setPlayerReady(Grid<Player> myGrid, Player player) {
        playerService.setPlayerReady(player.getId());
        Broadcaster.broadcast(player.getGameId(), "Player " + player.getName() + " ist Bereit");
        myGrid.setItems(playerService.getPlayerOfGame(player.getGameId()));
    }

    private void reloadListe(Grid<Player> myGrid, String gameId) {
        myGrid.setItems(playerService.getPlayerOfGame(gameId));
    }

    private String ladeReadyImage(Player p) {
        if (p.isReady()) {
            return VaadinIcons.CHECK_CIRCLE_O.getHtml();
        }
        return VaadinIcons.CIRCLE_THIN.getHtml();
    }

    /*
     * (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener. ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // Auto-generated method stub
    }

    @Override
    public void receiveBroadcast(String message) {
        getUI().access(() -> {
            String gameId = player.getGameId();
            readyButton.setEnabled(playerService.isMinPlayerCountOfGame(gameId));
            reloadListe(myGrid, gameId);
            if (!playerService.isMinPlayerCountOfGame(gameId)) {
                playerReady.setValue("Zu wenig Spieler (Min. 3)");
            } else {
                playerReady.setValue("Warte auf Spieler");
            }
            if (playerService.playerOfGameReady(gameId)) {
                startGame.setEnabled(true);
                playerReady.setValue("Alle Spieler Bereit");
            }
            startGame(player.getId(), gameId);
        });
    }
}
