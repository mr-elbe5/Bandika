/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.application.Configuration;
import de.bandika.user.UserData;
import de.bandika.rights.RightsCache;
import de.bandika.rights.UserRightsData;
import de.bandika.page.PageRightsProvider;
import de.bandika.link.LinkCache;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 * Class SessionData is the data class for holding session data of a single
 * user. <br>
 * Usage:
 */
public class SessionData extends ParamData implements HttpSessionBindingListener {

  private HttpSession session = null;
  private UserData user = null;
  HashSet<String> backendLinks = null;
  private UserRightsData userRights = null;
  private Locale locale=null;

  public void reset() {
    params.clear();
    user = null;
    userRights = null;
  }

  public HttpSession getSession() {
    return session;
  }

  public void setSession(HttpSession session) {
    this.session = session;
  }

  public UserData getUser() {
    return user;
  }

  public void setUser(UserData user) {
    this.user = user;
  }

  public int getUserId() {
    return user == null ? 0 : user.getId();
  }

  public boolean isLoggedIn() {
    return user != null;
  }

  public String getUserName() {
    return user == null ? "" : user.getName();
  }

  public boolean isRoot() {
    return user != null && (user.isRoot());
  }

  public boolean isAdministrator() {
    return user != null && (user.isAdministrator());
  }

  public boolean isApprover() {
    return user != null && (user.isApprover());
  }

  public boolean isEditor() {
    return user != null && (user.isEditor());
  }

  public void ensureBackendLinkGroups() {
    if (user == null) {
      backendLinks = null;
      return;
    }
    if (backendLinks == null)
      backendLinks = LinkCache.getInstance().getBackendLinks(user);
  }

  public boolean hasBackendLinkRight(String key) {
    ensureBackendLinkGroups();
    return user != null && (user.isRoot() || (backendLinks != null && backendLinks.contains(key)));
  }

  public boolean hasAnyBackendLinkRight() {
    ensureBackendLinkGroups();
    return isRoot() || (backendLinks != null && !backendLinks.isEmpty());
  }

  public void ensureUserRights() {
    if (user == null) {
      userRights = null;
      return;
    }
    userRights = RightsCache.getInstance().getAllRights(user, userRights);
  }

  public boolean hasRight(String type, int id, int right) {
    ensureUserRights();
    return user != null && (user.isRoot() || (userRights != null && userRights.hasRights(type) && userRights.getRights(type).hasRight(id, right)));
  }

  public boolean hasPageEditRight(int id) {
    return hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_EDITOR) || hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, id, IRights.ROLE_APPROVER);
  }

  public boolean hasAnyEditRight(String type) {
    ensureUserRights();
    return user != null && (user.isRoot() || user.isApprover() || user.isEditor() || (userRights != null && userRights.hasRights(type) && userRights.getRights(type).hasAnyEditRight()));
  }

  public boolean hasAnyPageEditRight() {
    return isApprover() || isEditor() || hasAnyEditRight(PageRightsProvider.RIGHTS_TYPE_PAGE);
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
    for (Object obj : params.values()) {
      if (obj instanceof HttpSessionBindingListener)
        ((HttpSessionBindingListener) obj).valueBound(httpSessionBindingEvent);
    }
  }

  public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
    for (Object obj : params.values()) {
      if (obj instanceof HttpSessionBindingListener)
        ((HttpSessionBindingListener) obj).valueUnbound(httpSessionBindingEvent);
    }
  }

}
