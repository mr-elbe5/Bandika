package de.bandika.base;

public class ContextConfig {

	private String fbaDataSource;
  private String cmsDataSource;
	private String basePath;

  public String getFbaDataSource() {
    return fbaDataSource;
  }

  public void setFbaDataSource(String fbaDataSource) {
    this.fbaDataSource = fbaDataSource;
  }

  public String getCmsDataSource() {
    return cmsDataSource;
  }

  public void setCmsDataSource(String cmsDataSource) {
    this.cmsDataSource = cmsDataSource;
  }

  public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}