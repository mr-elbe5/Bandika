package de.elbe5.html;

import de.elbe5.application.Configuration;
import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.IResponse;

public class Form {

    // form

    static public void appendFormError(StringBuilder sb, RequestData rdata){
        if (rdata.hasFormError()) {
            sb.append(Strings.format("""
                                      <div class="formError">
                                          {1}
                                      </div>
                            """,
                    Strings.toHtmlMultiline(rdata.getFormError(false).getFormErrorString())
            ));
        }
    }

    static public void appendFormStart(StringBuilder sb, String url, String name, boolean multi) {
        sb.append(Strings.format("""
                        <form action="{1}" method="post" id="{2}" name="{3}" accept-charset="UTF-8"{4}>
                        """,
                url,
                name,
                name,
                multi ? " enctype=\"multipart/form-data\"" : ""
        ));
    }

    static public void appendFormStart(StringBuilder sb, String url, String name) {
        appendFormStart(sb, url, name, false);
    }

    static public void appendFormEnd(StringBuilder sb, String url, String name, boolean multi, boolean ajax, String target) {
        sb.append("""
                    </form>
                """);
        if (ajax) {
            sb.append(Strings.format("""
                                <script type="text/javascript">
                                    $('#{1}').submit(function (event) {
                                    var $this = $(this);
                                        event.preventDefault();
                                        var params = $this.{2}();
                                        {3}('{4}', params,'{5}');
                                      });
                                </script>
                            """,
                    name,
                    multi ? "serializeFiles" : "serialize",
                    multi ? "postMultiByAjax" : "postByAjax",
                    url,
                    target.isEmpty() ? IResponse.MODAL_DIALOG_JQID : target));
        }
    }

    static public void appendFormEnd(StringBuilder sb) {
        sb.append("""
                    </form>
                """);
    }

    // form line

    static public void appendLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean padded) {
        sb.append(Strings.format("""
                        <div class="form-group row {1}">
                                <label class="col-md-3 col-form-label" for="{2}" >{3}{4}</label>
                                <div class="col-md-9" {5}>
                        """,
                hasError ? " error" : "",
                name,
                label,
                required ? " <sup>*</sup>" : "",
                padded ? " padded" : ""
        ));
    }

    static public void appendLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required) {
        appendLineStart(sb, hasError, name, label, required, false);
    }

    static public void appendLineStart(StringBuilder sb, String name, String label) {
        appendLineStart(sb, false, name, label, false, false);
    }

    static public void appendLineStart(StringBuilder sb, String name, String label, boolean padded) {
        appendLineStart(sb, false, name, label, false, padded);
    }

    static public void appendLineStart(StringBuilder sb, boolean hasError, String name, boolean required, boolean padded) {
        sb.append(Strings.format("""
                        <div class="form-group row {1}">
                            <div class="col-md-3"></div>
                            <div class="col-md-9" {2}>
                        """,
                hasError ? " error" : "",
                padded ? " padded" : ""
        ));
    }

    static public void appendLineEnd(StringBuilder sb) {
        sb.append("</div></div>");
    }

    // text

    public static void appendTextLine(StringBuilder sb, String label, String value) {
        appendLineStart(sb, "", label);
        sb.append(value);
        appendLineEnd(sb);
    }

    public static void appendTextInputLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Strings.format("""
                        <input type="text" id="{1}" name="{2}" class="form-control" value="{3}" {4}/>
                        """,
                name,
                name,
                value,
                maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : ""));
        appendLineEnd(sb);
    }

    public static void appendTextInputLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value) {
        appendTextInputLine(sb, hasError, name, label, required, value, 0);
    }

    public static void appendTextInputLine(StringBuilder sb, String name, String label, String value, int maxLength) {
        appendTextInputLine(sb, false, name, label, false, value, maxLength);
    }

    public static void appendTextInputLine(StringBuilder sb, String name, String label, String value) {
        appendTextInputLine(sb, name, label, value, 0);
    }

    public static void appendPasswordLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Strings.format("""
                        <input type="password" id="{1}" name="{2}" class="form-control" {4}/>
                        """,
                name,
                name,
                maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : ""));
        appendLineEnd(sb);
    }

    public static void appendPasswordLine(StringBuilder sb, String name, String label, int maxLength) {
        appendPasswordLine(sb, false, name, label, false, maxLength);
    }

    public static void appendPasswordLine(StringBuilder sb, String name, String label) {
        appendPasswordLine(sb, name, label, 0);
    }

    public static void appendTextareaLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, String height) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Strings.format("""
            <textarea id="{1}" name="{2}" class="form-control" {3}>{4}</textarea>
            """,
                name,
                name,
                height.isEmpty() ? "" : "style=\"height:" + height + "\"",
                value));
        appendLineEnd(sb);
    }

    public static void appendTextareaLine(StringBuilder sb, String name, String label, String value, String height) {
        appendTextareaLine(sb, false, name, label, false, value, height);
    }

    // date

    public static void appendDateLine(StringBuilder sb, boolean hasError, String name, String label, String value, boolean required) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Strings.format("""
                        <div class="input-group date">
                          <input type="text" id="{1}" name="{2}" class="form-control datepicker" value="{3}" />
                        </div>
                        <script type="text/javascript">$('#{4}').datepicker({language: '{5}'});</script>
                        """,
                name,
                name,
                value,
                name,
                Configuration.getLocale().getLanguage()));
        appendLineEnd(sb);
    }

    public static void appendDateLine(StringBuilder sb, String name, String label, String value) {
        appendDateLine(sb, false, name, label, value, false);
    }

    // file

    public static void appendFileLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean multiple) {
        appendLineStart(sb, hasError, name, label, required, true);
        sb.append(Strings.format("""
                <input type="file" class="form-control-file" id="{1}" name="{2}" {3}>
                """,
                name,
                name,
                multiple ? "multiple" : ""));
    }

    public static void appendFileLineStart(StringBuilder sb, String name, String label, boolean multiple) {
        appendFileLineStart(sb, false, name, label, false, multiple);
    }

    // select


    static public void appendSelectStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, String onchange) {
        appendLineStart(sb, hasError, name, label, required, true);
        sb.append(Strings.format("""
                    <select id="{1}" name="{2}" class="form-control" {3}>
                """,
                name,
                name,
                onchange.isEmpty() ? "" : "onchange=\"" + onchange + "\""
        ));
    }

    static public void appendSelectStart(StringBuilder sb, String name, String label, String onchange) {
        appendSelectStart(sb, false, name, label, false, onchange);
    }

    static public void appendSelectStart(StringBuilder sb, String name, String label) {
        appendSelectStart(sb, false, name, label, false, "");
    }

    static public void appendOption(StringBuilder sb, String value, String label, boolean selected) {
        sb.append(Strings.format("""
                        <option value="{1}" {2}>{3}</option>
                        """,
                value,
                selected ? "selected" : "",
                label
        ));
    }

    static public void appendSelectEnd(StringBuilder sb) {
        sb.append("""
            </select>""");
        appendLineEnd(sb);
    }

    // check

    public static void appendCheckbox(StringBuilder sb, String name, String label, String value, boolean checked){
        sb.append(Strings.format("""
                       <span>
                            <input type="checkbox" name="{1}" value="{2}" {3}/>
                                <label class="form-check-label">{4}</label>
                       </span>
                        """,
                name,
                value,
                checked ? "checked" : "",
                label));
    }

    public static void appendRadio(StringBuilder sb, String name, String label, String value, boolean checked){
        sb.append(Strings.format("""
                       <span>
                            <input type="radio" name="{1}" value="{2}" {3}/>
                            <label class="form-check-label">{4}</label>
                       </span>
                        """,
                name,
                value,
                checked ? "checked" : "",
                label));
    }
}
