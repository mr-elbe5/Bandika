/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.document;

import java.util.ArrayList;

/**
 * Class DocumentSelectData is the data class for document selection. <br>
 * Usage:
 */
public class DocumentSelectData {

  protected ArrayList<DocumentData> documents = new ArrayList<DocumentData>();
  protected boolean forFck = false;

  /**
   * Method getDocuments returns the documents of this DocumentSelectData object.
   *
   * @return the documents (type ArrayList<DocumentData>) of this DocumentSelectData object.
   */
  public ArrayList<DocumentData> getDocuments() {
    return documents;
  }

  /**
   * Method setDocuments sets the documents of this DocumentSelectData object.
   *
   * @param documents the documents of this DocumentSelectData object.
   */
  public void setDocuments(ArrayList<DocumentData> documents) {
    this.documents = documents;
  }

  /**
   * Method isForFck returns the forFck of this DocumentSelectData object.
   *
   * @return the forFck (type boolean) of this DocumentSelectData object.
   */
  public boolean isForFck() {
    return forFck;
  }

  /**
   * Method setForFck sets the forFck of this DocumentSelectData object.
   *
   * @param forFck the forFck of this DocumentSelectData object.
   */
  public void setForFck(boolean forFck) {
    this.forFck = forFck;
  }

}