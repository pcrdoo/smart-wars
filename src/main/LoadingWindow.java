package main;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import util.ImageCache;

public class LoadingWindow extends JFrame {
	public LoadingWindow() {
		super("Smart Wars | Loading...");
		BufferedImage splash = ImageCache.getInstance().get("splash.png");
		getContentPane().setSize(splash.getWidth(), splash.getHeight());
		setLayout(new BorderLayout());
		getContentPane().add(new ImagePanel(splash));
		pack();

		setLocationRelativeTo(null);
		setVisible(true);
	}
}
