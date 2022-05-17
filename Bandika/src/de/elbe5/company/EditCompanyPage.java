package de.elbe5.company;

import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class EditCompanyPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        CompanyData company = rdata.getSessionObject("companyData",CompanyData.class);
        List<UserData> users = UserBean.getInstance().getCompanyUsers(company.getId());
        String url = "/ctrl/company/saveCompany/" + company.getId();
        appendModalStart(sb, Strings.getHtml("_editUser"));
        Form.appendFormStart(sb, url, "companyform", true);
        appendModalBodyStart(sb, rdata, Strings.getHtml("_settings"));
        sb.append(Strings.format("""
                        <input type="hidden" name="companyId" value="{1}"/>
                        """,
                Integer.toString(company.getId())
        ));
        Form.appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(company.getId()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("name"), "name", Strings.getHtml("_name"), true, Strings.toHtml(company.getName()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_address")));
        Form.appendTextInputLine(sb, "street", Strings.getHtml("_street"), Strings.toHtml(company.getStreet()));
        Form.appendTextInputLine(sb, "zipCode", Strings.getHtml("_zipCode"), Strings.toHtml(company.getZipCode()));
        Form.appendTextInputLine(sb, "city", Strings.getHtml("_city"), Strings.toHtml(company.getCity()));
        Form.appendTextInputLine(sb, "country", Strings.getHtml("_country"), Strings.toHtml(company.getCountry()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_contact")));
        Form.appendTextInputLine(sb, "email", Strings.getHtml("_email"), Strings.toHtml(company.getEmail()));
        Form.appendTextInputLine(sb, "phone", Strings.getHtml("_phone"), Strings.toHtml(company.getPhone()));
        Form.appendTextInputLine(sb, "fax", Strings.getHtml("_fax"), Strings.toHtml(company.getFax()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_employees")));
        for (UserData user : users) {
            Form.appendTextLine(sb, "", Strings.toHtml(user.getName()));
        }
        appendModalFooter(sb, Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "companyform", true, true, "");
        appendModalEnd(sb);
    }
}
