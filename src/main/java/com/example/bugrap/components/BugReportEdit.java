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
import com.example.bugrap.views.bugs.events.ReportPostUpdateEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

public class BugReportEdit extends HorizontalLayout {
    Set<Report> reports;
    Report report;
    ReportData reportData;

    BugrapService bugrapService;
    final Binder<ReportData> binder;

    boolean multimode;

    private BugReportEdit(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.setWidthFull();
        this.setAlignItems(Alignment.BASELINE);
        this.setJustifyContentMode(JustifyContentMode.BETWEEN);
        this.binder = new BeanValidationBinder<>(ReportData.class);
    }

    public BugReportEdit(Report report, BugrapService bugrapService) {
        this(bugrapService);
        this.report = report;
        this.reportData = new ReportData(report);
        this.multimode = false;
        this.init();
    }

    public BugReportEdit(Set<Report> reports, BugrapService bugrapService) {
        this(bugrapService);
        this.reports = reports;
        this.reportData = new ReportData(reports);
        this.multimode = true;
        this.init();
    }

    private void init() {
        this.add(this.changeDropdowns());
        this.add(this.formButtons());
    }

    private Component formButtons() {
        final HorizontalLayout buttons = new HorizontalLayout();

        final Button saveChanges = new BugButton("Save changes").withTheme(ButtonVariant.LUMO_PRIMARY);
        saveChanges.addClickListener(event -> {
            if (this.binder.validate().isOk()) {
                this.binder.writeBeanIfValid(this.reportData);
                if (this.multimode) {
                    this.bugrapService.saveReports(this.reportData, this.reports);
                } else {
                    this.bugrapService.saveReport(this.reportData, this.report);
                }
                this.fireEvent(new ReportPostUpdateEvent(this));
                new BugNotification("Saved successfully").withThemeVariants(NotificationVariant.LUMO_SUCCESS).open();
            } else {
                new BugNotification("Validations failed").withThemeVariants(NotificationVariant.LUMO_ERROR).open();
            }
        });
        final ShortcutRegistration shortcut = saveChanges.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
        shortcut.setBrowserDefaultAllowed(false);

        final Button revert = new BugButton("Revert", VaadinIcon.ROTATE_LEFT.create()).lumoBaseColor();
        revert.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        revert.addClickListener(event -> this.binder.readBean(this.reportData));

        buttons.add(saveChanges, revert);

        return buttons;
    }

    private Component changeDropdowns() {
        final HorizontalLayout dropdowns = new HorizontalLayout();

        final Label prioritySelectHelper = new Label();
        prioritySelectHelper.setVisible(false);
        final Select<Priority> prioritySelect = new Select<>();
        prioritySelect.setLabel("Priority");
        prioritySelect.setRenderer(new ComponentRenderer<>(BugPriorityDisplay::new));
        prioritySelect.setItems(this.bugrapService.allPriorities());
        prioritySelect.setHelperComponent(prioritySelectHelper);

        final Label typeSelectHelper = new Label();
        typeSelectHelper.setVisible(false);
        final Select<Type> typeSelect = new Select<>();
        typeSelect.setLabel("Type");
        typeSelect.setItems(this.bugrapService.allReportTypes());
        typeSelect.setItemLabelGenerator(type -> StringUtils.capitalize(type.toString().toLowerCase()));
        typeSelect.setHelperComponent(typeSelectHelper);

        final Select<Status> statusSelect = new Select<>();
        statusSelect.setLabel("Status");
        statusSelect.setItems(this.bugrapService.allStatuses());
        statusSelect.setItemLabelGenerator(Status::toString);

        final Select<Reporter> reporterSelect = new Select<>();
        reporterSelect.setLabel("Assigned to");
        reporterSelect.setItems(this.bugrapService.allReporters());
        reporterSelect.setItemLabelGenerator(Reporter::getName);

        final Select<ProjectVersion> versionSelect = new Select<>();
        versionSelect.setLabel("Version");
        versionSelect.setItems(this.bugrapService.projectVersions(this.reportData.getProject()));
        versionSelect.setItemLabelGenerator(ProjectVersion::getVersion);

        dropdowns.add(prioritySelect, typeSelect, statusSelect, reporterSelect, versionSelect);

        this.binder.forField(prioritySelect)
                .asRequired("Priority is required")
                .withValidationStatusHandler(status -> {
                    prioritySelectHelper.setVisible(status.isError());
                    prioritySelectHelper.setText(status.getMessage().orElse(""));
                    prioritySelect.setInvalid(status.isError());
                    prioritySelectHelper.getStyle().set("color", "var(--lumo-error-color)");
                })
                .bind(ReportData::getPriority, ReportData::setPriority);
        this.binder.forField(typeSelect).asRequired("Type is required")
                .withValidationStatusHandler(status -> {
                    typeSelectHelper.setVisible(status.isError());
                    typeSelectHelper.setText(status.getMessage().orElse(""));
                    typeSelect.setInvalid(status.isError());
                    typeSelectHelper.getStyle().set("color", "var(--lumo-error-color)");
                }).bind(ReportData::getType, ReportData::setType);
        this.binder.bind(statusSelect, ReportData::getStatus, ReportData::setStatus);
        this.binder.bind(reporterSelect, ReportData::getAssignedTo, ReportData::setAssignedTo);
        this.binder.bind(versionSelect, ReportData::getVersion, ReportData::setVersion);

        this.binder.readBean(this.reportData);

        return dropdowns;
    }

    public Registration addReportPostUpdateEventListener(ComponentEventListener<ReportPostUpdateEvent> listener) {
        return this.addListener(ReportPostUpdateEvent.class, listener);
    }

}
