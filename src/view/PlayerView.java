package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Constants;
import model.Player;
import util.ImageCache;

public class PlayerView implements Drawable, Updatable {
	private final static int FRAME_COUNT = 6;
	
	private BufferedImage sprite;
	private Player player;
	private int frame = 0;
	private int spriteWidth, spriteHeight;
	private double time = 0.0;
	
	public PlayerView(Player player, boolean alternativeSprite) {
		sprite = ImageCache.getInstance().get(alternativeSprite ? "assets/player2.png" : "assets/player1.png");
		spriteWidth = sprite.getWidth();
		spriteHeight = sprite.getHeight() / FRAME_COUNT;
		this.player = player;
	}
	
	public void update(double dt) {
		time += dt;
		
		double frameTime = 1.0 / Constants.PLAYER_ANIMATION_FPS;
		while (time > frameTime) {
			time -= frameTime;
			frame++;
		}
		
		frame %= FRAME_COUNT;
	}
	
	public void draw(Graphics2D g) {		
		int x = (int)player.getPosition().getdX() - spriteWidth / 2;
		int y = (int)player.getPosition().getdY() - spriteHeight / 2;

		g.drawImage(sprite, x, y, x + spriteWidth, y + spriteHeight, 0, frame * spriteHeight, spriteWidth - 1, (frame + 1) * spriteHeight - 1, null);
	}
}
