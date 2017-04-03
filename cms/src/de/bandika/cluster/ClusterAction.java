/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cluster;

import de.bandika.base.log.Log;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.ActionDispatcher;
import de.bandika.servlet.ICmsAction;
import de.bandika.servlet.RequestError;
import de.bandika.servlet.RequestWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public enum ClusterAction implements ICmsAction {
    /**
     * no action
     */
    defaultAction {
        @Override
        public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return forbidden();
        }
    }, /**
     * shows current settings of the cluster
     */
    showClusterDetails {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    return showClusterDetails(request, response);
                }
            }, /**
     * opens dialog with cluster state
     */
    openViewCluster {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    return showCluster(request, response);
                }
            }, /**
     * reloads cluster information mailFrom database
     */
    reloadCluster {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    boolean active = ClusterManager.getInstance().isInCluster();
                    ClusterManager.getInstance().loadSelf();
                    ClusterManager.getInstance().loadOtherServers();
                    ClusterManager.getInstance().setBeingCluster();
                    if (active != ClusterManager.getInstance().isInCluster()) {
                        if (ClusterManager.getInstance().isInCluster()) {
                            Log.log("joining cluster...");
                            ClusterManager.getInstance().startNewListener();
                            RequestWriter.setMessageKey(request, "_clusterJoined");
                        } else {
                            Log.log("not joining cluster");
                            ClusterManager.getInstance().masterAddress = ClusterManager.getInstance().self.getAddress();
                            RequestError.setError(request, new RequestError("_clusterNotJoined"));
                        }
                    }
                    return showCluster(request, response);
                }
            }, /**
     * activates server as part of the cluster
     */
    activateSelf {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    if (ClusterManager.getInstance().self.isActive())
                        return showCluster(request, response);
                    ClusterBean.getInstance().activateServer(ClusterManager.getInstance().self.getAddress(), true);
                    RequestWriter.setMessageKey(request, "_serverActivated");
                    return reloadCluster.execute(request, response);
                }
            }, /**
     * deactivates server mailFrom being part of the cluster
     */
    deactivateSelf {
                @Override
                public boolean execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    if (!hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT))
                        return false;
                    if (!ClusterManager.getInstance().self.isActive())
                        return showCluster(request, response);
                    ClusterBean.getInstance().activateServer(ClusterManager.getInstance().self.getAddress(), false);
                    RequestWriter.setMessageKey(request, "_serverDeactivated");
                    return reloadCluster.execute(request, response);
                }
            };

    public static final String KEY = "cluster";

    public static void initialize() {
        ActionDispatcher.addClass(KEY, ClusterAction.class);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    protected boolean showCluster(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/cluster/viewCluster.ajax.jsp");
    }

    protected boolean showClusterDetails(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/cluster/clusterDetails.ajax.jsp");
    }

}
