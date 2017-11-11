package view;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.ArrayList;

import main.Constants;
import memory.Poolable;
import memory.Pools;
import model.Asteroid;
import util.ImageCache;
import util.Vector2D;
import view.gfx.AnimatedSprite;
import view.gfx.Sparks;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;

public class AsteroidView extends EntityView implements Poolable {
	private Asteroid asteroid;
	private AnimatedSprite sprite;
	private ArrayList<Sparks> sparks;
	private boolean disintegrating;
	private double disintegrationTime;

	public AsteroidView() {
		sparks = new ArrayList<>();
	}

	public void init(Asteroid asteroid) {
		this.asteroid = asteroid;

		sprite = new AnimatedSprite(ImageCache.getInstance().get("asteroid-" + (asteroid.getType() + 1) + ".png"),
				Constants.ASTEROID_SPRITES_X, Constants.ASTEROID_SPRITES_Y, 0);
		
		disintegrating = false;
		disintegrationTime = 0.0;
	}

	@Override
	public void reset() {
		asteroid = null;
		sprite = null;
	}
	
	@Override
	public void onRemoved() {
		for (Sparks s : sparks) {
			Pools.SPARKS.free(s);
		}
		sparks.clear();	
		
		Pools.ASTEROID_VIEW.free(this);
	}
	
	public void onAsteroidHit(Vector2D position) {
		sparks.add(Pools.SPARKS.create(position, 1.5, 0.15));
	}
	
	public void onAsteroidDisintegrated() {
		disintegrating = true;
		disintegrationTime = Constants.ASTEROID_DISINTEGRATION_TIME;
	}

	public boolean isDisintegrated() {
		return disintegrating && disintegrationTime == 0.0;
	}
	
	@Override
	public void update(double dt) {
		if (disintegrating) {
			disintegrationTime -= dt;
			if(disintegrationTime < 0) {
				disintegrationTime = 0;
			}
		}
		
		ArrayList<Sparks> finishedSparks = new ArrayList<>();
		for (Sparks s : sparks) {
			s.update(dt);

			if (s.isDone()) {
				finishedSparks.add(s);
			}
		}
		
		for (Sparks s : finishedSparks) {
			sparks.remove(s);
			Pools.SPARKS.free(s);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		sprite.setFrame(asteroid.getFrame());
		if (disintegrating) {
			Composite old = g.getComposite();

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(disintegrationTime / Constants.ASTEROID_DISINTEGRATION_TIME)));
			sprite.draw(g, (int)asteroid.getPosition().getX(), (int)asteroid.getPosition().getY());
			g.setComposite(old);
		} else {
			sprite.draw(g, (int)asteroid.getPosition().getX(), (int)asteroid.getPosition().getY());	
		}

		for (Sparks s : sparks) {
			s.draw(g);
		}
	}
}
