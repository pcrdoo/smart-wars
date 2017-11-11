package view;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import debug.Measurement;
import debug.PerformanceMonitor;
import main.Constants;
import model.Asteroid;
import model.Wormhole;
import model.Mirror;
import util.ImageCache;
import util.Vector2D;
import view.gfx.AnimatedSprite;
import view.gfx.Sparks;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleAffectorDeceleration;
import view.gfx.particles.ParticleAffectorRotation;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.SpriteParticleRenderer;

public class WormholeView extends EntityView {
	private Wormhole wormhole;
	private BufferedImage wormholeSprite;
	private BufferedImage glowSprite;
	private BufferedImage shadowSprite;
	private double spriteAngle;
	private double birthTimeRemaining;
	private ParticleSystem swirl;
	private PointParticleEmitter swirlEmitter;
	
	public WormholeView(Wormhole wormhole) {
		this.wormhole = wormhole;
		wormholeSprite = ImageCache.getInstance().get("assets/wormhole.png");
		glowSprite = ImageCache.getInstance().get("assets/wormhole-glow.png");
		shadowSprite = ImageCache.getInstance().get("assets/wormhole-shadow.png");
		spriteAngle = 0;
		birthTimeRemaining = Constants.WORMHOLE_BIRTH_TIME;
		
		swirl = new ParticleSystem(new SpriteParticleRenderer(ImageCache.getInstance().get("assets/wormhole-particle.png")), 200);
		swirl.addEmitter(swirlEmitter = new PointParticleEmitter(0.0, 1.0, 0.1, wormhole.getPosition(), new Vector2D(10, 10), 200, 10.0, 0, 2 * Math.PI));
		swirl.addAffector(new ParticleAffectorDecay(1.0));
		swirl.addAffector(new ParticleAffectorDeceleration(200));
		swirl.addAffector(new ParticleAffectorRotation(wormhole.getPosition(), Math.PI / 2));
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
		
		swirl.update(dt);
	}

	@Override
	public void draw(Graphics2D g) {
		double wormholeIntensity = 1.0;
		boolean fadingIn = false;
		if (birthTimeRemaining > 0) {
			wormholeIntensity = 1.0 - birthTimeRemaining / Constants.WORMHOLE_BIRTH_TIME;
			fadingIn = true;
		} else if (wormhole.getTimeRemaining() < Constants.WORMHOLE_LIFETIME - Constants.WORMHOLE_DEATH_START_TIME) {
			wormholeIntensity = wormhole.getTimeRemaining() / (Constants.WORMHOLE_LIFETIME - Constants.WORMHOLE_DEATH_START_TIME);
		}
		
		int w = wormholeSprite.getWidth(), h = wormholeSprite.getHeight();
		int wGlow = glowSprite.getWidth(), hGlow = glowSprite.getHeight();
		int x = (int) (wormhole.getPosition().getX()), y = (int) (wormhole.getPosition().getY());
		
		Composite old = g.getComposite();
		if (wormholeIntensity < 1.0) {
			float glowAlpha = fadingIn ? 1.0f - (float)wormholeIntensity : (float)wormholeIntensity;
		
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
			g.drawImage(glowSprite, x - wGlow / 2, y - hGlow / 2, null);
			
			swirlEmitter.setSpawnsPerSecond(fadingIn ? wormholeIntensity * 400.0 : 0.0);
		}
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) wormholeIntensity));
		g.drawImage(shadowSprite, x - shadowSprite.getWidth() / 2, y - shadowSprite.getHeight() / 2, null);

		Composite spriteComposite = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		Measurement ms = PerformanceMonitor.getInstance().measure("CompSwirl");
		swirl.draw(g);
		ms.done();
		g.setComposite(spriteComposite);
		
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
