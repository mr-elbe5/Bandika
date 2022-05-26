package de.elbe5.administration.response;

import de.elbe5.base.Log;
import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;

public class AdminPage {

    protected StringBuilder sb = null;

    void setSb(StringBuilder sb) {
        this.sb = sb;
    }

    public void appendHtml(RequestData rdata){
    }

    public void append(String s){
        if (sb==null){
            Log.warn("StringBuilder is missing");
        }
        sb.append(s);
    }

    public void append(String s, String... params){
        if (sb==null){
            Log.warn("StringBuilder is missing");
        }
        sb.append(Strings.format(s, params));
    }

}
