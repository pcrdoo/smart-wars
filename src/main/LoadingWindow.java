package main;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import util.ImageCache;

public class LoadingWindow extends JFrame {
	private static final long serialVersionUID = 1L;

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
