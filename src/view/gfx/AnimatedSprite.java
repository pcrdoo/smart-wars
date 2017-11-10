package view.gfx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import view.Drawable;
import view.Updatable;

public class AnimatedSprite implements Updatable {
	private BufferedImage spritesheet;
	private int framesPerX, framesPerY, frameX, frameY, frameWidth, frameHeight;
	double frameTime, accumulTime;
	
	public AnimatedSprite(BufferedImage spritesheet, int framesPerX, int framesPerY, int fps) {
		this.spritesheet = spritesheet;
		
		this.framesPerX = framesPerX;
		this.framesPerY = framesPerY;
		
		frameWidth = spritesheet.getWidth() / framesPerX;
		frameHeight = spritesheet.getHeight() / framesPerY;
		accumulTime = 0;
		
		frameX = 0;
		frameY = 0;
		
		frameTime = 1.0 / fps;
	}

	public void nextFrame() {
		frameX++;
		if (frameX >= framesPerX) {
			frameY++;
			frameX = 0;
		}
		
		if (frameY >= framesPerY) {
			frameX = 0;
			frameY = 0;
		}
	}
	
	public void draw(Graphics2D g, int x, int y) {
		g.drawImage(spritesheet, x - frameWidth / 2, y - frameHeight / 2, x + frameWidth / 2, y + frameHeight / 2,
				frameX * frameWidth, frameY * frameHeight, (frameX + 1) * frameWidth, (frameY + 1) * frameHeight, null);
		
	}

	public void setFrame(int frame) {
		frameX = frame / framesPerX;
		frameY = frame % framesPerY;
	}
	
	@Override
	public void update(double dt) {
		accumulTime += dt;
		while (accumulTime > frameTime) {
			accumulTime -= frameTime;
			nextFrame();
		}
		
	}
}
