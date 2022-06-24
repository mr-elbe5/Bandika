/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.request.RequestData;

import java.util.Map;

public interface IFormBuilder extends IHtmlBuilder {

    String errorHtml = """
            <div class="formError">{{error}}</div>
            """;

    default void appendFormError(StringBuilder sb, RequestData rdata) {
        if (rdata.hasFormError()) {
            append(sb, errorHtml,
                    Map.ofEntries(
                            Map.entry("error", toHtmlMultiline(rdata.getFormError(false).getFormErrorString()))));
        }
    }

    String formStartHtml = """
            <form action="{{url}}" method="post" id="{{name}}" name="{{name}}" accept-charset="UTF-8" {{multipart}}>
            """;

    default void appendFormStart(StringBuilder sb, String url, String name, boolean multi) {
        append(sb, formStartHtml,
                Map.ofEntries(
                        Map.entry("url", url),
                        Map.entry("name", name),
                        Map.entry("multipart", multi ? "enctype=\"multipart/form-data\"" : "")));
    }

    default void appendFormStart(StringBuilder sb, String url, String name) {
        appendFormStart(sb, url, name, false);
    }

    String formEndHtml = """
            </form>
            """;
    String ajaxHtml = """
            <script type="text/javascript">
                $('#{{name}}').submit(function (event) {
                var $this = $(this);
                    event.preventDefault();
                    var params = $this.{{serialize}}();
                    {{call}}('{{url}}', params,'{{target}}');
                  });
            </script>
            """;

    default void appendFormEnd(StringBuilder sb, String url, String name, boolean multi, boolean ajax, String target) {
        append(sb, formEndHtml);
        if (ajax) {
            append(sb, ajaxHtml,
                    Map.ofEntries(
                            Map.entry("name", name),
                            Map.entry("serialize", multi ? "serializeFiles" : "serialize"),
                            Map.entry("call", multi ? "postMultiByAjax" : "postByAjax"),
                            Map.entry("url", url),
                            Map.entry("target", target.isEmpty() ? IResponse.MODAL_DIALOG_JQID : target)));
        }
    }

    default void appendFormEnd(StringBuilder sb) {
        append(sb, formEndHtml);
    }

    String hiddenInputHtml = """
                <input type="hidden" name="{{name}}" id="{{name}}" value="{{value}}"/>
            """;

    default void appendHiddenField(StringBuilder sb, String name, String value) {
        append(sb, hiddenInputHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("value", toHtml(value))));
    }

    // form line

    String formLineStartHtml = """
                <div class="form-group row {{error}}">
                    <label class="col-md-3 col-form-label" for="{{name}}" >{{label}}{{required}}</label>
                    <div class="col-md-9" {{padded}}>
            """;

    default void appendLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean padded) {
        append(sb, formLineStartHtml,
                Map.ofEntries(
                        Map.entry("error", hasError ? "error" : ""),
                        Map.entry("name", name),
                        Map.entry("label", toHtml(label)),
                        Map.entry("required", required ? " <sup>*</sup>" : ""),
                        Map.entry("padded", padded ? " padded" : "")));
    }

    default void appendLineStart(StringBuilder sb, String name, String label) {
        appendLineStart(sb, false, name, label, false, false);
    }

    default void appendLineStart(StringBuilder sb, String name, String label, boolean padded) {
        appendLineStart(sb, false, name, label, false, padded);
    }

    String lineEndHtml = """
                    </div>
                </div>
            """;

    default void appendLineEnd(StringBuilder sb) {
        append(sb, lineEndHtml);
    }

    // text

    default void appendTextLine(StringBuilder sb, String label, String value) {
        appendLineStart(sb, "", label);
        append(sb, value);
        appendLineEnd(sb);
    }

    String textInputHtml = """
                    <input type="text" id="{{name}}" name="{{name}}" class="form-control" value="{{value}}" {{maxLength}}/>
            """;

    default void appendTextInputLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb, textInputHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("value", toHtml(value)),
                        Map.entry("maxLength", maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : "")));
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

    String passwordInputHtml = """
                    <input type="password" id="{{name}}" name="{{name}}" class="form-control" {{maxLength}}/>
            """;

    default void appendPasswordLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb, passwordInputHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("maxLength", maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : "")));
        appendLineEnd(sb);
    }

    String textareaHtml = """
                    <textarea id="{{name}}" name="{{name}}" class="form-control" {{height}}>{{value}}</textarea>
            """;

    default void appendTextareaLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, String height) {
        appendLineStart(sb, hasError, name, label, required, false);
        append(sb, textareaHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("height", height.isEmpty() ? "" : "style=\"height:" + height + "\""),
                        Map.entry("value", toHtml(value))));
        appendLineEnd(sb);
    }

    default void appendTextareaLine(StringBuilder sb, String name, String label, String value, String height) {
        appendTextareaLine(sb, false, name, label, false, value, height);
    }

    // file

    String fileInputHtml = """
                    <input type="file" class="form-control-file" id="{{name}}" name="{{name}}" {{multiple}}>
            """;

    default void appendFileLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean multiple) {
        appendLineStart(sb, hasError, name, label, required, true);
        append(sb, fileInputHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("multiple", multiple ? "multiple" : "")));
    }

    default void appendFileLineStart(StringBuilder sb, String name, String label, boolean multiple) {
        appendFileLineStart(sb, false, name, label, false, multiple);
    }

    // select

    String selectStartHtml = """
                    <select id="{{name}}" name="{{name}}" class="form-control" {{onchange}}>
            """;

    default void appendSelectStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, String onchange) {
        appendLineStart(sb, hasError, name, label, required, true);
        append(sb, selectStartHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("onchange", onchange.isEmpty() ? "" : "onchange=\"" + onchange + "\"")));
    }

    default void appendSelectStart(StringBuilder sb, String name, String label) {
        appendSelectStart(sb, false, name, label, false, "");
    }

    String optionHtml = """
                        <option value="{{value}}" {{selected}}>{{label}}</option>
            """;

    default void appendOption(StringBuilder sb, String value, String label, boolean selected) {
        append(sb, optionHtml,
                Map.ofEntries(
                        Map.entry("value", toHtml(value)),
                        Map.entry("selected", selected ? "selected" : ""),
                        Map.entry("label", toHtml(label))
                )
        );
    }

    String selectEndHtml = """
                    </select>
            """;

    default void appendSelectEnd(StringBuilder sb) {
        append(sb, selectEndHtml);
        appendLineEnd(sb);
    }

    // check

    String checkboxHtml = """
                        <span>
                             <input type="checkbox" name="{{name}}" value="{{value}}" {{checked}}/>
                             <label class="form-check-label">{{label}}</label><br/>
                        </span>
            """;

    default void appendCheckbox(StringBuilder sb, String name, String label, String value, boolean checked) {
        append(sb, checkboxHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("value", toHtml(value)),
                        Map.entry("checked", checked ? "checked" : ""),
                        Map.entry("label", toHtml(label))));
    }

    String radioHtml = """
                        <span>
                             <input type="radio" name="{{name}}" value="{{value}}" {{checked}}/>
                             <label class="form-check-label">{{label}}</label></br>
                        </span>
            """;

    default void appendRadio(StringBuilder sb, String name, String label, String value, boolean checked) {
        append(sb, radioHtml,
                Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("value", toHtml(value)),
                        Map.entry("checked", checked ? "checked" : ""),
                        Map.entry("label", toHtml(label))
                )
        );
    }

}
