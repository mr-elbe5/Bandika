<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<bandika:layout>
  <div class="row">
    <div class="span3 leftspan3"><bandika:area name="leftspan3" areaType="contentnavi"/></div>
    <div class="span6 mainspan6"><bandika:area name="mainspan6" areaType="content"/></div>
    <div class="span3 rightspan3"><bandika:area name="rightspan3" areaType="related"/></div>
  </div>
</bandika:layout>



