package com.example.bugrap.views.bugs.viewcomponents;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.bugrap.domain.entities.Project;

import com.example.bugrap.security.SecurityService;
import com.example.bugrap.service.BugrapService;
import com.example.bugrap.views.bugs.events.ProjectSelectionEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class BugsReportHeader extends HorizontalLayout {
    SecurityService securityService;
    BugrapService bugrapService;

    Project selectedProject;
    MenuItem projectMenuItem;
    MenuItem dropdownMenu;

    public BugsReportHeader(SecurityService securityService, BugrapService bugrapService) {
        this.securityService = securityService;
        this.bugrapService = bugrapService;

        this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        this.addAndExpand(this.menuBar());
        this.add(this.logOut());
        this.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        this.getStyle().set("padding", "var(--lumo-space-s) var(--lumo-space-l)");
    }

    public Component menuBar() {
        final MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_ICON);

        final List<Project> projects = this.bugrapService.allProjects();
        if (!ObjectUtils.isNotEmpty(projects)) {
            return new H2("No projects found.");
        }
        this.projectMenuItem = menuBar.addItem("");

        this.dropdownMenu = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
        projects.stream().forEach(project -> this.dropdownMenu.getSubMenu().addItem(project.getName(), event -> {
            this.selectedProject = project;
            this.projectMenuItem.setText(this.selectedProject.getName());
            this.fireEvent(new ProjectSelectionEvent(this, project));
        }));
        return menuBar;
    }

    private void selectFirstProjectByDefault() {
        final MenuItem firstProject = this.dropdownMenu.getSubMenu().getItems().get(0);
        ComponentUtil.fireEvent(firstProject, new ClickEvent<MenuItem>(firstProject));
    }

    public Component logOut() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        final Icon userIcon = new Icon(VaadinIcon.USER);
        userIcon.getStyle().set("font-size", "var(--lumo-font-size-xxs)");
        userIcon.getStyle().set("color", "var(--lumo-primary-text-color)");

        final Span userSpan = new Span();
        userSpan.add(this.securityService.get().map(UserDetails::getUsername).orElse(""));

        final Button logoutButton = new Button(new Icon(VaadinIcon.POWER_OFF), e -> this.securityService.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        logoutButton.getElement().setAttribute("aria-label", "Log Out");

        layout.add(userIcon, userSpan, logoutButton);
        return layout;
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return this.getEventBus().addListener(eventType, listener);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.selectFirstProjectByDefault();
    }

}
