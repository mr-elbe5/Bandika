package de.bandika.base;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public abstract class BaseAppConfig {

	protected String basePath=null;
	protected boolean initialized=false;
	protected Locale locale = Locale.ENGLISH;

	public boolean initialized() {
		return initialized;
	}

  public abstract void initialize(HttpServletRequest request, String basePath) throws Exception;

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getBasePath(){
		return basePath;
	}

  public String getXmlPath(){
		return basePath+"/_xml";
	}

	public Locale getLocale() {
		return locale;
	}

  protected void setLocale(String localeName) {
    try{
		  locale=new Locale(localeName);
    }
    catch (Exception e){
      locale=Locale.ENGLISH;
    }
	}

}