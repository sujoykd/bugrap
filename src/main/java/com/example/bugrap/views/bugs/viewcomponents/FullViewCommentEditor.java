package com.example.bugrap.views.bugs.viewcomponents;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.components.BugButton;
import com.example.bugrap.service.BugrapService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
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
    private static int MAX_FILE_SIZE_MB = 5;
    private static String[] ACCEPTED_FILE_TYPES = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_PDF_VALUE };

    Report report;
    BugrapService bugrapService;

    RichTextEditor richTextEditor;
    Upload singleFileUpload;

    String uploadedAttachmentName;
    byte[] uploadedAttachment;

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

        this.richTextEditor = new RichTextEditor();
        this.richTextEditor.setMaxHeight("400px");
        this.richTextEditor.getStyle().set("flex-grow", "5");

        layout.add(this.richTextEditor, this.uploadSection());
        return layout;
    }

    private Component uploadSection() {
        final VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("flex-grow", "1");
        layout.getStyle().set("padding-top", "0px");
        layout.setWidth(null);
        layout.setSpacing(false);

        final H5 heading = new H5("Attachments");
        heading.getStyle().set("margin", "0px");
        heading.getStyle().set("color", "var(--lumo-contrast-70pct)");

        final Span filesAllowed = new Span("Only PDF, PNG and JPG files are allowed.");
        filesAllowed.getStyle().set("color", "var(--lumo-contrast-70pct)");

        final Span maxFileSize = new Span(String.format("Max file size is %d MB", MAX_FILE_SIZE_MB));
        maxFileSize.getStyle().set("color", "var(--lumo-contrast-70pct)");

        layout.add(heading, filesAllowed, maxFileSize);

        final MemoryBuffer memoryBuffer = new MemoryBuffer();
        this.singleFileUpload = new Upload(memoryBuffer);
        this.singleFileUpload.setMaxFileSize(Long.valueOf(MAX_FILE_SIZE_MB * FileUtils.ONE_MB).intValue());
        this.singleFileUpload.setAcceptedFileTypes(ACCEPTED_FILE_TYPES);
        this.singleFileUpload.setSizeFull();

        this.singleFileUpload.addFileRejectedListener(event -> {
            System.out.println(event.getErrorMessage());
            Notification.show("rejected", 2000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        this.singleFileUpload.addFailedListener(event -> {
            System.out.println("failed!");
            Notification.show("failed", 2000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        this.singleFileUpload.addSucceededListener(event -> {
            this.uploadedAttachmentName = event.getFileName();
            try {
                this.uploadedAttachment = memoryBuffer.getInputStream().readAllBytes();
            } catch (final IOException ex) {
                Notification.show("Upload failed", 2000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        layout.add(this.singleFileUpload);
        return layout;
    }

    private Component buttons() {
        final HorizontalLayout layout = new HorizontalLayout();

        final Button commentBtn = new BugButton("Comment", VaadinIcon.CHECK.create()).withTheme(ButtonVariant.LUMO_PRIMARY);
        final Button cancelBtn = new BugButton("Cancel", VaadinIcon.CLOSE_SMALL.create()).lumoBaseColor();

        commentBtn.addClickListener(event -> {
            final String commentText = this.richTextEditor.asHtml().getValue();
            if (commentText != null || this.uploadedAttachment != null) {
                this.bugrapService.saveComment(this.report, this.uploadedAttachment, this.uploadedAttachmentName, commentText);
            }
            this.singleFileUpload.clearFileList();
            this.richTextEditor.clear();
        });

        layout.add(commentBtn, cancelBtn);
        return layout;
    }

}
