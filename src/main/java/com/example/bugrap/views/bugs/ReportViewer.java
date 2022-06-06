package com.example.bugrap.views.bugs;

import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.views.bugs.events.ReportSelectionEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class ReportViewer extends VerticalLayout {
    BugsReportBody bugsReportBody;
    SingleReportViewer singleReportViewer;
    MultiReportViewer multiReportViewer;

    boolean multimode;

    public ReportViewer(BugsReportBody bugsReportBody, SingleReportViewer singleReportViewer, MultiReportViewer multiReportViewer) {
        this.bugsReportBody = bugsReportBody;
        this.singleReportViewer = singleReportViewer;
        this.multiReportViewer = multiReportViewer;

        this.multimode = false;

        this.setSpacing(false);
        this.setPadding(false);
        this.addEventListeners();
    }

    private void addEventListeners() {
        this.bugsReportBody.addListener(ReportSelectionEvent.class, event -> {
            final Set<Report> reports = event.getReports();
            if (ObjectUtils.isNotEmpty(reports)) {
                this.removeAll();

                if (reports.size() > 1 && !this.multimode) {
                    this.setupMultiReportView(reports);
                } else if (reports.size() == 1) {
                    this.setupSingleReportView(reports.iterator().next());
                }
            } else {
                this.multimode = false;
                this.removeAll();
            }
        });
    }

    private void setupMultiReportView(Set<Report> reports) {
        this.multimode = true;
        this.add(this.multiReportViewer);
    }

    private void setupSingleReportView(Report report) {
        this.multimode = false;
        this.add(this.singleReportViewer);
        this.singleReportViewer.forReport(this.bugsReportBody.selectedProject, report);
    }

}
