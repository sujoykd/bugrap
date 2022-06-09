package com.example.bugrap.views.bugs.events;

import com.example.bugrap.components.BugReportEdit;
import com.vaadin.flow.component.ComponentEvent;

public class ReportPostUpdateEvent extends ComponentEvent<BugReportEdit> {

    public ReportPostUpdateEvent(BugReportEdit source) {
        super(source, false);
    }

}
