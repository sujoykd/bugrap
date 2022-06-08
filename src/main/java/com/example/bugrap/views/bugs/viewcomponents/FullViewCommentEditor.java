package com.example.bugrap.views.bugs.viewcomponents;

import java.io.InputStream;

import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.components.BugButton;
import com.example.bugrap.service.BugrapService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class FullViewCommentEditor extends VerticalLayout {
    Report report;
    BugrapService bugrapService;
    
    public FullViewCommentEditor(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.getStyle().set("background-color", "var(--lumo-contrast-10pct)");
        this.getStyle().set("padding", "var(--lumo-space-l)");
        this.setWidthFull();
    }
    
    public void forReport(Report report) {
        this.report = report;
        
        this.add(this.editorSection());
        this.add(this.buttons());
    }
    
    private Component editorSection() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        
        final RichTextEditor richTextEditor = new RichTextEditor();
        richTextEditor.setMaxHeight("400px");
        richTextEditor.getStyle().set("flex-grow", "5");
        
        final MemoryBuffer memoryBuffer = new MemoryBuffer();
        final Upload singleFileUpload = new Upload(memoryBuffer);
        
        singleFileUpload.addSucceededListener(event -> {
            // Get information about the uploaded file
            final InputStream fileData = memoryBuffer.getInputStream();
            final String fileName = event.getFileName();
            final long contentLength = event.getContentLength();
            final String mimeType = event.getMIMEType();
            
            // Do something with the file data
        });
        
        singleFileUpload.getStyle().set("flex-grow", "1");
        
        layout.add(richTextEditor, singleFileUpload);
        return layout;
    }
    
    private Component buttons() {
        final HorizontalLayout layout = new HorizontalLayout();
        
        final Button commentBtn = new BugButton("Comment", VaadinIcon.CHECK.create()).withTheme(ButtonVariant.LUMO_PRIMARY);
        final Button cancelBtn = new BugButton("Cancel", VaadinIcon.CLOSE_SMALL.create()).lumoBaseColor();
        
        layout.add(commentBtn, cancelBtn);
        
        return layout;
    }
    
}
