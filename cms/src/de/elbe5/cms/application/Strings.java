package de.elbe5.cms.application;

import de.elbe5.base.cache.StringCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Strings {

    _above, _active, _add, _addFiles, _addSharedPagePart, _address, _administration, _allInherited, _allPublished, _anonymous, _approvalCode, _approvalInfo, _approve, _approved, _author, _back, _badData, _badLogin, _below, _binaryFileCache, _cacheCleared, _cacheReloaded, _caches, _captcha, _captchaHint, _captchaRenew, _captchaError, _cancel, _changeDate, _changePassword, _changeProfile, _city, _clear, _clone, _close, _code, _configurationSaved, _confirmDelete, _confirmExecute, _contact, _content, _contentAdministration, _continous, _copyright, _country, _creationDate, _cssClass, _cssClasses, _daily, _dataId, _day, _dbAlmostReadyHint, _dbErrorHint, _dbReadyHint, _defaultLocale, _delete, _deleteAll, _deleteError, _description, _displayName, _down, _download, _dragFilesHere, _dynamic, _edit, _editFileSettings, _editFolder, _editGroup, _editModeOff, _editModeOn, _editPage, _editPageSettings, _editProfile, _editProject, _editUser, _email, _emailError, _emailInUseError, _emailReceiver, _emailSender, _emailVerified, _emailVerifiedText, _emailVerificationFailed, _error, _everyHour, _execute, _executeDatabaseScript, _fax, _file, _fileAdministration, _fileCache, _fileCreated, _fileDeleted, _fileMoved, _fileSaved, _files, _filesSaved, _firstName, _flexClass, _folderDeleted, _folderSaved, _forbidden, _formError, _fromTemplate, _general, _group, _groupDeleted, _groupIds, _groupSaved, _groups, _home, _hour, _htmlHint, _id, _import, _importError, _importTemplates, _imprint, _inFooter, _inGroup, _inTopNav, _indexAllContent, _indexAllUsers, _indexingContentQueued, _indexingUsersQueued, _info, _inheritAll, _inheritToSubpages, _inheritsRights, _installation, _interval, _intervalType, _issue, _issues, _javascriptHint, _keywords, _lastName, _load, _locale, _loggedOut, _login, _loginExistsError, _loginLengthError, _loginName, _logout, _mandatoryHint, _masterTemplates, _minute, _mobile, _monthly, _name, _new, _newFile, _newFolder, _newPage, _newPagePart, _newPassword, _noData, _noResults, _notComplete, _notDeletable, _notes, _ok, _oldPassword, _orphanedParts, _page, _pageAdministration, _pageCloned, _pageDeleted, _pageMoved, _pagePart, _pageParts, _pagePartAdded, _pagePartDeleted, _pagePartSettings, _pagePartSettingsSaved, _pagePartShared, _pagePartsDeleted, _pagePublished, _pageSaved, _pageTemplate, _pageTemplates, _pages, _parentFolder, _parentPage, _partAdministration, _partTemplates, _password, _passwordChanged, _passwordLengthError, _passwordNotSet, _passwordsDontMatch, _phone, _pleaseSelect, _portrait, _position, _print, _profile, _profileChanged, _project, _projectSaved, _projects, _publish, _ready, _register, _registered, _registrationHint, _registrationRequest, _registrationVerifyMail, _registrationRequestMail, _registrationApprovedMail, _relevance, _reload, _remove, _restart, _restartHint, _retypePassword, _rightapprove, _rightedit, _rightnone, _rightread, _rights, _save, _saveAsClone, _saveError, _script, _scriptExecuted, _search, _searchResults, _sectionTypes, _select, _selectImage, _selectLink, _selfRegistration, _settings, _share, _sharePagePart, _sharedPart, _sharedParts, _show, _smtpConnectionType, _smtpHost, _smtpPassword, _smtpPort, _smtpUser, _sqlHint, _street, _subpages, _success, _system, _systemAdministration, _systemPasswordHint, _systemPwd, _taskSaved, _taskSettings, _template, _templateAdministration, _templateDeleted, _templateSaved, _templateSnippets, _templates, _templatesImported, _text, _timerInterval, _timerSettingsError, _timers, _title, _true, _type, _up, _url, _user, _userDeleted, _userIds, _userSaved, _users, _view, _workflow, _zipCode;

    public String toString() {
        return name();
    }

    public String string(Locale locale) {
        return StringCache.getString(name(), locale);
    }

    public String html(Locale locale) {
        return StringCache.getHtml(name(), locale);
    }

    public String htmlMultiline(Locale locale) {
        return StringCache.getHtmlMultiline(name(), locale);
    }

    public String js(Locale locale) {
        return StringCache.getJavascript(name(), locale);
    }

    public static void ensureStrings() {
        StringCache.readFromCsv(ApplicationPath.getAppWEBINFPath() + "/strings.csv");
        List<String> list = new ArrayList();
        for (Strings e : Strings.values())
            list.add(e.name());
        StringCache.checkStrings(list);
    }

}
