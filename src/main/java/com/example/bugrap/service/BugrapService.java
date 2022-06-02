package com.example.bugrap.service;

import java.util.List;

import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.spring.ProjectRepository;
import org.vaadin.bugrap.domain.spring.ProjectVersionRepository;

import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class ProjectService {
    ProjectRepository projectRepository;
    ProjectVersionRepository projectVersionRepository;
    
    public ProjectService(ProjectRepository projectRepository, ProjectVersionRepository projectVersionRepository) {
        this.projectRepository = projectRepository;
        this.projectVersionRepository = projectVersionRepository;
    }
    
    public List<Project> allProjects() {
        return this.projectRepository.findAll();
    }
    
    public long projectCount() {
        return this.projectRepository.count();
    }
    
    public List<ProjectVersion> versions(Project project) {
        return this.projectVersionRepository.findAllByProject(project);
    }
    
}
