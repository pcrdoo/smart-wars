package view;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import view.gfx.ParticleSystem;
import main.Constants;
import model.Player;
import util.ImageCache;
import util.Vector2D;
import view.gfx.AdditiveComposite;
import view.gfx.Explosion;
import view.gfx.PointParticleEmitter;

public class PlayerView implements Drawable, Updatable {
	private final static int FRAME_COUNT = 6;
	private final static double FLARE_DURATION = 0.2;
	
	private BufferedImage sprite;
	private BufferedImage flare;
	private Player player;
	private int frame = 0;
	private int spriteWidth, spriteHeight, flareWidth, flareHeight;
	private double time = 0.0;
	private double flareOpacity = 0.0;
	private ParticleSystem trail;
	private PointParticleEmitter trailEmitter;
	private Explosion explosion;
	
	public PlayerView(Player player, boolean alternativeSprite) {
		sprite = ImageCache.getInstance().get(alternativeSprite ? "assets/player2.png" : "assets/player1.png");
		flare = ImageCache.getInstance().get("assets/player-flare.png");
		spriteWidth = sprite.getWidth();
		spriteHeight = sprite.getHeight() / FRAME_COUNT;
		flareWidth = flare.getWidth();
		flareHeight = flare.getHeight();
		
		this.player = player;
		trail = new ParticleSystem(ImageCache.getInstance().get("assets/player-trail.png"), 50, 0.4);
		trailEmitter = new PointParticleEmitter(0.0, 0.4, 0.0, player.getPosition(), new Vector2D(2.0, 5.0), 30.0, 0.0, 0, 2 * Math.PI);
		
		trail.addEmitter(trailEmitter);
	}
	
	public void update(double dt) {
		time += dt;
		
		double frameTime = 1.0 / Constants.PLAYER_ANIMATION_FPS;
		while (time > frameTime) {
			time -= frameTime;
			frame++;
		}
		
		frame %= FRAME_COUNT;
		
		flareOpacity -= dt * 1.0 / FLARE_DURATION;
		if (flareOpacity < 0.0) {
			flareOpacity = 0.0;
		}

		trailEmitter.setSpawnsPerSecond(player.getVelocity().length() / Constants.PLAYER_SPEED * 25.0 + 12.0);
		trailEmitter.setPosition(player.getPosition());
		trail.update(dt);
		
		if (explosion != null) {
			explosion.update(dt);

			if (explosion.isDone()) {
				explosion = null;
			}
		}
	}
	
	public void onShoot() {
		flareOpacity = 1.0;
	}

	public void onHit() {
		explosion = new Explosion(player.getPosition(), 1.5, 0.1);
	}
	
	public void draw(Graphics2D g) {
		trail.draw(g);
		int x = (int)player.getPosition().getdX() - spriteWidth / 2;
		int y = (int)player.getPosition().getdY() - spriteHeight / 2;

		g.drawImage(sprite, x, y, x + spriteWidth, y + spriteHeight, 0, frame * spriteHeight, spriteWidth - 1, (frame + 1) * spriteHeight - 1, null);
		
		if (flareOpacity > 0.0) {
			int fx = (int)player.getPosition().getdX() - flareWidth / 2;
			int fy = (int)player.getPosition().getdY() - flareHeight / 2;
			
			Composite old = g.getComposite();
			g.setComposite(AdditiveComposite.getInstance((int)(flareOpacity * 255.0)));
			g.drawImage(flare, fx, fy, fx + flareWidth, fy + flareHeight, 0, 0, flareWidth, flareHeight, null);
			g.setComposite(old);
		}
		
		if (explosion != null) {
			explosion.draw(g);
		}
	}
}
