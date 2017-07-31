package com.hacked.entity;

import com.vaadin.icons.VaadinIcons;

/**
 * @author pd06286
 */
public enum Role {
	UNKNOWN(VaadinIcons.QUESTION_CIRCLE_O, "Unkown"), HACKER(VaadinIcons.BUG_O, "Hacker"), USER(VaadinIcons.USER,
			"User"), ADMIN(VaadinIcons.SHIELD, "Admin");
	private VaadinIcons icon;
	private String titel;

	private Role(VaadinIcons icon, String titel) {
		this.icon = icon;
		this.titel = titel;
	}

	public VaadinIcons getIcon() {
		return icon;
	}

	public String getTitel() {
		return titel;
	}
}
