/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.tree;

import de.bandika.servlet.ICmsAction;
import de.bandika.servlet.RequestStatics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ITreeAction extends ICmsAction {

    default boolean showTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/tree/tree.ajax.jsp");
    }

    default boolean closeLayerToTree(HttpServletRequest request, HttpServletResponse response, String url) {
        return closeLayer(request, response, "closeLayerToTree('" + url + "')");
    }

    default boolean closeLayerToTree(HttpServletRequest request, HttpServletResponse response, String url, String messageKey) {
        return closeLayer(request, response, "closeLayerToTree('" + url + "&" + RequestStatics.KEY_MESSAGEKEY + "=" + messageKey + "');");
    }

}
