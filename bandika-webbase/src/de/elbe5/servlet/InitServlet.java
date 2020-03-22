/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.base.crypto.PBKDF2Encryption;
import de.elbe5.base.log.Log;
import de.elbe5.request.ResponseCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class InitServlet extends WebServlet {

    @Override
    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(ResponseCode.NOT_FOUND);
    }

    protected void generatePassword() {
        try {
            String salt = PBKDF2Encryption.generateSaltBase64();
            String password = PBKDF2Encryption.getEncryptedPasswordBase64("pass", salt);
            Log.info("salt= " + salt + " password= " + password);
        } catch (Exception e) {
            Log.warn("password generation failed");
        }
    }


}