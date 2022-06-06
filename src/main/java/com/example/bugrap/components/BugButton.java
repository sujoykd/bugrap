package com.example.bugrap.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class BugButton extends Button {

    public BugButton(Component icon) {
        super(icon);
        this.applyStyles();
    }

    public BugButton(String text, Component icon) {
        super(text, icon);
        this.applyStyles();
    }

    public BugButton(String text) {
        super(text);
        this.applyStyles();
    }

    private void applyStyles() {
        this.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");

    }

    public BugButton lumoBaseColor() {
        this.getStyle().set("background-color", "var(--lumo-base-color)");
        return this;
    }

    public BugButton withTheme(ButtonVariant... buttonVariants) {
        this.addThemeVariants(buttonVariants);
        return this;
    }
}
