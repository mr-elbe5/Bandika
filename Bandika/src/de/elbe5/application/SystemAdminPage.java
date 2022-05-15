package de.elbe5.application;

import de.elbe5.base.Strings;
import de.elbe5.layout.MessageTag;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.timer.Timer;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class SystemAdminPage implements IAdminIncludePage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        sb.append("""
                <div id="pageContent">
                """);
        MessageTag.appendMessageHtml(sb, rdata);
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
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            sb.append(Strings.format("""
                            <li>
                                <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');">{1}
                                </a>
                            <li class="open">
                                <a>{2}
                                </a>
                                <ul>
                                    <li>
                                        <span>{3}</span>
                                        <div class="icons">
                                            <a class="icon fa fa-recycle" href="/ctrl/admin/reloadUserCache" title="{4}"></a>
                                        </div>
                                    </li>
                                    <li>
                                        <span>{5}</span>
                                        <div class="icons">
                                            <a class="icon fa fa-recycle" href="/ctrl/admin/reloadContentCache" title="{6}>"></a>
                                        </div>
                                    </li>
                                    <li>
                                        <span>{7}</span>
                                        <div class="icons">
                                            <a class="icon fa fa-recycle" href="/ctrl/admin/clearPreviewCache" title="{8}"></a>
                                        </div>
                                    </li>
                                </ul>
                            </li>
                            """,
                    Strings.getHtml("_restart"),
                    Strings.getHtml("_caches"),
                    Strings.getHtml("_userCache"),
                    Strings.getHtml("_reload"),
                    Strings.getHtml("_contentCache"),
                    Strings.getHtml("_reload"),
                    Strings.getHtml("_previewCache"),
                    Strings.getHtml("_reload")
            ));
        }
        appendTimerList(sb, rdata);
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

}
