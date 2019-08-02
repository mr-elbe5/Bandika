<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.data.Pair" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.page.PageCache" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.page.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.template.TemplateBean" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="de.elbe5.cms.user.GroupBean" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageData pageData = rdata.getCurrentPage();
    assert (pageData != null);
    PageCache cache = PageCache.getInstance();
    PageData parentPage = null;
    if (pageData.getParentId() != 0) {
        parentPage = cache.getPage(pageData.getParentId());
    }
    List<String> templateNames = TemplateBean.getInstance().getTemplateNames(TemplateData.TYPE_PAGE);
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
    List<Pair<Integer, String>> childSortList = new ArrayList<>();
    if (!pageData.isNew()) {
        for (PageData subpage : cache.getPage(pageData.getId()).getSubPages()) {
            childSortList.add(new Pair<>(subpage.getId(), subpage.getName()));
        }
    }
    String label, name, onchange;
    String url = "/ctrl/page/savePage/" + pageData.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._editPageSettings.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="pageform" ajax="true">
            <div class="modal-body">
                <cms:formerror/>
                <h3><%=Strings._settings.html(locale)%>
                </h3>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(pageData.getId())%>
                </cms:line>
                <cms:line label="<%=Strings._creationDate.toString()%>"><%=StringUtil.toHtmlDateTime(pageData.getCreationDate(), locale)%>
                </cms:line>
                <cms:line label="<%=Strings._changeDate.toString()%>"><%=StringUtil.toHtmlDateTime(pageData.getChangeDate(), locale)%>
                </cms:line>
                <cms:line label="<%=Strings._parentPage.toString()%>"><%=(parentPage == null) ? "-" : StringUtil.toHtml(parentPage.getName()) + "&nbsp;(" + parentPage.getId() + ')'%>
                </cms:line>
                <cms:line label="<%=Strings._url.toString()%>"><%=StringUtil.toHtml(pageData.getUrl())%>
                </cms:line>
                <cms:line label="<%=Strings._position.toString()%>"><%=Integer.toString(pageData.getRanking())%>
                </cms:line>

                <cms:text name="name" label="<%=Strings._name.toString()%>" required="true" value="<%=StringUtil.toHtml(pageData.getName())%>"/>
                <cms:text name="displayName" label="<%=Strings._displayName.toString()%>" required="true" value="<%=StringUtil.toHtml(pageData.getDisplayName())%>"/>
                <cms:text name="description" label="<%=Strings._description.toString()%>" value="<%=StringUtil.toHtml(pageData.getDescription())%>"/>
                <cms:text name="keywords" label="<%=Strings._keywords.toString()%>" value="<%=StringUtil.toHtml(pageData.getKeywords())%>"/>
                <cms:line label="<%=Strings._author.toString()%>"><%=StringUtil.toHtml(pageData.getAuthorName())%>
                </cms:line>
                <% if (pageData instanceof TemplatePageData) {
                    TemplatePageData templatePageData = (TemplatePageData) pageData;%>
                <cms:select name="templateName" label="<%=Strings._pageTemplate.toString()%>" required="true">
                    <option value="" <%=templatePageData.getTemplateName().isEmpty() ? "selected" : ""%>><%=Strings._pleaseSelect.html(locale)%>
                    </option>
                    <% for (String templateName : templateNames) {%>
                    <option value="<%=StringUtil.toHtml(templateName)%>" <%=templateName.equals(templatePageData.getTemplateName()) ? "selected" : ""%>><%=StringUtil.toHtml(templateName)%>
                    </option>
                    <%}%>
                </cms:select>
                <%}%>
                <cms:line label="<%=Strings._inTopNav.toString()%>" padded="true">
                    <cms:check name="inTopNav" value="true" checked="<%=pageData.isInTopNav()%>"/>
                </cms:line>
                <cms:line label="<%=Strings._inFooter.toString()%>" padded="true">
                    <cms:check name="inFooter" value="true" checked="<%=pageData.isInFooter()%>"/>
                </cms:line>
                <h3><%=Strings._rights.html(locale)%>
                </h3>
                <cms:line label="<%=Strings._anonymous.toString()%>" padded="true">
                    <cms:check name="anonymous" value="true" checked="<%=pageData.isAnonymous()%>"/>
                </cms:line>
                <cms:line label="<%=Strings._inheritsRights.toString()%>" padded="true">
                    <cms:check name="inheritsRights" value="true" checked="<%=pageData.inheritsRights()%>"/>
                </cms:line>
                <%
                    for (GroupData group : groups) {
                        if (group.getId() <= GroupData.ID_MAX_FINAL)
                            continue;
                        {
                %><%
                label = StringUtil.toHtml(group.getName());
                name = "groupright_" + group.getId();%>
                <cms:line label="<%=label%>" padded="true">
                    <cms:radio name="<%=name%>" value="" checked="<%=!pageData.hasAnyGroupRight(group.getId())%>"><%=Strings._rightnone.html(locale)%>
                    </cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.READ.name()%>" checked="<%=pageData.isGroupRight(group.getId(), Right.READ)%>"><%=Strings._rightread.html(locale)%>
                    </cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.EDIT.name()%>" checked="<%=pageData.isGroupRight(group.getId(), Right.EDIT)%>"><%=Strings._rightedit.html(locale)%>
                    </cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.APPROVE.name()%>" checked="<%=pageData.isGroupRight(group.getId(), Right.APPROVE)%>"><%=Strings._rightapprove.html(locale)%>
                    </cms:radio><br/>
                </cms:line>
                <%
                        }
                    }
                %><% if (!pageData.isNew() && !pageData.getSubPages().isEmpty()) {%>
                <h3><%=Strings._subpages.html(locale)%>
                </h3>
                <cms:line label="<%=Strings._name.toString()%>" padded="true"><%=Strings._position.html(locale)%>
                </cms:line>
                <%
                    int idx = 0;
                    for (Pair<Integer, String> child : childSortList) {
                        name = "select" + child.getKey();
                        onchange = "setRanking(" + Integer.toString(child.getKey()) + ");";
                %>
                <cms:select name="<%=name%>" label="<%=StringUtil.toHtml(child.getValue())%>" onchange="<%=onchange%>">
                    <%for (int i = 0; i < childSortList.size(); i++) {%>
                    <option value="<%=i%>" <%=i == idx ? "selected" : ""%>><%=i + 1%>
                    </option>
                    <%}%>
                </cms:select>
                <%
                        idx++;
                    }%><%}%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
        <script type="text/javascript">

            function initRankingData() {
                $('select', '#subpages').each(function (i) {
                    setRankVal($(this), i);
                });
            }

            function setRanking(childId) {
                var select = $('#select' + childId);
                var newRanking = parseInt(select.val());
                var oldRanking = parseInt(select.attr('data-ranking'));
                $('select', '#subpages').each(function (i) {
                    var sel = $(this);
                    if (sel.attr('id') === 'select' + childId) {
                        setRankVal(sel, newRanking);
                    } else {
                        var val = parseInt(sel.val());
                        if (newRanking > oldRanking) {
                            if (val > oldRanking && val <= newRanking)
                                setRankVal(sel, val - 1);
                        } else {
                            if (val < oldRanking && val >= newRanking)
                                setRankVal(sel, val + 1);
                        }
                    }
                });
            }

            function setRankVal(sel, val) {
                sel.val(val);
                sel.attr('data-ranking', val);
            }

            initRankingData();
        </script>
    </div>
</div>


