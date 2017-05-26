/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

import de.bandika.base.log.Log;
import de.bandika.cms.configuration.Configuration;
import de.bandika.cms.timer.TimerBean;
import de.bandika.cms.timer.TimerTask;

import java.util.Date;
import java.util.Locale;

public class SearchIndexTask implements TimerTask {

    public boolean execute(Date executionTime, Date checkTime) {
        Log.log("indexing content for search at " + Configuration.getInstance().getDateTimeFormat(Locale.ENGLISH).format(TimerBean.getInstance().getServerTime()));
        SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_CONTENT, 0, null));
        SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_USERS, 0, null));
        return true;
    }

}