package com.hacked.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.hacked.controller.HackedService;
import com.hacked.entity.Player;
import com.hacked.entity.Role;
import com.hacked.service.HackedSessionService;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

/**
 * @author pd06286
 */
@SpringView(name = HelpView.VIEW_NAME)
public class HelpView extends VerticalLayout implements View {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "help";

	@PostConstruct
	void init() {
		Label hilfeText = new Label("Hilfe für Spieler!");
		addComponent(hilfeText);

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
