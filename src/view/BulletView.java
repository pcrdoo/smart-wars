package view;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jdesktop.swingx.graphics.BlendComposite;

import main.Constants;
import model.Bullet;
import util.ImageCache;

public class BulletView implements Drawable, Updatable {
	private Bullet bullet;
	private BufferedImage bulletSprite, particleSprite;
	
	int[] particleX, particleY;
	double[] particleTime;
	double time = 0.0;
	
	public BulletView(Bullet bullet) {
		this.bullet = bullet;
		bulletSprite = ImageCache.getInstance().get("assets/bullet.png");
		particleSprite = ImageCache.getInstance().get("assets/fire.png");
		
		particleX = new int[Constants.MAX_BULLET_FIRE_PARTICLES];
		particleY = new int[Constants.MAX_BULLET_FIRE_PARTICLES];
		particleTime = new double[Constants.MAX_BULLET_FIRE_PARTICLES];
		
		for (int i = 0; i < Constants.MAX_BULLET_FIRE_PARTICLES; i++) {
			particleX[i] = (int)bullet.getPosition().getdX();
			particleY[i] = (int)bullet.getPosition().getdY();
			particleTime[i] = -1.0;
		}
	}

	private void spawnParticle(int x, int y) {
		int minI = 0;
		double minTime = particleTime[0];
		for (int i = 1; i < Constants.MAX_BULLET_FIRE_PARTICLES; i++) {
			if (particleTime[i] < minTime) {
				minTime = particleTime[i];
				minI = i;
			}
		}
		
		particleTime[minI] = Constants.BULLET_FIRE_PARTICLE_LIFETIME;
		particleX[minI] = x;
		particleY[minI] = y;
	}
	
	@Override
	public void update(double dt) {
		time += dt;
		if (time > Constants.BULLET_FIRE_PARTICLE_SPAWN_INTERVAL) {
			time = 0.0;
			spawnParticle((int)bullet.getPosition().getdX() + (int)(Math.random() * 10.0 - 5) - bulletSprite.getWidth()/2, (int)bullet.getPosition().getdY());
		}
		
		for (int i = 0; i < Constants.MAX_BULLET_FIRE_PARTICLES; i++) {
			particleTime[i] -= dt; 
		}
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(bulletSprite,
				(int)(bullet.getPosition().getdX() - bulletSprite.getWidth() / 2),
				(int)(bullet.getPosition().getdY() + bulletSprite.getHeight() / 2), null);
		
		//g.setComposite(BlendComposite.Add);
		for (int i = 0; i < Constants.MAX_BULLET_FIRE_PARTICLES; i++) {
			if (particleTime[i] > 0) {
				g.drawImage(particleSprite, particleX[i], particleY[i], null);
			}
		}
	}
}
