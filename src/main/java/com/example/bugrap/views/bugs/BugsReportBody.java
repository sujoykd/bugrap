package com.example.bugrap.views.bugs;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import com.example.bugrap.security.SecurityService;
import com.example.bugrap.service.ProjectService;
import com.example.bugrap.views.BugButton;
import com.example.bugrap.views.bugs.events.ProjectSelectionEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class BugsReportBody extends VerticalLayout {
    SecurityService securityService;
    ProjectService projectService;
    
    BugsReportHeader bugsReportHeader;
    Select<ProjectVersion> versionSelector;
    
    public BugsReportBody(SecurityService securityService, ProjectService projectService, BugsReportHeader bugsReportHeader) {
        this.securityService = securityService;
        this.projectService = projectService;
        this.bugsReportHeader = bugsReportHeader;
        
        add(topSection());
        add(tableSection());
        setPadding(false);
        getStyle().set("padding-left", "var(--lumo-space-m)");
        getStyle().set("padding-right", "var(--lumo-space-m)");
        setupListeners();
    }
    
    private void setupListeners() {
        bugsReportHeader.addListener(ProjectSelectionEvent.class, event -> {
            Project selectedProject = event.getProject();
            List<ProjectVersion> versions = projectService.versions(selectedProject);
            if (ObjectUtils.isNotEmpty(selectedProject)) {
                versionSelector.setItems(versions);
                versionSelector.setItemLabelGenerator(ProjectVersion::toString);
                versionSelector.setValue(versions.get(0));
            } else {
                versionSelector.clear();
                versionSelector.setItems(Collections.emptyList());
            }
        });
    }
    
    private Component tableSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        
        HorizontalLayout versionRow = new HorizontalLayout();
        versionRow.setWidthFull();
        versionRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        
        versionSelector = new Select<>();
        
        versionRow.add(new Text("Reports for"));
        versionRow.add(versionSelector);
        versionRow.addAndExpand(new Div(new Text("Placeholder for distribution chart")));
        
        layout.add(versionRow);
        return layout;
    }
    
    private Component topSection() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addAndExpand(topButtons());
        
        TextField textField = new TextField();
        textField.getElement().setAttribute("aria-label", "search");
        textField.setPlaceholder("Search...");
        textField.setClearButtonVisible(true);
        textField.setPrefixComponent(VaadinIcon.SEARCH.create());
        layout.add(textField);
        
        return layout;
    }
    
    private Component topButtons() {
        HorizontalLayout layout = new HorizontalLayout();
        
        Button reportBugBtn = new BugButton("Report a bug", VaadinIcon.BUG.create());
        
        Button requestFeatureBtn = new BugButton("Request a feature", VaadinIcon.LIGHTBULB.create());
        
        Span projectCountBadge = new Span(String.valueOf(projectService.projectCount()));
        projectCountBadge.getElement().getThemeList().add("badge success small pill");
        
        HorizontalLayout manageProject = new HorizontalLayout();
        manageProject.add(VaadinIcon.COG.create());
        manageProject.add("Manage Project");
        manageProject.add(projectCountBadge);
        
        Button manageProjectBtn = new BugButton(manageProject);
        
        layout.add(reportBugBtn, requestFeatureBtn, manageProjectBtn, manageProjectBtn);
        return layout;
    }
    
}
