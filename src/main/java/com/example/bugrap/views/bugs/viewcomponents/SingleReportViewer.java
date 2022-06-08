package com.example.bugrap.views.bugs.viewcomponents;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;

import com.example.bugrap.components.BugComment;
import com.example.bugrap.components.BugReportEdit;
import com.example.bugrap.service.BugrapService;
import com.example.bugrap.views.bugs.FullReportView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class SingleReportViewer extends VerticalLayout {
    Report report;
    BugrapService bugrapService;

    public SingleReportViewer(BugrapService bugrapService) {
        this.bugrapService = bugrapService;
        this.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        this.getStyle().set("padding", "var(--lumo-space-l)");
        this.setSizeFull();
    }

    public void forReport(Report report, boolean showOpenButton) {
        this.report = report;

        this.add(this.heading(showOpenButton));
        this.add(new BugReportEdit(this.report, this.bugrapService));
        this.add(this.comments());
    }

    private Component comments() {
        final Scroller scroller = new Scroller(ScrollDirection.VERTICAL);
        scroller.setWidthFull();
        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        final List<Comment> allComments = this.bugrapService.allComments(this.report);
        if (ObjectUtils.isNotEmpty(allComments)) {
            allComments.forEach(comment -> layout.add(new BugComment(comment)));
        } else {
            final Paragraph noComments = new Paragraph("No comments found");
            layout.add(noComments);
        }
        scroller.setContent(layout);
        return scroller;
    }

    private Component heading(boolean showOpenButton) {
        final FlexLayout layout = new FlexLayout();

        final Span heading = new Span(this.report.getSummary());
        heading.getStyle().set("font-weight", "bold");
        layout.add(heading);

        if (showOpenButton) {
            final Button open = new Button("Open", VaadinIcon.EXTERNAL_LINK.create());
            open.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            final RouterLink link = new RouterLink(null, FullReportView.class);
            open.addClickListener(event -> {
                VaadinSession.getCurrent().setAttribute(Report.class, this.report);
                this.getUI().ifPresent(ui -> ui.getPage().open(link.getHref()));
            });
            open.addClickShortcut(Key.ENTER, KeyModifier.CONTROL);
            layout.add(open);
        }

        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        layout.setWidthFull();
        return layout;
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        this.removeAll();
    }

}
