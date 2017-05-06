package com.zach.helper.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.zach.helper.entity.Column;
import com.zach.helper.entity.ConConfig;
import com.zach.helper.entity.Table;

/**
 * ��Ӧsqlserver�ײ������<br>
 * �����õ����б��ֶΡ��õ������ݿ�����
 */
public class MssqlDBHelper {
	public final static String EN = " ";
	public final static String DS = ",";
	public final static String LS = "(";
	public final static String RS = ")";
	public final static String LM = "[";
	public final static String RM = "]";
	public final static String NL = "\r\n";

	/**
	 * �м�ȫ�ı���
	 */
	public final static String SSHCOLUMNS = "columnMate";

	/**
	 * ��ȫ�ı���
	 */
	public final static String SSHTABLES = "tableMate";

	/**
	 * ��ʼ�����ݿ�
	 */
	public static void initDB(String url, String user, String pwd, String dbName) {
		String sql = "SELECT name FROM sysobjects where xtype='U' and name='" + SSHCOLUMNS + "'";
		Connection conn = null;
		try {
			// �������ݿ�
			conn = ConnectionHelper.getCon(url, user, pwd, "master");// ������Ҫ��master����
			String createSql = "if not exists (select * from sysdatabases where name='" + dbName
					+ "') \r\n create database " + dbName;
			ConnectionHelper.execSql(createSql, conn);

			// ת��dbName���ݿ���
			ConnectionHelper.CloseCon(conn);
			conn = ConnectionHelper.getCon(url, user, pwd, dbName);

			// ��ʼ������Ԫ���ݱ�
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {// ������SSHCOLUMNS���򴴽�
				sql = "create table " + SSHCOLUMNS
						+ "(id int identity(1,1) not null primary key,tableName varchar(200),fieldName varchar(200),";
				sql += " fieldName2 varchar(200),dataType varchar(100),reference varchar(200),description varchar(500),fieldLength int)";
				ConnectionHelper.execSql(sql, conn);// ������
			}

			sql = "SELECT name FROM sysobjects where xtype='U' and name='" + SSHTABLES + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (!rs.next()) {// ������SSHTABLES���򴴽�
				sql = "create table " + SSHTABLES
						+ "(id int identity(1,1) not null primary key,packName varchar(400),tableName varchar(200),tableName_ch ";
				sql += " varchar(200),description varchar(500))";
				ConnectionHelper.execSql(sql, conn);// ������
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * �õ����ݿ��еı�,��������
	 * 
	 * @throws SQLException
	 */
	public static Map<String, Table> getAllTables(ConConfig config) throws Exception {
		Map<String, Table> tables = new HashMap<String, Table>();
		String sql = "SELECT id,packName,tableName,tableName_ch,description FROM  " + SSHTABLES;
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String packName = rs.getString("packName");
				String tableName = rs.getString("tableName");
				String tableName_ch = rs.getString("tableName_ch");
				String description = rs.getString("description");
				tables.put(tableName, new Table(id, packName, tableName, tableName_ch, description));// �����ݿ��ȡ�����ı���ȫ��ΪСд
			}
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
		return tables;
	}

	/**
	 * �õ�ĳһ�ű�
	 * 
	 * @param tableName
	 *            ����
	 * @param readColumn
	 *            �Ƿ���Ҫ��ȡ����Ϣ
	 */
	public static Table getTable(ConConfig config, String tableName, boolean readColumn) {
		Table table = null;
		String sql = "SELECT id,packName,tableName_ch,description FROM " + SSHTABLES + " where tableName='" + tableName
				+ "'";
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String packName = rs.getString("packName");
				String tableName_ch = rs.getString("tableName_ch");
				String description = rs.getString("description");
				table = new Table(id, packName, tableName, tableName_ch, description);
			}
			if (table != null && readColumn) {// �����Ҫ���У������м���
				table.setColumns(getColumnsByTable(config, tableName));
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
		return table;
	}

	/**
	 * �õ�ĳ��������ֶ�
	 * 
	 * @param tableName
	 *            ����
	 */
	public static Map<String, Column> getColumnsByTable(ConConfig config, String tableName) {
		Map<String, Column> columns = new HashMap<String, Column>();
		String sql = "SELECT id,fieldName,fieldName2,dataType,reference,description,fieldLength FROM " + SSHCOLUMNS
				+ " where tableName='" + tableName + "'";
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Column c = new Column();
				c.setId(rs.getInt("id"));
				c.setTableName(tableName);// ����
				String fieldName = rs.getString("fieldName");
				if (fieldName != null && "id".equals(fieldName.toLowerCase())) {// �����id��
					c.setFieldName("id");// �ֶ���
				} else {
					c.setFieldName(fieldName);// �ֶ���
				}
				c.setFieldNameCH(rs.getString("fieldName2"));// �ֶ���
				c.setFieldType(rs.getString("dataType"));// ����
				c.setForeignKey(rs.getString("reference"));// ���
				c.setDesc(rs.getString("description"));// ˵��
				c.setFieldLength(rs.getInt("fieldLength"));// ����

				columns.put(c.getFieldName(), c);
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
		return columns;
	}

	// -----------------------������ͬ������---------------------

	/**
	 * �������ݿ� 1:���ӵ�master 2:�������ݿ�
	 * 
	 * @throws Exception
	 *             ִ�г���
	 */
	public static void createDataBase(ConConfig config) {
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			String sql = "if not exists (select * from sysdatabases where name='" + config.getDbName()
					+ "') \r\n create database " + config.getDbName();
			ConnectionHelper.execSql(sql, conn);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * �����ݿ��д���һ�ű�
	 */
	public static void createTable(ConConfig config, Table table) {
		if (table == null) {
			return;
		}

		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			StringBuffer sql = new StringBuffer();
			sql.append("create table ").append(table.getTableName()).append(LS);
			int index = 0;
			for (String key : table.getColumns().keySet()) {
				Column c = table.getColumns().get(key);
				sql.append(getCommonColumnSql(c));
				if (index < table.getColumns().size() - 1) {
					sql.append(DS);// ����,
				}
				index++;
				// ��ӵ�SSHCOLUMNS����
				String inSql = "insert into " + SSHCOLUMNS + " values('" + table.getTableName() + "','"
						+ c.getFieldName() + "','" + c.getFieldNameCH() + "','" + c.getFieldType() + "','"
						+ c.getForeignKey() + "','" + c.getDesc() + "'," + c.getFieldLength() + ")";
				ConnectionHelper.execSql(inSql, conn);
			}
			sql.append(RS);
			ConnectionHelper.execSql(sql.toString(), conn);// ִ��sql

			sql.setLength(0);
			sql.append("insert into " + SSHTABLES + " values('").append(table.getPackName());
			sql.append("','").append(table.getTableName());
			sql.append("','").append(table.getTableNameCH());
			sql.append("','").append(table.getDescription()).append("')");
			ConnectionHelper.execSql(sql.toString(), conn);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * �޸ı���Ϣ
	 */
	public static void updateTable(ConConfig config, Table table, int id) throws Exception {
		if (table == null) {
			return;
		}
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			StringBuffer sql = new StringBuffer();
			sql.append(" update ").append(SSHTABLES).append(" set ");
			sql.append(" packName='").append(table.getPackName());
			sql.append("' ,tableName='").append(table.getTableName());
			sql.append("' ,tableName_ch='").append(table.getTableNameCH());
			sql.append("' ,description='").append(table.getDescription());
			sql.append("' where id=").append(id);

			ConnectionHelper.execSql(sql.toString(), conn);// ִ��sql
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * ���һ�����ֶ�
	 */
	public static void createColumn(ConConfig config, Column c) throws Exception {
		if (c == null) {
			return;
		}
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			StringBuffer sql = new StringBuffer();
			sql.append("alter table ").append(c.getTableName()).append(" add ");
			sql.append(getCommonColumnSql(c));
			ConnectionHelper.execSql(sql.toString(), conn);

			String inSql = "insert into " + SSHCOLUMNS + " values('" + c.getTableName() + "','" + c.getFieldName()
					+ "','" + c.getFieldNameCH() + "','" + c.getFieldType() + "','" + c.getForeignKey() + "','"
					+ c.getDesc() + "')";
			ConnectionHelper.execSql(inSql, conn);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * �޸ı���Ϣ
	 */
	public static boolean updateColumn(ConConfig config, Column cDB, Column cExcel) throws Exception {
		if (!cDB.equals(cExcel)) {// �����Ҫ�޸�
			Connection conn = null;
			try {
				conn = ConnectionHelper.getConnection(config);
				StringBuffer sql = new StringBuffer();
				sql.append("alter table ").append(cExcel.getTableName()).append(" alter column ");
				sql.append(getCommonColumnSql(cExcel));
				ConnectionHelper.execSql(sql.toString(), conn);// �������ݿ�

				String upSql = "update " + SSHCOLUMNS + " set fieldName2='" + cExcel.getFieldNameCH() + "',dataType='"
						+ cExcel.getFieldType() + "',reference='" + cExcel.getForeignKey() + "',description='"
						+ cExcel.getDesc() + "',fieldLength=" + cExcel.getFieldLength() + "  where tableName='"
						+ cExcel.getTableName() + "' and fieldName='" + cExcel.getFieldName() + "'";
				ConnectionHelper.execSql(upSql, conn);// �޸�SSHCOLUMNS
			} catch (Exception err) {
				err.printStackTrace();
			} finally {
				ConnectionHelper.CloseCon(conn);
			}
			return true;
		} else {
			return false;
		}

	}

	/**
	 * ɾ��һ���ֶ�
	 */
	public static void dropColumn(ConConfig config, Column c) throws Exception {
		Connection conn = null;
		try {
			conn = ConnectionHelper.getConnection(config);
			if (c == null) {
				return;
			}
			String inSql = "delete from " + SSHCOLUMNS + " where id=" + c.getId();
			ConnectionHelper.execSql(inSql, conn);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * ��������Ϣ
	 */
	public static String getCommonColumnSql(Column c) {
		if (c == null) {
			return "";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(LM + c.getFieldName() + RM).append(EN).append(c.getFieldType());// �ֶ���+����
		if (c.isHasLength()) {// �г���
			if (c.isHasPrecision()) {// �о���
				sql.append(LS).append(c.getFieldLength()).append(DS).append(c.getPrecision()).append(RS);
			} else {// �޾���
				sql.append(LS).append(c.getFieldLength()).append(RS);
			}
		}
		if (c.isPrimaryKey()) {// id����
			sql.append(" primary key");
		}
		return sql.toString();
	}
}
