package de.bandika.lucene;

import de.bandika.timer.TimerTask;
import de.bandika.timer.TimerBean;
import de.bandika._base.Logger;
import de.bandika.application.Configuration;

import java.util.Date;

public class SearchIndexTask implements TimerTask {

  public boolean execute(Date executionTime, Date checkTime) {
    Logger.info(null, "indexing content for search at " + Configuration.getDateTimeFormat().format(TimerBean.getInstance().getServerTime()));
    SearchQueue.getInstance().addAction(new SearchActionData(SearchActionData.ACTION_INDEX_ALL, 0, null));
    return true;
  }

}