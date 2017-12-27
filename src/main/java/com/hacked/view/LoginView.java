package com.hacked.view;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.appreciated.material.MaterialTheme;
import com.hacked.controller.Broadcaster;
import com.hacked.controller.HackedSessionService;
import com.hacked.controller.PlayerService;
import com.hacked.controller.SessionKonstanten;
import com.hacked.entity.Player;
import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.jarektoro.responsivelayout.ResponsiveRow.SpacingSize;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import de.steinwedel.messagebox.MessageBox;

/**
 * @author
 */
@SpringView(name = LoginView.VIEW_NAME)
@UIScope
public class LoginView extends VerticalLayout implements View {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private TextField textFieldplayerName;

    private TextField textFieldgameId;

    private Button buttonAnmelden;

    @Autowired
    private PlayerService playerService;

    public static final String VIEW_NAME = "login";

    @PostConstruct
    void init() {

        setSizeFull();
        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        // textFieldgameId, textFieldplayerName, buttonAnmelden
        addComponent(responsiveLayout);
        ResponsiveRow rootRow = responsiveLayout.addRow();
        rootRow.setHeight("100%");

        rootRow.setHorizontalSpacing(SpacingSize.NORMAL, true);
        rootRow.setVerticalSpacing(SpacingSize.SMALL, false);
        rootRow.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        MessageBox.create().withCaption("Willkommen").withMessage(
            "Du wurdes eingeladen an einem Spiel \"hacked\" teilzunehmen.\nMelde dich mit einer Spiel-ID und deinem Nicknamen an!")
            .withOkButton().open();

        // MessageBox.createInfo().withCaption("Custom button captions").withMessage("Button captions replaced!")
        // .withYesButton(ButtonOption.caption("Yea")).withNoButton(ButtonOption.caption("Nay")).open();
        //
        // MessageBox.createQuestion().withCaption("Example 6").withMessage("Do you really want to continue?")
        // .withYesButton(() -> {
        // System.out.println("Yes button was pressed.");
        // }).withNoButton(() -> {
        // System.out.println("No button was pressed.");
        // }).open();
        //
        // MessageBox.createInfo().withCaption("Example 1").withMessage("Hello World!").withOkButton().open();
        String gameIdText = (String) UI.getCurrent().getSession().getAttribute(SessionKonstanten.GAME_ID);

        textFieldgameId = new TextField("Spiel-ID");
        textFieldgameId.setSizeFull();
        textFieldgameId.setValue(gameIdText);
        ResponsiveColumn gameIdInput = new ResponsiveColumn();
        gameIdInput.setComponent(textFieldgameId);
        rootRow.addColumn(gameIdInput);

        textFieldplayerName = new TextField("Spielername");
        textFieldplayerName.setSizeFull();
        ResponsiveColumn playerNameInput = new ResponsiveColumn();
        playerNameInput.setComponent(textFieldplayerName);
        rootRow.addColumn(playerNameInput);

        buttonAnmelden = new Button("Anmelden");
        buttonAnmelden.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
        buttonAnmelden.addClickListener(e -> addPlayerToGame(textFieldgameId.getValue(), textFieldplayerName.getValue()));
        ResponsiveColumn anmeldenInput = new ResponsiveColumn();
        anmeldenInput.setComponent(buttonAnmelden);
        rootRow.addColumn(anmeldenInput);

    }

    /**
     * @param textFieldgameId
     * @param textFieldplayerName
     */
    private void addPlayerToGame(String gameId, String playerName) {
        if (playerService.isPlayerInGame(playerName, gameId)) {
            textFieldplayerName.setComponentError(new UserError("Spieler exestiert schon!"));
            return;
        }

        long id = playerService.addPlayerToGame(playerName, gameId);
        Player player = playerService.getPlayer(id);
        HackedSessionService.setGameId(gameId);
        HackedSessionService.setPlayerId(player.getId());
        HackedSessionService.setPlayer(player);
        Broadcaster.broadcast(gameId, "Spieler " + playerName + " tritt dem Spiel bei.");
        getUI().getNavigator().navigateTo(LobbyView.VIEW_NAME);
    }

    /*
     * (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener. ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
    }
}
