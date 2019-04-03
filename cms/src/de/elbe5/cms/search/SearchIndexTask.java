/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.search;

import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.timer.TimerBean;
import de.elbe5.cms.timer.TimerTaskData;

import java.time.LocalDateTime;

public class SearchIndexTask extends TimerTaskData {

    @Override
    public String getName() {
        return "searchindex";
    }

    public boolean execute(LocalDateTime executionTime, LocalDateTime checkTime) {
        Log.log("indexing content for search at " + StringUtil.toHtmlDateTime(TimerBean.getInstance().getServerTime(), Locales.getInstance().getDefaultLocale()));
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_PAGES);
        SearchQueue.getInstance().addAction(SearchQueue.ACTION_INDEX_USERS);
        return true;
    }

}