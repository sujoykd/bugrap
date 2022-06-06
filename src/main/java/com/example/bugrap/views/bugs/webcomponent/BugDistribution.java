package com.example.bugrap.views.bugs.webcomponent;

import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import com.example.bugrap.data.dto.BugDistributionData;
import com.example.bugrap.service.BugrapService;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@JsModule("./component/bug-distribution.ts")
@Tag("bug-distribution")
@SpringComponent
@UIScope
public class BugDistribution extends FlexLayout {
    
    BugrapService bugrapService;
    
    public BugDistribution(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.setJustifyContentMode(JustifyContentMode.START);
    }
    
    public void updateBar(Project project, ProjectVersion version) {
        final BugDistributionData data = this.bugrapService.fetchBugDistributionData(project, version);
        
        this.getElement().setProperty("closedBugs", data.getClosed());
        this.getElement().setProperty("assignedUnresolvedBugs", data.getAssignedUnresolved());
        this.getElement().setProperty("unassignedBugs", data.getUnassigned());
        
    }
}
