package de.elbe5.user.html;

import de.elbe5.user.CompanyData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class EditCompanyPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        CompanyData company = rdata.getSessionObject("companyData", CompanyData.class);
        List<UserData> users = UserBean.getInstance().getCompanyUsers(company.getId());
        String url = "/ctrl/company/saveCompany/" + company.getId();
        appendModalStart(getString("_editUser"));
        appendFormStart(sb, url, "companyform", true);
        appendModalBodyStart(getString("_settings"));
        appendHiddenField(sb, "companyId", Integer.toString(company.getId()));
        appendTextLine(sb, getString("_id"), Integer.toString(company.getId()));
        appendTextInputLine(sb, rdata.hasFormErrorField("name"), "name", getString("_name"), true, company.getName());
        append(sb, """
                <h3>{{_address}}</h3>
                """, null);
        appendTextInputLine(sb, "street", getString("_street"), company.getStreet());
        appendTextInputLine(sb, "zipCode", getString("_zipCode"), company.getZipCode());
        appendTextInputLine(sb, "city", getString("_city"), company.getCity());
        appendTextInputLine(sb, "country", getString("_country"), company.getCountry());
        append(sb, """
                <h3>{{_contact}}</h3>
                """, null);
        appendTextInputLine(sb, "email", getString("_email"), company.getEmail());
        appendTextInputLine(sb, "phone", getString("_phone"), company.getPhone());
        appendTextInputLine(sb, "fax", getString("_fax"), company.getFax());
        append(sb, """
                <h3>{{_employees}}</h3>
                """, null);
        for (UserData user : users) {
            appendTextLine(sb, "", user.getName());
        }
        appendModalFooter(getString("_cancel"), getString("_save"));
        appendFormEnd(sb, url, "companyform", true, true, "");
        appendModalEnd();
    }
}
