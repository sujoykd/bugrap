package com.example.bugrap.views.bugs.viewcomponents;

import java.util.Optional;

import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class FullViewHeader extends HorizontalLayout {
    Report report;

    public FullViewHeader(Report report) {
        this.report = report;
        this.setWidthFull();
        this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        this.add(this.projectName(), this.projectVersion());
        this.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
    }

    private Component projectName() {
        final Div projectName = new Div(new Span(this.report.getProject().getName()));
        projectName.addClassName("ui-full-project-name");
        final Div arrow = new Div();
        arrow.addClassName("ui-full-arrow");

        final FlexLayout layout = new FlexLayout();
        layout.add(projectName);
        layout.add(arrow);
        return layout;
    }

    private Component projectVersion() {
        final FlexLayout layout = new FlexLayout();
        layout.add(new Span(Optional.ofNullable(this.report.getVersion()).map(ProjectVersion::getVersion).orElse("")));
        return layout;
    }

}
