package com.example.bugrap.views.bugs;

import javax.annotation.security.PermitAll;

import com.example.bugrap.views.bugs.viewcomponents.BugSplitter;
import com.example.bugrap.views.bugs.viewcomponents.BugsReportHeader;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Bugrap | Main")
@Route(value = "")
@PermitAll
public class BugsView extends VerticalLayout {

    public BugsView(BugsReportHeader bugsReportHeader, BugSplitter bugSplitter) {
        this.setSpacing(false);
        this.setPadding(false);
        this.add(bugsReportHeader);
        this.add(bugSplitter);
        this.setSizeFull();
    }

}
