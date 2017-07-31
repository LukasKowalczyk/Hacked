package com.hacked.view;

import javax.annotation.PostConstruct;


import com.hacked.controller.HackedSessionService;
import com.hacked.entity.Role;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = EndeView.VIEW_NAME)
public class EndeView extends VerticalLayout implements View {
	/**
	*
	*/
	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = "ende";

	@PostConstruct
	void init() {
		Role winner = HackedSessionService.getWinner();
		String text = "Spiel ist zu ende!\n";
		if(winner!=null){
			 text += winner.name() + " haben gewonnen!";
		}		
		Label hilfeText = new Label(
				text);
		addComponent(hilfeText);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
