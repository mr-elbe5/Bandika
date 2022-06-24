package de.elbe5.administration.html;

import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;
import de.elbe5.response.*;

public class AdminPage extends HtmlIncludePage implements MessageHtml {

    String title;

    public AdminPage(String title) {
        this.title = title;
    }

    @Override
    public void prepareMaster(RequestData rdata) {
        rdata.getTemplateAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getTemplateAttributes().put("title", toHtml(Configuration.getAppTitle() + " | " + title));
        rdata.getTemplateAttributes().put("reallyDelete", toJs(getString("_reallyDelete")));
        rdata.getTemplateAttributes().put("reallyExecute", toJs(getString("_reallyExecute")));
    }
}
