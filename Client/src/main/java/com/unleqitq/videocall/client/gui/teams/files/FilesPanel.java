package com.unleqitq.videocall.client.gui.teams.files;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class FilesPanel {
	
	JPanel panel = new JPanel();
	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	JTree filesTree = new JTree(root);
	
	public FilesPanel() {
		panel.setLayout(new BorderLayout());
		
	}
	
}
