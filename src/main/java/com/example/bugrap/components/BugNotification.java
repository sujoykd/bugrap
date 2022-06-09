package com.example.bugrap.components;

import java.util.function.Supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class BugNotification extends Notification {

    public BugNotification() {
        this.init();
    }

    public BugNotification(Component... components) {
        super(components);
        this.init();
    }

    public BugNotification(Supplier<Component> componentCreator) {
        super(componentCreator.get());
        this.init();
    }

    public BugNotification(String text) {
        super(text);
        this.init();
    }

    private void init() {
        this.setDuration(2000);
        this.setPosition(Position.TOP_END);
    }

    public BugNotification withThemeVariants(NotificationVariant... variants) {
        this.addThemeVariants(variants);
        return this;
    }

}
