package com.example.bugrap.views.bugs;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
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
        
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        addAndExpand(menuBar());
        add(logOut());
        getStyle().set("box-shadow", "var(--lumo-box-shadow-s");
        getStyle().set("padding-left", "var(--lumo-space-m)");
        getStyle().set("padding-right", "var(--lumo-space-m)");
    }
    
    public Component menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_ICON);
        
        List<Project> projects = bugrapService.allProjects();
        if (ObjectUtils.isNotEmpty(projects)) {
            projectMenuItem = menuBar.addItem("");
            
            dropdownMenu = menuBar.addItem(new Icon(VaadinIcon.CHEVRON_DOWN));
            projects.stream().forEach(project -> dropdownMenu.getSubMenu().addItem(project.getName(), event -> {
                this.selectedProject = project;
                projectMenuItem.setText(this.selectedProject.getName());
                fireEvent(new ProjectSelectionEvent(this, project));
            }));
        } else {
            return new H2("No projects found.");
        }
        return menuBar;
    }
    
    private void selectFirstProjectByDefault() {
        MenuItem firstProject = dropdownMenu.getSubMenu().getItems().get(0);
        ComponentUtil.fireEvent(firstProject, new ClickEvent<MenuItem>(firstProject));
    }
    
    public Component logOut() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        
        Icon userIcon = new Icon(VaadinIcon.USER);
        userIcon.getStyle().set("width", "var(--lumo-icon-size-s)");
        userIcon.getStyle().set("height", "var(--lumo-icon-size-s)");
        userIcon.getStyle().set("color", "var(--lumo-primary-text-color)");
        
        Span userSpan = new Span();
        userSpan.add(this.securityService.get().map(user -> user.getUsername()).orElse(""));
        
        Button logoutButton = new Button(new Icon(VaadinIcon.POWER_OFF), e -> securityService.logout());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        logoutButton.getElement().setAttribute("aria-label", "Log Out");
        
        layout.add(userIcon, userSpan, logoutButton);
        return layout;
    }
    
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
    
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        selectFirstProjectByDefault();
    }
    
}
