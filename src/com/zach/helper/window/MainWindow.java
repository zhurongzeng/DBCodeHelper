package com.zach.helper.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.zach.helper.biz.EntityBuilderBiz;
import com.zach.helper.entity.Table;
import com.zach.helper.helper.ExcelHelper;
import com.zach.helper.util.FileUtil;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JFrame mainFrame;
	private JTabbedPane tabbedPane;
	private JPanel panelBuild;
	private JPanel panelSync;
	private JPanel panelConsole;
	private JButton btnSelectFile;
	private JButton btnBuildEntity;
	private JButton btnSync;
	private JScrollPane scrTables;
	private JScrollPane scrConsole;
	private JTable dataTable;
	private static MenuTextArea txtConsole;
	private FileDialog fileDialog;
	
	Map<String, Table> excelTables = new LinkedHashMap<String, Table>();
	private String[] columns = new String[] { "", "表名", "中文名", "包名", "" };// 列名
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// 时间格式

	public MainWindow() {
		initComponents();
		initWindow();
	}

	private void initComponents() {
		mainFrame = new JFrame();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panelBuild = new JPanel();
		panelSync = new JPanel();
		panelConsole = new JPanel();
		btnSelectFile = new JButton("选择文件");
		btnBuildEntity = new JButton("生成实体");
		btnSync = new JButton("同步");
		scrTables = new JScrollPane();
		scrConsole = new JScrollPane();
		txtConsole = new MenuTextArea();
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

		Container c = getContentPane();
		c.setLayout(new BorderLayout(0, 0));
		panelBuild.setLayout(null);
		panelSync.setLayout(null);
		panelConsole.setLayout(null);
		panelConsole.setPreferredSize(new Dimension(790, 280));

		// 选择文件
		btnSelectFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				btnSelectFileActionPerformed(evt);
			}
		});
		panelBuild.add(btnSelectFile);
		btnSelectFile.setBounds(10, 10, 86, 25);

		// 生成实体
		btnBuildEntity.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnBuildEntityActionPerformed(evt, "GBK");
			}
		});
		panelBuild.add(btnBuildEntity);
		btnBuildEntity.setBounds(100, 10, 86, 25);

		// 数据表格
		dataTable.setModel(new DefaultTableModel(new Object[][] {}, columns));
		// 设置表格单元格内容居中
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		dataTable.setDefaultRenderer(Object.class, renderer);
		scrTables.setViewportView(dataTable);
		panelBuild.add(scrTables);
		scrTables.setBounds(10, 45, 790, 210);

		// 同步
		btnSync.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				btnBuildingActionPerformed(evt);
			}
		});
		panelSync.add(btnSync);
		btnSync.setBounds(10, 10, 70, 25);

		// 控制台
		txtConsole.setEditable(false);
		scrConsole.setViewportView(txtConsole);
		panelConsole.add(scrConsole);
		scrConsole.setBounds(10, 5, 795, 270);

		// 将标签面板加入到选项卡面板对象上
		tabbedPane.addTab("生成实体", null, panelBuild, "生成Java实体类");
		tabbedPane.addTab("同步", null, panelSync, "同步数据库表");

		c.add(BorderLayout.CENTER, tabbedPane);
		c.add(BorderLayout.SOUTH, panelConsole);

		mainFrame.add(c);
		mainFrame.pack();
	}

	private void initWindow() {
		mainFrame.setResizable(false);// 不可修改大小
		mainFrame.setTitle("数据库工具");
		mainFrame.setSize(820, 600);

		// 初始化窗口位置
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = mainFrame.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}

		mainFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 初始化文件选择器
		fileDialog = new FileDialog(this);
		fileDialog.setMultipleMode(false);// 多选

		// 初始化表格
		initTabTable(null);

		print("根目录:" + FileUtil.getProjectPath());
	}

	/**
	 * 选择文件
	 */
	private void btnSelectFileActionPerformed(ActionEvent evt) {
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
				EntityBuilderBiz.builder(tableMap, encoding);// 生成实体
			} else {
				throw new Exception("无数据库表！");
			}
		} catch (Exception e) {
			print("实体类生成失败，失败原因：" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 点击同步
	 */
	private void btnBuildingActionPerformed(ActionEvent evt) {
		// if (!isSelect()) {
		// return;// 没有选中的行
		// }
		//
		// // 有选中的行
		// print("开始同步······");
		// print("开始测试连接······");
		//
		// String dbType = comboDB.getSelectedItem().toString();
		// String url = txtURL.getText();
		// String user = txtUser.getText();
		// String pwd = new String(txtPassword.getPassword());
		// String dbName = txtDB.getText();
		//
		// con = new ConConfig(dbType, url, user, pwd);
		//
		// // 测试连接
		// boolean isConOk = ConnectionHelper.testConnection(con);
		// if (!isConOk) {
		// print("连接失败！");
		// print("同步失败！");
		// return;
		// }
		// print("连接成功！");
		// con.setDbName(dbName);
		//
		// // 测试数据库是否存在或有权限
		// boolean isDBOk = ConnectionHelper.testConnection(con);
		// if (!isDBOk) {
		// print("正在初始化数据库······");
		// MssqlDBHelper.initDB(url, user, pwd, dbName);
		// print("数据库初始化成功！");
		// }
		//
		// // 开始同步表
		// String filePath = FileUtil.getProjectPath() + "createTable.sql";
		//
		// Map<String, Table> excelTemps = new HashMap<String, Table>();
		// for (int i = 0; i < dataTable.getRowCount(); i++) {
		// Boolean isSelect = (Boolean) dataTable.getModel().getValueAt(i, 0);
		// if (isSelect) {
		// Table t = (Table) dataTable.getModel().getValueAt(i, 4);
		// excelTemps.put(t.getTableName(), t);
		// }
		// }

		// SyncDbBiz.builderTable(this, con, excelTemps);// 同步库
		// SyncDbBiz.builderSql(this, excelTemps, filePath);// 生成sql脚本

		print("同步完成！");
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
			arrTemp.add(
					new Object[] { false, ttemp.getTableName(), ttemp.getTableNameCH(), ttemp.getPackName(), ttemp });
		}

		// 构建数据数组
		Object[][] tarr = new Object[arrTemp.size()][5];
		for (int i = 0; i < tarr.length; i++) {
			for (int j = 0; j < 5; j++) {
				tarr[i][j] = arrTemp.get(i)[j];
			}
		}
		dataTable.setModel(new DefaultTableModel(tarr, columns));

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
		setCW(1, 244);
		setCW(2, 280);
		setCW(3, 244);
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
	 * 将信息输出到控制台
	 */
	public static void print(String str) {
		txtConsole.append("[" + sdf.format(new Date()) + "]\b" + str + "\r\n");
	}

	public static void main(String[] args) {
		new MainWindow();
	}

}