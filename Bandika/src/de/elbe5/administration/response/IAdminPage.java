package de.elbe5.administration.response;

import de.elbe5.request.RequestData;

public interface IAdminPage {

    void appendHtml(StringBuilder sb, RequestData rdata);

}
