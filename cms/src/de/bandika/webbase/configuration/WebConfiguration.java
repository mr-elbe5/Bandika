/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.configuration;

import de.bandika.base.data.BaseData;
import de.bandika.base.data.Locales;
import de.bandika.base.log.Log;
import de.bandika.base.mail.Mailer;

import java.util.Locale;

public class WebConfiguration extends BaseData implements Cloneable {

    protected Locale defaultLocale = Locale.GERMAN;

    protected String smtpHost = null;
    protected int smtpPort = 25;
    protected Mailer.SmtpConnectionType smtpConnectionType = Mailer.SmtpConnectionType.plain;
    protected String smtpUser = "";
    protected String smtpPassword = "";
    protected String mailSender = null;

    public WebConfiguration() {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public Mailer.SmtpConnectionType getSmtpConnectionType() {
        return smtpConnectionType;
    }

    public void setSmtpConnectionType(Mailer.SmtpConnectionType smtpConnectionType) {
        this.smtpConnectionType = smtpConnectionType;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    public String getMailSender() {
        return mailSender;
    }

    public void setMailSender(String mailSender) {
        this.mailSender = mailSender;
    }

    public Mailer getMailer() {
        Mailer mailer = new Mailer();
        mailer.setSmtpHost(getSmtpHost());
        mailer.setSmtpPort(getSmtpPort());
        mailer.setSmtpConnectionType(getSmtpConnectionType());
        mailer.setSmtpUser(getSmtpUser());
        mailer.setSmtpPassword(getSmtpPassword());
        mailer.setFrom(getMailSender());
        mailer.setReplyTo(getMailSender());
        return mailer;
    }

    public void setLocalesDefault() {
        Locales.getInstance().setDefaultLocale(defaultLocale);
        for (Locale locale : Locales.getInstance().getLocales().keySet()) {
            Log.log("found locale: " + locale.getLanguage() + '(' + locale.getDisplayName() + ')');
        }
        Log.log("default locale is " + Locales.getInstance().getDefaultLocale().getLanguage());
    }
}
