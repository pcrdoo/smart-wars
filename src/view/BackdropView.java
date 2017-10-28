package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Constants;
import rafgfxlib.Util;
import util.ImageCache;

public class BackdropView implements Drawable, Updatable {
	private BufferedImage background, stars;
	private double backgroundAlpha = 0.f, starsAlpha = 0.0;
	
	public BackdropView() {
		background = ImageCache.getInstance().get("assets/background.png");
		stars = ImageCache.getInstance().get("assets/stars.png");
	}
	
	public void update(double dt) {
		backgroundAlpha += Constants.BACKGROUND_ALPHA_PER_SECOND * dt;
		starsAlpha += Constants.STARS_ALPHA_PER_SECOND * dt;
		
		while (backgroundAlpha > 1.0) {
			backgroundAlpha -= 1.0;
		}
		
		while (starsAlpha > 1.0) {
			starsAlpha -= 1.0;
		}
	}
	
	public void draw(Graphics2D g) {
		int backgroundX = (int)(backgroundAlpha * background.getWidth()),
			starsX = (int)(starsAlpha * stars.getWidth());
		
		g.drawImage(background, backgroundX, 0, null);
		g.drawImage(background, backgroundX - background.getWidth(), 0, null);
		
		g.drawImage(stars, starsX, 0, null);
		g.drawImage(stars, starsX - stars.getWidth(), 0, null);
	}
}
