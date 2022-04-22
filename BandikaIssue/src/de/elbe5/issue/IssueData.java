package de.elbe5.issue;

import de.elbe5.base.BaseData;

import java.util.ArrayList;
import java.util.List;

public class IssueData extends BaseData {

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
