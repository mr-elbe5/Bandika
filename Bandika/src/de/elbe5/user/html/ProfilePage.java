package de.elbe5.user.html;

import de.elbe5.response.HtmlIncludePage;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.Map;

public class ProfilePage extends HtmlIncludePage implements IFormBuilder, MessageHtml {

    final static String startHtml = """
            <div id="pageContent" class="viewArea">
                <section class="contentTop">
                    <h1>{{_profile}}</h1>
                </section>
                <div class="row">
                    <section class="col-md-8 contentSection">
                        <div class="paragraph form">
            """;
    final static String portraitHtml = """
                            <img src="/ctrl/user/showPortrait/{{id}}" alt="{{name}}"/>
            """;
    final static String endHtml = """
                        </div>
                    </section>
                    <aside class="col-md-4 asideSection">
                        <div class="section">
                            <div class="paragraph form">
                                <div>
                                    <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangePassword');">{{_changePassword}}
                                    </a>
                                </div>
                                <div>
                                    <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangeProfile');">{{_changeProfile}}
                                    </a>
                                </div>
                            </div>
                        </div>
                    </aside>
                </div>
            </div>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        appendMessageHtml(sb, rdata);
        append(sb, startHtml, null);
        appendTextLine(sb, getString("_id"), Integer.toString(user.getId()));
        appendTextLine(sb, getString("_login"), user.getLogin());
        appendTextLine(sb, getString("_title"), user.getTitle());
        appendTextLine(sb, getString("_firstName"), user.getFirstName());
        appendTextLine(sb, getString("_lastName"), user.getLastName());
        appendTextLine(sb, getString("_notes"), user.getNotes());
        appendLineStart(sb, "", getString("_portrait"));
        if (user.hasPortrait()) {
            append(sb, portraitHtml,
                    Map.ofEntries(
                            Map.entry("id", Integer.toString(user.getId())),
                            Map.entry("name", toHtml(user.getName()))));
        }
        appendLineEnd(sb);
        append(sb, """
                <h3>{{_address}}</h3>
                """, null);
        appendTextLine(sb, getString("_street"), user.getStreet());
        appendTextLine(sb, getString("_zipCode"), user.getZipCode());
        appendTextLine(sb, getString("_city"), user.getCity());
        appendTextLine(sb, getString("_country"), user.getCountry());
        append(sb, """
                <h3>{{_contact}}</h3>
                """, null);
        appendTextLine(sb, getString("_email"), user.getEmail());
        appendTextLine(sb, getString("_phone"), user.getPhone());
        appendTextLine(sb, getString("_fax"), user.getFax());
        appendTextLine(sb, getString("_mobile"), user.getMobile());
        append(sb, endHtml, null);
    }
}
