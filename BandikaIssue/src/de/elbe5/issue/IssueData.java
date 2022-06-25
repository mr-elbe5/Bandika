package de.elbe5.issue;

import de.elbe5.data.BaseData;
import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;

import java.util.ArrayList;
import java.util.List;

@JsonClass
public class IssueData extends BaseData {

    @JsonField
    protected List<IssueWorkflowStep> steps = new ArrayList<>();

    public List<IssueWorkflowStep> getSteps() {
        return steps;
    }

    public IssueState getState(){
        if (steps.isEmpty())
                return IssueState.open;
        return steps.get(steps.size()-1).getState();
    }

}
