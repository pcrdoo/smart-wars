package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import model.Mirror;
import util.ImageCache;

public class MirrorView extends EntityView {
	private Mirror mirror;
	private BufferedImage mirrorSprite;

	public MirrorView(Mirror mirror) {
		this.mirror = mirror;
		mirrorSprite = ImageCache.getInstance().get("assets/mirror_long.png");
		// TODO: Short mirrors
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public void draw(Graphics2D g) {
		int w = mirrorSprite.getWidth(), h = mirrorSprite.getHeight();

		int x = (int) (mirror.getPosition().getdX()) - w / 2, y = (int) (mirror.getPosition().getdY()) - h / 2;
		
		g.setTransform(new AffineTransform());
		g.translate(x, y);
		g.rotate(mirror.getAngle());
		g.translate(-w/2, -h/2);
		
		g.drawImage(mirrorSprite, 0, 0, null);

		g.setTransform(new AffineTransform());
	}

	public void onMirrorHit() {
		// TODO sparkle
	}
}