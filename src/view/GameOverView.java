package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Constants;
import main.GameState;
import util.ImageCache;
import util.Vector2D;
import view.gfx.particles.ParticleSystem;
import view.gfx.particles.PointParticleEmitter;
import view.gfx.particles.AdditiveSpriteParticleRenderer;
import view.gfx.particles.AlphaSpriteParticleRenderer;
import view.gfx.particles.ParticleAffectorDecay;
import view.gfx.particles.ParticleAffectorDeceleration;
import view.gfx.particles.ParticleAffectorRotation;

public class GameOverView implements Drawable, Updatable {
	
	private String finalMessage;
	private BackdropView backdrop;
	private ParticleSystem victoryFlares;
	private BufferedImage backTexture;
	private Font font;
	
	public GameOverView(GameState gameState) {
		assert(gameState != GameState.RUNNING);
		BufferedImage particle = null;
		
		switch(gameState) {
		case DRAW:
			finalMessage = "Damn it's a draw. Buy your momma a house.";
			break;
		case LEFT_WIN:
			finalMessage = "You smart. You loyal.";
			particle = ImageCache.getInstance().get("gameover-flare-left.png");
			backTexture = ImageCache.getInstance().get("gameover-left.png");
			break;
		case RIGHT_WIN:
			finalMessage = "They don't want you to win so go win more.";
			particle = ImageCache.getInstance().get("gameover-flare-right.png");
			backTexture = ImageCache.getInstance().get("gameover-right.png");

			break;
		default:
			throw new IllegalArgumentException("Game state shouldn't be running when game is over.");
		}
		
		backdrop = new BackdropView();
		font = new Font("default", Font.BOLD, 16);
		
		victoryFlares = new ParticleSystem(new AlphaSpriteParticleRenderer(particle), 150);
		victoryFlares.addEmitter(new PointParticleEmitter(25.0, 3.0, 0.0, new Vector2D(Constants.WINDOW_WIDTH / 2, Constants.WINDOW_HEIGHT / 2), new Vector2D(0, 0), 300.0, 300.0, 0, 2 * Math.PI));
		victoryFlares.addAffector(new ParticleAffectorDecay(3.5));
		victoryFlares.addAffector(new ParticleAffectorDeceleration(50));
		victoryFlares.addAffector(new ParticleAffectorRotation(new Vector2D(Constants.WINDOW_WIDTH / 2, Constants.WINDOW_HEIGHT / 2), Math.PI / 3));
	}
	
	@Override
	public void update(double dt) {
		backdrop.update(dt);
		victoryFlares.update(dt);
	}
	
	public void draw(Graphics2D g) {
		backdrop.draw(g);
		victoryFlares.draw(g);
		g.drawImage(backTexture, 0, 0, null);
		g.setColor(Color.WHITE);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		
		String s = finalMessage + " Press ENTER for another one.";
		int strWidth = metrics.stringWidth(s);
		g.drawString(s, Constants.WINDOW_WIDTH / 2 - strWidth / 2, Constants.WINDOW_HEIGHT - 50);
	}

}
