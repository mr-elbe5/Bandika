/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page.fields;

import de.bandika.base.RequestError;
import de.bandika.base.XmlData;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Class MailField is the data class for mail forms.  <br>
 * Usage:
 */
public class MailField extends BaseField {

  protected String receiver = "";

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    receiver = rdata.getParamString(getIdentifier());
  }

	@Override
	public Element generateXml(Document doc, Element parent){
		Element elem= super.generateXml(doc,parent);
		XmlData.createCDATA(doc,elem,receiver);
		return elem;
	}

	@Override
	public void evaluateXml(Element node){
		super.evaluateXml(node);
		receiver=XmlData.getCData(node);
	}

}