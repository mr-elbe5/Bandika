package de.bandika.base;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class AppConfig extends BaseAppConfig{

  protected static AppConfig instance;

  public static AppConfig getInstance(){
    if (instance==null)
      instance=new AppConfig();
    return instance;
  }

  protected DataSource fbaDataSource = null;
  protected DataSource cmsDataSource = null;
  protected HashMap<String,String> configs =new HashMap<String,String>();

  public HashMap<String, String> getConfigs() {
		return configs;
	}

	public String getConfig(String key) {
		String s = configs.get(key);
		return s==null ? "" : s;
	}

  public Connection getFbaConnection() throws SQLException {
		if (fbaDataSource == null)
			return null;
		return fbaDataSource.getConnection();
	}

  public Connection getCmsConnection() throws SQLException {
		if (cmsDataSource == null)
			return null;
		return cmsDataSource.getConnection();
	}

  public void initialize(HttpServletRequest request, String basePath) throws Exception {
    Logger.info(null, "initializing configuration...");
    this.basePath = basePath;
    Logger.info(null, "base path is: " + this.basePath);
    String s=basePath.replace('\\','/');
    int pos=s.lastIndexOf("/ROOT");
    if (pos!=-1)
      s=s.substring(0,pos);
    pos=s.lastIndexOf('/');
    if (pos!=-1)
      s=s.substring(pos+1);
    Logger.info(null, "app folder is: " + s);
    InitialContext context = new InitialContext();
    Context envCtx = (Context) context.lookup("java:comp/env");
    String fbaDataSourceName=(String) envCtx.lookup("connector_"+s+"_fba");
    String cmsDataSourceName=(String) envCtx.lookup("connector_"+s+"_cms");
    Logger.info(null, "fba data source name is: " + fbaDataSourceName);
    fbaDataSource = (DataSource) envCtx.lookup(fbaDataSourceName);
    Connection con = getFbaConnection();
    String catalog = con.getCatalog();
    con.close();
    Logger.info(null, "fba data source is working on schema " + catalog);
    Logger.info(null, "cms data source name is: " + cmsDataSourceName);
    cmsDataSource = (DataSource) envCtx.lookup(cmsDataSourceName);
    con = getCmsConnection();
    catalog = con.getCatalog();
    con.close();
    Logger.info(null, "cms data source is working on schema " + catalog);
    readConfig();
    setLocale(configs.get("locale"));
    Logger.info(null, "locale is: " + locale.getLanguage());
    Strings.init("bandika");
    Logger.info(null, "language dependent strings initialized with " + Strings.getBundleSize() + " entries");
    Logger.info(null, "configuration initialized");
		initialized=true;
	}

  public void readConfig() {
		Connection con = null;
		try {
			con = getCmsConnection();
			readConfig(con);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try{
        if (con!=null)
          con.close();
      }
      catch (SQLException ignored){
      }
		}
	}

  public void readConfig(Connection con) throws SQLException {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select config_key,config_value from t_config");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i=1;
				String key= rs.getString(i++);
				String value= rs.getString(i);
				AppConfig.getInstance().getConfigs().put(key,value);
				Logger.info(null, "config " + key + " is: " + value);
			}
			rs.close();
		}
		finally {
			try{
        if (pst!=null)
          pst.close();
      }
      catch (SQLException ignored){
      }
		}
	}

}