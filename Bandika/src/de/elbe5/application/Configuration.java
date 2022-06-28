/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import de.elbe5.data.IJsonData;
import de.elbe5.data.JsonClass;
import de.elbe5.data.JsonField;
import de.elbe5.mail.Mailer;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import java.util.*;

@JsonClass
public class Configuration implements IJsonData {

    private static final Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    public static String ENCODING = "UTF-8";

    @JsonField(baseClass = String.class)
    private String appTitle = "";
    @JsonField(baseClass = String.class)
    private String appName = "";
    @JsonField(baseClass = String.class)
    private String salt = "";
    @JsonField(baseClass = String.class)
    private String smtpHost = null;
    @JsonField(baseClass = Integer.class)
    private int smtpPort = 25;
    @JsonField(baseClass = Mailer.SmtpConnectionType.class)
    private Mailer.SmtpConnectionType smtpConnectionType = Mailer.SmtpConnectionType.plain;
    @JsonField(baseClass = String.class)
    private String smtpUser = "";
    @JsonField(baseClass = String.class)
    private String smtpPassword = "";
    @JsonField(baseClass = String.class)
    private String mailSender = null;
    @JsonField(baseClass = String.class)
    private String mailReceiver = null;
    @JsonField(baseClass = Integer.class)
    private int timerInterval = 30;
    private Locale locale = Locale.GERMAN;
    private final Map<String,Locale> locales = new HashMap<>();

    public Configuration(){
        locales.put("de",Locale.GERMAN);
        locales.put("en",Locale.ENGLISH);
    }

    public JSONObject getJsonObject(){
        return this.toJSONObject();
    }

    // base data

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        if (locale == null || !locales.containsValue(locale))
            return;
        this.locale = locale;
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    public void setTimerInterval(int timerInterval) {
        this.timerInterval = timerInterval;
    }

    // read from config file

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
            setLocale(new Locale(language));
        } catch (Exception ignore) {
        }
        System.out.println("language is "+ getLocale().getDisplayName());
    }

}
