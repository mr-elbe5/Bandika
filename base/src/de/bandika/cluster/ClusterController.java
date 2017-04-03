/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cluster;

import de.bandika._base.*;
import de.bandika.application.Configuration;
import de.bandika.application.ApplicationContextListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ClusterController extends Controller {

  public static final String LINKKEY_CLUSTER = "link|cluster";

  private static ClusterController instance = null;

  public static ClusterController getInstance() {
    if (instance == null) {
      instance = new ClusterController();
      instance.initialize();
    }
    return instance;
  }

  protected InetAddress ownAddress;
  protected ArrayList<ServerData> otherServers = new ArrayList<ServerData>();
  boolean inCluster = false;
  ServerData self = null;
  String masterAddress = null;
  boolean masterPending = false;
  PortListener listener = null;
  int senderTimeout = 1000;

  public void initialize() {
    senderTimeout = Configuration.getConfigInt("clusterTimeout");
    loadSelf();
    loadOtherServers();
    setBeingCluster();
    if (isInCluster()) {
      Logger.info(null, "joining cluster...");
      startNewListener();
    } else {
      Logger.info(null, "not joining cluster");
      masterAddress = self.getAddress();
    }
  }

  public boolean isInCluster() {
    return inCluster;
  }

  public void setBeingCluster() {
    int count = 0;
    for (ServerData server : otherServers) {
      if (server.isActive())
        count++;
    }
    inCluster = self.isActive() && count > 0;
  }

  public void loadSelf() {
    try {
      ownAddress = InetAddress.getLocalHost();
      Logger.info(null, "registering self - own IP address is : " + ownAddress.getHostAddress());
    } catch (UnknownHostException e) {
      Logger.error(null, "error finding own IP address", e);
    }
    self = ClusterBean.getInstance().assertSelf(ownAddress.getHostAddress());
  }

  public void loadOtherServers() {
    ArrayList<ServerData> list = ClusterBean.getInstance().getOtherServers(ownAddress.getHostAddress());
    for (ServerData data : list) {
      Logger.info(null, "found other server registered with IP address " + data.getAddress());
    }
    otherServers = list;
  }

  public void startNewListener() {
    if (listener != null) {
      listener.stopRunning();
      listener = null;
    }
    try {
      listener = new PortListener("o5imsClusterListener", self.getPort(), ClusterMessageProcessor.getInstance());
      self.setPort(Configuration.getConfigInt("clusterPort"));
      listener.startRunning();
      setPortFromListener();
      ApplicationContextListener.registerThread(listener);
    } catch (Exception e) {
      Logger.error(getClass(), "could not start listener", e);
    }
  }

  public void setPortFromListener() {
    self.setPort(listener.getPort());
    ClusterBean.getInstance().updatePort(self);
  }

  public void stopListener() {
    if (listener != null) {
      listener.stopRunning();
      listener = null;
    }
  }

  public boolean processClusterMessage(ClusterMessage msg) {
    if (msg.getAction().equals(ClusterMessage.ACTION_STATE)) {
      if (!self.isActive())
        msg.setAnswer(ClusterMessage.STATE_INACTIVE);
      else if (isMaster() || masterPending)
        msg.setAnswer(ClusterMessage.STATE_MASTER);
      else
        msg.setAnswer(ClusterMessage.STATE_ACTIVE);
      return true;
    } else if (msg.getAction().equals(ClusterMessage.ACTION_MASTER)) {
      if (isMaster()) {
        msg.setAnswer(ClusterMessage.MASTER_EXISTS);
      } else {
        masterAddress = msg.getSender();
        if (masterPending) {
          masterPending = false;
          msg.setAnswer(ClusterMessage.MASTER_PENDING_CANCELLED);
        } else
          msg.setAnswer(ClusterMessage.MASTER_ACCEPTED);
      }
      return true;
    }
    return false;
  }

  public ArrayList<ServerData> getOtherServers() {
    return otherServers;
  }

  public ServerData getSelf() {
    return self;
  }

  public String getMasterAddress() {
    return masterAddress;
  }

  public boolean isMaster() {
    return isInCluster() && masterAddress != null && self.getAddress().equals(masterAddress);
  }

  public void checkOtherServers() {
    if (!isInCluster())
      return;
    ClusterMessage msg = new ClusterMessage(self.getAddress(), ClusterMessage.MSGTYPE_CLUSTER);
    msg.setAction(ClusterMessage.ACTION_STATE);
    for (ServerData server : otherServers) {
      if (!server.isActive())
        return;
      ClusterMessage answer = sendMessage(msg, server);
      if (answer != null) {
        if (answer.getAnswer().equals(ClusterMessage.STATE_MASTER) && !answer.getResponder().equals(masterAddress)) {
          masterAddress = answer.getResponder();
        }
        server.setTimeouts(0);
        //Logger.info(null,String.format("server %s is alive ",server.getAddress()));
      } else {
        server.setTimeouts(server.getTimeouts() + 1);
        Logger.warn(null, String.format("server %s is not alive (%s)", server.getAddress(), server.getTimeouts()));
        if (server.getAddress().equals(masterAddress)) {
          masterAddress = null;
          Logger.warn(null, "master is not reachable");
        }
        int maxTimeouts = Configuration.getConfigInt("maxClusterTimeouts");
        if (server.getTimeouts() > maxTimeouts) {
          Logger.warn(null, String.format("Server %s had more than %s connection failures - declaring as inactive", server.getAddress(), maxTimeouts));
          server.setActive(false);
        }
      }
    }
    if (StringHelper.isNullOrEmtpy(masterAddress)) {
      declareMaster();
    }
  }

  public void declareMaster() {
    if (!isInCluster())
      return;
    Logger.info(null, String.format("trying to declare self as master."));
    ClusterMessage msg = new ClusterMessage(self.getAddress(), ClusterMessage.MSGTYPE_CLUSTER);
    msg.setAction(ClusterMessage.ACTION_MASTER);
    masterAddress = "";
    masterPending = true;
    boolean otherPendingCancelled = false;
    for (ServerData server : otherServers) {
      if (!server.isActive() || !masterPending)
        return;
      ClusterMessage answer = sendMessage(msg, server);
      if (answer != null) {
        if (answer.getAnswer().equals(ClusterMessage.MASTER_EXISTS)) {
          masterAddress = answer.getResponder();
          masterPending = false;
          Logger.info(null, String.format("server %s is master ", server.getAddress()));
          break;
        } else if (answer.getAnswer().equals(ClusterMessage.MASTER_PENDING_CANCELLED)) {
          otherPendingCancelled = true;
        }
      } else {
        Logger.warn(null, String.format("server %s is not alive", server.getAddress()));
      }
    }
    if (StringHelper.isNullOrEmtpy(masterAddress)) {
      masterAddress = self.getAddress();
      masterPending = false;
      Logger.info(null, String.format("declared self as master."));
    }
    if (otherPendingCancelled) {
      for (ServerData server : otherServers) {
        if (server.isActive())
          sendMessage(msg, server);
      }
    }
  }

  public void broadcastMessage(String sourceType, String action, int id) {
    if (!isInCluster())
      return;
    ClusterMessage msg = new ClusterMessage(self.getAddress(), ClusterMessage.MSGTYPE_LISTENER);
    msg.setMessageKey(sourceType);
    msg.setAction(action);
    msg.setId(id);
    Logger.info(null, String.format("broadcasting message: %s", msg.toString()));
    for (ServerData server : otherServers) {
      if (server.isActive())
        sendMessage(msg, server);
    }
  }

  public ClusterMessage sendMessage(ClusterMessage msg, ServerData server) {
    return (ClusterMessage) PortSender.sendObject(msg, server.getAddress(), server.getPort(), senderTimeout);
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (sdata.hasBackendLinkRight(LINKKEY_CLUSTER)) {
      if ("openViewCluster".equals(method)) return openViewCluster();
      if ("reloadCluster".equals(method)) return reloadCluster(rdata);
      if ("activateSelf".equals(method)) return activateSelf(rdata);
      if ("deactivateSelf".equals(method)) return deactivateSelf(rdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showCluster() {
    return new JspResponse("/_jsp/cluster/viewCluster.jsp", MasterResponse.TYPE_ADMIN);
  }

  public Response openViewCluster() throws Exception {
    return showCluster();
  }

  public Response activateSelf(RequestData rdata) throws Exception {
    if (self.isActive())
      return showCluster();
    ClusterBean.getInstance().activateServer(self.getAddress(), true);
    rdata.setMessageKey("serverActivated");
    return reloadCluster(rdata);
  }

  public Response deactivateSelf(RequestData rdata) throws Exception {
    if (!self.isActive())
      return showCluster();
    ClusterBean.getInstance().activateServer(self.getAddress(), false);
    rdata.setMessageKey("serverDeactivated");
    return reloadCluster(rdata);
  }

  public Response reloadCluster(RequestData rdata) throws Exception {
    boolean active = isInCluster();
    loadSelf();
    loadOtherServers();
    setBeingCluster();
    if (active != isInCluster()) {
      if (isInCluster()) {
        Logger.info(null, "joining cluster...");
        startNewListener();
        rdata.setMessageKey("clusterJoined");
      } else {
        Logger.info(null, "not joining cluster");
        masterAddress = self.getAddress();
        rdata.setError(new RequestError("clusterNotJoined"));
      }
    }
    return showCluster();
  }

}