package de.elbe5.test;

import de.elbe5.data.BaseData;
import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;

@JsonClass
public class TestData extends BaseData {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonField(baseClass = String.class)
    protected String title = "";

    @JsonField(baseClass = TestData.class)
    public TestData subData = null;

}
