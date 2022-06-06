package com.example.bugrap.views.bugs.webcomponent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@JsModule("./component/bug-priority.ts")
@Tag("bug-priority")
public class BugPriority extends Component {

    public BugPriority() {
    }

    public void update(long priority) {
        this.getElement().setProperty("priority", priority);
    }
}
