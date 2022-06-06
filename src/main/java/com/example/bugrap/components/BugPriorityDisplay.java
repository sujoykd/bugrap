package com.example.bugrap.components;

import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.bugrap.domain.entities.Report.Priority;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class BugPriorityDisplay extends HorizontalLayout {

    public BugPriorityDisplay(Priority priority) {
        this.setSpacing(false);
        this.setPadding(false);

        final int ordinal = priority != null ? priority.ordinal() + 1 : 0;

        IntStream.rangeClosed(1, ordinal).forEach($ -> {
            final Span span = new Span();
            span.getElement().getClassList().add("ui-bug-priority");
            this.add(span);
        });
        this.getElement().setProperty("title", StringUtils.capitalize(priority.toString().toLowerCase()));
    }

}
