package view.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import util.Vector2D;

public class ParticleCompositor {
	private BufferedImage canvas;
	private int width, height, minX, minY, maxX, maxY;
	
	public ParticleCompositor(int width, int height) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	    canvas = gd.getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	    
	    this.width = width;
	    this.height = height;
	}
	
	public void compositeOnCanvas(Particles particles, BufferedImage sprite) {
		// Find the bounds
		int sw = sprite.getWidth();
		int sh = sprite.getHeight();
		
		this.minX = width;
		this.maxX = 0;
		this.minY = height;
		this.maxY = 0;
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) <= 0.0) {
				continue;
			}
			
			Vector2D pos = particles.getPosition(i);
			
			int topLeftX = (int)Math.floor(pos.getdX() - sw / 2),
				topLeftY = (int)Math.floor(pos.getdY() - sh / 2),
				bottomRightX = (int)Math.ceil(pos.getdX() + sw / 2),
				bottomRightY = (int)Math.ceil(pos.getdY() + sh / 2);
			
			if (topLeftX < minX) {
				minX = topLeftX;
			}
			
			if (topLeftY < minY) {
				minY = topLeftY;
			}
			
			if (bottomRightX > maxX) {
				maxX = bottomRightX;
			}
			
			if (bottomRightY > maxY) {
				maxY = bottomRightY;
			}
		}
		
		if (minX < 0) {
			minX = 0;
		}
		
		if (maxX > width) {
			maxX = width;
		}
		
		if (minY < 0) {
			minY = 0;
		}
		
		if (maxY > height) {
			maxY = height;
		}
		
		// Clear the canvas
		WritableRaster r = canvas.getRaster();
		double[] samples = new double[4];
		samples[0] = samples[1] = samples[2] = samples[3] = 0.0;
		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				r.setPixel(x, y, samples);
			}
		}
		
		// Blit the particles additively
		WritableRaster sr = sprite.getRaster();
		double[] srcPixel = new double[4], dstPixel = new double[4];
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) <= 0.0) {
				continue;
			}
			
			Vector2D pos = particles.getPosition(i);
			int px = (int)pos.getdX(), py = (int)pos.getdY();
			
			for (int y = 0; y < sh; y++) {
				for (int x = 0; x < sw; x++) {
					int dx = px + x - sw / 2, dy = py + y - sh / 2;
					if (dx < 0 || dy < 0 || dx >= width || dy >= height) {
						continue;
					}
					
					int dst = canvas.getRGB(dx, dy);
					int src = sprite.getRGB(x, y);
					 
					int res = 0;
					for (int c = 0; c < 3; c++) {
						int value = ((dst >> (8 * c)) & 0xff) + ((src >> (8 * c)) & 0xff);
						if (value > 255) {
							value = 255;
						}
						res |= value << (8 * c);
						dstPixel[c] = (double)value / 255;
					}
					int alpha = (int)(255.0 * (.2126 * dstPixel[0] + .7152 * dstPixel[1] + .0722 * dstPixel[2]));
					res |= alpha << 24;
					canvas.setRGB(dx, dy, res);
					
				}
			}
		}
	}
	
	public void compose(Graphics g, Particles particles, BufferedImage sprite) {
		compositeOnCanvas(particles, sprite);
		g.drawImage(canvas, minX, minY, maxX, maxY, minX, minY, maxX, maxY, null);
		g.setColor(Color.RED);
		g.drawRect(minX, minY, maxX-minX, maxY-minY);
	}
}
