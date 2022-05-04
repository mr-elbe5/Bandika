package de.elbe5.serverpagetags;

import de.elbe5.serverpage.SPTag;

public class SPFieldTag extends SPTag {

    protected String name = "";
    protected String placeholder = "";

    @Override
    public void collectParameters() {
        name = getParameters().getString("name", "");
        placeholder = getParameters().getString("placeholder", "");
    }
}
