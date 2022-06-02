package de.elbe5.company.html;

import de.elbe5.company.CompanyData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;
import java.util.Map;

public class EditCompanyPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        CompanyData company = rdata.getSessionObject("companyData",CompanyData.class);
        List<UserData> users = UserBean.getInstance().getCompanyUsers(company.getId());
        String url = "/ctrl/company/saveCompany/" + company.getId();
        appendModalStart(getHtml("_editUser"));
        appendFormStart(sb, url, "companyform", true);
        appendModalBodyStart(getHtml("_settings"));
        appendHiddenField(sb, "companyId", Integer.toString(company.getId()));
        appendTextLine(sb, getHtml("_id"), Integer.toString(company.getId()));
        appendTextInputLine(sb, rdata.hasFormErrorField("name"), "name", getHtml("_name"), true, toHtml(company.getName()));
        append(sb, """
                <h3>$address$
                </h3>
                """,
                Map.ofEntries(
                        param("address","_address")
                )
        );
        appendTextInputLine(sb, "street", getHtml("_street"), toHtml(company.getStreet()));
        appendTextInputLine(sb, "zipCode", getHtml("_zipCode"), toHtml(company.getZipCode()));
        appendTextInputLine(sb, "city", getHtml("_city"), toHtml(company.getCity()));
        appendTextInputLine(sb, "country", getHtml("_country"), toHtml(company.getCountry()));
        append(sb, """
                <h3>$contact$
                </h3>
                """,
                Map.ofEntries(
                        param("contact","_contact")
                )
        );
        appendTextInputLine(sb, "email", getHtml("_email"), toHtml(company.getEmail()));
        appendTextInputLine(sb, "phone", getHtml("_phone"), toHtml(company.getPhone()));
        appendTextInputLine(sb, "fax", getHtml("_fax"), toHtml(company.getFax()));
        append(sb, """
                <h3>$employees$
                </h3>
                """,
                Map.ofEntries(
                        param("employees","_employees")
                )
        );
        for (UserData user : users) {
            appendTextLine(sb, "", toHtml(user.getName()));
        }
        appendModalFooter(getHtml("_cancel"), getHtml("_save"));
        appendFormEnd(sb, url, "companyform", true, true, "");
        appendModalEnd();
    }
}
