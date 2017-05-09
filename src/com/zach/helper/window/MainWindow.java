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
	private String[] columns = new String[] { "", "����", "������", "����", "" };// ����
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// ʱ���ʽ

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
		btnSelectFile = new JButton("ѡ���ļ�");
		btnBuildEntity = new JButton("����ʵ��");
		btnSync = new JButton("ͬ��");
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

		// ѡ���ļ�
		btnSelectFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				btnSelectFileActionPerformed(evt);
			}
		});
		panelBuild.add(btnSelectFile);
		btnSelectFile.setBounds(10, 10, 86, 25);

		// ����ʵ��
		btnBuildEntity.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnBuildEntityActionPerformed(evt, "GBK");
			}
		});
		panelBuild.add(btnBuildEntity);
		btnBuildEntity.setBounds(100, 10, 86, 25);

		// ���ݱ��
		dataTable.setModel(new DefaultTableModel(new Object[][] {}, columns));
		// ���ñ��Ԫ�����ݾ���
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		dataTable.setDefaultRenderer(Object.class, renderer);
		scrTables.setViewportView(dataTable);
		panelBuild.add(scrTables);
		scrTables.setBounds(10, 45, 790, 210);

		// ͬ��
		btnSync.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				btnBuildingActionPerformed(evt);
			}
		});
		panelSync.add(btnSync);
		btnSync.setBounds(10, 10, 70, 25);

		// ����̨
		txtConsole.setEditable(false);
		scrConsole.setViewportView(txtConsole);
		panelConsole.add(scrConsole);
		scrConsole.setBounds(10, 5, 795, 270);

		// ����ǩ�����뵽ѡ���������
		tabbedPane.addTab("����ʵ��", null, panelBuild, "����Javaʵ����");
		tabbedPane.addTab("ͬ��", null, panelSync, "ͬ�����ݿ��");

		c.add(BorderLayout.CENTER, tabbedPane);
		c.add(BorderLayout.SOUTH, panelConsole);

		mainFrame.add(c);
		mainFrame.pack();
	}

	private void initWindow() {
		mainFrame.setResizable(false);// �����޸Ĵ�С
		mainFrame.setTitle("���ݿ⹤��");
		mainFrame.setSize(820, 600);

		// ��ʼ������λ��
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

		// ��ʼ���ļ�ѡ����
		fileDialog = new FileDialog(this);
		fileDialog.setMultipleMode(false);// ��ѡ

		// ��ʼ�����
		initTabTable(null);

		print("��Ŀ¼:" + FileUtil.getProjectPath());
	}

	/**
	 * ѡ���ļ�
	 */
	private void btnSelectFileActionPerformed(ActionEvent evt) {
		fileDialog.setVisible(true);// ��ʾ�ļ�ѡ����
		File[] fs = fileDialog.getFiles();
		if (fs.length > 0) {
			excelTables.clear();// �������
			for (int i = 0; i < fs.length; i++) {
				try {
					print("�������ݿ������������");
					Map<String, Table> ts = ExcelHelper.getAllTables(fs[i], true);
					excelTables.putAll(ts);
					print("�������ݿ����ɣ�");
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
			initTabTable(null);// ��excelTables�е����ݣ���ӵ�tabTabes�ؼ���
		}
	}

	/**
	 * �������ʵ��
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
				print("����ʵ���ࡤ����������");
				EntityBuilderBiz.builder(tableMap, encoding);// ����ʵ��
			} else {
				throw new Exception("�����ݿ��");
			}
		} catch (Exception e) {
			print("ʵ��������ʧ�ܣ�ʧ��ԭ��" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * ���ͬ��
	 */
	private void btnBuildingActionPerformed(ActionEvent evt) {
		// if (!isSelect()) {
		// return;// û��ѡ�е���
		// }
		//
		// // ��ѡ�е���
		// print("��ʼͬ��������������");
		// print("��ʼ�������ӡ�����������");
		//
		// String dbType = comboDB.getSelectedItem().toString();
		// String url = txtURL.getText();
		// String user = txtUser.getText();
		// String pwd = new String(txtPassword.getPassword());
		// String dbName = txtDB.getText();
		//
		// con = new ConConfig(dbType, url, user, pwd);
		//
		// // ��������
		// boolean isConOk = ConnectionHelper.testConnection(con);
		// if (!isConOk) {
		// print("����ʧ�ܣ�");
		// print("ͬ��ʧ�ܣ�");
		// return;
		// }
		// print("���ӳɹ���");
		// con.setDbName(dbName);
		//
		// // �������ݿ��Ƿ���ڻ���Ȩ��
		// boolean isDBOk = ConnectionHelper.testConnection(con);
		// if (!isDBOk) {
		// print("���ڳ�ʼ�����ݿ⡤����������");
		// MssqlDBHelper.initDB(url, user, pwd, dbName);
		// print("���ݿ��ʼ���ɹ���");
		// }
		//
		// // ��ʼͬ����
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

		// SyncDbBiz.builderTable(this, con, excelTemps);// ͬ����
		// SyncDbBiz.builderSql(this, excelTemps, filePath);// ����sql�ű�

		print("ͬ����ɣ�");
	}

	/**
	 * ��excelTables�е����ݣ���ӵ�tabTabes�ؼ���
	 * 
	 * @param str
	 *            �����ַ�
	 */
	private void initTabTable(String str) {
		dataTable.removeAll();
		ArrayList<Object[]> arrTemp = new ArrayList<Object[]>();
		for (String key : excelTables.keySet()) {
			if (str != null) {// ��Ҫ����
				if (key.indexOf(str) < 0) {
					continue;
				}
			}
			Table ttemp = excelTables.get(key);
			arrTemp.add(
					new Object[] { false, ttemp.getTableName(), ttemp.getTableNameCH(), ttemp.getPackName(), ttemp });
		}

		// ������������
		Object[][] tarr = new Object[arrTemp.size()][5];
		for (int i = 0; i < tarr.length; i++) {
			for (int j = 0; j < 5; j++) {
				tarr[i][j] = arrTemp.get(i)[j];
			}
		}
		dataTable.setModel(new DefaultTableModel(tarr, columns));

		// ��һ��Ϊѡ����
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
	 * �����п�
	 */
	private void setCW(int index, int width) {
		TableColumn column = dataTable.getColumnModel().getColumn(index);
		if (column != null) {
			column.setPreferredWidth(width);
			column.setMinWidth(width);
		}
	}

	/**
	 * ����Ϣ���������̨
	 */
	public static void print(String str) {
		txtConsole.append("[" + sdf.format(new Date()) + "]\b" + str + "\r\n");
	}

	public static void main(String[] args) {
		new MainWindow();
	}

}