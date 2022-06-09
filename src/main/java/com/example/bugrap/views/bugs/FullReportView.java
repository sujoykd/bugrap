package com.example.bugrap.views.bugs;

import javax.annotation.security.PermitAll;

import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.components.BugNotification;
import com.example.bugrap.service.BugrapService;
import com.example.bugrap.views.bugs.viewcomponents.FullViewCommentEditor;
import com.example.bugrap.views.bugs.viewcomponents.FullViewHeader;
import com.example.bugrap.views.bugs.viewcomponents.SingleReportViewer;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.NotificationVariant;
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

    SingleReportViewer singleReportViewer;
    FullViewCommentEditor fullViewCommentEditor;

    public FullReportView(BugrapService bugrapService, SingleReportViewer singleReportViewer, FullViewCommentEditor fullViewCommentEditor) {
        this.bugrapService = bugrapService;
        this.singleReportViewer = singleReportViewer;
        this.fullViewCommentEditor = fullViewCommentEditor;
        this.setPadding(false);
        this.setSpacing(false);
        this.setSizeFull();
        this.setupEventHandlers();
    }

    private void setupEventHandlers() {
        this.fullViewCommentEditor.addCommentAddedEventListener(event -> this.singleReportViewer.updateComments());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.report = VaadinSession.getCurrent().getAttribute(Report.class);
        if (this.report == null) {
            UI.getCurrent().navigate(BugsView.class);
            new BugNotification("Please select a report").withThemeVariants(NotificationVariant.LUMO_ERROR).open();
        } else {
            this.setupUI();
        }
    }

    private void setupUI() {
        this.add(new FullViewHeader(this.report));
        this.addAndExpand(this.singleReportViewer);
        this.singleReportViewer.forReport(this.report, false);

        this.add(this.fullViewCommentEditor);
        this.fullViewCommentEditor.forReport(this.report);
    }

}
