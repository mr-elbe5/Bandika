/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cluster;

import java.io.Serializable;

public class ClusterMessage implements Serializable {

    // requests
    public static final int MSGTYPE_CLUSTER = 0;
    public static final int MSGTYPE_LISTENER = 1;

    public static final String MSGKEY_SERVER = "server";

    public static final String ACTION_STATE = "state";
    public static final String ACTION_MASTER = "master";

    public static final String ACTION_SETDIRTY = "setDirty";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_CHANGE = "change";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_RESTART = "restart";

    //answers
    public static final String STATE_ACTIVE = "active";
    public static final String STATE_INACTIVE = "inactive";
    public static final String STATE_MASTER = "master";

    public static final String MASTER_ACCEPTED = "masterAccepted";
    public static final String MASTER_EXISTS = "masterExists";
    public static final String MASTER_PENDING_CANCELLED = "masterPendingCancelled";

    String sender = null;
    int messageType = MSGTYPE_CLUSTER;
    String messageKey = "";
    String action = "";
    int id = 0;
    String responder = "";
    String answer = "";

    public ClusterMessage() {
    }

    public ClusterMessage(String sender, int type) {
        this.sender = sender;
        messageType = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void answer(String responder, String answer) {
        this.responder = responder;
        this.answer = answer;
    }

    public String toString() {
        return String.format("ClusterMessage of type %s, source is '%s', action is %s, id=%s, answer is '%s'", messageType, messageKey, action, id, answer);
    }

}