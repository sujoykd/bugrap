package com.example.bugrap.views.bugs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class SingleReportViewer extends VerticalLayout {
    
    public SingleReportViewer() {
        this.add(new Text("Show me one report..."));
    }
    
}
