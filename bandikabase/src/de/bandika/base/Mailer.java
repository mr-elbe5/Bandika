/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.data.BaseData;
import de.bandika.data.DataHelper;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.Properties;
import java.util.Date;

/**
 * Class Mailer is for basic mailing tasks.<br>
 * Usage:
 */
public class Mailer extends BaseData {

  protected String fromAddress = null;
  protected String toAddress = null;
  protected String subject = null;
  protected String content = null;
  protected String replyTo = null;
  protected String smtpHost = null;

  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public void setToAddress(String toAddress) {
    this.toAddress = toAddress;
  }

  public String getToAddress() {
    return toAddress;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
  }

  public void setContent(String str) {
    this.content = str;
  }

  public String getContent() {
    return content;
  }

  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  public String getReplyTo() {
    return replyTo;
  }

  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  public String getSmtpHost() {
    return smtpHost;
  }

  public boolean isComplete() {
    return DataHelper.isComplete(smtpHost) && DataHelper.isComplete(fromAddress) && DataHelper.isComplete(toAddress) && DataHelper.isComplete(subject) && DataHelper.isComplete(content);
  }

  public void sendMail() throws Exception {
    Properties props = new Properties();
    Session session;
    if (smtpHost == null)
      return;
    props.put("mail.smtp.host", smtpHost);
    session = Session.getDefaultInstance(props, null);
    MimeMessage msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(fromAddress));
    msg.setSentDate(new Date());
    if (subject == null)
      return;
    msg.setSubject(subject);
    InternetAddress[] addressArray;
    if (replyTo == null)
      replyTo = fromAddress;
    addressArray = new InternetAddress[1];
    addressArray[0] = new InternetAddress(replyTo);
    msg.setReplyTo(addressArray);
    if (toAddress == null)
      return;
    addressArray = new InternetAddress[1];
    addressArray[0] = new InternetAddress(toAddress);
    msg.setRecipients(Message.RecipientType.TO, addressArray);
    msg.setContent(content, "text/plain");
    Transport.send(msg);
  }

  public void sendMail(String username,String password) throws Exception {
    Properties props = new Properties();
    Session session;
    if (smtpHost == null)
      return;
    session = Session.getDefaultInstance(props, null);
    MimeMessage msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(fromAddress));
    msg.setSentDate(new Date());
    if (subject == null)
      return;
    msg.setSubject(subject);
    InternetAddress[] addressArray;
    if (replyTo == null)
      replyTo = fromAddress;
    addressArray = new InternetAddress[1];
    addressArray[0] = new InternetAddress(replyTo);
    msg.setReplyTo(addressArray);
    if (toAddress == null)
      return;
    addressArray = new InternetAddress[1];
    addressArray[0] = new InternetAddress(toAddress);
    msg.setRecipients(Message.RecipientType.TO, addressArray);
    msg.setContent(content, "text/plain");
    Transport transport = session.getTransport("smtp");
    transport.connect(smtpHost, username, password);
    transport.sendMessage(msg, msg.getAllRecipients());
    transport.close();
  }

}



