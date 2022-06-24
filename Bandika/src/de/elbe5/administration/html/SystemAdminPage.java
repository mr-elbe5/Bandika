package de.elbe5.administration.html;

import de.elbe5.data.LocalizedStrings;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class SystemAdminPage extends AdminPage {

    public SystemAdminPage() {
        super(LocalizedStrings.getString("_systemAdministration"));
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        appendPageHtmlStart(sb, rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            appendRestart(sb);
            appendCachesStart(sb);
            appendUserCache(sb);
            appendTemplateCache(sb);
            appendCachesEnd(sb);
            appendTimerList(sb);
        }
        appendPageHtmlEnd(sb);
    }

    static final String htmlStart = """
            <div id="pageContent">
            """;
    static final String htmlSectionStart = """
                <section class="treeSection">
                    <ul class="tree">
                        <li class="open">
                            <a class="treeRoot">{{_system}}</a>
                            <ul>
            """;
    static final String restartHtml = """
                                <li>
                                    <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');">{{_restart}}</a>
                                </li>
            """;
    static final String cachesStart = """
                                <li class="open">
                                    <a>{{_caches}}</a>
                                    <ul>
            """;
    static final String userCacheHtml = """
                                        <li>
                                            <span>{{_userCache}}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadUserCache" title="{{_reload}}"></a>
                                            </div>
                                        </li>
            """;
    static final String templateCacheHtml = """
                                        <li>
                                            <span>{{_templateCache}}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadTemplateCache" title="{{_reload}}"></a>
                                            </div>
                                        </li>
            """;
    static final String cachesEnd = """
                                    </ul>
                                </li>
            """;
    static final String timerStart = """
                                <li class="open">
                                    {{_timers}}
                                    <ul>
            """;
    static final String timerHtml = """
                                        <li>
                                            <span>{{displayName}}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/timer/openEditTimerTask?timerName={{name}}');" title="{{_edit}}"></a>
                                            </div>
                                        </li>
            """;
    static final String timerEnd = """
                                    </ul>
                                </li>
            """;
    static final String htmlEnd = """
                            </ul>
                        </li>
                    </ul>
                </section>
            </div>
            <script type="text/javascript">
                $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
            </script>
            """;

    public void appendPageHtmlStart(StringBuilder sb, RequestData rdata) {
        append(sb, htmlStart);
        appendMessageHtml(sb, rdata);
        append(sb, htmlSectionStart, null);
    }

    public void appendRestart(StringBuilder sb) {
        append(sb, restartHtml, null);
    }

    public void appendCachesStart(StringBuilder sb) {
        append(sb, cachesStart, null);
    }

    void appendUserCache(StringBuilder sb) {
        append(sb, userCacheHtml, null);
    }

    void appendTemplateCache(StringBuilder sb) {
        append(sb, templateCacheHtml, null);
    }

    public void appendCachesEnd(StringBuilder sb) {
        append(sb, cachesEnd);
    }

    void appendTimerList(StringBuilder sb) {
        Map<String, TimerTaskData> tasks = null;
        try {
            Timer timerCache = Timer.getInstance();
            tasks = timerCache.getTasks();
        } catch (Exception ignore) {
        }
        append(sb, timerStart, null);
        if (tasks != null) {
            for (TimerTaskData task : tasks.values()) {
                append(sb, timerHtml,
                        Map.ofEntries(
                                Map.entry("displayName", toHtml(task.getDisplayName())),
                                Map.entry("name", toHtml(task.getName()))));
            }
        }
        append(sb, timerEnd);
    }

    public void appendPageHtmlEnd(StringBuilder sb) {
        append(sb, htmlEnd);
    }

}
