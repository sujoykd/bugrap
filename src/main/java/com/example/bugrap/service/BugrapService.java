package com.example.bugrap.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Report.Priority;
import org.vaadin.bugrap.domain.entities.Report.Status;
import org.vaadin.bugrap.domain.entities.Report.Type;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.domain.spring.CommentRepository;
import org.vaadin.bugrap.domain.spring.ProjectRepository;
import org.vaadin.bugrap.domain.spring.ProjectVersionRepository;
import org.vaadin.bugrap.domain.spring.ReportRepository;
import org.vaadin.bugrap.domain.spring.ReporterRepository;

import com.example.bugrap.data.dto.BugDistributionData;
import com.example.bugrap.data.dto.ReportData;
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
    CommentRepository commentRepository;

    public BugrapService(SecurityService securityService, ProjectRepository projectRepository, ReporterRepository reporterRepository,
            ProjectVersionRepository projectVersionRepository, ReportRepository reportRepository, CommentRepository commentRepository) {
        this.securityService = securityService;
        this.projectRepository = projectRepository;
        this.reporterRepository = reporterRepository;
        this.projectVersionRepository = projectVersionRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
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

    public List<ProjectVersion> projectVersions(Project project) {
        return this.projectVersionRepository.findAllByProject(project);
    }

    public List<Reporter> allReporters() {
        return this.reporterRepository.findAll();
    }

    public List<Status> allStatuses() {
        return Arrays.asList(Report.Status.values());
    }

    public List<Type> allReportTypes() {
        return Arrays.asList(Report.Type.values());
    }

    public List<Priority> allPriorities() {
        return Stream.of(Report.Priority.values()).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
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
            report.setAssigned(this.loggedInReporter());
        }

        return this.reportRepository
                .findAll(
                        Example.of(report,
                                ExampleMatcher.matchingAll()
                                        .withIgnorePaths("id", "consistencyVersion")
                                        .withMatcher("summary", GenericPropertyMatchers.contains().ignoreCase())),
                        PageRequest.of(
                                query.getPage(),
                                query.getPageSize()))
                .stream().filter(rpt -> ObjectUtils.isEmpty(selectedStatusSet) || selectedStatusSet.contains(rpt.getStatus()))
                .sorted(query.getSortingComparator().orElseGet(this::defaultReportSorting));
    }

    private Comparator<Report> defaultReportSorting() {
        return Comparator.comparing(
                (Report report) -> Optional.ofNullable(report.getVersion()).map(ProjectVersion::getVersion).orElse(""),
                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(Comparator.comparing((Report report) -> Optional.ofNullable(report.getPriority()).map(Priority::ordinal).orElse(-1)).reversed());
    }

    private Reporter loggedInReporter() {
        final String username = this.securityService.get().map(UserDetails::getUsername).orElse(null);
        return this.reporterRepository.getByNameOrEmail(username, null);
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

    public List<Comment> allComments(Report report) {
        return this.commentRepository.findAllByReportOrderByTimestampDesc(report);
    }

    public void saveReport(ReportData reportData, Report report) {
        final Report updatedReport = this.updateReport(report, reportData);
        this.reportRepository.save(updatedReport);
    }

    public void saveReports(ReportData reportData, Set<Report> reports) {
        final Set<Report> updatedReports = reports.stream().map(report -> this.updateReport(report, reportData)).collect(Collectors.toSet());
        this.reportRepository.saveAll(updatedReports);
    }

    private Report updateReport(Report report, ReportData reportData) {
        report.setPriority(reportData.getPriority());
        report.setType(reportData.getType());
        report.setStatus(reportData.getStatus());
        report.setAssigned(reportData.getAssignedTo());
        report.setVersion(reportData.getVersion());
        return report;
    }

    public void saveComment(Report report, byte[] uploadedAttachment, String uploadedAttachmentName, String commentText) {
        final Comment comment = new Comment();
        comment.setComment(commentText);
        comment.setTimestamp(new Date());
        comment.setReport(report);
        comment.setType(commentText != null ? Comment.Type.COMMENT : Comment.Type.ATTACHMENT);
        comment.setAttachmentName(uploadedAttachmentName);
        comment.setAttachment(uploadedAttachment);
        comment.setAuthor(this.loggedInReporter());

        this.commentRepository.save(comment);
    }

}
