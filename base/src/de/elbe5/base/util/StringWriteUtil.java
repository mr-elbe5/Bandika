/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.util;

import java.io.IOException;
import java.io.Writer;

public class StringWriteUtil {

    private Writer writer;

    public StringWriteUtil(Writer writer){
        assert writer!=null;
        this.writer=writer;
    }

    public void write(String src, String... params) throws IOException {
        int p1=0;
        int p2;
        String placeholder;
        for (int i=0;i<params.length;i++){
            placeholder="{"+(i+1)+"}";
            p2=src.indexOf(placeholder, p1);
            if (p2==-1)
                break;
            writer.write(src.substring(p1,p2));
            writer.write(params[i]);
            p1=p2+placeholder.length();
        }
        writer.write(src.substring(p1));
    }

    public void write(String s) throws IOException {
        writer.write(s);
    }

}
