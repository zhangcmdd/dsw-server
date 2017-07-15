package com.szu.insight.dsw.utls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang.NullArgumentException;

import com.szu.insight.dsw.domain.DataSourceConfig;
/**
 * 
 * @author Cloud
 *
 */
public class DataSourceUtil {
	
	public static void connect(DataSourceConfig config) throws ClassNotFoundException, SQLException{
		if(config.getDriver() == null){
			throw new NullArgumentException("Driver is empty");
		}
		Class.forName(config.getDriver());

		Connection conn = DriverManager.getConnection(config.getUrl(), config.getUsername() , config.getPassword());
		
		if(conn != null && !conn.isClosed()){
			conn.close();
		}
	}
}
