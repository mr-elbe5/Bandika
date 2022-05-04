package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import java.util.List;

public class SPTimerListTag extends SPTag {

    public static final String TYPE = "timerlist";

    public SPTimerListTag() {
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {

    }

}
