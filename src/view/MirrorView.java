package view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.Mirror;
import util.ImageCache;

public class MirrorView extends EntityView {
	private Mirror mirror;
	private BufferedImage mirrorSprite;

	public MirrorView(Mirror mirror) {
		this.mirror = mirror;
		mirrorSprite = ImageCache.getInstance().get("assets/bullet.png");
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public void draw(Graphics2D g) {
		int w = mirrorSprite.getWidth(), h = mirrorSprite.getHeight();

		int x = (int) (mirror.getPosition().getdX()) - w / 2, y = (int) (mirror.getPosition().getdY()) - h / 2;

		g.drawImage(mirrorSprite, x, y, null);
		g.drawString(mirror.getMirrorState().toString(), 200, 200);
	}

	public void onMirrorHit() {
		// TODO sparkle
	}
}