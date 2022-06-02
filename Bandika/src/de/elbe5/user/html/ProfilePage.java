package de.elbe5.user.html;

import de.elbe5.response.HtmlIncludePage;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.Map;

public class ProfilePage extends HtmlIncludePage implements IFormBuilder, MessageHtml {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        appendMessageHtml(sb, rdata);
        append(sb, """
                        <div id="pageContent" class="viewArea">
                        <section class="contentTop">
                            <h1>$profile$</h1>
                        </section>
                        <div class="row">
                            <section class="col-md-8 contentSection">
                                <div class="paragraph form">
                                """,
                Map.ofEntries(
                        param("profile","_profile")
                )
        );
        appendTextLine(sb, getHtml("_id"), Integer.toString(user.getId()));
        appendTextLine(sb, getHtml("_login"),toHtml(user.getLogin()));
        appendTextLine(sb, getHtml("_title"), toHtml(user.getTitle()));
        appendTextLine(sb, getHtml("_firstName"), toHtml(user.getFirstName()));
        appendTextLine(sb, getHtml("_lastName"), toHtml(user.getLastName()));
        appendTextLine(sb, getHtml("_notes"), toHtml(user.getNotes()));
        appendLineStart(sb, "", getHtml("_portrait"));
        if (user.hasPortrait()) {
            append(sb, """
                            <img src="/ctrl/user/showPortrait/$id$" alt="$name$"/>
                            """,
                    Map.ofEntries(
                            param("id",user.getId()),
                            param("name",user.getName())
                    )
            );
        }
        appendLineEnd(sb);
        append(sb,"""
                <h3>$address$</h3>
                """,
                Map.ofEntries(
                        param("address","_address")
                )
        );
        appendTextLine(sb, getHtml("_street"),toHtml(user.getStreet()));
        appendTextLine(sb, getHtml("_zipCode"), toHtml(user.getZipCode()));
        appendTextLine(sb, getHtml("_city"), toHtml(user.getCity()));
        appendTextLine(sb, getHtml("_country"), toHtml(user.getCountry()));
        append(sb,"""
                <h3>$contact$</h3>
                """,
                Map.ofEntries(
                        param("contact","_contact")
                )
        );
        appendTextLine(sb, getHtml("_email"), toHtml(user.getEmail()));
        appendTextLine(sb, getHtml("_phone"), toHtml(user.getPhone()));
        appendTextLine(sb, getHtml("_fax"), toHtml(user.getFax()));
        appendTextLine(sb, getHtml("_mobile"), toHtml(user.getMobile()));
        append(sb,"""
                                </div>
                            </section>
                            <aside class="col-md-4 asideSection">
                                <div class="section">
                                    <div class="paragraph form">
                                        <div>
                                            <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangePassword');">$changePassword$
                                            </a>
                                        </div>
                                        <div>
                                            <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangeProfile');">$changeProfile$
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </aside>
                        </div>
                        </div>
                        """,
                Map.ofEntries(
                        param("changePassword","_changePassword"),
                        param("changeProfile","_changeProfile")
                )
        );
    }
}
