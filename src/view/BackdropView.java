package view;

import java.awt.Color;
import java.awt.Graphics2D;

public class BackdropView implements Drawable {
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawString("neznam kako", 200, 200);
	}
}
