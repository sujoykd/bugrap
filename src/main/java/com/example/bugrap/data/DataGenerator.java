package com.example.bugrap.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.vaadin.bugrap.domain.spring.DBTools;
import org.vaadin.bugrap.domain.spring.ReporterRepository;

import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class DataGenerator {
    
    @Bean
    public CommandLineRunner loadData(DBTools dbTools, ReporterRepository reporterRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (reporterRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            logger.info("Generating demo data");
            dbTools.create();
            logger.info("Generated demo data");
        };
    }
}
