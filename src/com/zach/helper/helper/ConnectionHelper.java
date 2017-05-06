package com.zach.helper.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.zach.helper.entity.ConConfig;

public class ConnectionHelper {
	/**
	 * �õ�ĳ�����ݿ������
	 * 
	 * @param url
	 *            ������databaseName��url
	 * @param user
	 *            ���ݿ��û�
	 * @param pwd
	 *            ����
	 * @param dbName
	 *            ���ݿ���
	 */
	public static Connection getCon(String url, String user, String pwd, String dbName) throws SQLException {
		Connection con = DriverManager.getConnection(url + ";databaseName=" + dbName, user, pwd);
		return con;
	}

	/**
	 * �õ����ݿ������,��һ��Ҫָ�����ݿ�
	 * 
	 * @param url
	 *            url
	 * @param user
	 *            ���ݿ��û�
	 * @param pwd
	 *            ����
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnection(ConConfig config) throws Exception {
		String dbType = config.getDbType();
		if ("MySql".equals(dbType)) {
			Class.forName("com.mysql.jdbc.Driver");
		} else if ("Oracle".equals(dbType)) {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} else if ("SQLServer".equals(dbType)) {
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		} else if ("DB2".equals(dbType)) {
			Class.forName("com.ibm.db2.jdbc.app.DB2Driver");
		} else if ("Sybase".equals(dbType)) {
			Class.forName("com.sybase.jdbc.SybDriver");
		}
		Connection con = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPwd());
		return con;
	}

	/**
	 * �����Ƿ����������ݿ������
	 */
	public static boolean testConnection(ConConfig config) {
		try {
			getConnection(config);
			return true;
		} catch (Exception err) {
		}
		return false;
	}

	/**
	 * �ر�����
	 */
	public static void CloseCon(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * ִ��sql
	 */
	public static boolean execSql(String sql, Connection conn) throws Exception {
		PreparedStatement ps = conn.prepareStatement(sql);
		return ps.execute();
	}
}
