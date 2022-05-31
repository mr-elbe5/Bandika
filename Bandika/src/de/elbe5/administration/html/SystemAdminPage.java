package de.elbe5.administration.html;

import de.elbe5.base.Strings;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class SystemAdminPage extends AdminPage {

    public SystemAdminPage() {
        super(Strings.getString("_systemAdministration"));
    }

    @Override
    public void appendPageHtml(RequestData rdata) {
        appendPageHtmlStart(rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            appendRestart(rdata);
            appendCachesStart(rdata);
            appendUserCache(rdata);
            appendTemplateCache(rdata);
            appendCachesEnd();
            appendTimerList(rdata);
        }
        appendPageHtmlEnd();
    }

    public void appendPageHtmlStart(RequestData rdata) {
        append("""
                <div id="pageContent">
                """);
        MessageHtml.appendMessageHtml(sb, rdata);
        append("""
                        <section class="treeSection">
                            <ul class="tree">
                                <li class="open">
                                    <a class="treeRoot">{1}
                                    </a>
                                    <ul>
                                    """,
                Strings.getHtml("_system")
        );
    }

    public void appendRestart(RequestData rdata) {
        append("""
                        <li>
                            <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');">{1}
                            </a>
                        </li>
                        """,
                Strings.getHtml("_restart")
        );
    }

    public void appendCachesStart(RequestData rdata) {

        append("""
                    <li class="open">
                        <a>{1}
                        </a>
                        <ul>
                """,
                Strings.getHtml("_caches")
                );
    }

    void appendUserCache(RequestData rdata) {
        append("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadUserCache" title="{2}"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_userCache"),
                Strings.getHtml("_reload")
        );
    }

    void appendTemplateCache(RequestData rdata) {
        append("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadTemplateCache" title="{2}"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_templateCache"),
                Strings.getHtml("_reload")
        );
    }

    public void appendCachesEnd() {
        append("""
                    </ul>
                </li>
                """
        );
    }

    void appendTimerList(RequestData rdata) {
        Map<String, TimerTaskData> tasks = null;
        try {
            Timer timerCache = Timer.getInstance();
            tasks = timerCache.getTasks();
        } catch (Exception ignore) {
        }
        append("""
                        <li class="open">
                            {1}
                            <ul>
                            """,
                Strings.getHtml("_timers")
        );
        if (tasks != null) {
            for (TimerTaskData task : tasks.values()) {
                append("""
                                <li>
                                    <span>{1}</span>
                                    <div class="icons">
                                        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/timer/openEditTimerTask?timerName={2}');" title="{3}"></a>
                                    </div>
                                </li>
                                """,
                        Strings.toHtml(task.getDisplayName()),
                        Strings.toHtml(task.getName()),
                        Strings.getHtml("_edit")
                );
            }
        }
        append("""
                    </ul>
                </li>
                """);
    }

    public void appendPageHtmlEnd() {
        append("""
                                </ul>
                            </li>
                        </ul>
                    </section>
                </div>
                <script type="text/javascript">
                    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                </script>
                                
                """);
    }

}
