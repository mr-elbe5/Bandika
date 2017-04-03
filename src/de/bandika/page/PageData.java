/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.*;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;
import de.bandika.user.UserController;
import de.bandika.menu.MenuController;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Class PageData is the data class for content pages. <br>
 * Usage:
 */
public class PageData extends VersionedData implements Comparable {

	protected int parentId = 0;
	protected int ranking = 0;
	protected String name = "";
	protected String description = "";
	protected String keywords = "";
	protected int state = 0;
  protected boolean restricted = false;
  protected boolean inMenu = true;
	protected int authorId = 0;
  protected String xml = "";
	protected ArrayList<ParagraphData> paragraphs = new ArrayList<ParagraphData>();
	protected ParagraphData editParagraph = null;
	protected HashMap<Integer, Integer> groupRights = new HashMap<Integer, Integer>();
  protected ArrayList<PageData> childPages = new ArrayList<PageData>();

  protected PageData parent=null;


  public PageData() {
	}

	public int compareTo(Object o) {
		if (!(o instanceof PageData))
			return 0;
		PageData node = (PageData) o;
		return ranking - node.ranking;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		if (parentId == id) {
			Logger.error(getClass(), "parentId must not be this");
			this.parentId = 0;
		} else
			this.parentId = parentId;
	}

  public void setParent(HashMap<Integer, PageData> pageMap) {
		if (parentId==0)
      return;
    PageData data=pageMap.get(parentId);
    if (data!=null){
      parent=data;
      parent.getChildPages().add(this);
    }
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

  public void sortRecursive() {
		if (childPages != null) {
			Collections.sort(childPages);
      for (PageData child : childPages)
        child.sortRecursive();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? "" : name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

  public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

  public boolean isRestricted() {
    return restricted;
  }

  public void setRestricted(boolean restricted) {
    this.restricted = restricted;
  }

  public boolean isInMenu() {
    return inMenu;
  }

  public void setInMenu(boolean inMenu) {
    this.inMenu = inMenu;
  }

  public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

	public void generateXml(){
		Document doc=XmlData.createXmlDocument();
		Element root=XmlData.createRootNode(doc,"page");
		XmlData.createIntAttribute(doc,root,"id",id);
		XmlData.createAttribute(doc,root,"name",name);
		for (ParagraphData para : paragraphs){
			para.generateXml(doc,root);
		}
		xml=XmlData.xmlToString(doc);
	}

	public void evaluateXml(){
		paragraphs.clear();
    if (xml==null || xml.length()==0)
      return;
    Document doc=XmlData.getXmlDocument(xml);
    if (doc==null)
      return;
    Element root=XmlData.getRootNode(doc);
    if (root==null)
      return;
    NodeList paragraphNodes=XmlData.getChildNodes(root,"paragraph");
    for (int i=0;i<paragraphNodes.getLength();i++){
      Element child=(Element)paragraphNodes.item(i);
      ParagraphData para=new ParagraphData();
      para.setRanking(i);
      para.evaluateXml(child);
      paragraphs.add(para);
    }
	}

  public HashSet<Integer> getDocumentUsage() {
    HashSet<Integer> list = new HashSet<Integer>();
    for (ParagraphData pdata : paragraphs) {
      pdata.getDocumentUsage(list);
    }
    return list;
  }

  public HashSet<Integer> getImageUsage() {
    HashSet<Integer> list = new HashSet<Integer>();
    for (ParagraphData pdata : paragraphs) {
      pdata.getImageUsage(list);
    }
    return list;
  }

  public ArrayList<ParagraphData> getParagraphs() {
    return paragraphs;
  }

  public void setParagraphs(ArrayList<ParagraphData> paragraphs) {
    this.paragraphs = paragraphs;
  }

  public ParagraphData getParagraphById(int pid) {
    for (ParagraphData pdata : paragraphs) {
      if (pdata.getId() == pid)
        return pdata;
    }
    return null;
  }

  public ParagraphData getEditParagraph() {
    return editParagraph;
  }

  public int getEditParagraphIdx() {
    for (int i = 0; i < paragraphs.size(); i++)
      if (paragraphs.get(i) == editParagraph)
        return i;
    return -1;
  }

  public void setEditParagraph(ParagraphData editParagraph) {
    this.editParagraph = editParagraph;
  }

  public void setEditParagraph(int idx) {
    setEditParagraph(paragraphs.get(idx));
  }

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

  public void removeParagraph(int idx) {
    editParagraph = null;
    paragraphs.remove(idx);
  }


	public HashMap<Integer, Integer> getGroupRights() {
		return groupRights;
	}

	public boolean hasGroupRight(int id, int right) {
		Integer rgt = groupRights.get(id);
		return rgt != null && rgt >= right;
	}

	public void setGroupRights(HashMap<Integer, Integer> groupRights) {
		this.groupRights = groupRights;
	}

	public void setGroupRights(HashSet<Integer> groupIds, int right) {
		HashSet<Integer> ids = new HashSet<Integer>(groupRights.keySet());
		for (int id : ids) {
			int rgt = groupRights.get(id);
			if (rgt <= right)
				groupRights.remove(id);
		}
		for (int id : groupIds) {
			if (groupRights.keySet().contains(id))
				continue;
			groupRights.put(id, right);
		}
	}

	public boolean hasGroupReadRight(int groupId) {
		return hasGroupRight(groupId, UserController.RIGHT_READ);
	}

  public ArrayList<PageData> getChildPages() {
		return childPages;
	}

	public void setChildPages(ArrayList<PageData> childPages) {
		this.childPages = childPages;
	}

  public void getTree(ArrayList<PageData> list) {
		list.add(this);
		if (childPages != null) {
			for (PageData child : childPages) {
				child.getTree(list);
			}
		}
	}

	public void prepareSave() throws Exception {

	}

	@Override
	public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
		int editType = rdata.getParamInt("editType");
		switch (editType) {
			case PageController.EDIT_METADATA: {
				setName(rdata.getParamString("name"));
				setDescription(rdata.getParamString("description"));
				setKeywords(rdata.getParamString("metaKeywords"));
				setRestricted(rdata.getParamBoolean("restricted"));
				HashSet<Integer> groupIds = rdata.getParamIntegerSet("groupIds");
				setGroupRights(groupIds, UserController.RIGHT_READ);
				if (!isComplete(name))
					err.addErrorString(AdminStrings.notcomplete);
			}
			break;
			case PageController.EDIT_PARENT: {
        int par = rdata.getParamInt("parent");
        if (((MenuController) Controller.getController(MenuController.KEY_MENU)).isParentPage(getId(), par))
          err.addErrorString(AdminStrings.badparent);
        else
          setParentId(par);
        if (!isComplete(par) && id != BaseConfig.ROOT_PAGE_ID)
          err.addErrorString(AdminStrings.notcomplete);
			}
			break;
			case PageController.EDIT_CONTENT: {
        if (editParagraph != null)
          editParagraph.readRequestData(rdata, sdata);
			}
			break;
			default:
				err.addErrorString(AdminStrings.notcomplete);
		}
		if (!err.isEmpty()) {
			rdata.setError(err);
			return false;
		}
		return true;
	}

	public boolean isAllComplete(RequestData rdata, SessionData sdata) {
		boolean valid = isComplete(name);
		valid &= (isComplete(parentId) || id == BaseConfig.ROOT_PAGE_ID);
		if (!valid) {
			RequestError err = new RequestError();
			err.addErrorString(AdminStrings.notcomplete);
			rdata.setError(err);
			return false;
		}
		return true;
	}

}
