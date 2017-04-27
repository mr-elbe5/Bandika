/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.base.data.Locales;
import de.bandika.base.log.Log;
import de.bandika.cms.configuration.Configuration;

import java.util.Date;

public class HeartbeatTask implements TimerTask {

    @Override
    public boolean execute(Date executionTime, Date checkTime) {
        Log.log(String.format("heartbeat at %s", Configuration.getInstance().getDateTimeFormat(Locales.getInstance().getDefaultLocale()).format(TimerBean.getInstance().getServerTime())));
        return true;
    }
}