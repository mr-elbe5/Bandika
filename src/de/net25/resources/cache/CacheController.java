/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.cache;

import de.net25.base.controller.*;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.base.exception.RightException;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;

/**
 * Class CacheController controlls the requests from the user.
 * This class controlls the settings for the maximum image and content cache size.
 */
public class CacheController extends Controller {

  public static final String pageCacheEditProperties = "/jsps/resources/cache/editCacheProperties.jsp";

  /**
   * Method doMethod evaluates the current user request.
   */
  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {

    if (!sdata.isEditor())
      throw new RightException();
    if (method.equals("openEditCaches")) return openEditCaches(sdata);
    if (method.equals("setCacheSizes")) return setCacheSizes(rdata, sdata);
    if (method.equals("resetCacheSizes")) return resetCacheSizes(sdata);
    return noMethod(rdata, sdata);
  }

  /**
   * Method openEditCaches initialize a new session for the user to control
   * the cache size.
   *
   * @param sdata session data
   * @return Response
   * @throws Exception if fails
   */
  public Response openEditCaches(SessionData sdata) throws Exception {
    return new PageResponse(Strings.getString("cacheAdministration", sdata.getLocale()), "", pageCacheEditProperties);
  }

  /**
   * Method setNewSettings sets the new maximum cache sizes.
   *
   * @param rdata request data
   * @param sdata session data
   * @return Response
   * @throws Exception if fails
   */
  public Response setCacheSizes(RequestData rdata, SessionData sdata) throws Exception {
    Statics.getCache(Statics.KEY_DOCUMENT).setMaxSize(rdata.getParamLong("documentCacheSize") * 1024);
    Statics.getCache(Statics.KEY_IMAGE).setMaxSize(rdata.getParamLong("imageCacheSize") * 1024);
    Statics.getCache(Statics.KEY_CONTENT).setMaxSize(rdata.getParamLong("pageCacheSize") * 1024);
    return new PageResponse(Strings.getString("cacheAdministration", sdata.getLocale()), "", pageCacheEditProperties);
  }

  /**
   * Method resetSettings resets the current maximum cache sizes.
   *
   * @param sdata session data
   * @return Response
   * @throws Exception if fails
   */
  public Response resetCacheSizes(SessionData sdata) throws Exception {
    Statics.getCache(Statics.KEY_DOCUMENT).setMaxSize(Statics.DOC_CACHE_SIZE * 1024);
    Statics.getCache(Statics.KEY_IMAGE).setMaxSize(Statics.IMG_CACHE_SIZE * 1024);
    Statics.getCache(Statics.KEY_CONTENT).setMaxSize(Statics.PAGE_CACHE_SIZE * 1024);
    return new PageResponse(Strings.getString("cacheAdministration", sdata.getLocale()), "", pageCacheEditProperties);
	}

}