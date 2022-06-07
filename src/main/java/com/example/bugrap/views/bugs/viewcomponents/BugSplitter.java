package com.example.bugrap.views.bugs.viewcomponents;

import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.views.bugs.events.ReportSelectionEvent;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class BugSplitter extends SplitLayout {
    BugsReportBody bugsReportBody;
    ReportViewer reportViewer;

    public BugSplitter(BugsReportBody bugsReportBody, ReportViewer reportViewer) {
        this.bugsReportBody = bugsReportBody;
        this.reportViewer = reportViewer;
        this.setSizeFull();
        this.addToPrimary(bugsReportBody);
        this.addToSecondary(reportViewer);
        this.setOrientation(Orientation.VERTICAL);
        this.addEventListeners();
    }

    private void addEventListeners() {
        this.bugsReportBody.addListener(ReportSelectionEvent.class, event -> {
            final Set<Report> reports = event.getReports();
            if (ObjectUtils.isNotEmpty(reports)) {
                if (reports.size() > 1) {
                    this.setSplitterPosition(85d);
                } else if (reports.size() == 1) {
                    this.setSplitterPosition(70d);
                }
            } else {
                this.setSplitterPosition(100d);
            }
        });
    }

}
