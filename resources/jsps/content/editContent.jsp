<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.resources.template.TemplateBean" %>
<%@ page import="de.net25.resources.template.TemplateData" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  ContentData data = (ContentData) sdata.getParam("contentData");
  ParagraphData editParagraph = data.getEditParagraph();
  TemplateBean tb = (TemplateBean) Statics.getBean(Statics.KEY_TEMPLATE);
  ArrayList<TemplateData> templates = tb.getTemplateList();
  rdata.setParam("editView", "1");
%>
<script type="text/javascript">

  function saveParagraph(idx) {
    document.form.idx.value = idx;
    submitMethod('saveParagraph');
    return false;
  }
  function openSetImage(ident) {
    imageIdent = ident;
    imgSelectPopup = window.open("<%=Statics.DYNAMIC_BASE%>srv25?ctrl=img&method=openFieldImageSelector", "ImageSelector", "width=800,height=600");
    return false;
  }
  function setImage(imgId, imgWidth, imgHeight, altText) {
    var elemId = imageIdent + "ImgId";
    var elem = document.getElementById(elemId);
    elem.value = imgId;
    elemId = imageIdent + "Width";
    elem = document.getElementById(elemId);
    elem.value = imgWidth;
    elemId = imageIdent + "Height";
    elem = document.getElementById(elemId);
    elem.value = imgHeight;
    elemId = imageIdent + "Alt";
    elem = document.getElementById(elemId);
    elem.value = altText;
    var img = document.getElementById(imageIdent);
    if (img) {
      img.src = "srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=show&iid=" + imgId;
      img.alt = altText;
      if (imgWidth > 0)
        img.width = imgWidth;
      if (imgHeight > 0)
        img.height = imgHeight;
    }
  }
  function openSetDocument(ident) {
    docIdent = ident;
    docSelectPopup = window.open("<%=Statics.DYNAMIC_BASE%>srv25?ctrl=doc&method=openFieldDocumentSelector", "DocumentSelector", "width=800,height=600");
    return false;
  }
  function setDocument(docId) {
    var elemId = docIdent + "DocId";
    var elem = document.getElementById(elemId);
    elem.value = docId;
  }

</script>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_CONTENT%>"/>
  <input type="hidden" name="method" value="save"/>
  <input type="hidden" name="editType" value="<%=ContentData.EDIT_CONTENT%>"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>
  <input type="hidden" name="idx" value="0"/>

  <div class="adminTopHeader"><%=Strings.getHtml("contentHtml", sdata.getLocale())%>
  </div>
  <table class="editTable">
    <colgroup>
    <col width="205">
    <col width="20">
    <col width="205">
    <col width="20">
    <col width="205">
    <col width="20">
    <col width="205">
  </colgroup>
    <%
      int idx = 0;
      for (ParagraphData pdata : data.getParagraphs()) {
        rdata.setParam("pdata", pdata);
        rdata.setParam("editMode", pdata == editParagraph ? "1" : "0");
    %>
    <tr>
      <td colspan="7">
        <% if (pdata == editParagraph) {%>
        <div id="editToolsAccept" class="editToolsAccept">
          <% if (pdata.getTemplateId() > 0) {%>
          <a href="#" class="editBtn" onclick="saveParagraph(<%=idx%>);"><%=Strings.getHtml("ok", sdata.getLocale())%>
          </a>
          <%}%>
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=cancelEditParagraph&id=<%=data.getId()%>" class="editBtn"
             onclick=""><%=Strings.getHtml("cancel", sdata.getLocale())%>
          </a>
        </div>
        <% if (pdata.getTemplateId() > 0) {%>
        <div class="editline">&nbsp;</div>
        <%} else {%>
        <table class="editSelectTable">
          <% for (int j = 0; j < templates.size(); j += 3) {%>
          <tr>
            <% for (int k = 0; k < 3 && j + k < templates.size(); k++) {
              TemplateData tdata = templates.get(j + k);%>
            <td class="editTemplateSelect"><a
                href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=setTemplate&id=<%=data.getId()%>&template=<%=tdata.getId()%>">
              <%=tdata.getName()%>
            </a></td>
            <% if(k>0){%>
            <td><div class="c_colspacer">&nbsp;</div></td>
            <%}}%>
          </tr>
          <%}%>
        </table>
        <%
          }
        } else {
          if (editParagraph == null) {
        %>
        <div class="editTools button5">
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=addParagraph&idx=<%=idx%>&id=<%=data.getId()%>"
             class="editBtn"
             onmouseover="showTools('tools<%=idx%>');" onmouseout="hideToolsWait(500);" onclick="">
            <img src="<%=Statics.IMG_PATH%>new.gif" alt="<%=Strings.getHtml("new",sdata.getLocale())%>"></a>
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=editParagraph&idx=<%=idx%>&id=<%=data.getId()%>"
             class="editBtn"
             onmouseover="showTools('tools<%=idx%>');" onmouseout="hideToolsWait(500);" onclick=""><img
              src="<%=Statics.IMG_PATH%>edit.gif" alt="<%=Strings.getHtml("change",sdata.getLocale())%>"></a>
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=moveParagraph&idx=<%=idx%>&dir=-1&id=<%=data.getId()%>"
             class="editBtn"
             onmouseover="showTools('tools<%=idx%>');" onmouseout="hideToolsWait(500);" onclick=""><img
              src="<%=Statics.IMG_PATH%>arrup.gif" alt="<%=Strings.getHtml("up",sdata.getLocale())%>"></a>
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=moveParagraph&idx=<%=idx%>&dir=1&id=<%=data.getId()%>"
             class="editBtn"
             onmouseover="showTools('tools<%=idx%>');" onmouseout="hideToolsWait(500);" onclick=""><img
              src="<%=Statics.IMG_PATH%>arrdn.gif" alt="<%=Strings.getHtml("down",sdata.getLocale())%>"></a>
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=deleteParagraph&idx=<%=idx%>&id=<%=data.getId()%>"
             class="editBtn"
             onmouseover="showTools('tools<%=idx%>');" onmouseout="hideToolsWait(500);" onclick=""><img
              src="<%=Statics.IMG_PATH%>del.gif" alt="<%=Strings.getHtml("delete",sdata.getLocale())%>"></a>
        </div>
        <%} else {%>
        <div class="editTools">&nbsp;
        </div>
        <%
            }
          }
        %>
      </td>
    </tr>
    <% if (pdata.getTemplateId() > 0) {%>
    <jsp:include page="<%=pdata.getTemplateUrl()%>" flush="true"/>
    <%}%>
    <%if (pdata == editParagraph) {%>
    <tr>
      <td colspan="7">
        <div class="editline">&nbsp;</div>
      </td>
    </tr>
    <%}%>
    <%
        idx++;
      }%>
    <% if (editParagraph == null) {%>
    <tr>
      <td colspan="7">
        <div class="editTools button1">
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=addParagraph&idx=-1&id=<%=data.getId()%>" class="editBtn">
            <img src="<%=Statics.IMG_PATH%>new.gif" alt="<%=Strings.getHtml("new",sdata.getLocale())%>"></a>
        </div>
      </td>
    </tr>
    <%}%>
  </table>
  <ul class="adminButtonList">
    <li class="adminTabButton"><a href="#"
                                  onClick="submitMethod('switchMetaData');"><%=Strings.getHtml("metaData", sdata.getLocale())%>
    </a></li>
    <li class="adminTabButton"><a href="#"
                                  onClick="submitMethod('switchParent');"><%=Strings.getHtml("parentMenu", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a href="#" onClick="submitMethod('save');"><%=Strings.getHtml("save", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=data.getId()%>"><%=Strings.getHtml("cancel", sdata.getLocale())%>
    </a></li>
  </ul>
  <% if (editParagraph != null) {%>
  <script type="text/javascript">
    window.location.hash = 'editToolsAccept';
  </script>
  <%}%>
</form>
