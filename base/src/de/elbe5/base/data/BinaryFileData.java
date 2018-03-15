/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinaryFileData extends BinaryFileBaseData {

    protected byte[] bytes = null;

    public BinaryFileData() {
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setBytesFromStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer, 0, 4096)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        bytes = outputStream.toByteArray();
    }

}
