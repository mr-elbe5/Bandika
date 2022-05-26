package de.elbe5.company.response;

import de.elbe5.base.Strings;
import de.elbe5.company.CompanyData;
import de.elbe5.response.FormHtml;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class EditCompanyPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        CompanyData company = rdata.getSessionObject("companyData",CompanyData.class);
        List<UserData> users = UserBean.getInstance().getCompanyUsers(company.getId());
        String url = "/ctrl/company/saveCompany/" + company.getId();
        appendModalStart(Strings.getHtml("_editUser"));
        FormHtml.appendFormStart(sb, url, "companyform", true);
        appendModalBodyStart(Strings.getHtml("_settings"));
        append("""
                        <input type="hidden" name="companyId" value="{1}"/>
                        """,
                Integer.toString(company.getId())
        );
        FormHtml.appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(company.getId()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("name"), "name", Strings.getHtml("_name"), true, Strings.toHtml(company.getName()));
        append("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_address")
        );
        FormHtml.appendTextInputLine(sb, "street", Strings.getHtml("_street"), Strings.toHtml(company.getStreet()));
        FormHtml.appendTextInputLine(sb, "zipCode", Strings.getHtml("_zipCode"), Strings.toHtml(company.getZipCode()));
        FormHtml.appendTextInputLine(sb, "city", Strings.getHtml("_city"), Strings.toHtml(company.getCity()));
        FormHtml.appendTextInputLine(sb, "country", Strings.getHtml("_country"), Strings.toHtml(company.getCountry()));
        append("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_contact")
        );
        FormHtml.appendTextInputLine(sb, "email", Strings.getHtml("_email"), Strings.toHtml(company.getEmail()));
        FormHtml.appendTextInputLine(sb, "phone", Strings.getHtml("_phone"), Strings.toHtml(company.getPhone()));
        FormHtml.appendTextInputLine(sb, "fax", Strings.getHtml("_fax"), Strings.toHtml(company.getFax()));
        append("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_employees")
        );
        for (UserData user : users) {
            FormHtml.appendTextLine(sb, "", Strings.toHtml(user.getName()));
        }
        appendModalFooter(Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "companyform", true, true, "");
        appendModalEnd();
    }
}
