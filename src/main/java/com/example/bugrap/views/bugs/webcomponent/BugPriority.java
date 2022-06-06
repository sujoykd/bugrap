package com.example.bugrap.views.bugs.webcomponent;

import org.vaadin.bugrap.domain.entities.Report.Priority;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@JsModule("./component/bug-priority.ts")
@Tag("bug-priority")
public class BugPriority extends HorizontalLayout {
    
    public BugPriority() {
    }
    
    public void update(Priority priority) {
        final int ordinal = priority != null ? priority.ordinal() + 1 : -1;
        this.getElement().setProperty("priority", ordinal);
    }
}
