package de.elbe5.issue;

import de.elbe5.data.BaseData;
import de.elbe5.data.AJsonClass;
import de.elbe5.data.AJsonField;

@AJsonClass
public class IssueWorkflowStep extends BaseData {

    @AJsonField(baseClass = IssueState.class)
    protected IssueState state = IssueState.open;

    public IssueState getState() {
        return state;
    }

    public void setState(IssueState state) {
        this.state = state;
    }

}
