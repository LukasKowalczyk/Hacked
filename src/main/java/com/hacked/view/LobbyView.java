package com.hacked.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.jarektoro.responsivelayout.ResponsiveRow.SpacingSize;
import com.vaadin.annotations.Push;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
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

    private Button showQrCode;

    private Label playerReady;

    private Button readyButton;

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

        player = HackedSessionService.getPlayer();
        String gameId = player.getGameId();

        playerReady = new Label("Warte auf Spieler");
        if (!playerService.isMinPlayerCountOfGame(gameId)) {
            playerReady.setValue("Zu wenig Spieler (Min. 3)");
        }
        ResponsiveColumn gameIdInput = new ResponsiveColumn();
        gameIdInput.setComponent(playerReady);
        rootRow.addColumn(gameIdInput);

        myGrid = generatePlayerTable(gameId, player);
        reloadListe(myGrid, gameId);
        ResponsiveColumn gridOutput = new ResponsiveColumn();
        gridOutput.setComponent(myGrid);
        rootRow.addColumn(gridOutput);

        showQrCode = new Button("QR-Code");
        showQrCode.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        showQrCode.setVisible(playerService.isPlayerMasterOfGame(player.getId(), gameId));
        // showQrCode.setEnabled(playerService.playerOfGameReady(gameId));
        showQrCode.addClickListener(e -> {
            Image qrCode = new Image();
            qrCode.setSource(new StreamResource(new StreamResource.StreamSource() {
                /**
                *
                */
                private static final long serialVersionUID = 1L;

                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(gameService.getQR(gameId));

                }
            }, ""));
            utilityService.generateMeldung(gameId, qrCode);
        });

        readyButton = new Button("Bereit");
        readyButton.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        readyButton.setEnabled(playerService.isMinPlayerCountOfGame(gameId));
        readyButton.setDisableOnClick(true);
        readyButton.addClickListener(e -> {
            setPlayerReady(myGrid, player);
            readyButton.setEnabled(false);
        });

        HorizontalLayout buttonBar = new HorizontalLayout(readyButton, showQrCode);
        buttonBar.setComponentAlignment(readyButton, Alignment.MIDDLE_CENTER);
        buttonBar.setComponentAlignment(showQrCode, Alignment.MIDDLE_CENTER);
        buttonBar.setSpacing(true);
        buttonBar.setMargin(true);
        ResponsiveColumn readyButtonInput = new ResponsiveColumn();
        readyButtonInput.setComponent(buttonBar);
        rootRow.addColumn(readyButtonInput);

        Broadcaster.register(gameId, this);
    }

    @Override
    public void detach() {
        Broadcaster.unregister(player.getGameId(), this);
        super.detach();
    }

    private Grid<Player> generatePlayerTable(String gameId, Player player) {
        Grid<Player> myGrid = new Grid<>();
        myGrid.removeHeaderRow(0);
        myGrid.setResponsive(true);
        myGrid.setSelectionMode(SelectionMode.NONE);
        myGrid.setHeightByRows(5);
        myGrid.setWidth("100%");
        myGrid.setStyleName(MaterialTheme.TABLE_COMPACT);
        myGrid.addColumn(p -> ladeReadyImage(p), new HtmlRenderer()).setExpandRatio(0);
        myGrid.addColumn(Player::getName).setExpandRatio(2);
        return myGrid;
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
        Player player = playerService.getPlayer(playerId);

        String text = "Du bist ein " + player.getRole().getTitel() + "\n\n" + "deine aufgabe ist es....";

        utilityService.generateInfoMeldung("Hallo " + player.getName(), text);
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
                showQrCode.setEnabled(true);
                playerReady.setValue("Alle Spieler Bereit");
            }
            startGame(player.getId(), gameId);
        });
    }
}
