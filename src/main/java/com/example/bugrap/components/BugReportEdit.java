package com.example.bugrap.components;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.example.bugrap.data.dto.ReportData;
import com.example.bugrap.service.BugrapService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class BugReportEdit extends HorizontalLayout {
    Set<Report> reports;
    Report report;
    ReportData reportData;

    BugrapService bugrapService;

    private BugReportEdit(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.setWidthFull();
        this.setAlignItems(Alignment.BASELINE);
        this.setJustifyContentMode(JustifyContentMode.BETWEEN);
    }

    public BugReportEdit(Report report, BugrapService bugrapService) {
        this(bugrapService);
        this.report = report;
        this.reportData = new ReportData(report);
        this.init();
    }

    public BugReportEdit(Set<Report> reports, BugrapService bugrapService) {
        this(bugrapService);
        this.reports = reports;
        this.reportData = new ReportData(reports);
        this.init();
    }

    private void init() {
        this.add(this.changeDropdowns());
        this.add(this.formButtons());
    }

    private Component formButtons() {
        final HorizontalLayout buttons = new HorizontalLayout();

        final Button saveChanges = new BugButton("Save changes").withTheme(ButtonVariant.LUMO_PRIMARY);

        final Button revert = new BugButton("Revert", VaadinIcon.ROTATE_LEFT.create()).lumoBaseColor();
        revert.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        buttons.add(saveChanges, revert);

        return buttons;
    }

    private Component changeDropdowns() {
        final HorizontalLayout dropdowns = new HorizontalLayout();

        final Select<Priority> prioritySelect = new Select<>();
        prioritySelect.setLabel("Priority");
        prioritySelect.setRenderer(new ComponentRenderer<>(BugPriorityDisplay::new));
        prioritySelect.setItems(this.bugrapService.allPriorities());
        prioritySelect.setValue(this.reportData.getPriority());
        prioritySelect.addValueChangeListener(event -> System.out.println(event.getValue()));

        final Select<Type> typeSelect = new Select<>();
        typeSelect.setLabel("Type");
        typeSelect.setItems(this.bugrapService.allReportTypes());
        typeSelect.setItemLabelGenerator(type -> StringUtils.capitalize(type.toString().toLowerCase()));
        typeSelect.setValue(this.reportData.getType());

        final Select<Status> statusSelect = new Select<>();
        statusSelect.setLabel("Status");
        statusSelect.setItems(this.bugrapService.allStatuses());
        statusSelect.setItemLabelGenerator(Status::toString);
        statusSelect.setValue(this.reportData.getStatus());

        final Select<Reporter> reporterSelect = new Select<>();
        reporterSelect.setLabel("Assigned to");
        reporterSelect.setItems(this.bugrapService.allReporters());
        reporterSelect.setItemLabelGenerator(Reporter::getName);
        reporterSelect.setValue(this.reportData.getAssignedTo());

        final Select<ProjectVersion> versionSelect = new Select<>();
        versionSelect.setLabel("Version");
        versionSelect.setItems(this.bugrapService.projectVersions(this.reportData.getProject()));
        versionSelect.setItemLabelGenerator(ProjectVersion::getVersion);
        versionSelect.setValue(this.reportData.getVersion());

        dropdowns.add(prioritySelect, typeSelect, statusSelect, reporterSelect, versionSelect);

        return dropdowns;
    }
}