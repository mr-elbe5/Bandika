/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.configuration;

import de.bandika.base.data.Locales;
import de.bandika.configuration.WebConfiguration;

public class Configuration extends WebConfiguration implements Cloneable {

    private static Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    protected int timerInterval = 30;
    protected int maxVersions = 5;

    public Configuration() {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    public void setTimerInterval(int timerInterval) {
        this.timerInterval = timerInterval;
    }

    public int getMaxVersions() {
        return maxVersions;
    }

    public void setMaxVersions(int maxVersions) {
        this.maxVersions = maxVersions;
    }

    public void loadAppConfiguration(Configuration config) {
        instance = config;
        ConfigurationBean.getInstance().setLocales(Locales.getInstance().getLocales());
        setLocalesDefault();
    }
}
