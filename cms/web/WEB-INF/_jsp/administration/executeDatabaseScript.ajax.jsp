<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%RequestData rdata=RequestData.getRequestData(request);
Locale locale = rdata.getSessionLocale();%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._executeDatabaseScript.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/admin/executeDatabaseScript" name="executeDbScript" >
            <div class="modal-body">
                <cms:formerror/>
                <cms:file name="file" label="<%=Strings._file.toString()%>" />
                <cms:editor name="script" label="<%=Strings._script.toString()%>" type="pgsql" hint="<%=Strings._sqlHint.toString()%>" height="20rem" required="true">
                    <%=StringUtil.toHtml(rdata.getString("script"))%>
                </cms:editor>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._execute.html(locale)%>
                </button>
            </div>
        </cms:form>
        <script type="text/javascript">
            var editor = initAce($('#script'));
            $('#executeDbScript').submit(function (event) {
                var $this = $(this);
                event.preventDefault();
                $('#script').val(editor.getSession().getValue());
                var params = $this.serialize();
                postByAjax('/admin/executeDatabaseScript', params, '<%=Statics.MODAL_DIALOG_JQID%>');
            });
            $('#file').change(function(){
                if (this.files){
                    var file=this.files[0];
                    var fr = new FileReader();
                    fr.onload = function(){
                        editor.setValue(fr.result);
                    };
                    fr.readAsText(file);
                }
            });
        </script>
    </div>
</div>
