package view.gfx.particles;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import util.Vector2D;

public class AlphaSpriteParticleRenderer implements ParticleRenderer {
	private BufferedImage sprite;

	public AlphaSpriteParticleRenderer(BufferedImage sprite) {
		this.sprite = sprite;
	}

	@Override
	public void render(Graphics2D g, Particles particles) {
		int sw = sprite.getWidth(), sh = sprite.getHeight();

		Composite old = g.getComposite();
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) < 0.0) {
				continue;
			}

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) particles.getAlpha(i)));

			Vector2D pos = particles.getPosition(i);
			g.drawImage(sprite, (int) pos.getX() - sw / 2, (int) pos.getY() - sh / 2, null);
		}

		g.setComposite(old);
	}

}
