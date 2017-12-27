package com.hacked.ui;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import com.hacked.controller.SessionKonstanten;
import com.hacked.view.AddGameView;
import com.hacked.view.HelpView;
import com.hacked.view.LoginView;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

/**
 * @author
 */
@Theme("hackedTheme")
@SpringUI
@Push
public class MainUI extends UI {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    private SpringViewProvider viewProvider;

    /*
     * (non-Javadoc)
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        // wenn ?gameId=... im path ist muss das in die session mit rein
        String gameIdText = "";
        setResponsive(true);

        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);

        URI location = Page.getCurrent().getLocation();
        String url = location.toString();

        if (url.contains("?" + SessionKonstanten.GAME_ID + "=") && !url.contains("#")) {
            String gamIdParameter = location.getQuery();
            gameIdText = gamIdParameter.substring(gamIdParameter.indexOf("=") + 1);
            url = url.substring(0, url.indexOf("?"));
            Page.getCurrent().replaceState(URI.create(url));
        }

        UI.getCurrent().getSession().setAttribute(SessionKonstanten.GAME_ID, gameIdText);
        if (url.endsWith("#!" + HelpView.VIEW_NAME)) {
            getUI().getNavigator().navigateTo(HelpView.VIEW_NAME);
        } else if (url.endsWith("#!" + AddGameView.VIEW_NAME)) {
            getUI().getNavigator().navigateTo(AddGameView.VIEW_NAME);
        } else {
            getUI().getNavigator().navigateTo(LoginView.VIEW_NAME);
        }
    }

}
