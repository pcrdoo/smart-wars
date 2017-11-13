package view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import memory.Pools;
import model.Mirror;
import model.PlayerSide;
import util.ImageCache;
import util.Vector2D;
import view.gfx.GlassSparks;

public class MirrorView extends EntityView {
	private final static double SPARKS_SPAWN_COOLDOWN = 0.2;

	private Mirror mirror;
	private BufferedImage mirrorSprite;
	private ArrayList<GlassSparks> sparks;
	private double timeSinceLastSparksSpawn = 0.0;

	public MirrorView(Mirror mirror) {
		this.mirror = mirror;
		mirrorSprite = ImageCache.getInstance()
				.get("mirror-" + (mirror.getOwner().getPlayerSide() == PlayerSide.LEFT_PLAYER ? "left" : "right") + "-"
						+ (mirror.isLong() ? "long" : "short") + ".png");

		sparks = new ArrayList<>();
	}

	@Override
	public void update(double dt) {
		ArrayList<GlassSparks> finishedSparks = new ArrayList<>();
		for (GlassSparks s : sparks) {
			s.update(dt);

			if (s.isDone()) {
				finishedSparks.add(s);
			}
		}

		for (GlassSparks s : finishedSparks) {
			sparks.remove(s);
			Pools.GLASS_SPARKS.free(s);
		}

		timeSinceLastSparksSpawn += dt;
	}

	@Override
	public void onRemoved() {
		for (GlassSparks s : sparks) {
			Pools.GLASS_SPARKS.free(s);
		}
		sparks.clear();
	}

	@Override
	public void draw(Graphics2D g) {
		int w = mirrorSprite.getWidth(), h = mirrorSprite.getHeight();

		int x = (int) (mirror.getPosition().getX()), y = (int) (mirror.getPosition().getY());
		AffineTransform cache = g.getTransform();

		g.setTransform(new AffineTransform());

		g.scale(cache.getScaleX(), cache.getScaleY());
		g.translate(x, y);
		g.rotate(mirror.getAngle());
		g.translate(-w / 2, -h / 2);

		g.drawImage(mirrorSprite, 0, 0, null);

		g.setTransform(cache);
		// g.setColor(Color.RED);
		// g.fillOval(x-5, y-5, 10, 10);

		for (GlassSparks s : sparks) {
			s.draw(g);
		}
	}

	public void onMirrorHit(Vector2D bulletPosition) {
		if (timeSinceLastSparksSpawn < SPARKS_SPAWN_COOLDOWN) {
			return;
		}

		timeSinceLastSparksSpawn = 0.0;
		sparks.add(Pools.GLASS_SPARKS.create(bulletPosition, mirror.getAngle(),
				!mirror.isPointOnBottomSide(bulletPosition), 1.5, 0.15));
	}
}