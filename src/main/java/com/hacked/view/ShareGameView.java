package com.hacked.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hacked.controller.GameService;
import com.hacked.controller.HackedSessionService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author pd06286
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
	

	@PostConstruct
	void init() {
		String id = gameService.generateGame();
		Label gameId = new Label("GameId : " + id);
		Image qrCode = new Image();
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
		Button addGame = new Button("Login");
		addGame.addClickListener(e -> {
			HackedSessionService.setGameId(id);
			UI.getCurrent().getNavigator().navigateTo(LoginView.VIEW_NAME);
		});
		addComponents(gameId, qrCode, addGame);

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
