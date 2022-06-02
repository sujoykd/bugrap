package com.example.bugrap.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
        List<ProjectVersion> projectVersions = this.projectVersionRepository.findAllByProject(project);
        if (ObjectUtils.isNotEmpty(projectVersions) && projectVersions.size() > 1) {
            projectVersions.add(0, ALL_VERSIONS);
        }
        return projectVersions;
    }
    
    public Stream<Report> reportsFor(Project project, ProjectVersion projectVersion, boolean reportsForSelf, Set<Status> selectedStatusSet,
            Query<Report, ?> query) {
        Report report = new Report();
        report.setProject(project);
        if (projectVersion != ALL_VERSIONS) {
            report.setVersion(projectVersion);
        }
        if (reportsForSelf) {
            String username = this.securityService.get().map(UserDetails::getUsername).orElse(null);
            Reporter reporter = reporterRepository.getByNameOrEmail(username, null);
            report.setAssigned(reporter);
        }
        
        return reportRepository.findAll(Example.of(report, ExampleMatcher.matchingAll().withIgnorePaths("id", "consistencyVersion")),
                PageRequest.of(
                        query.getPage(),
                        query.getPageSize(),
                        Sort.by("version").ascending()
                                .and(Sort.by("priority").descending())))
                .stream().filter(rpt -> ObjectUtils.isEmpty(selectedStatusSet) || selectedStatusSet.contains(rpt.getStatus()));
    }
}
