package com.example.bugrap.views.bugs;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.example.bugrap.components.BugButton;
import com.example.bugrap.components.BugPriorityDisplay;
import com.example.bugrap.service.BugrapService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class SingleReportViewer extends VerticalLayout {
    Project project;
    Report report;
    BugrapService bugrapService;

    public SingleReportViewer(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.setSpacing(false);
        this.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        this.setSizeFull();
    }

    public void forReport(Project project, Report report) {
        this.project = project;
        this.report = report;

        this.add(this.heading());
        this.add(this.inputRow());
        this.add(this.comments());
    }

    private Component comments() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        final List<Comment> allComments = this.bugrapService.allComments(this.report);
        allComments.forEach(comment -> layout.add(this.commentComponent(comment)));

        return layout;
    }

    private Component commentComponent(Comment comment) {
        final FlexLayout flex = new FlexLayout();

        final Paragraph text = new Paragraph(comment.getComment());
        flex.add(text);

        return flex;
    }

    private Component heading() {
        final FlexLayout layout = new FlexLayout();

        final Span heading = new Span(this.report.getSummary());
        heading.getStyle().set("font-weight", "bold");
        final Button open = new Button("Open", VaadinIcon.EXTERNAL_LINK.create());
        open.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        layout.add(heading, open);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setWidthFull();
        return layout;
    }

    private Component inputRow() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.BASELINE);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.add(this.changeDropdowns());
        layout.add(this.formButtons());
        return layout;
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
        prioritySelect.setValue(this.report.getPriority());
        prioritySelect.addValueChangeListener(event -> System.out.println(event.getValue()));

        final Select<Type> typeSelect = new Select<>();
        typeSelect.setLabel("Type");
        typeSelect.setItems(this.bugrapService.allReportTypes());
        typeSelect.setItemLabelGenerator(type -> StringUtils.capitalize(type.toString().toLowerCase()));
        typeSelect.setValue(this.report.getType());

        final Select<Status> statusSelect = new Select<>();
        statusSelect.setLabel("Status");
        statusSelect.setItems(this.bugrapService.allStatuses());
        statusSelect.setItemLabelGenerator(Status::toString);
        statusSelect.setValue(this.report.getStatus());

        final Select<Reporter> reporterSelect = new Select<>();
        reporterSelect.setLabel("Assigned to");
        reporterSelect.setItems(this.bugrapService.allReporters());
        reporterSelect.setItemLabelGenerator(Reporter::getName);
        reporterSelect.setValue(this.report.getAssigned());

        final Select<ProjectVersion> versionSelect = new Select<>();
        versionSelect.setLabel("Version");
        versionSelect.setItems(this.bugrapService.projectVersions(this.project));
        versionSelect.setItemLabelGenerator(ProjectVersion::getVersion);
        versionSelect.setValue(this.report.getVersion());

        dropdowns.add(prioritySelect, typeSelect, statusSelect, reporterSelect, versionSelect);

        return dropdowns;
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.removeAll();
    }

}
