package com.zach.helper.window;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import java.awt.datatransfer.Clipboard;
import javax.swing.JTextArea;

/**
 * ���й��ܲ˵���JTextArea
 */
public class MenuTextArea extends JTextArea implements MouseListener {
	private static final long serialVersionUID = -2308615404205560110L;
	private JPopupMenu menu; // �����˵�
	private JMenuItem copy, clear; // ���ܲ˵�

	public MenuTextArea() {
		super();
		init();
	}

	private void init() {
		this.addMouseListener(this);
		menu = new JPopupMenu();
		menu.add(copy = new JMenuItem("����"));
		menu.add(clear = new JMenuItem("���"));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		this.add(menu);
	}

	/**
	 * �˵�����
	 * 
	 * @param e
	 */
	public void action(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(copy.getText())) { // ����
			this.copy();
		} else if (str.equals(clear.getText())) { // ���
			this.clear();
		}
	}
	
	private void clear() {
		this.setText("");
	}

	/**
	 * ���а����Ƿ����ı����ݿɹ�ճ��
	 *
	 * @return trueΪ���ı�����
	 */
	public boolean isClipboardString() {
		boolean b = false;
		Clipboard clipboard = this.getToolkit().getSystemClipboard();
		Transferable content = clipboard.getContents(this);
		try {
			if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
				b = true;
			}
		} catch (Exception e) {
		}
		return b;
	}

	/**
	 * �ı�������Ƿ�߱����Ƶ�����
	 *
	 * @return trueΪ�߱�
	 */
	public boolean isCanCopy() {
		boolean b = false;
		int start = this.getSelectionStart();
		int end = this.getSelectionEnd();
		if (start != end)
			b = true;
		return b;
	}

	private boolean isCanClear() {
		return this.getText().length() > 0;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			copy.setEnabled(isCanCopy());
			clear.setEnabled(isCanClear());
			menu.show(this, e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {
	}
}