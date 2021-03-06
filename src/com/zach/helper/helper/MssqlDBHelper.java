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
 * 对应sqlserver底层操作类<br>
 * 包括得到所有表及字段、得到与数据库连接
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
	 * 列集全的表名
	 */
	public final static String SSHCOLUMNS = "columnMate";

	/**
	 * 表集全的表名
	 */
	public final static String SSHTABLES = "tableMate";

	/**
	 * 初始化数据库
	 */
	public static void initDB(String url, String user, String pwd, String dbName) {
		String sql = "SELECT name FROM sysobjects where xtype='U' and name='" + SSHCOLUMNS + "'";
		Connection conn = null;
		try {
			// 创建数据库
			conn = ConnectionHelper.getCon(url, user, pwd, "master");// 建库需要在master库中
			String createSql = "if not exists (select * from sysdatabases where name='" + dbName
					+ "') \r\n create database " + dbName;
			ConnectionHelper.execSql(createSql, conn);

			// 转到dbName数据库中
			ConnectionHelper.CloseCon(conn);
			conn = ConnectionHelper.getCon(url, user, pwd, dbName);

			// 初始化两张元数据表
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {// 不存在SSHCOLUMNS表，则创建
				sql = "create table " + SSHCOLUMNS
						+ "(id int identity(1,1) not null primary key,tableName varchar(200),fieldName varchar(200),";
				sql += " fieldName2 varchar(200),dataType varchar(100),reference varchar(200),description varchar(500),fieldLength int)";
				ConnectionHelper.execSql(sql, conn);// 创建表
			}

			sql = "SELECT name FROM sysobjects where xtype='U' and name='" + SSHTABLES + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (!rs.next()) {// 不存在SSHTABLES表，则创建
				sql = "create table " + SSHTABLES
						+ "(id int identity(1,1) not null primary key,packName varchar(400),tableName varchar(200),tableName_ch ";
				sql += " varchar(200),description varchar(500))";
				ConnectionHelper.execSql(sql, conn);// 创建表
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * 得到数据库中的表,不包括列
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
				tables.put(tableName, new Table(id, packName, tableName, tableName_ch, description));// 从数据库读取出来的表名全改为小写
			}
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
		return tables;
	}

	/**
	 * 得到某一张表
	 * 
	 * @param tableName
	 *            表名
	 * @param readColumn
	 *            是否需要读取列信息
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
			if (table != null && readColumn) {// 如果需要查列，则查出列集合
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
	 * 得到某表的所有字段
	 * 
	 * @param tableName
	 *            表名
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
				c.setTableName(tableName);// 表名
				String fieldName = rs.getString("fieldName");
				if (fieldName != null && "id".equals(fieldName.toLowerCase())) {// 如果是id列
					c.setFieldName("id");// 字段名
				} else {
					c.setFieldName(fieldName);// 字段名
				}
				c.setFieldNameCH(rs.getString("fieldName2"));// 字段名
				c.setFieldType(rs.getString("dataType"));// 类型
				c.setForeignKey(rs.getString("reference"));// 外键
				c.setDesc(rs.getString("description"));// 说明
				c.setFieldLength(rs.getInt("fieldLength"));// 长度

				columns.put(c.getFieldName(), c);
			}
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
		return columns;
	}

	// -----------------------以下是同步方法---------------------

	/**
	 * 创建数据库 1:连接到master 2:创建数据库
	 * 
	 * @throws Exception
	 *             执行出错
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
	 * 在数据库中创建一张表
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
					sql.append(DS);// 加上,
				}
				index++;
				// 添加到SSHCOLUMNS表中
				String inSql = "insert into " + SSHCOLUMNS + " values('" + table.getTableName() + "','"
						+ c.getFieldName() + "','" + c.getFieldNameCH() + "','" + c.getFieldType() + "','"
						+ c.getForeignKey() + "','" + c.getDesc() + "'," + c.getFieldLength() + ")";
				ConnectionHelper.execSql(inSql, conn);
			}
			sql.append(RS);
			ConnectionHelper.execSql(sql.toString(), conn);// 执行sql

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
	 * 修改表信息
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

			ConnectionHelper.execSql(sql.toString(), conn);// 执行sql
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			ConnectionHelper.CloseCon(conn);
		}
	}

	/**
	 * 添加一个新字段
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
	 * 修改表信息
	 */
	public static boolean updateColumn(ConConfig config, Column cDB, Column cExcel) throws Exception {
		if (!cDB.equals(cExcel)) {// 如果需要修改
			Connection conn = null;
			try {
				conn = ConnectionHelper.getConnection(config);
				StringBuffer sql = new StringBuffer();
				sql.append("alter table ").append(cExcel.getTableName()).append(" alter column ");
				sql.append(getCommonColumnSql(cExcel));
				ConnectionHelper.execSql(sql.toString(), conn);// 更新数据库

				String upSql = "update " + SSHCOLUMNS + " set fieldName2='" + cExcel.getFieldNameCH() + "',dataType='"
						+ cExcel.getFieldType() + "',reference='" + cExcel.getForeignKey() + "',description='"
						+ cExcel.getDesc() + "',fieldLength=" + cExcel.getFieldLength() + "  where tableName='"
						+ cExcel.getTableName() + "' and fieldName='" + cExcel.getFieldName() + "'";
				ConnectionHelper.execSql(upSql, conn);// 修改SSHCOLUMNS
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
	 * 删除一个字段
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
	 * 处理列信息
	 */
	public static String getCommonColumnSql(Column c) {
		if (c == null) {
			return "";
		}
		StringBuffer sql = new StringBuffer();
		sql.append(LM + c.getFieldName() + RM).append(EN).append(c.getFieldType());// 字段名+类型
		if (c.isHasLength()) {// 有长度
			if (c.isHasPrecision()) {// 有精度
				sql.append(LS).append(c.getFieldLength()).append(DS).append(c.getPrecision()).append(RS);
			} else {// 无精度
				sql.append(LS).append(c.getFieldLength()).append(RS);
			}
		}
		if (c.isPrimaryKey()) {// id主键
			sql.append(" primary key");
		}
		return sql.toString();
	}
}
