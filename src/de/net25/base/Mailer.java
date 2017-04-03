/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Authenticator;
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

  /**
   * Method setFromAddress sets the fromAddress of this Mailer object.
   *
   * @param fromAddress the fromAddress of this Mailer object.
   */
  public void setFromAddress(String fromAddress) {
    this.fromAddress = fromAddress;
  }

  /**
   * Method getFromAddress returns the fromAddress of this Mailer object.
   *
   * @return the fromAddress (type String) of this Mailer object.
   */
  public String getFromAddress() {
    return fromAddress;
  }

  /**
   * Method setToAddress sets the toAddress of this Mailer object.
   *
   * @param toAddress the toAddress of this Mailer object.
   */
  public void setToAddress(String toAddress) {
    this.toAddress = toAddress;
  }

  /**
   * Method getToAddress returns the toAddress of this Mailer object.
   *
   * @return the toAddress (type String) of this Mailer object.
   */
  public String getToAddress() {
    return toAddress;
  }

  /**
   * Method setSubject sets the subject of this Mailer object.
   *
   * @param subject the subject of this Mailer object.
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Method getSubject returns the subject of this Mailer object.
   *
   * @return the subject (type String) of this Mailer object.
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Method setContent sets the content of this Mailer object.
   *
   * @param str the content of this Mailer object.
   */
  public void setContent(String str) {
    this.content = str;
  }

  /**
   * Method getContent returns the content of this Mailer object.
   *
   * @return the content (type String) of this Mailer object.
   */
  public String getContent() {
    return content;
  }

  /**
   * Method setReplyTo sets the replyTo of this Mailer object.
   *
   * @param replyTo the replyTo of this Mailer object.
   */
  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  /**
   * Method getReplyTo returns the replyTo of this Mailer object.
   *
   * @return the replyTo (type String) of this Mailer object.
   */
  public String getReplyTo() {
    return replyTo;
  }

  /**
   * Method setSmtpHost sets the smtpHost of this Mailer object.
   *
   * @param smtpHost the smtpHost of this Mailer object.
   */
  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  /**
   * Method getSmtpHost returns the smtpHost of this Mailer object.
   *
   * @return the smtpHost (type String) of this Mailer object.
   */
  public String getSmtpHost() {
    return smtpHost;
  }

  /**
   * Method isComplete returns the complete of this Mailer object.
   *
   * @return the complete (type boolean) of this Mailer object.
   */
  public boolean isComplete() {
    return isComplete(smtpHost) && isComplete(fromAddress) && isComplete(toAddress) && isComplete(subject) && isComplete(content);
  }

  /**
   * Method sendMail
   *
   * @throws Exception when data processing is not successful
   */
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



