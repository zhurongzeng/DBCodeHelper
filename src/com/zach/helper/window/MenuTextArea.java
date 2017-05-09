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
 * 带有功能菜单的JTextArea
 */
public class MenuTextArea extends JTextArea implements MouseListener {
	private static final long serialVersionUID = -2308615404205560110L;
	private JPopupMenu menu; // 弹出菜单
	private JMenuItem copy, clear; // 功能菜单

	public MenuTextArea() {
		super();
		init();
	}

	private void init() {
		this.addMouseListener(this);
		menu = new JPopupMenu();
		menu.add(copy = new JMenuItem("复制"));
		menu.add(clear = new JMenuItem("清空"));
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
	 * 菜单动作
	 * 
	 * @param e
	 */
	public void action(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(copy.getText())) { // 复制
			this.copy();
		} else if (str.equals(clear.getText())) { // 清空
			this.clear();
		}
	}
	
	private void clear() {
		this.setText("");
	}

	/**
	 * 剪切板中是否有文本数据可供粘贴
	 *
	 * @return true为有文本数据
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
	 * 文本组件中是否具备复制的条件
	 *
	 * @return true为具备
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