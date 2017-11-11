package view.gfx.particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

import debug.DebugDisplay;
import util.Vector2D;

public class ParticleAdditiveCompositor {
	private BufferedImage canvas;
	private int width, height, minX, minY, maxX, maxY;
	private int[][] spritePixels;
	private int spriteWidth, spriteHeight;
	
	public ParticleAdditiveCompositor(int width, int height, BufferedImage sprite) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	    canvas = gd.getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	    
	    this.width = width;
	    this.height = height;
	    
	    spriteWidth = sprite.getWidth();
	    spriteHeight = sprite.getHeight();
	    
	    spritePixels = new int[spriteWidth][spriteHeight];
	    for (int y = 0; y < spriteHeight; y++) {
	    	for (int x = 0; x < spriteWidth; x++) {
	    		spritePixels[y][x] = sprite.getRGB(x, y);
	    	}
	    }
	}
	
	public void compositeOnCanvas(Particles particles) {
		// Find the bounds
		int sw = spriteWidth;
		int sh = spriteHeight;
		
		this.minX = Integer.MAX_VALUE;
		this.maxX = Integer.MIN_VALUE;
		this.minY = Integer.MAX_VALUE;
		this.maxY = Integer.MIN_VALUE;
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) <= 0.0) {
				continue;
			}
			
			Vector2D pos = particles.getPosition(i);
			
			int topLeftX = (int)Math.floor(pos.getdX()) - sw / 2,
				topLeftY = (int)Math.floor(pos.getdY()) - sh / 2,
				bottomRightX = (int)Math.ceil(pos.getdX()) + sw / 2,
				bottomRightY = (int)Math.ceil(pos.getdY()) + sh / 2;
			
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
		
		if (minX >= maxX || minY >= maxY) {
			minX = maxX = minY = maxY = 0;
			return;
		}
		
		// Blit the particles additively
		int bw = maxX - minX, bh = maxY - minY;
		int[] bitmap = new int[bw * bh];
		
		for (int i = 0; i < particles.size(); i++) {
			if (particles.getTime(i) <= 0.0) {
				continue;
			}
			
			Vector2D pos = particles.getPosition(i);
			int alpha = (int)(particles.getAlpha(i) * 255.0);
			int px = (int)pos.getdX(), py = (int)pos.getdY();
			
			int dx = 0, dy = py - sh / 2 - minY;
			for (int y = 0; y < sh; y++, dy++) {
				dx = px - sw / 2 - minX;
				for (int x = 0; x < sw; x++, dx++) {
					if (dy*bw+dx>=bitmap.length||dy*bw+dx<0) {
						System.out.println("dx=" + dx+",dy="+dy+",bw="+bw+",bh="+bh+",minx="+minX+",maxx="+maxX+",miny="+minY+",maxy="+maxY);
					}
					int dst = bitmap[dy * bw + dx];
					int src = spritePixels[y][x];
					
					/* Hand-unrolled:
					 * 
					 *	for (int c = 0; c < 3; c++) {
					 *		int value = ((dst >> (8 * c)) & 0xff) + (alpha * ((src >> (8 * c)) & 0xff) / 255);
					 *		if (value > 255) {
					 *			value = 255;
					 *		}
					 *		res |= value << (8 * c);
					 *	}
					 *		
					 *	int intensity =
					 *			(dst & 0xFF) / 5 +
					 *			((dst >> 8) & 0xFF) * 7 / 10 +
					 *			((dst >> 16) & 0xFF) * 7 / 10;
					 *	
					 *	if (intensity > 255) {
					 *		intensity = 255;
					 *	}
					 *
					 *	res |= intensity << 24;
					 *	
					 */
					
					int r = ((dst >> 0) & 0xff) + (alpha * ((src >> 0) & 0xff) / 255),
						g = ((dst >> 8) & 0xff) + (alpha * ((src >> 8) & 0xff) / 255),
						b = ((dst >> 16) & 0xff) + (alpha * ((src >> 16) & 0xff) / 255);
					
					r = Math.min(255, r);
					g = Math.min(255, g);
					b = Math.min(255, b);
					
					int luma = Math.min(255, (r + g + b) / 3);
					
					int res = 
							r |
							(g << 8) |
							(b << 16) |
							(luma << 24);
					
					bitmap[dy * bw + dx] = res;
				}
			}
		}

		SampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, bw, bh, new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000 });
		DataBufferInt db = new DataBufferInt(bitmap, bw);
		WritableRaster wr = Raster.createWritableRaster(sm, db, new Point());
		canvas = new BufferedImage(ColorModel.getRGBdefault(), wr, false, null);
	}
	
	public void compose(Graphics g, Particles particles) {
		compositeOnCanvas(particles);
		
		g.drawImage(canvas, minX, minY, maxX, maxY, 0, 0, maxX - minX, maxY - minY, null);
		
		DebugDisplay dd = DebugDisplay.getInstance();
		if (dd.isEnabled()) {
			dd.addRectangle("ParticleCompositorRects", new Rectangle(minX, minY, maxX - minX, maxY - minY));
		}
		
		/*g.setColor(Color.RED);
		g.drawRect(minX, minY, maxX-minX, maxY-minY);*/
	}
}
