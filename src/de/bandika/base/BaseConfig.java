package de.bandika.base;

import de.bandika.admin.AdminBean;
import de.bandika.admin.AdminController;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Locale;

public class BaseConfig {

	public static final Date ERROR_DATE = new Date(0x0);
	public static final int ROOT_PAGE_ID = 100;

  protected static DataSource dataSource = null;
	protected static String basePath=null;
	private static HashMap<String,String> configs =new HashMap<String,String>();
	private static boolean initialized=false;
	private static Locale locale = Locale.ENGLISH;

	public static boolean initialized() {
		return initialized;
	}

	public static void initialize(HttpServletRequest request) throws Exception {
		Logger.info(null, "initializing application...");
		String host = request.getServerName();
		InitialContext context = new InitialContext();
    Context envCtx = (Context) context.lookup("java:comp/env");
    BaseConfig config = (BaseConfig) envCtx.lookup("config_bandika_"+host);
		String dataSourceName = config.getDataSource();
		Logger.info(null, "data source name is: " + dataSourceName);
		BaseConfig.dataSource = (DataSource) envCtx.lookup(dataSourceName);
		Connection con = getConnection();
		String catalog = con.getCatalog();
		con.close();
		Logger.info(null, "data source is working on schema " + catalog);
		BaseConfig.basePath = config.getPath();
    Logger.info(null, "base path is: " + basePath);
		Bean.addBean(AdminController.KEY_ADMIN, new AdminBean());
		AdminBean bean = (AdminBean) Bean.getBean(AdminController.KEY_ADMIN);
		bean.readConfig();
		Controller.DEFAULT_KEY=configs.get("defaultController");
		//UserStrings.init(getConfig("properties"));
		String localeString=getConfig("locale");
		if (!localeString.equals(""))
		  locale=new Locale(localeString);
		StringTokenizer stk=new StringTokenizer(getConfig("initSequence"),";");
		while(stk.hasMoreTokens()){
			String token=stk.nextToken();
			Controller ctrl=Controller.getController(token);
			if (ctrl!=null){
				Logger.info(null, "initializing controller of type " + token);
			  ctrl.initialize();
			}
		}
		initialized=true;
		Logger.info(null, "application initialized");
	}

	public static Connection getConnection() throws SQLException {
		if (dataSource == null)
			return null;
		return dataSource.getConnection();
	}

  public static void setBasePath(String basePath) {
    BaseConfig.basePath = basePath;
  }

  public static String getBasePath(){
		return basePath;
	}

  public static String getXmlPath(){
		return basePath+"/_xml";
	}
	public static HashMap<String, String> getConfigs() {
		return configs;
	}

	public static String getConfig(String key) {
		String s = configs.get(key);
		return s==null ? "" : s;
	}

	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		BaseConfig.locale = locale;
	}

	// non statics

	private String _dataSource;
	private String _path;

	public String getDataSource() {
		return _dataSource;
	}

	public void setDataSource(String dataSource) {
		this._dataSource = dataSource;
	}

	public String getPath() {
		return _path;
	}

	public void setPath(String path) {
		this._path = path;
	}

}