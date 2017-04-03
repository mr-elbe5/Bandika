/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import de.bandika.user.UserData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

public class UserSearchData extends SearchData {

    protected static final int CONTEXT_LENGTH_NAME = 100;
    protected static final int CONTEXT_LENGTH_EMAIL = 100;

    protected String firstName = "";
    protected String lastName = "";
    protected String email = "";
    protected String emailContext = "";

    public UserSearchData() {
    }

    public UserSearchData(int id) {
        this.id = id;
    }

    public String getDataClass() {
        return UserData.DATAKEY;
    }

    public String getTypeIcon() {
        return "/_statics/images/s_user.png";
    }

    public String getLink() {
        return null;
    }

    public void setDoc() {
        super.setDoc();
        doc.add(new Field("firstName", firstName, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("lastName", lastName, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("email", email, Field.Store.YES, Field.Index.ANALYZED));
    }

    public void evaluateDoc() {
        super.evaluateDoc();
        if (doc == null)
            return;
        firstName = doc.get("firstName");
        lastName = doc.get("lastName");
        email = doc.get("email");
        setName();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setName() {
        if (firstName.length() == 0)
            name = lastName;
        name = firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailContext() {
        return emailContext;
    }

    public void setEmailContext(String emailContext) {
        this.emailContext = emailContext;
    }

    public void setContexts(Query query, Analyzer analyzer) {
        Highlighter highlighter = new Highlighter(new SearchContextFormatter(), new QueryScorer(query));
        String context = getContext(highlighter, analyzer, "name", CONTEXT_LENGTH_NAME);
        setNameContext(context);
        context = getContext(highlighter, analyzer, "email", CONTEXT_LENGTH_EMAIL);
        setEmailContext(context);
    }
}