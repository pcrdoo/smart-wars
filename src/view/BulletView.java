package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Bullet;
import util.ImageCache;

public class BulletView implements Drawable, Updatable {
	private Bullet bullet;
	private BufferedImage bulletSprite, particleSprite;
	
	public BulletView(Bullet bullet) {
		this.bullet = bullet;
		bulletSprite = ImageCache.getInstance().get("assets/bullet.png");
		particleSprite = ImageCache.getInstance().get("assets/fire.png");
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(bulletSprite,
				(int)(bullet.getPosition().getdX() - bulletSprite.getWidth() / 2),
				(int)(bullet.getPosition().getdY() + bulletSprite.getHeight() / 2), null);
	}
}
