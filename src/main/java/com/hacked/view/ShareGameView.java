package com.hacked.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.appreciated.material.MaterialTheme;
import com.hacked.controller.GameService;
import com.hacked.controller.HackedSessionService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author
 */
@SpringView(name = ShareGameView.VIEW_NAME)
public class ShareGameView extends VerticalLayout implements View {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "shareGame";

	@Autowired
	private GameService gameService;

	private Label gameId;

	private Image qrCode;

	private Button joinGame;

	@PostConstruct
	void init() {
		String id = gameService.generateGame();
		gameId = new Label("GameId : " + id);
		qrCode = new Image();
		qrCode.setSource(new StreamResource(new StreamResource.StreamSource() {
			/**
			*
			*/
			private static final long serialVersionUID = 1L;

			@Override
			public InputStream getStream() {
				return new ByteArrayInputStream(gameService.getQR(id));

			}
		}, ""));
		joinGame = new Button("Login");
		joinGame.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
		joinGame.addClickListener(e -> {
			HackedSessionService.setGameId(id);
			UI.getCurrent().getNavigator().navigateTo(LoginView.VIEW_NAME);
		});
		
		Panel container = new Panel();
		VerticalLayout content = new VerticalLayout(gameId, qrCode, joinGame);
		content.setSpacing(true);
		content.setMargin(true);
		content.setComponentAlignment(gameId, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(qrCode, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(joinGame, Alignment.MIDDLE_CENTER);
		container.setContent(content);
		addComponents(container);
		
		this.setSizeFull();
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
