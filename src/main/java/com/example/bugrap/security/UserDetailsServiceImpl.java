package com.example.bugrap.security;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.domain.spring.ReporterRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final ReporterRepository reporterRepository;
    
    @Autowired
    public UserDetailsServiceImpl(ReporterRepository reporterRepository) {
        this.reporterRepository = reporterRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Reporter reporter = reporterRepository.getByNameOrEmail(username, null);
        if (reporter == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(reporter.getName(), reporter.getPassword(),
                    getAuthorities(reporter));
        }
    }
    
    private static List<GrantedAuthority> getAuthorities(Reporter reporter) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + reporter.getName().toUpperCase(Locale.ROOT)));
    }
    
}
