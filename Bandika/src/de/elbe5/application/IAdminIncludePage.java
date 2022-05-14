package de.elbe5.application;

import de.elbe5.request.RequestData;

public interface IAdminIncludePage {

    void appendHtml(StringBuilder sb, RequestData rdata);

}
