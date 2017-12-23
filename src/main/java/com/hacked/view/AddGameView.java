package com.hacked.view;

import javax.annotation.PostConstruct;

import com.github.appreciated.material.MaterialTheme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
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

	private Label titel;
	private Label text;
	private Button addGame;

	@PostConstruct
	void init() {
		titel = new Label("Wilkommen!");
		text = new Label("Klicke auf den Button um ein neues Spiel zu erstellen.");
		addGame = new Button("Erstelle Spiel");
		addGame.addStyleName(MaterialTheme.BUTTON_BORDERLESS);
		addGame.addClickListener(e -> {
			UI.getCurrent().getNavigator().navigateTo(ShareGameView.VIEW_NAME);
		});

		Panel container = new Panel();
		VerticalLayout content = new VerticalLayout(titel, text, addGame);
		content.setSpacing(true);
		content.setMargin(true);
		content.setComponentAlignment(titel, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(text, Alignment.MIDDLE_CENTER);
		content.setComponentAlignment(addGame, Alignment.MIDDLE_CENTER);
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
