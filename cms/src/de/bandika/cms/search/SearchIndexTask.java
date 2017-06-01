/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

import de.bandika.base.data.Locales;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.timer.TimerBean;
import de.bandika.cms.timer.TimerTask;

import java.time.LocalDateTime;

public class SearchIndexTask extends TimerTask {

    @Override
    public String getName() {
        return "searchindex";
    }

    public boolean execute(LocalDateTime executionTime, LocalDateTime checkTime) {
        Log.log("indexing content for search at " + StringUtil.toHtmlDateTime(TimerBean.getInstance().getServerTime(), Locales.getInstance().getDefaultLocale()));
        SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_CONTENT, 0, null));
        SearchQueue.getInstance().addAction(new SearchQueueAction(SearchQueueAction.ACTION_INDEX_ALL_USERS, 0, null));
        return true;
    }

}