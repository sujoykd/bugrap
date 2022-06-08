package com.example.bugrap.data.dto;

import java.util.Optional;
import java.util.Set;

import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;

public class ReportData {
    final Project project;
    Priority priority;
    Type type;
    Status status;
    Reporter assignedTo;
    ProjectVersion version;

    public ReportData(Report report) {
        this.project = report.getProject();
        this.priority = report.getPriority();
        this.type = report.getType();
        this.status = report.getStatus();
        this.assignedTo = report.getAssigned();
        this.version = report.getVersion();
    }

    public ReportData(Set<Report> reports) {
        this.project = reports.stream().map(Report::getProject).distinct().findFirst().get();
        this.priority = reports.stream().map(Report::getPriority).map(Optional::ofNullable)
                .reduce((x, y) -> x.equals(y) ? x : Optional.empty())
                .get().orElse(null);
        this.type = reports.stream().map(Report::getType).map(Optional::ofNullable)
                .reduce((x, y) -> x.equals(y) ? x : Optional.empty())
                .get().orElse(null);
        this.status = reports.stream().map(Report::getStatus).map(Optional::ofNullable)
                .reduce((x, y) -> x.equals(y) ? x : Optional.empty())
                .get().orElse(null);
        this.assignedTo = reports.stream().map(Report::getAssigned).map(Optional::ofNullable)
                .reduce((x, y) -> x.equals(y) ? x : Optional.empty())
                .get().orElse(null);
        this.version = reports.stream().map(Report::getVersion).map(Optional::ofNullable)
                .reduce((x, y) -> x.equals(y) ? x : Optional.empty())
                .get().orElse(null);
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Reporter getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(Reporter assignedTo) {
        this.assignedTo = assignedTo;
    }

    public ProjectVersion getVersion() {
        return this.version;
    }

    public void setVersion(ProjectVersion version) {
        this.version = version;
    }

    public Project getProject() {
        return this.project;
    }

}
