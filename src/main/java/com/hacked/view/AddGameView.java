package com.hacked.view;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author  
 */
@SpringView(name = AddGameView.VIEW_NAME)
public class AddGameView extends VerticalLayout implements View {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "addGame";

	@PostConstruct
	void init() {
		Label titel = new Label("Wilkommen!");
		Label text = new Label("Klicke auf den Button um ein neues Spiel zu erstellen.");
		Button addGame = new Button("Erstelle Spiel");
		addGame.addClickListener(e -> {
			UI.getCurrent().getNavigator().navigateTo(ShareGameView.VIEW_NAME);
		});
		addComponents(titel, text, addGame);
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
