package de.elbe5.layout;

import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.response.IMasterInclude;
import de.elbe5.template.TemplateTag;

public class ContentTag extends TemplateTag {

    public static final String TYPE = "content";

    public ContentTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        IMasterInclude masterInclude = rdata.getRequestObject(RequestKeys.KEY_MASTERINCLUDE, IMasterInclude.class);
        if (masterInclude!=null){
            masterInclude.appendHtml(sb, rdata);
        }
    }

}
