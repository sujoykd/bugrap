package com.example.bugrap.views.bugs;

import javax.annotation.security.PermitAll;

import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.service.BugrapService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Bugrap | Report View")
@Route(value = "report")
@PermitAll
public class FullReportView extends VerticalLayout implements AfterNavigationObserver {
    Report report;
    BugrapService bugrapService;

    public FullReportView(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.report = VaadinSession.getCurrent().getAttribute(Report.class);
        if (this.report == null) {
            UI.getCurrent().navigate(BugsView.class);
            Notification.show("Please select a report", 1000, Position.TOP_CENTER);
        } else {
            this.setupUI();
        }
    }

    private void setupUI() {
        this.add(new Text(this.report.getDescription()));
    }

}
