/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.data.IChangeListener;
import de.bandika.data.IController;
import de.bandika.data.StringCache;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller implements IController {

    public abstract Response doAction(String action, RequestData rdata, SessionData sdata) throws Exception;

    public static void addError(RequestData rdata, String s) {
        RequestError err = rdata.getError();
        if (err == null) {
            err = new RequestError();
            rdata.setError(err);
        }
        err.addErrorString(s);
    }

    public static void addError(RequestData rdata, Exception e) {
        RequestError err = rdata.getError();
        if (err == null) {
            err = new RequestError();
            rdata.setError(err);
        }
        err.addError(e);
    }

    protected List<IChangeListener> changeListeners = new ArrayList<>();


    public List<IChangeListener> getChangeListeners() {
        return changeListeners;
    }

    public void itemChanged(String type, String action, String itemName, int itemId) {
        for (IChangeListener listener : changeListeners)
            listener.itemChanged(type, action, itemName, itemId, true);
    }

    public static Response showBlankPage(RequestData rdata, String master) {
        rdata.remove("id");
        rdata.remove(BaseServlet.PARAM_ACTION);
        return new JspResponse("/blank.jsp", master);
    }

    public static Response showBlankPage(RequestData rdata, String title, String master) {
        rdata.remove("id");
        rdata.remove(BaseServlet.PARAM_ACTION);
        return new JspResponse("/blank.jsp", title, master);
    }

    public static Response showCloseLayer(RequestData rdata, String master) {
        rdata.remove("id");
        rdata.remove(BaseServlet.PARAM_ACTION);
        return new JspResponse("/WEB-INF/_jsp/closeLayer.inc.jsp", master);
    }

    public Response noAction(RequestData rdata, SessionData sdata, String master) {
        String action = rdata.getString(BaseServlet.PARAM_ACTION);
        if (action == null)
            action = "[EMPTY]";
        rdata.setError(new RequestError(StringCache.getHtml("webapp_noAction", sdata.getLocale()) + ": " + this.getClass().getName() + "." + action));
        return new JspResponse("/blank.jsp", master);
    }

    public Response noRight(RequestData rdata, SessionData sdata, String master) {
        String action = rdata.getString(BaseServlet.PARAM_ACTION);
        if (action == null)
            action = "[EMPTY]";
        rdata.setError(new RequestError(StringCache.getHtml("webapp_noRight", sdata.getLocale()) + ": " + this.getClass().getName() + "." + action));
        return new JspResponse("/blank.jsp", master);
    }

    public Response noData(RequestData rdata, SessionData sdata, String master) {
        String action = rdata.getString(BaseServlet.PARAM_ACTION);
        if (action == null)
            action = "[EMPTY]";
        rdata.setError(new RequestError(StringCache.getHtml("webapp_noData", sdata.getLocale()) + ": " + this.getClass().getName() + "." + action));
        return new JspResponse("/blank.jsp", master);
    }

}
