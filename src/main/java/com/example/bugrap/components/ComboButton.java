package com.example.bugrap.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Style;

public class ComboButton extends HorizontalLayout {
    List<Button> buttons;
    String selectedTheme;
    Style defaultStyle;
    ButtonVariant[] buttonVariants;
    
    Button defaultButton;
    
    private ComboButton() {
        this.buttons = new ArrayList<>();
        setSpacing(false);
        setPadding(false);
    }
    
    public static final class Builder {
        ComboButton comboButton;
        
        public Builder() {
            comboButton = new ComboButton();
        }
        
        public Builder add(Button button) {
            comboButton.buttons.add(button);
            return this;
        }
        
        public Builder add(Button button, boolean triggerDefault) {
            comboButton.buttons.add(button);
            if (triggerDefault) {
                comboButton.defaultButton = button;
            }
            return this;
        }
        
        public Builder selectedTheme(String theme) {
            comboButton.selectedTheme = theme;
            return this;
        }
        
        public Builder addThemeVariants(ButtonVariant... buttonVariants) {
            comboButton.buttonVariants = buttonVariants;
            return this;
        }
        
        public Builder setStyle(String name, String value) {
            comboButton.getStyle().set(name, value);
            return this;
        }
        
        public ComboButton build() {
            comboButton.create();
            comboButton.attachListeners();
            return comboButton;
        }
    }
    
    private void create() {
        this.buttons.forEach(btn -> {
            if (ObjectUtils.isNotEmpty(buttonVariants)) {
                btn.addThemeVariants(buttonVariants);
            }
            add(btn);
        });
    }
    
    private void attachListeners() {
        this.buttons.forEach(button -> {
            button.addClickListener(event -> {
                event.getSource().getThemeNames().add(this.selectedTheme);
                this.buttons.stream().filter(btn -> !btn.equals(event.getSource()))
                        .forEach(b -> b.getThemeNames().remove(this.selectedTheme));
            });
        });
    }
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (this.defaultButton != null) {
            ComponentUtil.fireEvent(this.defaultButton, new ClickEvent<Button>(this.defaultButton));
        }
    }
    
}
