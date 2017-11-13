package view.gfx.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Constants;

public class AdditiveSpriteParticleRenderer implements ParticleRenderer {
	private ParticleAdditiveCompositor compositor;

	public AdditiveSpriteParticleRenderer(BufferedImage sprite) {
		this.compositor = new ParticleAdditiveCompositor(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, sprite);
	}

	@Override
	public void render(Graphics2D g, Particles particles) {
		compositor.compose(g, particles);
	}
}
