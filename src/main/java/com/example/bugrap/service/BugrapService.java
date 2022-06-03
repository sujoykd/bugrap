package com.example.bugrap.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.domain.spring.ProjectRepository;
import org.vaadin.bugrap.domain.spring.ProjectVersionRepository;
import org.vaadin.bugrap.domain.spring.ReportRepository;
import org.vaadin.bugrap.domain.spring.ReporterRepository;

import com.example.bugrap.data.dto.BugDistributionData;
import com.example.bugrap.security.SecurityService;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class BugrapService {
    public static final ProjectVersion ALL_VERSIONS = new ProjectVersion();
    {
        ALL_VERSIONS.setVersion("All Versions");
    }

    ProjectRepository projectRepository;
    ProjectVersionRepository projectVersionRepository;
    ReportRepository reportRepository;
    SecurityService securityService;
    ReporterRepository reporterRepository;

    public BugrapService(SecurityService securityService, ProjectRepository projectRepository, ReporterRepository reporterRepository,
            ProjectVersionRepository projectVersionRepository, ReportRepository reportRepository) {
        this.securityService = securityService;
        this.projectRepository = projectRepository;
        this.reporterRepository = reporterRepository;
        this.projectVersionRepository = projectVersionRepository;
        this.reportRepository = reportRepository;
    }

    public List<Project> allProjects() {
        return this.projectRepository.findAll();
    }

    public long projectCount() {
        return this.projectRepository.count();
    }

    public List<ProjectVersion> versions(Project project) {
        final List<ProjectVersion> projectVersions = this.projectVersionRepository.findAllByProject(project);
        if (ObjectUtils.isNotEmpty(projectVersions) && projectVersions.size() > 1) {
            projectVersions.add(0, ALL_VERSIONS);
        }
        return projectVersions;
    }

    public Stream<Report> reportsFor(Project project, ProjectVersion projectVersion, boolean reportsForSelf, Set<Status> selectedStatusSet,
            String summary, Query<Report, ?> query) {
        final Report report = new Report();
        report.setProject(project);
        report.setSummary(summary);
        if (projectVersion != ALL_VERSIONS) {
            report.setVersion(projectVersion);
        }
        if (reportsForSelf) {
            final String username = this.securityService.get().map(UserDetails::getUsername).orElse(null);
            final Reporter reporter = this.reporterRepository.getByNameOrEmail(username, null);
            report.setAssigned(reporter);
        }

        return this.reportRepository
                .findAll(
                        Example.of(report,
                                ExampleMatcher.matchingAll()
                                        .withIgnorePaths("id", "consistencyVersion")
                                        .withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase())),
                        PageRequest.of(
                                query.getPage(),
                                query.getPageSize(),
                                Sort.by("version").ascending()
                                        .and(Sort.by("priority").descending())))
                .stream().filter(rpt -> ObjectUtils.isEmpty(selectedStatusSet) || selectedStatusSet.contains(rpt.getStatus()));
    }

    public BugDistributionData fetchBugDistributionData(Project project, ProjectVersion version) {
        final Report report = new Report();
        report.setProject(project);
        if (version != ALL_VERSIONS) {
            report.setVersion(version);
        }

        final List<Report> reports = this.reportRepository.findAll(
                Example.of(report,
                        ExampleMatcher.matchingAll()
                                .withIgnorePaths("id", "consistencyVersion")
                                .withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase())));

        final long closed = reports.stream().filter(rpt -> rpt.getStatus() != Status.OPEN).count();
        final long assignedUnresolved = reports.stream().filter(rpt -> rpt.getAssigned() != null && rpt.getStatus() == Status.OPEN).count();
        final long unassigned = reports.stream().filter(rpt -> rpt.getAssigned() == null && rpt.getStatus() == Status.OPEN).count();

        return new BugDistributionData(closed, assignedUnresolved, unassigned);
    }

}
