package com.example.bugrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(scanBasePackages = { "org.vaadin.bugrap.domain.spring", "com.example.bugrap" })
@Theme(value = "bugrap")
@PWA(name = "Bugrap", shortName = "Bugrap", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Bugrap extends SpringBootServletInitializer implements AppShellConfigurator {
    
    public static void main(String[] args) {
        SpringApplication.run(Bugrap.class, args);
    }
    
}
