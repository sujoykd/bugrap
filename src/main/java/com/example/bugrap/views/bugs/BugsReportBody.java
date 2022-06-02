package com.example.bugrap.views.bugs;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Reporter;

import com.example.bugrap.components.BugButton;
import com.example.bugrap.components.ComboButton;
import com.example.bugrap.security.SecurityService;
import com.example.bugrap.service.BugrapService;
import com.example.bugrap.views.bugs.events.ProjectSelectionEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class BugsReportBody extends VerticalLayout {
    SecurityService securityService;
    BugrapService bugrapService;

    BugsReportHeader bugsReportHeader;
    Select<ProjectVersion> versionSelector;
    Grid<Report> reportGrid;
    Column<Report> versionColumn;

    Project selectedProject;
    ProjectVersion selectedVersion;
    boolean reportsForSelf;
    Set<Status> selectedStatusSet;
    boolean customStatusMode;

    public BugsReportBody(BugrapService bugrapService, BugsReportHeader bugsReportHeader) {
        this.bugrapService = bugrapService;
        this.bugsReportHeader = bugsReportHeader;

        this.reportsForSelf = true;
        this.selectedStatusSet = Collections.emptySet();
        this.customStatusMode = false;

        this.add(this.topSection());
        this.add(this.tableSection());
        this.setPadding(false);
        this.getStyle().set("padding-left", "var(--lumo-space-m)");
        this.getStyle().set("padding-right", "var(--lumo-space-m)");
        this.setupEventHandlers();
        this.setSizeFull();
    }

    private void setupEventHandlers() {
        this.bugsReportHeader.addListener(ProjectSelectionEvent.class, event -> {
            this.selectedProject = event.getProject();
            final List<ProjectVersion> versions = this.bugrapService.versions(this.selectedProject);
            if (ObjectUtils.isNotEmpty(this.selectedProject)) {
                this.versionSelector.setItems(versions);
                this.versionSelector.setItemLabelGenerator(ProjectVersion::toString);
                this.versionSelector.setValue(versions.get(0));
            } else {
                this.versionSelector.clear();
                this.versionSelector.setItems(Collections.emptyList());
            }
        });
    }

    private Component tableSection() {
        final VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
        layout.setSizeFull();
        layout.add(this.versionRow());
        layout.add(this.filterRow());
        layout.add(this.grid());
        return layout;
    }

    private Component grid() {
        this.reportGrid = new Grid<>(Report.class, false);
        this.reportGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        this.reportGrid.setSizeFull();

        this.versionColumn = this.reportGrid
                .addColumn(report -> Optional.ofNullable(report.getVersion()).map(ProjectVersion::getVersion).orElse(""))
                .setHeader("Version");
        this.reportGrid.addColumn(report -> report.getPriority().ordinal()).setHeader("Priority");
        this.reportGrid.addColumn(Report::getType).setHeader("Type");
        this.reportGrid.addColumn(Report::getSummary).setHeader("Summary");
        this.reportGrid.addColumn(report -> Optional.ofNullable(report.getAssigned()).map(Reporter::getName).orElse(""))
                .setHeader("Assigned to");
        this.reportGrid.addColumn(Report::getTimestamp).setHeader("Last modified");
        this.reportGrid.addColumn(Report::getReportedTimestamp).setHeader("Reported");

        this.reportGrid.addSelectionListener(selection -> {
            System.out.printf("Number of selected items: %s%n", selection.getAllSelectedItems().size());
        });

        return this.reportGrid;
    }

    private Component versionRow() {
        final HorizontalLayout versionRow = new HorizontalLayout();
        versionRow.setWidthFull();
        versionRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        this.versionSelector = new Select<>();
        this.versionSelector.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                this.selectedVersion = event.getValue();
                this.versionColumn.setVisible(this.selectedVersion == BugrapService.ALL_VERSIONS);
                this.updateReportGrid();
            }
        });

        versionRow.add(new Text("Reports for"));
        versionRow.add(this.versionSelector);
        versionRow.addAndExpand(new Div(new Text("Placeholder for distribution chart")));
        return versionRow;
    }

    private Component filterRow() {
        final HorizontalLayout filterRow = new HorizontalLayout();
        filterRow.setWidthFull();
        filterRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        final ComboButton assigneeCombo = new ComboButton.Builder()
                .add(
                        new Button("Only me", event -> {
                            this.reportsForSelf = true;
                            this.updateReportGrid();
                        }), true)
                .add(
                        new Button("Everyone", event -> {
                            this.reportsForSelf = false;
                            this.updateReportGrid();
                        }))
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                .setStyle("box-shadow", "rgb(28 55 90 / 16%) 0px 0px 0px 0px, rgb(28 52 84 / 26%) 0px 0px 2px 1px")
                .setStyle("border-radius", "5px").selectedTheme("selected-button").build();

        final Button customButton = new Button("Custom...", event -> {
            if (!this.customStatusMode) {
                this.customStatusMode = true;
                this.selectedStatusSet = new HashSet<>();
                this.updateReportGrid();
            }
        });
        final ContextMenu menu = new ContextMenu();
        menu.setTarget(customButton);
        menu.setOpenOnClick(true);

        for (final Status status : Report.Status.values()) {
            final MenuItem menuItem = menu.addItem(status.toString(), event -> {
                if (event.getSource().isChecked()) {
                    this.selectedStatusSet.add(status);
                } else {
                    this.selectedStatusSet.remove(status);
                }
                this.updateReportGrid();
            });
            menuItem.setCheckable(true);
        }

        final ComboButton statusCombo = new ComboButton.Builder()
                .add(
                        new Button("Open", event -> {
                            this.customStatusMode = false;
                            this.selectedStatusSet = Collections.singleton(Status.OPEN);
                            this.updateReportGrid();
                        }), true)
                .add(
                        new Button("All kinds", event -> {
                            this.customStatusMode = false;
                            this.selectedStatusSet = Collections.emptySet();
                            this.updateReportGrid();
                        }))
                .add(customButton)
                .addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                .setStyle("box-shadow", "rgb(28 55 90 / 16%) 0px 0px 0px 0px, rgb(28 52 84 / 26%) 0px 0px 2px 1px")
                .setStyle("border-radius", "5px")
                .selectedTheme("selected-button")
                .build();

        filterRow.add(new Label("Assignees"), assigneeCombo, new Label("Status"), statusCombo);
        return filterRow;
    }

    private Component topSection() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.addAndExpand(this.topButtons());

        final TextField textField = new TextField();
        textField.getElement().setAttribute("aria-label", "search");
        textField.setPlaceholder("Search...");
        textField.setClearButtonVisible(true);
        textField.setPrefixComponent(VaadinIcon.SEARCH.create());
        layout.add(textField);

        return layout;
    }

    private Component topButtons() {
        final HorizontalLayout layout = new HorizontalLayout();

        final Button reportBugBtn = new BugButton("Report a bug", VaadinIcon.BUG.create());

        final Button requestFeatureBtn = new BugButton("Request a feature", VaadinIcon.LIGHTBULB.create());

        final Span projectCountBadge = new Span(String.valueOf(this.bugrapService.projectCount()));
        projectCountBadge.getElement().getThemeList().add("badge success small pill");

        final HorizontalLayout manageProject = new HorizontalLayout();
        manageProject.add(VaadinIcon.COG.create());
        manageProject.add("Manage Project");
        manageProject.add(projectCountBadge);

        final Button manageProjectBtn = new BugButton(manageProject);

        layout.add(reportBugBtn, requestFeatureBtn, manageProjectBtn, manageProjectBtn);
        return layout;
    }

    private void updateReportGrid() {
        this.reportGrid.setItems(Collections.emptyList());
        this.reportGrid.setItems(query -> this.bugrapService.reportsFor(this.selectedProject, this.selectedVersion, this.reportsForSelf,
                this.selectedStatusSet, query));
    }
}
