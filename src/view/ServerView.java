package view;

import java.awt.BorderLayout;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ServerView extends MainView {
	private JFrame frame;
	private JTextArea area;

	public ServerView() {
		frame = new JFrame("Server");
		frame.setSize(500, 500);
		area = new JTextArea();
		frame.add(area, BorderLayout.CENTER);
	}

	public void setVisible(boolean flag) {
		frame.setVisible(flag);
	}

	public void addText(String text) {
		area.append(text + "\n");
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public void draw(Graphics2D g) {
	}

}
