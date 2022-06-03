package com.example.bugrap.views.bugs.events;

import java.util.Set;

import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.views.bugs.BugsReportBody;
import com.vaadin.flow.component.ComponentEvent;

public class ReportSelectionEvent extends ComponentEvent<BugsReportBody> {
    final Set<Report> reports;

    public ReportSelectionEvent(BugsReportBody source, Set<Report> reports) {
        super(source, false);
        this.reports = reports;
    }

    public Set<Report> getReports() {
        return this.reports;
    }

}
