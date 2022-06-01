package com.example.bugrap.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class BugButton extends Button {
    
    public BugButton(Component icon) {
        super(icon);
        applyTheme();
    }
    
    public BugButton(String text, Component icon) {
        super(text, icon);
        applyTheme();
    }
    
    private void applyTheme() {
        addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getStyle().set("box-shadow", "var(--lumo-box-shadow-s");
    }
}
