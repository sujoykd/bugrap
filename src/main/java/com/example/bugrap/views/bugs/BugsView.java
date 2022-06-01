package com.example.bugrap.views.bugs;

import javax.annotation.security.PermitAll;

import org.vaadin.bugrap.domain.entities.Project;

import com.example.bugrap.security.SecurityService;
import com.example.bugrap.service.ProjectService;
import com.example.bugrap.views.MainLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Bugrap | Reports")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class BugsView extends VerticalLayout {
    SecurityService securityService;
    ProjectService projectService;
    
    Project selectedProject;
    MenuItem projectMenuItem;
    
    public BugsView(SecurityService securityService, ProjectService projectService, BugsReportHeader bugsReportHeader,
            BugsReportBody bugsReportBody) {
        this.securityService = securityService;
        this.projectService = projectService;
        setPadding(false);
        add(bugsReportHeader);
        add(bugsReportBody);
    }
    
}
