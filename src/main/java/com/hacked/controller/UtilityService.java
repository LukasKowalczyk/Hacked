package com.hacked.controller;

import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@SpringComponent
public class UtilityService {
	
	public Notification generateMeldung(String titel, String text) {
		Notification notif = new Notification(titel, Type.ASSISTIVE_NOTIFICATION);
		notif.setHtmlContentAllowed(true);
		notif.setPosition(Position.TOP_LEFT);
		notif.setDescription(text);
		return notif;
	}

	public Notification generateMeldung(String titel, String text, Position position) {
		Notification notif = new Notification(titel, Type.ASSISTIVE_NOTIFICATION);
		notif.setHtmlContentAllowed(true);
		notif.setPosition(position);
		notif.setDescription(text);
		return notif;
	}
}