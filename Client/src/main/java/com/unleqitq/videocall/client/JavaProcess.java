package com.unleqitq.videocall.client;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaProcess {
	
	JFrame frame = new JFrame();
	JTextPane textPane = new JTextPane();
	Process process;
	Style outputStyle;
	Style errorStyle;
	
	String errorString = "";
	String outputString = "";
	
	private JavaProcess(Process process) {
		frame.setBounds(50, 50, 500, 700);
		frame.add(textPane);
		StyledDocument document = textPane.getStyledDocument();
		outputStyle = document.addStyle("output", null);
		errorStyle = document.addStyle("error", null);
		StyleConstants.setForeground(outputStyle, Color.BLACK);
		StyleConstants.setForeground(errorStyle, Color.RED);
		
		frame.setVisible(true);
		this.process = process;
		Thread thread = new Thread(this::runError);
		thread.setDaemon(true);
		thread.start();
		thread = new Thread(this::runOutput);
		thread.setDaemon(true);
		thread.start();
	}
	
	public Process getProcess() {
		return process;
	}
	
	public static JavaProcess exec(Class<?> clazz, List<String> jvmArgs, List<String> args) throws IOException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = clazz.getName();
		
		List<String> command = new ArrayList<>();
		command.add(javaBin);
		command.addAll(jvmArgs);
		command.add("-cp");
		command.add(classpath);
		command.add(className);
		command.addAll(args);
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		return new JavaProcess(process);
	}
	
	private void runError() {
		//StringBuffer buffer = new StringBuffer();
		while (process.isAlive()) {
			try {
				int b = process.getErrorStream().read();
				errorString += (char) b;
				if (b == (int) '\n') {
					textPane.getStyledDocument().insertString(textPane.getStyledDocument().getLength(), errorString,
							errorStyle);
					System.out.print("[Call Error] " + errorString);
					errorString = "";
				}
			} catch (IOException | BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void runOutput() {
		while (process.isAlive()) {
			try {
				int b = process.getInputStream().read();
				outputString += Character.toString(b);
				if (b == (int) '\n') {
					textPane.getStyledDocument().insertString(textPane.getStyledDocument().getLength(), outputString,
							outputStyle);
					System.out.print("[Call output] " + outputString);
					outputString = "";
				}
			} catch (IOException | BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
}