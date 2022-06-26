package de.elbe5.issue;

import de.elbe5.data.BaseData;
import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;

@JsonClass
public class IssueWorkflowStep extends BaseData {

    @JsonField(baseClass = IssueState.class)
    protected IssueState state = IssueState.open;

    public IssueState getState() {
        return state;
    }

    public void setState(IssueState state) {
        this.state = state;
    }

}
