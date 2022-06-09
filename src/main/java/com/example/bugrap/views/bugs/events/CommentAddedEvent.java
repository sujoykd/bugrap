package com.example.bugrap.views.bugs.events;

import com.example.bugrap.views.bugs.viewcomponents.FullViewCommentEditor;
import com.vaadin.flow.component.ComponentEvent;

public class CommentAddedEvent extends ComponentEvent<FullViewCommentEditor> {

    public CommentAddedEvent(FullViewCommentEditor source) {
        super(source, false);
    }

}
