package com.example.bugrap.views.bugs.viewcomponents;

import java.util.Set;

import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.components.BugReportEdit;
import com.example.bugrap.service.BugrapService;
import com.example.bugrap.views.bugs.events.ReportPostUpdateEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class MultiReportViewer extends VerticalLayout {
    Set<Report> reports;
    BugrapService bugrapService;

    BugReportEdit bugReportEdit;

    public MultiReportViewer(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        this.setWidthFull();
        this.getStyle().set("height", "100vh");
    }

    public void forReports(Set<Report> reports) {
        this.reports = reports;
        this.bugReportEdit = new BugReportEdit(this.reports, this.bugrapService);

        this.add(this.heading());
        this.add(this.bugReportEdit);
    }

    private Component heading() {
        final HorizontalLayout layout = new HorizontalLayout();

        final Span heading = new Span(String.format("%d items selected", this.reports.size()));
        heading.getStyle().set("font-weight", "bold");

        final Span subHeading = new Span("Select a single report to view contents");
        subHeading.getStyle().set("color", "var(--lumo-contrast-50pct)");
        subHeading.getStyle().set("font-weight", "500");

        layout.add(heading, subHeading);
        return layout;
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.removeAll();
    }

    public Registration addReportPostUpdateEventListener(ComponentEventListener<ReportPostUpdateEvent> listener) {
        return this.bugReportEdit.addReportPostUpdateEventListener(listener);
    }
}
