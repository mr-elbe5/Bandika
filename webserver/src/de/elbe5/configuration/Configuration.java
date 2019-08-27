/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.configuration;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.log.Log;
import de.elbe5.base.mail.Mailer;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Configuration extends BaseData {

    private static Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    protected String appTitle = "";
    protected String salt = "";
    protected String smtpHost = null;
    protected int smtpPort = 25;
    protected Mailer.SmtpConnectionType smtpConnectionType = Mailer.SmtpConnectionType.plain;
    protected String smtpUser = "";
    protected String smtpPassword = "";
    protected String mailSender = null;
    protected String mailReceiver = null;
    protected int timerInterval = 30;
    protected Locale defaultLocale = Locale.ENGLISH;
    protected Set<Locale> locales = new HashSet<>();

    public Configuration() {
    }

    private String getSafeInitParameter(ServletContext servletContext, String key){
        String s=servletContext.getInitParameter(key);
        return s==null ? "" : s;
    }

    public void setConfigs(ServletContext servletContext) {
        setSalt(getSafeInitParameter(servletContext,"salt"));
        setSmtpHost(getSafeInitParameter(servletContext,"mailHost"));
        setSmtpPort(Integer.parseInt(getSafeInitParameter(servletContext,"mailPort")));
        setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(getSafeInitParameter(servletContext,"mailConnectionType")));
        setSmtpUser(getSafeInitParameter(servletContext,"mailUser"));
        setSmtpPassword(getSafeInitParameter(servletContext,"mailPassword"));
        setMailSender(getSafeInitParameter(servletContext,"mailSender"));
        setMailReceiver(getSafeInitParameter(servletContext,"mailReceiver"));
        setTimerInterval(Integer.parseInt(getSafeInitParameter(servletContext,"timerInterval")));
        String language = getSafeInitParameter(servletContext,"defaultLanguage");
        try {
            setDefaultLocale(new Locale(language));
        } catch (Exception e) {
            Log.warn("no default locale set");
        }
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public String getMailReceiver() {
        return mailReceiver;
    }

    public void setMailReceiver(String mailReceiver) {
        this.mailReceiver = mailReceiver;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        if (defaultLocale == null)
            return;
        this.defaultLocale = defaultLocale;
        locales.add(defaultLocale);
    }

    public Set<Locale> getLocales() {
        return locales;
    }

    public boolean hasLocale(Locale locale) {
        return locales.contains(locale);
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

    public int getTimerInterval() {
        return timerInterval;
    }

    public void setTimerInterval(int timerInterval) {
        this.timerInterval = timerInterval;
    }

    public void loadLocales() {
        ConfigurationBean.getInstance().readLocales(locales);
        for (Locale locale : locales) {
            Log.log("found locale: " + locale.getLanguage() + '(' + locale.getDisplayName() + ')');
        }
        Log.log("default locale is " + getDefaultLocale().getLanguage());
    }

}
