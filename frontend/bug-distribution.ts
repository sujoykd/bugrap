import { css, html, LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('bug-distribution')
export class BugDistribution extends LitElement {

    @property({ attribute: true, type: Number })
    closedBugs: number = 0;
    @property({ attribute: true, type: Number })
    assignedUnresolvedBugs: number = 0;
    @property({ attribute: true, type: Number })
    unassignedBugs: number = 0;


    static get styles() {
        return css`
            span {
                display: flex;
                color: white;
                min-width: 30px;
                width: 100%;
                padding-left: 5px;
            }

            .ui-closed {
                background-color: var(--lumo-primary-color);
                flex-shrink: 1;
                flex-basis: 10px
            }

            .ui-unresolved {
                background-color: var(--lumo-secondary-color);
                flex-shrink: 1;
                flex-basis: 10px
            }

            .ui-unassigned {
                background-color: var(--lumo-tertiary-color);
                flex-shrink: 1;
                flex-basis: 10px
            }
        `;
    }


    render() {
        return html`
            <span class="ui-closed" style="flex-grow: ${this.closedBugs}">${this.closedBugs}</span>
            <span class="ui-unresolved" style="flex-grow: ${this.assignedUnresolvedBugs}">${this.assignedUnresolvedBugs}</span>
            <span class="ui-unassigned" style="flex-grow: ${this.unassignedBugs}">${this.unassignedBugs}</span>
	    `;
    }
}