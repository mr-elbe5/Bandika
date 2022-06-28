package de.elbe5.issue;

import de.elbe5.data.BaseData;
import de.elbe5.data.AJsonClass;
import de.elbe5.data.AJsonField;

import java.util.ArrayList;
import java.util.List;

@AJsonClass
public class IssueData extends BaseData {

    @AJsonField(baseClass = ArrayList.class, valueClass = IssueWorkflowStep.class)
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
