package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.IMasterInclude;
import de.elbe5.serverpage.SPTag;

public class SPContentTag extends SPTag {

    public static final String TYPE = "content";

    public SPContentTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        IMasterInclude masterInclude = rdata.getRequestObject(RequestKeys.KEY_MASTERINCLUDE, IMasterInclude.class);
        if (masterInclude!=null){
            masterInclude.appendContent(sb, rdata);
        }
    }

}
