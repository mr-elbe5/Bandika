package de.elbe5.issue;

import de.elbe5.data.BaseData;

public class IssueWorkflowStep extends BaseData {

    protected IssueState state = IssueState.open;

    public IssueState getState() {
        return state;
    }

    public void setState(IssueState state) {
        this.state = state;
    }

}
