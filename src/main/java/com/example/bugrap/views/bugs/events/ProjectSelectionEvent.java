package com.example.bugrap.views.bugs.events;

import org.vaadin.bugrap.domain.entities.Project;

import com.example.bugrap.views.bugs.BugsReportHeader;
import com.vaadin.flow.component.ComponentEvent;

public class ProjectSelectionEvent extends ComponentEvent<BugsReportHeader> {
    Project project;
    
    public ProjectSelectionEvent(BugsReportHeader source, Project project) {
        super(source, false);
        this.project = project;
    }
    
    public Project getProject() {
        return project;
    }
    
}
