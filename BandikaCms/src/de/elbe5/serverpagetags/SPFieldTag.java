package de.elbe5.serverpagetags;

import de.elbe5.base.StringMap;
import de.elbe5.serverpage.SPTag;

public class SPFieldTag extends SPTag {

    protected String name = "";
    protected String placeholder = "";

    @Override
    public void collectParameters(StringMap parameters) {
        name = parameters.getString("name", "");
        placeholder = parameters.getString("placeholder", "");
    }
}
