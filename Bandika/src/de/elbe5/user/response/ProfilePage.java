package de.elbe5.user.response;

import de.elbe5.base.Strings;
import de.elbe5.response.FormHtml;
import de.elbe5.response.HtmlIncludePage;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

public class ProfilePage extends HtmlIncludePage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        MessageHtml.appendMessageHtml(sb, rdata);
        sb.append(Strings.format("""
                        <div id="pageContent" class="viewArea">
                        <section class="contentTop">
                            <h1>
                                {1}
                            </h1>
                        </section>
                        <div class="row">
                            <section class="col-md-8 contentSection">
                                <div class="paragraph form">
                                """,
                Strings.getHtml("_profile")
        ));
        FormHtml.appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(user.getId()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_login"),Strings.toHtml(user.getLogin()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_title"), Strings.toHtml(user.getTitle()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_firstName"), Strings.toHtml(user.getFirstName()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_lastName"), Strings.toHtml(user.getLastName()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_notes"), Strings.toHtml(user.getNotes()));
        FormHtml.appendLineStart(sb, "", Strings.getHtml("_portrait"));
        if (user.hasPortrait()) {
            sb.append(Strings.format("""
                            <img src="/ctrl/user/showPortrait/{1}" alt="{2}"/>
                            """,
                    Integer.toString(user.getId()),
                    Strings.toHtml(user.getName())
            ));
        }
        FormHtml.appendLineEnd(sb);
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_address")
        ));
        FormHtml.appendTextLine(sb, Strings.getHtml("_street"),Strings.toHtml(user.getStreet()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_zipCode"), Strings.toHtml(user.getZipCode()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_city"), Strings.toHtml(user.getCity()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_country"), Strings.toHtml(user.getCountry()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_contact")
        ));
        FormHtml.appendTextLine(sb, Strings.getHtml("_email"), Strings.toHtml(user.getEmail()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_phone"), Strings.toHtml(user.getPhone()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_fax"), Strings.toHtml(user.getFax()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_mobile"), Strings.toHtml(user.getMobile()));
        sb.append(Strings.format("""
                                </div>
                            </section>
                            <aside class="col-md-4 asideSection">
                                <div class="section">
                                    <div class="paragraph form">
                                        <div>
                                            <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangePassword');">{1}
                                            </a>
                                        </div>
                                        <div>
                                            <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangeProfile');">{2}
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </aside>
                        </div>
                        </div>
                        """,
                Strings.getHtml("_changePassword"),
                Strings.getHtml("_changeProfile")));
    }
}
