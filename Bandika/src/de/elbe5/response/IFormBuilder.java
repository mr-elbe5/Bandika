/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;

import java.util.Map;

public interface IFormBuilder extends IHtmlBuilder{

    default void appendFormError(StringBuilder sb, RequestData rdata){
        if (rdata.hasFormError()) {
            append(sb, """
                          <div class="formError">$error$</div>
                    """,
                    Map.ofEntries(
                            htmlParam("error", toHtmlMultiline(rdata.getFormError(false).getFormErrorString()))
                    )
            );
        }
    }

    default void appendFormStart(StringBuilder sb, String url, String name, boolean multi) {
        append(sb,"""
                        <form action="$url$" method="post" id="$name$" name="$name$" accept-charset="UTF-8" $multipart$>
                """,
                Map.ofEntries(
                        param("url",url),
                        param("name",name),
                        param("multipart",multi ? "enctype=\"multipart/form-data\"" : "")
                )
        );
    }

    default void appendFormStart(StringBuilder sb, String url, String name) {
        appendFormStart(sb, url, name, false);
    }

    default void appendFormEnd(StringBuilder sb, String url, String name, boolean multi, boolean ajax, String target) {
        append(sb, """
                    </form>
                """);
        if (ajax) {
            append(sb,"""
                                <script type="text/javascript">
                                    $('#$name$').submit(function (event) {
                                    var $this = $(this);
                                        event.preventDefault();
                                        var params = $this.$serialize$();
                                        $call$('$url$', params,'$target$');
                                      });
                                </script>
                            """,
                    Map.ofEntries(
                            param("name",name),
                            param("serialize",multi ? "serializeFiles" : "serialize"),
                            param("call",multi ? "postMultiByAjax" : "postByAjax"),
                            param("url",url),
                            param("target",target.isEmpty() ? IResponse.MODAL_DIALOG_JQID : target)
                    )
            );
        }
    }

    default void appendFormEnd(StringBuilder sb) {
        append(sb, """
                    </form>
                """);
    }

    default void appendHiddenField(StringBuilder sb, String name, String value) {
        append(sb, """
                        <input type="hidden" name="$name$" id="$name" value="$value$"/>
                        """,
                Map.ofEntries(
                        param("name",name),
                        param("value", value)
                )
        );
    }

    // form line

    default void appendLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean padded) {
        append(sb,"""
                        <div class="form-group row $error$">
                                <label class="col-md-3 col-form-label" for="$name$" >$label$$required$</label>
                                <div class="col-md-9" $padded$>
                """,
                Map.ofEntries(
                        param("error",hasError ? "error" : ""),
                        param("name",name),
                        param("label",label),
                        htmlParam("required",required ? " <sup>*</sup>" : ""),
                        param("padded",padded ? " padded" : "")
                )
        );
    }

    default void appendLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required) {
        appendLineStart(sb, hasError, name, label, required, false);
    }

    default void appendLineStart(StringBuilder sb, String name, String label) {
        appendLineStart(sb, false, name, label, false, false);
    }

    default void appendLineStart(StringBuilder sb, String name, String label, boolean padded) {
        appendLineStart(sb, false, name, label, false, padded);
    }

    default void appendLineStart(StringBuilder sb, boolean hasError, String name, boolean required, boolean padded) {
        append(sb,"""
                        <div class="form-group row $error$">
                            <div class="col-md-3"></div>
                            <div class="col-md-9" $padded$>
                        """,
                Map.ofEntries(
                        param("error",hasError ? " error" : ""),
                        param("padded",padded ? " padded" : "")
                )
        );
    }

    default void appendLineEnd(StringBuilder sb) {
        append(sb, "</div></div>");
    }

    // text

    default void appendTextLine(StringBuilder sb, String label, String value) {
        appendLineStart(sb, "", label);
        append(sb, value);
        appendLineEnd(sb);
    }

    default void appendTextInputLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb,"""
                        <input type="text" id="$name$" name="$name$" class="form-control" value="$value$" $maxLength$/>
                        """,
                Map.ofEntries(
                        param("name",name),
                        param("value",value),
                        param("maxLength",maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : "")
                )
        );
        appendLineEnd(sb);
    }

    default void appendTextInputLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value) {
        appendTextInputLine(sb, hasError, name, label, required, value, 0);
    }

    default void appendTextInputLine(StringBuilder sb, String name, String label, String value, int maxLength) {
        appendTextInputLine(sb, false, name, label, false, value, maxLength);
    }

    default void appendTextInputLine(StringBuilder sb, String name, String label, String value) {
        appendTextInputLine(sb, name, label, value, 0);
    }

    default void appendPasswordLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb,"""
                        <input type="password" id="$name$" name="$name$" class="form-control" $maxLength$/>
                        """,
                Map.ofEntries(
                        param("name",name),
                        param("maxLength",maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : "")
                )
        );
        appendLineEnd(sb);
    }

    default void appendPasswordLine(StringBuilder sb, String name, String label, int maxLength) {
        appendPasswordLine(sb, false, name, label, false, maxLength);
    }

    default void appendPasswordLine(StringBuilder sb, String name, String label) {
        appendPasswordLine(sb, name, label, 0);
    }

    default void appendTextareaLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, String height) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb,"""
            <textarea id="$name" name="$name$" class="form-control" $height$>$value$</textarea>
            """,
                Map.ofEntries(
                        param("name",name),
                        param("height",height.isEmpty() ? "" : "style=\"height:" + height + "\""),
                        param("value",value)
                )
        );
        appendLineEnd(sb);
    }

    default void appendTextareaLine(StringBuilder sb, String name, String label, String value, String height) {
        appendTextareaLine(sb, false, name, label, false, value, height);
    }

    // date

    default void appendDateLine(StringBuilder sb, boolean hasError, String name, String label, String value, boolean required) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb,"""
                        <div class="input-group date">
                          <input type="text" id="$name$" name="$name$" class="form-control datepicker" value="$value$" />
                        </div>
                        <script type="text/javascript">$('#$name$').datepicker({language: '$lang$'});</script>
                        """,
                Map.ofEntries(
                        param("name",name),
                        param("value",value),
                        param("lang",Configuration.getLocale().getLanguage())
                )
        );
        appendLineEnd(sb);
    }

    default void appendDateLine(StringBuilder sb, String name, String label, String value) {
        appendDateLine(sb, false, name, label, value, false);
    }

    // file

    default void appendFileLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean multiple) {
        appendLineStart(sb, hasError, name, label, required, true);
        append(sb,"""
                <input type="file" class="form-control-file" id="$name$" name="$name$" $multiple$>
                """,
                Map.ofEntries(
                        param("name",name),
                        param("multiple",multiple ? "multiple" : "")
                )
        );
    }

    default void appendFileLineStart(StringBuilder sb, String name, String label, boolean multiple) {
        appendFileLineStart(sb, false, name, label, false, multiple);
    }

    // select


    default void appendSelectStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, String onchange) {
        appendLineStart(sb, hasError, name, label, required, true);
        append(sb,"""
                    <select id="$name$" name="$name$" class="form-control" $onchange$>
                """,
                Map.ofEntries(
                        param("name",name),
                        param("onchange",onchange.isEmpty() ? "" : "onchange=\"" + onchange + "\"")
                )
        );
    }

    default void appendSelectStart(StringBuilder sb, String name, String label, String onchange) {
        appendSelectStart(sb, false, name, label, false, onchange);
    }

    default void appendSelectStart(StringBuilder sb, String name, String label) {
        appendSelectStart(sb, false, name, label, false, "");
    }

    default void appendOption(StringBuilder sb, String value, String label, boolean selected) {
        append(sb,"""
                        <option value="$value$" $selected$>$label$</option>
                        """,
                Map.ofEntries(
                        param("value",value),
                        param("selected",selected ? "selected" : ""),
                        param("label",label)
                )
        );
    }

    default void appendSelectEnd(StringBuilder sb) {
        append(sb, """
            </select>""");
        appendLineEnd(sb);
    }

    // check

    default void appendCheckbox(StringBuilder sb, String name, String label, String value, boolean checked){
        append(sb,"""
                       <span>
                            <input type="checkbox" name="$name$" value="$value$" $checked$/>
                                <label class="form-check-label">$label$</label><br/>
                       </span>
                        """,
                Map.ofEntries(
                        param("name",name),
                        param("value",value),
                        param("checked",checked ? "checked" : ""),
                        param("label",label)
                )
        );
    }

    default void appendRadio(StringBuilder sb, String name, String label, String value, boolean checked){
        append(sb,"""
                       <span>
                            <input type="radio" name="$name$" value="$value$" $checked$/>
                            <label class="form-check-label">$label$</label></br>
                       </span>
                        """,
                Map.ofEntries(
                        param("name",name),
                        param("value",value),
                        param("checked",checked ? "checked" : ""),
                        param("label",label)
                )
        );
    }

}
