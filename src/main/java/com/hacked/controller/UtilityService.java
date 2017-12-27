package com.hacked.controller;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;

@SpringComponent
public class UtilityService {

    public void generateInfoMeldung(String titel, String text) {
        MessageBox.setButtonDefaultAlignment(Alignment.BOTTOM_CENTER);
        MessageBox.setButtonDefaultIconsVisible(false);
        MessageBox.create().withCaption(titel).withMessage(text).withOkButton().open();
    }

    public void generateMeldung(String titel, String text) {
        MessageBox.setButtonDefaultAlignment(Alignment.BOTTOM_CENTER);
        MessageBox.setButtonDefaultIconsVisible(false);
        MessageBox.create().withCaption(titel).withMessage(text).open();
    }

    public void generateMeldung(String titel, Image image) {
        MessageBox.setButtonDefaultAlignment(Alignment.BOTTOM_CENTER);
        MessageBox.setButtonDefaultIconsVisible(false);
        MessageBox.create().withHeight("50%").withWidth("50%").withIcon(image, "300", "300").withCaption(titel).open();

    }

    public void generateJaNeinMeldung(String titel, String text, ButtonOption jaFunktion, ButtonOption neinFunktion) {
        MessageBox.setButtonDefaultAlignment(Alignment.BOTTOM_CENTER);
        MessageBox.setButtonDefaultIconsVisible(false);
        MessageBox.create().withCaption(titel).withMessage(text).withYesButton(jaFunktion).withNoButton(neinFunktion).open();
    }
}