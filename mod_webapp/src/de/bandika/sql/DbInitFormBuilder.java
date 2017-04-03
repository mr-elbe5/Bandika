/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.sql;

/**
 *
 */
public class DbInitFormBuilder {

    public static String POSTGRES_CLASS = "org.postgresql.Driver";
    public static String POSTGRES_URL = "jdbc:postgresql://localhost/mydb";

    public static String getSAVEDHTML() {
        return SAVEDHTML;
    }

    public static String getCONFIGHTML(String classDefault, String urlDefault) {
        return String.format(CONFIGHTML, classDefault, urlDefault);
    }

    private static String SAVEDHTML =
            "<html>\n" +
                    "<head><title>Database Configuration ok</title></head>\n" +
                    "<body>Configuration saved</body>\n" +
                    "</html>";

    private static String CONFIGHTML =
            "<html>\n" +
                    "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/><title>Database Configuration</title></head>\n" +
                    "<body>\n" +
                    "<div style=\"width:600px;margin-top:20px;margin-left:auto;margin-right:auto;border:solid 1px #aaa;padding:10px;\"><div style=\"padding:0\">\n" +
                    "<div style=\"font-weight: 700; margin-bottom: 20px;\">Please fill all necessary values:</div>\n" +
                    "<div>\n" +
                    "<form action=\"/std.srv\" method=\"post\" name=\"form\" accept-charset=\"UTF-8\">\n" +
                    "<input type=\"hidden\" name=\"act\" value=\"initDb\"/>\n" +
                    "<table style=\"width:100%%\">\n" +
                    "<colgroup>\n" +
                    "<col style=\"width:100px\"/>\n" +
                    "<col style=\"width:400px\"/>\n" +
                    "</colgroup>\n" +
                    "<tr><td><label for=\"dbClass\">Driver class</label></td><td><input type=\"text\" id=\"dbClass\" name=\"dbClass\" value=\"%s\" maxlength=\"255\" style=\"width:250px\"/></td></tr>\n" +
                    "<tr><td><label for=\"dbPort\">Database Url</label></td><td><input type=\"text\" id=\"dbUrl\" name=\"dbUrl\" value=\"%s\" maxlength=\"255\" style=\"width:250px\"/></td></tr>\n" +
                    "<tr><td><label for=\"dbUser\">User</label></td><td><input type=\"text\" id=\"dbUser\" name=\"dbUser\" maxlength=\"255\" style=\"width:250px\"/></td></tr>\n" +
                    "<tr><td><label for=\"dbPwd\">Password</label></td><td><input type=\"password\" id=\"dbPwd\" name=\"dbPwd\" maxlength=\"255\" style=\"width:250px\"/></td></tr>\n" +
                    "</table>\n" +
                    "<div style=\"margin-top:10px\"><input type=\"submit\" value=\"Save\"/></div>\n" +
                    "</form>\n" +
                    "</div>\n" +
                    "</div></div>\n" +
                    "</body>\n" +
                    "</html>";
}
