<%--
  Elbe 5 CMS  - A Java based modular Content Management System including Content Management and other features
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%--TEMPLATE==Home Page==Home Page Layout==none==all--%>
<%@ taglib uri = "/WEB-INF/cmstags.tld" prefix = "cms" %>
<cms:layout>
    <section class = "mainSection flexBox">
        <section class = "contentSection content flexItemTwo">
            <div class = "sectionInner">
                <cms:area areaName = "main" areaType = "wide"/>
            </div>
        </section>
        <aside class = "asideSection content flexItemOne">
            <div class = "sectionInner">
                <cms:area areaName = "aside" areaType = "small"/>
            </div>
        </aside>
    </section>
</cms:layout>



