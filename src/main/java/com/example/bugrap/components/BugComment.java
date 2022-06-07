package com.example.bugrap.components;

import java.io.ByteArrayInputStream;

import org.springframework.util.StringUtils;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Comment.Type;

import com.example.bugrap.util.GenericUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

public class BugComment extends HorizontalLayout {

    Comment comment;

    public BugComment(Comment comment) {
        this.comment = comment;
        this.addClassName("ui-bug-comment");
        this.setSizeFull();

        final Component commentText = this.commentText();
        final Component attachmentSection = this.attachmentSection();
        this.add(commentText);
        this.add(attachmentSection);
        this.setFlexGrow(6, commentText);
        this.setFlexGrow(1, attachmentSection);
    }

    private Component commentText() {
        final FlexLayout layout = new FlexLayout();
        layout.add(new Span(this.comment.getComment()));
        return layout;
    }

    private Component attachmentSection() {
        final VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("width", "auto");
        layout.setPadding(false);
        layout.add(this.author(), this.attachments());
        return layout;
    }

    private Component attachments() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        if (this.comment.getType() == Type.ATTACHMENT) {
            final Span numberOfAttachments = new Span("1 attachment");

            final StreamResource resource = new StreamResource(this.comment.getAttachmentName(),
                    () -> new ByteArrayInputStream(this.comment.getAttachment()));

            final HorizontalLayout fileLayout = new HorizontalLayout();
            fileLayout.add(VaadinIcon.FILE_O.create());
            fileLayout.add(new Span(this.comment.getAttachmentName()));
            fileLayout.getStyle().set("padding-left", "var(--lumo-space-l)");

            final Anchor attachmentLink = new Anchor();
            attachmentLink.setHref(resource);
            attachmentLink.add(fileLayout);

            attachmentLink.getElement().setAttribute("download", "");
            layout.add(numberOfAttachments, attachmentLink);
        }
        return layout;
    }

    private Component author() {
        final VerticalLayout nameLayout = new VerticalLayout();
        nameLayout.setSpacing(false);
        nameLayout.setPadding(false);

        final Span name = new Span(StringUtils.capitalize(this.comment.getAuthor().getName().toLowerCase()));
        name.getStyle().set("font-weight", "bold");
        final Span timestamp = new Span(GenericUtil.relativeTimeSpan(this.comment.getTimestamp()));
        nameLayout.add(name, timestamp);

        final HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.START);

        layout.add(new Avatar(this.comment.getAuthor().getName().toUpperCase()));
        layout.add(nameLayout);
        return layout;
    }

}
