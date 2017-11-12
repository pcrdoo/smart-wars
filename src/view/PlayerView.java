package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import debug.Measurement;
import debug.PerformanceMonitor;
import main.Constants;
import memory.Pools;
import model.Player;
import model.PlayerSide;
import util.ImageCache;
import util.Vector2D;
import view.gfx.AnimatedSprite;
import view.gfx.Explosion;
import view.gfx.Sparks;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.SpriteParticleRenderer;

public class PlayerView extends EntityView {
	private final static int FRAME_COUNT = 6;
	private final static int HEALTHBAR_X_OFFSET = 40;
	private final static double FLARE_DURATION = 0.2;

	private AnimatedSprite sprite;
	private BufferedImage flare;
	private BufferedImage healthbar;
	private Player player;
	private int frame = 0;
	private int flareWidth, flareHeight;
	private double flareOpacity = 0.0;
	private ParticleSystem trail;
	private PointParticleEmitter trailEmitter;
	private Explosion explosion;

	public PlayerView(Player player) {
		sprite = new AnimatedSprite(ImageCache.getInstance()
				.get(player.getPlayerSide() == PlayerSide.LEFT_PLAYER ? "player1.png" : "player2.png"),
				1,
				FRAME_COUNT,
				Constants.PLAYER_ANIMATION_FPS);
		
		flare = ImageCache.getInstance().get("player-flare.png");
		healthbar = ImageCache.getInstance().get("healthbar.png");
		flareWidth = flare.getWidth();
		flareHeight = flare.getHeight();

		this.player = player;
		trail = new ParticleSystem(new SpriteParticleRenderer(ImageCache.getInstance().get("player-" + (player.getPlayerSide() == PlayerSide.LEFT_PLAYER ? "left" : "right") + "-trail.png")), 150);
		trailEmitter = new PointParticleEmitter(0.0, 0.5, 0.0, player.getPosition(), new Vector2D(5.0, 2.0), 30.0, 0.0,
				0, 2 * Math.PI);

		trail.addEmitter(trailEmitter);
		trail.addAffector(new ParticleAffectorDecay(0.4));
	}

	public void update(double dt) {
		sprite.update(dt);
		
		flareOpacity -= dt * 1.0 / FLARE_DURATION;
		if (flareOpacity < 0.0) {
			flareOpacity = 0.0;
		}

		trailEmitter.setSpawnsPerSecond(player.getVelocity().length() / Constants.PLAYER_SPEED * 60.0 + 20.0);
		trailEmitter.setPosition(player.getPosition());
		trail.update(dt);

		if (explosion != null) {
			explosion.update(dt);

			if (explosion.isDone()) {
				Pools.EXPLOSION.free(explosion);
				explosion = null;
			}
		}
	}

	public void onBulletFired() {
		flareOpacity = 1.0;
	}

	public void onPlayerHit() {
		if (explosion != null) {
			Pools.EXPLOSION.free(explosion);
		}
		explosion = Pools.EXPLOSION.create(player.getPosition(), 1.0, 0.1);
	}

	public void draw(Graphics2D g) {
		Measurement ms = PerformanceMonitor.getInstance().measure("CompPlayerTrail");
		trail.draw(g);
		ms.done();
		
		sprite.draw(g, (int)player.getPosition().getX(), (int)player.getPosition().getY());

		if (flareOpacity > 0.0) {
			int fx = (int) player.getPosition().getX() - flareWidth / 2;
			int fy = (int) player.getPosition().getY() - flareHeight / 2;

			Composite old = g.getComposite();
			
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)flareOpacity);
			g.setComposite(ac);
			g.drawImage(flare, fx, fy, fx + flareWidth, fy + flareHeight, 0, 0, flareWidth, flareHeight, null);
			g.setComposite(old);
		}

		if (explosion != null) {
			explosion.draw(g);
		}

		// draw health
		int sign = player.getPlayerSide() == PlayerSide.LEFT_PLAYER ? -1 : 1;
		int hbw = healthbar.getWidth() / 2, hbh = healthbar.getHeight();
		int hbx = (int)player.getPosition().getX() - hbw / 2 + sign * HEALTHBAR_X_OFFSET, hby = (int)player.getPosition().getY() - hbh / 2;
		g.drawImage(healthbar, hbx, hby, hbx + hbw, hby + hbh, 0, 0, hbw, hbh, null);
		
		int health_height = (int)(hbh * (double)player.getCurrHealth() / Constants.PLAYER_HEALTH);
		g.drawImage(healthbar, hbx, hby + hbh - health_height, hbx + hbw, hby + hbh, hbw, hbh - health_height, 2 * hbw, hbh, null);
	}
}
