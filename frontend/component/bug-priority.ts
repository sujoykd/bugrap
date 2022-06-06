import { css, html, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('bug-priority')
export class BugPriority extends LitElement {

    @property({ attribute: true, type: Number })
    priority: number = 0;

    static get styles() {
        return css`
            .ui-priority {
                background-color: var(--lumo-secondary-color);
                border-radius: 5px;
                margin-right: 2px;
                display: inline-block;
                width: 5px;
            }
        `;
    }


    render() {
        return html`
            ${Array(this.priority).fill('').map(_ => html`<span class="ui-priority">&nbsp;</span>`)}
	    `;
    }
}