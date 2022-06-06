package com.example.bugrap.views.bugs;

import javax.annotation.security.PermitAll;

import org.vaadin.bugrap.domain.entities.Project;

import com.example.bugrap.views.MainLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Bugrap | Reports")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class BugsView extends VerticalLayout {

    Project selectedProject;
    MenuItem projectMenuItem;

    public BugsView(BugsReportHeader bugsReportHeader, BugSplitter bugSplitter) {
        this.setSpacing(false);
        this.setPadding(false);
        this.add(bugsReportHeader);
        this.add(bugSplitter);
        this.setSizeFull();
    }

}
