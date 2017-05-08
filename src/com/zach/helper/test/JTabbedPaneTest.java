package com.zach.helper.test;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JTabbedPaneTest extends JFrame {
	private static final long serialVersionUID = 1L;

	public JTabbedPaneTest() {
		super();
		setTitle("���");
		setBounds(100, 100, 350, 150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		//TOP,BOTTOM,LEFT,RIGHT
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		// //����ѡ��Ĳ��ַ�ʽ������
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT); // ����ѡ��Ĳ��ַ�ʽ��
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int selectedIndex = tabbedPane.getSelectedIndex(); // ���ѡ�е�ѡ�����
				String title = tabbedPane.getTitleAt(selectedIndex); // ���ѡ���ǩ
				System.out.println(title);
			}
		});
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		URL resource = JTabbedPaneTest.class.getResource("imageButton.gif");
		ImageIcon imageIcon = new ImageIcon("");
		final JLabel tabLabelA = new JLabel();
		tabLabelA.setText("ѡ�A");
		tabbedPane.addTab("ѡ�A", imageIcon, tabLabelA, "����鿴ѡ�A");
		final JLabel tabLabelB = new JLabel();
		tabLabelB.setText("ѡ�B");
		tabbedPane.addTab("ѡ�B", imageIcon, tabLabelB, "����鿴ѡ�B");
		final JLabel tabLabelC = new JLabel();
		tabLabelC.setText("ѡ�C");
		tabbedPane.addTab("ѡ�C", imageIcon, tabLabelC, "����鿴ѡ�C");
		tabbedPane.setSelectedIndex(2); // ����Ĭ��ѡ�е�
		tabbedPane.setEnabledAt(0, false); // ��������0����岻����
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JTabbedPaneTest jTabbedPaneTest = new JTabbedPaneTest();
		jTabbedPaneTest.setVisible(true);
	}

}