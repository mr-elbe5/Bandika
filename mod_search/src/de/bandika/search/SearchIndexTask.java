package de.bandika.search;

import de.bandika.application.AppConfiguration;
import de.bandika.data.Log;
import de.bandika.timer.TimerBean;
import de.bandika.timer.TimerTask;

import java.util.Date;

public class SearchIndexTask implements TimerTask {

    public boolean execute(Date executionTime, Date checkTime) {
        Log.info("indexing content for search at " + AppConfiguration.getInstance().getDateTimeFormat(AppConfiguration.getInstance().getStdLocale()).format(TimerBean.getInstance().getServerTime()));
        SearchQueue.getInstance().addAction(new SearchActionData(SearchActionData.ACTION_INDEX_ALL, 0, null));
        return true;
    }

}