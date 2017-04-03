/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content;

import de.net25.base.BaseData;
import de.net25.base.RequestError;
import de.net25.content.fields.BaseField;
import de.net25.resources.statics.Statics;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;


/**
 * Class ParagraphData is the data class for a single paragraph in a content page. <br>
 * Usage:
 */
public class ParagraphData extends BaseData {

  protected int contentId;
  protected int ranking = 0;
  protected int templateId;

  protected HashMap<String, BaseField> fields = new HashMap<String, BaseField>();

  /**
   * Method getContentId returns the contentId of this ParagraphData object.
   *
   * @return the contentId (type int) of this ParagraphData object.
   */
  public int getContentId() {
    return contentId;
  }

  /**
   * Method setContentId sets the contentId of this ParagraphData object.
   *
   * @param contentId the contentId of this ParagraphData object.
   */
  public void setContentId(int contentId) {
    this.contentId = contentId;
  }

  /**
   * Method getRanking returns the ranking of this ParagraphData object.
   *
   * @return the ranking (type int) of this ParagraphData object.
   */
  public int getRanking() {
    return ranking;
  }

  /**
   * Method setRanking sets the ranking of this ParagraphData object.
   *
   * @param ranking the ranking of this ParagraphData object.
   */
  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  /**
   * Method getTemplateId returns the templateId of this ParagraphData object.
   *
   * @return the templateId (type int) of this ParagraphData object.
   */
  public int getTemplateId() {
    return templateId;
  }

  /**
   * Method getTemplateUrl returns the templateUrl of this ParagraphData object.
   *
   * @return the templateUrl (type String) of this ParagraphData object.
   */
  public String getTemplateUrl() {
    return "/" + Statics.TEMPLATE_DIR + "/tpl" + getTemplateId() + ".jsp";
  }

  /**
   * Method setTemplateId sets the templateId of this ParagraphData object.
   *
   * @param templateId the templateId of this ParagraphData object.
   */
  public void setTemplateId(int templateId) {
    this.templateId = templateId;
  }

  /**
   * Method ensureFields
   *
   * @param fieldDescriptions of type String[][]
   */
  public void ensureFields(String[][] fieldDescriptions) {
    for (String[] fieldDescription : fieldDescriptions) {
      String fieldName = fieldDescription[0];
      if (fields.get(fieldName) == null) {
        String fieldType = fieldDescription[1];
        BaseField field = BaseField.getNewBaseField(fieldType);
        field.setName(fieldName);
        field.setParagraphId(getId());
        fields.put(fieldName, field);
      }
    }
  }

  /**
   * Method getFields returns the fields of this ParagraphData object.
   *
   * @return the fields (type HashMap<String, BaseField>) of this ParagraphData object.
   */
  public HashMap<String, BaseField> getFields() {
    return fields;
  }

  /**
   * Method getField
   *
   * @param name of type String
   * @return BaseField
   */
  public BaseField getField(String name) {
    return fields.get(name);
  }

  /**
   * Method getFieldHtml
   *
   * @param name       of type String
   * @param locale     of type Locale
   * @param isEditMode of type boolean
   * @return String
   */
  public String getFieldHtml(String name, Locale locale, boolean isEditMode) {
    BaseField field = getField(name);
    if (field == null)
      return "";
    if (isEditMode)
      return field.getEditHtml(locale);
    return field.getHtml(locale);
  }

  /**
   * Method getDocumentUsage
   *
   * @param list of type HashSet<Integer>
   */
  public void getDocumentUsage(HashSet<Integer> list) {
    for (BaseField field : fields.values()) {
      field.getDocumentUsage(list);
    }
  }

  /**
   * Method getImageUsage
   *
   * @param list of type HashSet<Integer>
   */
  public void getImageUsage(HashSet<Integer> list) {
    for (BaseField field : fields.values()) {
      field.getImageUsage(list);
    }
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   * @return boolean
   */
  @Override
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    for (BaseField field : fields.values()) {
      field.readRequestData(rdata, sdata, err);
    }
    return err.isEmpty();
  }

}
