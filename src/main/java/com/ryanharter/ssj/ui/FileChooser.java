package com.ryanharter.ssj.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

public class FileChooser extends JPanel
{
    static private final String newline = System.getProperty("line.separator");
    JButton chooseButton;
    JFileChooser fc;

    public FileChooser() {
	super(new BorderLayout());

	// Create the file chooser
	fc = new JFileChooser();
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public File choose() {
	File file = null;
	int returnVal = fc.showOpenDialog(FileChooser.this);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    file = fc.getSelectedFile();
	}
	return file;
    }
}