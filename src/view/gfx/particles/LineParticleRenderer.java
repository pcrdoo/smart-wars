package view.gfx.particles;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import util.Vector2D;

public class LineParticleRenderer implements ParticleRenderer {
	private double minLength, maxLength, minVelocity, maxVelocity;
	private Color leftColor, rightColor;
	
	public LineParticleRenderer(Color leftColor, Color rightColor, double minLength, double maxLength, double minVelocity, double maxVelocity) {
		this.leftColor = leftColor;
		this.rightColor = rightColor;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.minVelocity = minVelocity;
		this.maxVelocity = maxVelocity;
	}

	@Override
	public void render(Graphics2D g, Particles particles) {
		Composite old = g.getComposite();
		
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) < 0.0) {
				continue;
			}
			
			Vector2D pos = particles.getPosition(i), vel = particles.getVelocity(i);
			
			double lengthFactor = (vel.length() - minVelocity) / (maxVelocity - minVelocity);
			if (lengthFactor < 0.0) {
				lengthFactor = 0.0;
			}
			
			if (lengthFactor > 1.0) {
				lengthFactor = 1.0;
			}
			
			double length = lengthFactor * (maxLength - minLength) + minLength;
			Vector2D endPoint = pos.add(vel.scale(1.0 / vel.length()).scale(length));
			
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)particles.getAlpha(i)));
			
			double r = Math.random();
			double newR = leftColor.getRed() * r + rightColor.getRed() * (1.0 - r),
					newG = leftColor.getGreen() * r + rightColor.getGreen() * (1.0 - r),
					newB = leftColor.getBlue() * r + rightColor.getBlue() * (1.0 - r);
			
			g.setColor(new Color((int)newR, (int)newG, (int)newB));
			g.drawLine((int)pos.getdX(),(int)pos.getdY(), (int)endPoint.getdX(), (int)endPoint.getdY());
		}
		
		g.setComposite(old);
	}
}
