package de.elbe5.test;

import de.elbe5.data.BaseData;
import de.elbe5.data.AJsonClass;
import de.elbe5.data.AJsonField;

@AJsonClass
public class TestData extends BaseData {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @AJsonField(baseClass = String.class)
    protected String title = "";

    @AJsonField(baseClass = TestData.class)
    public TestData subData = null;

}
