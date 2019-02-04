/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.configuration;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.base.mail.Mailer;
import de.elbe5.cms.servlet.IRequestData;
import de.elbe5.cms.servlet.RequestError;
import de.elbe5.cms.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Configuration extends BaseData implements IRequestData, Cloneable {

    private static Configuration instance = new Configuration();

    public static Configuration getInstance() {
        return instance;
    }

    protected Map<String,String> configs = new HashMap<>();

    protected String appTitle="";
    protected String smtpHost = null;
    protected int smtpPort = 25;
    protected Mailer.SmtpConnectionType smtpConnectionType = Mailer.SmtpConnectionType.plain;
    protected String smtpUser = "";
    protected String smtpPassword = "";
    protected String mailSender = null;
    protected String mailReceiver = null;
    protected int timerInterval = 30;
    protected boolean editProfile = true;
    protected boolean selfRegistration = false;

    public Configuration() {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public void evaluateConfigs(){
        setSmtpHost(getConfigs().get("mailHost"));
        setSmtpPort(Integer.parseInt(getConfigs().get("mailPort")));
        setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(getConfigs().get("mailConnectionType")));
        setSmtpUser(getConfigs().get("mailUser"));
        String s="";
        try{
            s=new String(Base64.getDecoder().decode(getConfigs().get("mailPassword").getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception ignore){

        }
        setSmtpPassword(s);
        setMailSender(getConfigs().get("mailSender"));
        setMailReceiver(getConfigs().get("mailReceiver"));
        setTimerInterval(Integer.parseInt(getConfigs().get("timerInterval")));
        setEditProfile(getConfigs().get("editProfile").equalsIgnoreCase("true"));
        setSelfRegistration(getConfigs().get("selfRegistration").equalsIgnoreCase("true"));
    }

    public void putConfigs(){
        getConfigs().put("mailHost",getSmtpHost());
        getConfigs().put("mailPort",Integer.toString(getSmtpPort()));
        getConfigs().put("mailConnectionType",getSmtpConnectionType().name());
        getConfigs().put("mailUser",getSmtpUser());
        getConfigs().put("mailPassword",new String(Base64.getEncoder().encode(getSmtpPassword().getBytes(StandardCharsets.UTF_8))));
        getConfigs().put("mailSender",getMailSender());
        getConfigs().put("mailReceiver",getMailReceiver());
        getConfigs().put("timerInterval",Integer.toString(getTimerInterval()));
        getConfigs().put("editProfile",Boolean.toString(isEditProfile()));
        getConfigs().put("selfRegistration",Boolean.toString(isSelfRegistration()));
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
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

    public boolean isEditProfile() {
        return editProfile;
    }

    public void setEditProfile(boolean editProfile) {
        this.editProfile = editProfile;
    }

    public boolean isSelfRegistration() {
        return selfRegistration;
    }

    public void setSelfRegistration(boolean selfRegistration) {
        this.selfRegistration = selfRegistration;
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

    public void loadAppConfiguration(Configuration config) {
        instance = config;
        ConfigurationBean.getInstance().readLocales(Locales.getInstance().getLocales());
        for (Locale locale : Locales.getInstance().getLocales().keySet()) {
            Log.log("found locale: " + locale.getLanguage() + '(' + locale.getDisplayName() + ')');
        }
        Log.log("default locale is " + Locales.getInstance().getDefaultLocale().getLanguage());
    }

    @Override
    public void readRequestData(HttpServletRequest request, RequestError error) {
        setSmtpHost(RequestReader.getString(request, "smtpHost"));
        setSmtpPort(RequestReader.getInt(request, "smtpPort"));
        setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(RequestReader.getString(request, "smtpConnectionType")));
        setSmtpUser(RequestReader.getString(request, "smtpUser"));
        setSmtpPassword(RequestReader.getString(request, "smtpPassword"));
        setMailSender(RequestReader.getString(request, "emailSender"));
        setMailReceiver(RequestReader.getString(request, "emailReceiver"));
        setTimerInterval(RequestReader.getInt(request, "timerInterval"));
        setEditProfile(RequestReader.getBoolean(request, "editProfile"));
        setSelfRegistration(RequestReader.getBoolean(request, "selfRegistration"));
    }
}
