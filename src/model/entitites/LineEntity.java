package model.entitites;

import java.awt.Rectangle;

import util.Vector2D;

public class LineEntity extends Entity {

	double length;
	double angle; // [0, 2*PI]
	
	protected LineEntity(Vector2D position, double length) {
		super(position);
		this.length = length;
		this.angle = 0;
	}

	public boolean hitTest(Vector2D point) {
		return true;
	}
	
	@Override
	public Rectangle getBoundingBox() {
		double height = Math.sin(angle) * length;
		double width = Math.cos(angle) * length;
		return new Rectangle((int) (position.getdX() - width/2), (int) (position.getdY() - height/2), (int)width, (int)height);
	}

	@Override
	public boolean shouldCull() {
		return false;
	}
}
