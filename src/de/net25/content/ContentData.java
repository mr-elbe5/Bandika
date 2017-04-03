/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content;

import de.net25.base.RequestError;
import de.net25.base.Logger;
import de.net25.base.SizedData;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.user.ContentRightData;

import java.util.*;

/**
 * Class ContentData is the data class for content pages. <br>
 * Usage:
 */
public class ContentData extends SizedData implements Comparable {

  public static final int EDIT_CONTENT = 1;
  public static final int EDIT_METADATA = 2;
  public static final int EDIT_PARENT = 3;


  protected int parent = 0;
  protected int ranking = 0;
  protected String name = "";
  protected String description = "";
  protected String metaKeywords = "";
  protected boolean restricted = false;
  protected int state = 0;
  protected int authorId = 0;
  protected boolean showMenu = true;
  protected ContentRightData rightData = new ContentRightData();

  protected ArrayList<ContentData> children = null;
  protected int level = 0;
  protected ArrayList<ParagraphData> paragraphs = new ArrayList<ParagraphData>();
  protected ParagraphData editParagraph = null;

  /**
   * Constructor ContentData creates a new ContentData instance.
   */
  public ContentData() {
  }

  /**
   * Method compareTo
   *
   * @param o of type Object
   * @return int
   */
  public int compareTo(Object o) {
    if (!(o instanceof ContentData))
      return 0;
    ContentData node = (ContentData) o;
    return ranking - node.ranking;
  }

  /**
   * Method getParent returns the parent of this ContentData object.
   *
   * @return the parent (type int) of this ContentData object.
   */
  public int getParent() {
    return parent;
  }

  /**
   * Method setParent sets the parent of this ContentData object.
   *
   * @param parent the parent of this ContentData object.
   */
  public void setParent(int parent) {
    if (parent == id) {
      Logger.error(getClass(), "parent must not be this");
      this.parent = 0;
    } else
      this.parent = parent;
  }

  /**
   * Method setParent sets the parent of this ContentData object.
   *
   * @param parent the parent of this ContentData object.
   * @param locale of type Locale
   */
  public void setParent(int parent, Locale locale) {
    if (parent == id) {
      Logger.error(getClass(), "parent must not be this");
      this.parent = Statics.getContentHomeId(locale);
    } else
      this.parent = parent;
  }

  /**
   * Method getRanking returns the ranking of this ContentData object.
   *
   * @return the ranking (type int) of this ContentData object.
   */
  public int getRanking() {
    return ranking;
  }

  /**
   * Method setRanking sets the ranking of this ContentData object.
   *
   * @param ranking the ranking of this ContentData object.
   */
  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  /**
   * Method getName returns the name of this ContentData object.
   *
   * @return the name (type String) of this ContentData object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method setName sets the name of this ContentData object.
   *
   * @param name the name of this ContentData object.
   */
  public void setName(String name) {
    this.name = name == null ? "" : name;
  }

  /**
   * Method getDescription returns the description of this ContentData object.
   *
   * @return the description (type String) of this ContentData object.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Method setDescription sets the description of this ContentData object.
   *
   * @param description the description of this ContentData object.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Method getMetaKeywords returns the metaKeywords of this ContentData object.
   *
   * @return the metaKeywords (type String) of this ContentData object.
   */
  public String getMetaKeywords() {
    return metaKeywords;
  }

  /**
   * Method setMetaKeywords sets the metaKeywords of this ContentData object.
   *
   * @param metaKeywords the metaKeywords of this ContentData object.
   */
  public void setMetaKeywords(String metaKeywords) {
    this.metaKeywords = metaKeywords;
  }

  /**
   * Method isRestricted returns the restricted of this ContentData object.
   *
   * @return the restricted (type boolean) of this ContentData object.
   */
  public boolean isRestricted() {
    return restricted;
  }

  /**
   * Method setRestricted sets the restricted of this ContentData object.
   *
   * @param restricted the restricted of this ContentData object.
   */
  public void setRestricted(boolean restricted) {
    this.restricted = restricted;
  }

  /**
   * Method getState returns the state of this ContentData object.
   *
   * @return the state (type int) of this ContentData object.
   */
  public int getState() {
    return state;
  }

  /**
   * Method setState sets the state of this ContentData object.
   *
   * @param state the state of this ContentData object.
   */
  public void setState(int state) {
    this.state = state;
  }

  /**
   * Method getAuthorId returns the authorId of this ContentData object.
   *
   * @return the authorId (type int) of this ContentData object.
   */
  public int getAuthorId() {
    return authorId;
  }

  /**
   * Method setAuthorId sets the authorId of this ContentData object.
   *
   * @param authorId the authorId of this ContentData object.
   */
  public void setAuthorId(int authorId) {
    this.authorId = authorId;
  }

  /**
   * Method isShowMenu returns the showMenu of this ContentData object.
   *
   * @return the showMenu (type boolean) of this ContentData object.
   */
  public boolean isShowMenu() {
    return showMenu;
  }

  /**
   * Method setShowMenu sets the showMenu of this ContentData object.
   *
   * @param showMenu the showMenu of this ContentData object.
   */
  public void setShowMenu(boolean showMenu) {
    this.showMenu = showMenu;
  }

  /**
   * Method getRightData returns the rightData of this ContentData object.
   *
   * @return the rightData (type ContentRightData) of this ContentData object.
   */
  public ContentRightData getRightData() {
    return rightData;
  }

  /**
   * Method setRightData sets the rightData of this ContentData object.
   *
   * @param rightData the rightData of this ContentData object.
   */
  public void setRightData(ContentRightData rightData) {
    this.rightData = rightData;
  }

  /**
   * Method hasGroupRight
   *
   * @param id    of type int
   * @param right of type int
   * @return boolean
   */
  public boolean hasGroupRight(int id, int right) {
    return rightData != null && rightData.hasGroupRight(id, right);
  }

  /**
   * Method hasUserRight
   *
   * @param id    of type int
   * @param right of type int
   * @return boolean
   */
  public boolean hasUserRight(int id, int right) {
    return rightData != null && rightData.hasUserRight(id, right);
  }

  /**
   * Method hasUserReadRight
   *
   * @param userId of type int
   * @return boolean
   */
  public boolean hasUserReadRight(int userId) {
    return !restricted || hasUserRight(userId, Statics.RIGHT_READ);
  }

  /**
   * Method hasGroupReadRight
   *
   * @param groupId of type int
   * @return boolean
   */
  public boolean hasGroupReadRight(int groupId) {
    return !restricted || hasGroupRight(groupId, Statics.RIGHT_READ);
  }

  /**
   * Method getChildren returns the children of this ContentData object.
   *
   * @return the children (type ArrayList<ContentData>) of this ContentData object.
   */
  public ArrayList<ContentData> getChildren() {
    return children;
  }

  /**
   * Method setChildren sets the children of this ContentData object.
   *
   * @param children the children of this ContentData object.
   */
  public void setChildren(ArrayList<ContentData> children) {
    this.children = children;
  }

  /**
   * Method setAsChild sets the asChild of this ContentData object.
   *
   * @param allNodes the asChild of this ContentData object.
   */
  public void setAsChild(HashMap<Integer, ContentData> allNodes) {
    if (parent == 0)
      return;
    ContentData par = allNodes.get(parent);
    if (par != null) {
      if (par.getChildren() == null)
        par.setChildren(new ArrayList<ContentData>());
      par.getChildren().add(this);
    }
  }

  /**
   * Method sortChildren
   */
  public void sortChildren() {
    if (children == null)
      return;
    Collections.sort(children);
    for (ContentData child : children) {
      child.setLevel(getLevel() + 1);
      child.sortChildren();
    }
  }

  /**
   * Method getLevel returns the level of this ContentData object.
   *
   * @return the level (type int) of this ContentData object.
   */
  public int getLevel() {
    return level;
  }

  /**
   * Method setLevel sets the level of this ContentData object.
   *
   * @param level the level of this ContentData object.
   */
  public void setLevel(int level) {
    this.level = level;
  }

  /**
   * Method getParagraphs returns the paragraphs of this ContentData object.
   *
   * @return the paragraphs (type ArrayList<ParagraphData>) of this ContentData object.
   */
  public ArrayList<ParagraphData> getParagraphs() {
    return paragraphs;
  }

  /**
   * Method setParagraphs sets the paragraphs of this ContentData object.
   *
   * @param paragraphs the paragraphs of this ContentData object.
   */
  public void setParagraphs(ArrayList<ParagraphData> paragraphs) {
    this.paragraphs = paragraphs;
  }

  /**
   * Method getParagraphById ...
   *
   * @param pid of type int
   * @return ParagraphData
   */
  public ParagraphData getParagraphById(int pid) {
    for (ParagraphData pdata : paragraphs) {
      if (pdata.getId() == pid)
        return pdata;
    }
    return null;
  }

  /**
   * Method getEditParagraph returns the editParagraph of this ContentData object.
   *
   * @return the editParagraph (type ParagraphData) of this ContentData object.
   */
  public ParagraphData getEditParagraph() {
    return editParagraph;
  }

  /**
   * Method getEditParagraphIdx returns the editParagraphIdx of this ContentData object.
   *
   * @return the editParagraphIdx (type int) of this ContentData object.
   */
  public int getEditParagraphIdx() {
    for (int i = 0; i < paragraphs.size(); i++)
      if (paragraphs.get(i) == editParagraph)
        return i;
    return -1;
  }

  /**
   * Method setEditParagraph sets the editParagraph of this ContentData object.
   *
   * @param editParagraph the editParagraph of this ContentData object.
   */
  public void setEditParagraph(ParagraphData editParagraph) {
    this.editParagraph = editParagraph;
  }

  /**
   * Method setEditParagraph sets the editParagraph of this ContentData object.
   *
   * @param idx the editParagraph of this ContentData object.
   */
  public void setEditParagraph(int idx) {
    setEditParagraph(paragraphs.get(idx));
  }

  /**
   * Method moveParagraph
   *
   * @param idx of type int
   * @param dir of type int
   */
  public void moveParagraph(int idx, int dir) {
    editParagraph = null;
    ParagraphData pdata = paragraphs.remove(idx);
    idx += dir;
    if (pdata != null) {
      if (idx >= paragraphs.size())
        paragraphs.add(pdata);
      else {
        if (idx < 0)
          idx = 0;
        paragraphs.add(idx, pdata);
      }
    }
  }

  /**
   * Method removeParagraph
   *
   * @param idx of type int
   */
  public void removeParagraph(int idx) {
    editParagraph = null;
    paragraphs.remove(idx);
  }

  /**
   * Method getDocumentUsage returns the documentUsage of this ContentData object.
   *
   * @return the documentUsage (type HashSet<Integer>) of this ContentData object.
   */
  public HashSet<Integer> getDocumentUsage() {
    HashSet<Integer> list = new HashSet<Integer>();
    for (ParagraphData pdata : paragraphs) {
      pdata.getDocumentUsage(list);
    }
    return list;
  }

  /**
   * Method getImageUsage returns the imageUsage of this ContentData object.
   *
   * @return the imageUsage (type HashSet<Integer>) of this ContentData object.
   */
  public HashSet<Integer> getImageUsage() {
    HashSet<Integer> list = new HashSet<Integer>();
    for (ParagraphData pdata : paragraphs) {
      pdata.getImageUsage(list);
    }
    return list;
  }

  /**
   * Method getUserTree
   *
   * @param userId   of type int
   * @param isEditor of type boolean
   * @param list     of type ArrayList<ContentData>
   */
  public void getUserTree(int userId, boolean isEditor, ArrayList<ContentData> list) {
    list.add(this);
    if (children != null) {
      for (ContentData child : children) {
        if (child.getId() < Statics.CONT_MIN)
          continue;
        if (!isEditor && !child.isShowMenu())
          continue;
        if (!isEditor && !child.hasUserReadRight(userId))
          continue;
        child.getUserTree(userId, isEditor, list);
      }
    }
  }

  /**
   * Method getTree
   *
   * @param list of type ArrayList<ContentData>
   */
  public void getTree(ArrayList<ContentData> list) {
    list.add(this);
    if (children != null) {
      for (ContentData child : children) {
        if (!child.isShowMenu())
          continue;
        child.getTree(list);
      }
    }
  }

  /**
   * Method prepareSave
   *
   * @throws Exception when data processing is not successful
   */
  public void prepareSave() throws Exception {
    for (int i = 0; i < paragraphs.size(); i++)
      paragraphs.get(i).setRanking(i);
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
    int editType = rdata.getParamInt("editType");
    switch (editType) {
      case ContentData.EDIT_METADATA: {
        setName(rdata.getParamString("name"));
        setDescription(rdata.getParamString("description"));
        setMetaKeywords(rdata.getParamString("metaKeywords"));
        setShowMenu(rdata.getParamBoolean("showMenu"));
        setRestricted(rdata.getParamBoolean("restricted"));
        HashSet<Integer> groupIds = rdata.getParamIntegerSet("groupIds");
        rightData.setGroupRights(groupIds, Statics.RIGHT_READ);
        if (!isComplete(name))
          err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      }
      break;
      case ContentData.EDIT_PARENT: {
        int par = rdata.getParamInt("parent");
        if (MenuCache.getInstance(sdata.getLocale()).isParent(getId(), par))
          err.addErrorString(Strings.getString("err_bad_parent", sdata.getLocale()));
        else
          setParent(par, sdata.getLocale());
        if (!isComplete(par) && id != Statics.getContentHomeId(sdata.getLocale()))
          err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      }
      break;
      case ContentData.EDIT_CONTENT: {
        if (editParagraph != null)
          editParagraph.readRequestData(rdata, sdata);
      }
      break;
      default:
        err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
    }
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

  /**
   * Method isAllComplete
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return boolean
   */
  public boolean isAllComplete(RequestData rdata, SessionData sdata) {
    boolean valid = isComplete(name);
    valid &= (isComplete(parent) || id == Statics.getContentHomeId(sdata.getLocale()));
    if (!valid) {
      RequestError err = new RequestError();
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
