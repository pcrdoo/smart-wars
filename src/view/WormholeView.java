package view;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Constants;
import model.Asteroid;
import model.Wormhole;
import model.Mirror;
import util.ImageCache;
import util.Vector2D;
import view.gfx.AnimatedSprite;
import view.gfx.Sparks;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;

public class WormholeView extends EntityView {
	private Wormhole wormhole;
	private BufferedImage wormholeSprite;
	private BufferedImage glowSprite;
	private double spriteAngle;
	private double birthTimeRemaining;

	public WormholeView(Wormhole wormhole) {
		this.wormhole = wormhole;
		wormholeSprite = ImageCache.getInstance().get("assets/wormhole.png");
		glowSprite = ImageCache.getInstance().get("assets/wormhole-glow.png");
		spriteAngle = 0;
		birthTimeRemaining = Constants.WORMHOLE_BIRTH_TIME;
	}

	@Override
	public void update(double dt) {
		spriteAngle += Constants.WORMHOLE_ANGULAR_VELOCITY * dt;
		while (spriteAngle > Math.PI) {
			spriteAngle -= 2 * Math.PI;
		}
		birthTimeRemaining -= dt;
		if (birthTimeRemaining < 0) {
			birthTimeRemaining = 0;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		int w = wormholeSprite.getWidth(), h = wormholeSprite.getHeight();
		int wGlow = glowSprite.getWidth(), hGlow = glowSprite.getHeight();
		int x = (int) (wormhole.getPosition().getdX()), y = (int) (wormhole.getPosition().getdY());
		Composite old = g.getComposite();
		if (birthTimeRemaining > 0) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					(float) (birthTimeRemaining / Constants.WORMHOLE_BIRTH_TIME)));
			g.drawImage(glowSprite, x - wGlow / 2, y - hGlow / 2, null);
		}
		AffineTransform cache = g.getTransform();
		g.setTransform(new AffineTransform());
		g.scale(cache.getScaleX(), cache.getScaleY());
		g.translate(x, y);
		g.rotate(spriteAngle);
		g.translate(-w / 2, -h / 2);
		g.drawImage(wormholeSprite, 0, 0, null);
		g.setTransform(cache);

		g.setComposite(old);
	}
}
