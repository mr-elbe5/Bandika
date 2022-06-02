package de.elbe5.administration.html;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class SystemAdminPage extends AdminPage{

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
        append(sb, """
                <div id="pageContent">
                """);
        appendMessageHtml(sb, rdata);
        append(sb, """
                        <section class="treeSection">
                            <ul class="tree">
                                <li class="open">
                                    <a class="treeRoot">$system$
                                    </a>
                                    <ul>
                                    """,
                Map.ofEntries(
                        param("system","_system")
                )
        );
    }

    public void appendRestart(RequestData rdata) {
        append(sb, """
                        <li>
                            <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');">$restart$
                            </a>
                        </li>
                        """,
                Map.ofEntries(
                        param("restart","_restart")
                )
        );
    }

    public void appendCachesStart(RequestData rdata) {

        append(sb, """
                    <li class="open">
                        <a>$caches$</a>
                        <ul>
                """,
                Map.ofEntries(
                        param("caches","_caches")
                )
        );
    }

    void appendUserCache(RequestData rdata) {
        append(sb, """
                        <li>
                            <span>$userCache$</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadUserCache" title="$reload$"></a>
                            </div>
                        </li>
                """,
                Map.ofEntries(
                        param("userCache","_userCache"),
                        param("reload","_reload")
                )
        );
    }

    void appendTemplateCache(RequestData rdata) {
        append(sb, """
                        <li>
                            <span>$templateCache$</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadTemplateCache" title="$reload$"></a>
                            </div>
                        </li>
                """,
                Map.ofEntries(
                        param("templateCache","_templateCache"),
                        param("reload","_reload")
                )
        );
    }

    public void appendCachesEnd() {
        append(sb, """
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
        append(sb, """
                        <li class="open">
                            $timers$
                            <ul>
                            """,
                Map.ofEntries(
                        param("timers","_timers")
                )
        );
        if (tasks != null) {
            for (TimerTaskData task : tasks.values()) {
                append(sb,"""
                                <li>
                                    <span>$displayName$</span>
                                    <div class="icons">
                                        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/timer/openEditTimerTask?timerName=$name$');" title="$edit$"></a>
                                    </div>
                                </li>
                                """,
                        Map.ofEntries(
                                param("displayName",task.getDisplayName()),
                                param("name",task.getName()),
                                param("edit","_edit")
                        )
                );
            }
        }
        append(sb,"""
                    </ul>
                </li>
                """);
    }

    public void appendPageHtmlEnd() {
        append(sb, """
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
