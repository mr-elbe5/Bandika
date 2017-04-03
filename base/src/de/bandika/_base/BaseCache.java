/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.cluster.ClusterMessage;
import de.bandika.cluster.ClusterController;

public abstract class BaseCache {

  protected boolean dirty = true;
  protected final Integer lockObj = 1;

  public abstract String getCacheKey();

  public void setDirty() {
    dirty = true;
  }

  public void setClusterDirty() {
    setDirty();
    ClusterController.getInstance().broadcastMessage(getCacheKey(), ClusterMessage.ACTION_SETDIRTY, 0);
  }

  public void checkDirty() {
    if (dirty) {
      synchronized (lockObj) {
        if (dirty) {
          load();
          dirty = false;
        }
      }
    }
  }

  public abstract void load();

}
