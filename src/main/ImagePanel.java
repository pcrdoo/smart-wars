package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private BufferedImage image;
	
	public ImagePanel(BufferedImage image) {
		this.image = image;
		setSize(image.getWidth(), image.getHeight());
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
