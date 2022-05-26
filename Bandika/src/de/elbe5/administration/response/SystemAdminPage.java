package de.elbe5.administration.response;

import de.elbe5.base.Strings;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class SystemAdminPage implements IAdminPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        appendHtmlStart(sb, rdata);
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            appendRestart(sb, rdata);
            appendCachesStart(sb, rdata);
            appendUserCache(sb, rdata);
            appendTemplateCache(sb,rdata);
            appendCachesEnd(sb);
            appendTimerList(sb, rdata);
        }
        appendHtmlEnd(sb);
    }

    public void appendHtmlStart(StringBuilder sb, RequestData rdata) {
        sb.append("""
                <div id="pageContent">
                """);
        MessageHtml.appendMessageHtml(sb, rdata);
        sb.append(Strings.format("""
                        <section class="treeSection">
                            <ul class="tree">
                                <li class="open">
                                    <a class="treeRoot">{1}
                                    </a>
                                    <ul>
                                    """,
                Strings.getHtml("_system")
        ));
    }

    public void appendRestart(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                        <li>
                            <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');">{1}
                            </a>
                        </li>
                        """,
                Strings.getHtml("_restart")
        ));
    }

    public void appendCachesStart(StringBuilder sb, RequestData rdata) {

        sb.append(Strings.format("""
                    <li class="open">
                        <a>{1}
                        </a>
                        <ul>
                """,
                Strings.getHtml("_caches")
                ));
    }

    void appendUserCache(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadUserCache" title="{2}"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_userCache"),
                Strings.getHtml("_reload")
        ));
    }

    void appendTemplateCache(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                        <li>
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-recycle" href="/ctrl/admin/reloadTemplateCache" title="{2}"></a>
                            </div>
                        </li>
                """,
                Strings.getHtml("_templateCache"),
                Strings.getHtml("_reload")
        ));
    }

    public void appendCachesEnd(StringBuilder sb) {
        sb.append("""
                    </ul>
                </li>
                """
        );
    }

    void appendTimerList(StringBuilder sb, RequestData rdata) {
        Map<String, TimerTaskData> tasks = null;
        try {
            Timer timerCache = Timer.getInstance();
            tasks = timerCache.getTasks();
        } catch (Exception ignore) {
        }
        sb.append(Strings.format("""
                        <li class="open">
                            {1}
                            <ul>
                            """,
                Strings.getHtml("_timers")
        ));
        if (tasks != null) {
            for (TimerTaskData task : tasks.values()) {
                sb.append(Strings.format("""
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
                ));
            }
        }
        sb.append("""
                    </ul>
                </li>
                """);
    }

    public void appendHtmlEnd(StringBuilder sb) {
        sb.append("""
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
