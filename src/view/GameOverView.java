package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import main.Constants;
import main.GameState;

public class GameOverView implements Drawable {
	
	private String finalMessage;

	public GameOverView(GameState gameState) {
		assert(gameState != GameState.RUNNING);
		switch(gameState) {
		case DRAW:
			finalMessage = "Damn it's a draw. Buy your momma a house.";
			break;
		case LEFT_WIN:
			finalMessage = "Left ship won. You smart. You loyal.";
			break;
		case RIGHT_WIN:
			finalMessage = "Right ship won. They don't want you to win so go win more.";
			break;
		default:
			throw new IllegalArgumentException("Game state shouldn't be running when game is over.");
		}
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawString(finalMessage + " Press ENTER for another one.", 100, Constants.WINDOW_HEIGHT - 100);
	}

}
