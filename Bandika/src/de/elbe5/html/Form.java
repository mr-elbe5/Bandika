package de.elbe5.html;

import de.elbe5.application.Configuration;
import de.elbe5.base.LocalizedStrings;
import de.elbe5.base.StringFormatter;
import de.elbe5.base.StringHelper;
import de.elbe5.request.RequestData;

public class Form {

    // form

    static public void appendFormStart(StringBuilder sb, String url, String name, boolean multi) {
        sb.append(Html.format("""
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
            sb.append(StringFormatter.format("""
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
                    target));
        }
    }

    static public void appendFormEnd(StringBuilder sb) {
        sb.append("""
                    </form>
                """);
    }

    // form line

    static public void appendLineStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean padded) {
        sb.append(Html.format("""
                        <div class="form-group row {1}">
                                <label class="col-md-3 col-form-label" for="{2}" >{3}{4}</label>
                                <div class="col-md-9" {5}>
                        """,
                hasError ? " error" : "",
                Html.html(name),
                label.startsWith("_") ? LocalizedStrings.html(label) : label,
                required ? " <sup>*</sup>" : "",
                padded ? " padded" : ""
        ));
    }

    static public void appendLineStart(StringBuilder sb, boolean hasError, String name, boolean required, boolean padded) {
        sb.append(Html.format("""
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

    public static void appendTextLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Html.format("""
                        <input type="text" id="{1}" name="{2}" class="form-control" value="{3}" {4}/>
                        """,
                name,
                name,
                value,
                maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : ""));
        appendLineEnd(sb);
    }

    public static void appendPasswordLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, int maxLength) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Html.format("""
                        <input type="password" id="{1}" name="{2}" class="form-control" {4}/>
                        """,
                name,
                name,
                maxLength > 0 ? "maxlength=\"" + maxLength + "\"" : ""));
        appendLineEnd(sb);
    }

    public static void appendTextareaLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, String value, String height) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(StringFormatter.format("""
            <textarea id="{1}" name="{2}" class="form-control" {3}>{4}</textarea>
            """,
                name,
                name,
                height.isEmpty() ? "" : "style=\"height:" + height + "\"",
                value));
    }

    // date

    public static void appendDateLine(StringBuilder sb, boolean hasError, String name, String label, String value, boolean required) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Html.format("""
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

    // file

    public static void appendFileLine(StringBuilder sb, boolean hasError, String name, String label, boolean required, boolean multiple) {
        appendLineStart(sb, hasError, name, label, required, false);
        sb.append(Html.format("<input type=\"file\" class=\"form-control-file\" id=\"{1}\" name=\"{2}\" {3}>",
                name,
                name,
                multiple ? "multiple" : ""));
    }

    // select


    static public void appendSelectStart(StringBuilder sb, boolean hasError, String name, String label, boolean required, String onchange) {
        appendLineStart(sb, hasError, name, label, required, true);
        sb.append(Html.format("""
                    <select id="{1}" name="{2}" class="form-control" {3}>
                """,
                name,
                name,
                onchange.isEmpty() ? "" : "onchange=\"" + onchange + "\""
        ));
    }

    static public void appendSelectEnd(StringBuilder sb) {
        sb.append("""
            </select>""");
    }

    // check

    public static void appendCheckbox(StringBuilder sb, String name, String label, String value, boolean checked){
        sb.append(Html.format("""
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
        sb.append(Html.format("""
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
