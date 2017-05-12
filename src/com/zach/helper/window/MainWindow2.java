package com.zach.helper.window;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Label;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.zach.helper.biz.EntityBuilderBiz;
import com.zach.helper.biz.SyncDbBiz;
import com.zach.helper.entity.ConConfig;
import com.zach.helper.entity.Table;
import com.zach.helper.helper.ConnectionHelper;
import com.zach.helper.helper.ExcelHelper;
import com.zach.helper.helper.MssqlDBHelper;
import com.zach.helper.util.FileUtil;

public class MainWindow2 extends java.awt.Frame {
	private static final long serialVersionUID = -4259339680151285634L;

	private Button btnBuildEntity;
	private Button btnSync;
	private Button btnSelectFile;
	private Button btnTestCon;
	private Label labDBType;
	private Label labDB;
	private Label labPassword;
	private Label labURL;
	private Label labUser;
	private Label labPort;
	private JScrollPane scrTables;
	private JTable dataTable;
	private JTextField txtDB;
	private JPasswordField txtPassword;
	private JTextField txtURL;
	private JTextField txtUser;
	private JTextField txtPort;
	private JComboBox<String> comboDB;
	private static TextArea txtConsole;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// 时间格式
	FileDialog fileDialog = null;// 文件选择器
	Map<String, Table> excelTables = new LinkedHashMap<String, Table>();
	private String[] columnStrs = new String[] { "", "表名", "中文名", "包名", "" };// 列名
	private ConConfig con;// 连接信息

	public MainWindow2() {
		initComponents();
		initWindow();
	}

	/**
	 * 初始化页面组件
	 */
	private void initComponents() {
		btnSelectFile = new Button();
		scrTables = new JScrollPane();
		btnSync = new Button();
		labDBType = new Label();
		labURL = new Label();
		txtURL = new JTextField();
		labUser = new Label();
		txtUser = new JTextField();
		labPassword = new Label();
		txtPassword = new JPasswordField();
		btnTestCon = new Button();
		txtConsole = new TextArea();
		labDB = new Label();
		txtDB = new JTextField();
		labPort = new Label();
		txtPort = new JTextField();
		btnBuildEntity = new Button();
		comboDB = new JComboBox<>();
		dataTable = new JTable() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return true;
				}
				return false;
			}
		};
		setBackground(SystemColor.control);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});
		setLayout(null);

		btnSelectFile.setLabel("选择文件");
		btnSelectFile.setName("");
		btnSelectFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSelectFileActionPerformed(evt);
			}
		});
		add(btnSelectFile);
		btnSelectFile.setBounds(20, 40, 70, 26);
		
		btnSync.setLabel("同步");
		btnSync.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBuildingActionPerformed(evt);
			}
		});
		add(btnSync);
		btnSync.setBounds(100, 40, 40, 26);
		
		btnBuildEntity.setActionCommand("生成实体");
		btnBuildEntity.setLabel("生成实体");
		btnBuildEntity.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnBuildEntityActionPerformed(evt, "GBK");
			}
		});
		add(btnBuildEntity);
		btnBuildEntity.setBounds(150, 40, 70, 26);

		dataTable.setModel(new DefaultTableModel(new Object[][] {}, columnStrs));
		// 设置表格单元格内容居中
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		dataTable.setDefaultRenderer(Object.class, renderer);
		scrTables.setViewportView(dataTable);

		add(scrTables);
		scrTables.setBounds(15, 70, 550, 220);

		labDBType.setText("库类型:");
		add(labDBType);
		labDBType.setBounds(590, 75, 53, 22);
		labDBType.setAlignment(Label.RIGHT);

		comboDB.addItem("MySql");
		comboDB.addItem("Oracle");
		comboDB.addItem("SQLServer");
		comboDB.addItem("DB2");
		comboDB.addItem("Sybase");
		add(comboDB);
		comboDB.setBounds(640, 70, 150, 22);

		labURL.setText("主机地址:");
		add(labURL);
		labURL.setBounds(590, 105, 53, 22);
		labURL.setAlignment(Label.RIGHT);

		add(txtURL);
		txtURL.setBounds(640, 100, 150, 22);

		labUser.setText("用户名:");
		add(labUser);
		labUser.setBounds(590, 135, 53, 22);
		labUser.setAlignment(Label.RIGHT);

		add(txtUser);
		txtUser.setBounds(640, 130, 150, 22);

		labPassword.setText("密码:");
		add(labPassword);
		labPassword.setBounds(590, 165, 53, 22);
		labPassword.setAlignment(Label.RIGHT);

		add(txtPassword);
		txtPassword.setBounds(640, 160, 150, 22);
		
		labPort.setText("端口:");
		add(labPort);
		labPort.setBounds(590, 195, 53, 22);
		labPort.setAlignment(Label.RIGHT);

		add(txtPort);
		txtPort.setBounds(640, 190, 150, 22);
		
		labDB.setText("库实例:");
		add(labDB);
		labDB.setBounds(590, 225, 53, 22);
		labDB.setAlignment(Label.RIGHT);

		add(txtDB);
		txtDB.setBounds(640, 220, 150, 22);

		btnTestCon.setLabel("测试连接");
		btnTestCon.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnTestConActionPerformed(evt);
			}
		});
		add(btnTestCon);
		btnTestCon.setBounds(735, 260, 57, 26);

		add(txtConsole);
		txtConsole.setBounds(15, 310, 800, 270);

		pack();
	}

	/**
	 * 初始化窗口
	 */
	private void initWindow() {
		this.setResizable(false);// 不可修改大小
		this.setTitle("数据库工具");
		this.setSize(820, 600);

		// 初始化文件选择器
		fileDialog = new FileDialog(this);
		fileDialog.setMultipleMode(true);// 多选

		// 初始化窗口位置
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		// 初始化表格
		initTabTable(null);

		print("根目录:" + FileUtil.getProjectPath());
	}

	/**
	 * 退出应用
	 * 
	 * @param evt
	 */
	private void exitForm(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitForm
		System.exit(0);
	}

	/**
	 * 测试数据库连接
	 */
	private void btnTestConActionPerformed(java.awt.event.ActionEvent evt) {
		String dbType = comboDB.getSelectedItem().toString();
		String url = txtURL.getText();
		String user = txtUser.getText();
		String pwd = new String(txtPassword.getPassword());
		String dbName = txtDB.getText();

		con = new ConConfig(dbType, url, user, pwd);
		if (ConnectionHelper.testConnection(con)) {
			print("连接成功.");
		} else {
			print("连接失败.");
		}

//		con.setDbName(dbName);
//		if (!ConnectionHelper.testConnection(con)) {
//			print("[" + dbName + "]库不存在或无权限.");
//			print("正在初始化数据库...");
//			MssqlDBHelper.initDB(url, user, pwd, dbName);
//			print("数据库初始化完毕..");
//		}
	}

	/**
	 * 点击同步
	 */
	private void btnBuildingActionPerformed(java.awt.event.ActionEvent evt) {
		if (!isSelect()) {
			return;// 没有选中的行
		}

		// 有选中的行
		print("开始同步······");
		print("开始测试连接······");

		String dbType = comboDB.getSelectedItem().toString();
		String url = txtURL.getText();
		String user = txtUser.getText();
		String pwd = new String(txtPassword.getPassword());
		String dbName = txtDB.getText();

		con = new ConConfig(dbType, url, user, pwd);

		// 测试连接
		boolean isConOk = ConnectionHelper.testConnection(con);
		if (!isConOk) {
			print("连接失败！");
			print("同步失败！");
			return;
		}
		print("连接成功！");
		con.setDbName(dbName);

		// 测试数据库是否存在或有权限
		boolean isDBOk = ConnectionHelper.testConnection(con);
		if (!isDBOk) {
			print("正在初始化数据库······");
			MssqlDBHelper.initDB(url, user, pwd, dbName);
			print("数据库初始化成功！");
		}

		// 开始同步表
		String filePath = FileUtil.getProjectPath() + "createTable.sql";

		Map<String, Table> excelTemps = new HashMap<String, Table>();
		for (int i = 0; i < dataTable.getRowCount(); i++) {
			Boolean isSelect = (Boolean) dataTable.getModel().getValueAt(i, 0);
			if (isSelect) {
				Table t = (Table) dataTable.getModel().getValueAt(i, 4);
				excelTemps.put(t.getTableName(), t);
			}
		}

//		SyncDbBiz.builderTable(this, con, excelTemps);// 同步库
//		SyncDbBiz.builderSql(this, excelTemps, filePath);// 生成sql脚本

		print("同步完成！");
	}

	/**
	 * 点击生成实体
	 */
	private void btnBuildEntityActionPerformed(ActionEvent evt, String encoding) {
		Map<String, Table> tableMap = new LinkedHashMap<String, Table>();
		try {
			if (dataTable.getRowCount() > 0) {

				for (int i = 0; i < dataTable.getRowCount(); i++) {
					Boolean isSelect = (Boolean) dataTable.getModel().getValueAt(i, 0);
					if (isSelect) {
						Table table = (Table) dataTable.getModel().getValueAt(i, 4);
						tableMap.put(table.getTableName(), table);
					}
				}

				print("生成实体类······");
//				EntityBuilderBiz.builder(tableMap, encoding);// 生成实体
			} else {
				throw new Exception("无数据库表！");
			}
		} catch (Exception e) {
			print("实体类生成失败，失败原因：" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 选择文件
	 */
	private void btnSelectFileActionPerformed(java.awt.event.ActionEvent evt) {
		fileDialog.setVisible(true);// 显示文件选择器
		File[] fs = fileDialog.getFiles();
		if (fs.length > 0) {
			excelTables.clear();// 清空所有
			for (int i = 0; i < fs.length; i++) {
				try {
					print("加载数据库表······");
					Map<String, Table> ts = ExcelHelper.getAllTables(fs[i], true);
					excelTables.putAll(ts);
					print("加载数据库表完成！");
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
			initTabTable(null);// 将excelTables中的数据，添加到tabTabes控件中
		}
	}

	/**
	 * 将excelTables中的数据，添加到tabTabes控件中
	 * 
	 * @param str
	 *            过滤字符
	 */
	private void initTabTable(String str) {
		dataTable.removeAll();
		ArrayList<Object[]> arrTemp = new ArrayList<Object[]>();
		for (String key : excelTables.keySet()) {
			if (str != null) {// 需要过滤
				if (key.indexOf(str) < 0) {
					continue;
				}
			}
			Table ttemp = excelTables.get(key);
			arrTemp.add(new Object[] { false, ttemp.getTableName(), ttemp.getTableNameCH(), ttemp.getPackName(), ttemp });
		}

		// 构建数据数组
		Object[][] tarr = new Object[arrTemp.size()][5];
		for (int i = 0; i < tarr.length; i++) {
			for (int j = 0; j < 5; j++) {
				tarr[i][j] = arrTemp.get(i)[j];
			}
		}
		dataTable.setModel(new DefaultTableModel(tarr, columnStrs));

		// 第一列为选择列
		dataTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JCheckBox checkBox = new JCheckBox();
				if ("true".equals(value.toString())) {
					checkBox.setSelected(true);
				}
				return checkBox;
			}
		});
		dataTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		setCW(0, 22);
		setCW(1, 164);
		setCW(2, 200);
		setCW(3, 164);
		setCW(4, 0);
	}

	/**
	 * 设置列宽
	 */
	private void setCW(int index, int width) {
		TableColumn column = dataTable.getColumnModel().getColumn(index);
		if (column != null) {
			column.setPreferredWidth(width);
			column.setMinWidth(width);
		}
	}

	/**
	 * 判断tabTables是否有选中的行
	 */
	private boolean isSelect() {
		for (int i = 0; i < dataTable.getRowCount(); i++) {
			Boolean isSelect = (Boolean) dataTable.getModel().getValueAt(i, 0);
			if (isSelect) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将信息输出到控制台
	 */
	public static void print(String str) {
		txtConsole.append("[" + sdf.format(new Date()) + "] " + str + "\r\n");
	}

	public void textSet(JTextField field) {
		field.setBackground(new Color(255, 255, 255));
		field.setPreferredSize(new Dimension(150, 28));
		MatteBorder border = new MatteBorder(0, 0, 2, 0, new Color(192, 192, 192));
		field.setBorder(border);
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainWindow2().setVisible(true);
			}
		});
	}
}