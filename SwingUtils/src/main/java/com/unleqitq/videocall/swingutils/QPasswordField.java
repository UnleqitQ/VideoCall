package com.unleqitq.videocall.swingutils;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

public class QPasswordField extends JPasswordField {
	
	@NotNull
	private String placeholder = "";
	
	public QPasswordField() {
		this(null, null, 0);
	}
	
	public QPasswordField(String text) {
		this(null, text, 0);
	}
	
	public QPasswordField(int columns) {
		this(null, null, columns);
	}
	
	public QPasswordField(String text, int columns) {
		this(null, text, columns);
	}
	
	public QPasswordField(Document doc, String text, int columns) {
		super(doc, text, columns);
	}
	
	public void setPlaceholder(@NotNull String placeholder) {
		this.placeholder = placeholder;
	}
	
	@NotNull
	public String getPlaceholder() {
		return placeholder;
	}
	
	@Override
	protected void paintComponent(final Graphics pG) {
		super.paintComponent(pG);
		
		if (placeholder.length() == 0 || getPassword().length > 0) {
			return;
		}
		
		final Graphics2D g = (Graphics2D) pG;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getDisabledTextColor());
		g.drawString(placeholder, getInsets().left,
				pG.getFontMetrics().getMaxAscent() / 2 + getInsets().top + (getSize().height - getInsets().bottom - getInsets().top) / 2);
	}
	
	
}
