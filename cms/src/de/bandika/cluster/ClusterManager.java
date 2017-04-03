/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cluster;

import de.bandika.application.AppContextListener;
import de.bandika.base.log.Log;
import de.bandika.base.port.PortListener;
import de.bandika.base.port.PortSender;
import de.bandika.configuration.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClusterManager {

    private static ClusterManager instance = null;

    public static void setInstance(ClusterManager instance) {
        ClusterManager.instance = instance;
    }

    public static ClusterManager getInstance() {
        if (instance == null) {
            instance = new ClusterManager();
            instance.initialize();
        }
        return instance;
    }

    protected InetAddress ownAddress;
    protected List<ServerData> otherServers = new ArrayList<>();
    boolean inCluster = false;
    ServerData self = null;
    String masterAddress = null;
    boolean masterPending = false;
    PortListener listener = null;
    int senderTimeout = 1000;

    public String getKey() {
        return "cluster";
    }

    public void initialize() {
        Log.log("initializing cluster...");
        senderTimeout = Configuration.getInstance().getClusterTimeout();
        loadSelf();
        loadOtherServers();
        setBeingCluster();
        if (isInCluster()) {
            Log.log("joining cluster...");
            startNewListener();
        } else {
            Log.log("not joining cluster");
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
            Log.log("registering self - own IP address is : " + ownAddress.getHostAddress());
        } catch (UnknownHostException e) {
            Log.error("error finding own IP address", e);
        }
        self = ClusterBean.getInstance().assertSelf(ownAddress.getHostAddress());
    }

    public void loadOtherServers() {
        List<ServerData> list = ClusterBean.getInstance().getOtherServers(ownAddress.getHostAddress());
        for (ServerData data : list) {
            Log.log("found other server registered with IP address " + data.getAddress());
        }
        otherServers = list;
    }

    public void startNewListener() {
        if (listener != null) {
            listener.stopRunning();
            listener = null;
        }
        try {
            listener = new PortListener("bandikaClusterListener", self.getPort(), ClusterMessageProcessor.getInstance());
            self.setPort(Configuration.getInstance().getClusterPort());
            listener.startRunning();
            setPortFromListener();
            AppContextListener.registerThread(listener);
        } catch (Exception e) {
            Log.error("could not start listener", e);
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

    public List<ServerData> getOtherServers() {
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
                Log.log(String.format("server %s is alive ", server.getAddress()));
            } else {
                server.setTimeouts(server.getTimeouts() + 1);
                Log.warn(String.format("server %s is not alive (%s)", server.getAddress(), server.getTimeouts()));
                if (server.getAddress().equals(masterAddress)) {
                    masterAddress = null;
                    Log.warn("master is not reachable");
                }
                int maxTimeouts = Configuration.getInstance().getMaxClusterTimeouts();
                if (server.getTimeouts() > maxTimeouts) {
                    Log.warn(String.format("Server %s had more than %s connection failures - declaring as inactive", server.getAddress(), maxTimeouts));
                    server.setActive(false);
                }
            }
        }
        if (masterAddress.isEmpty()) {
            declareMaster();
        }
    }

    public void declareMaster() {
        if (!isInCluster())
            return;
        Log.log("trying to declare self as master.");
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
                    Log.log(String.format("server %s is master ", server.getAddress()));
                    break;
                } else if (answer.getAnswer().equals(ClusterMessage.MASTER_PENDING_CANCELLED)) {
                    otherPendingCancelled = true;
                }
            } else {
                Log.warn(String.format("server %s is not alive", server.getAddress()));
            }
        }
        if (masterAddress.isEmpty()) {
            masterAddress = self.getAddress();
            masterPending = false;
            Log.log("declared self as master.");
        }
        if (otherPendingCancelled) {
            for (ServerData server : otherServers) {
                if (server.isActive())
                    sendMessage(msg, server);
            }
        }
    }

    public void broadcastMessage(String receiverType, String action, int id) {
        if (!isInCluster())
            return;
        ClusterMessage msg = new ClusterMessage(self.getAddress(), ClusterMessage.MSGTYPE_LISTENER);
        msg.setReceiverType(receiverType);
        msg.setAction(action);
        msg.setId(id);
        Log.log(String.format("broadcasting message: %s", msg.toString()));
        for (ServerData server : otherServers) {
            if (server.isActive())
                sendMessage(msg, server);
        }
    }

    public ClusterMessage sendMessage(ClusterMessage msg, ServerData server) {
        return (ClusterMessage) PortSender.sendObject(msg, server.getAddress(), server.getPort(), senderTimeout);
    }

}