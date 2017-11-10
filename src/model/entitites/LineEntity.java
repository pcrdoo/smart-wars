package model.entitites;

import java.awt.Rectangle;

import main.Constants;
import util.Vector2D;

public class LineEntity extends Entity {

	protected double length;
	protected double angle; // [-PI, PI]

	protected LineEntity(Vector2D position, Vector2D velocity, double length) {
		super(position, velocity);
		this.length = length;
		this.angle = Math.PI / 2;
	}
	
	private Vector2D getLineVector() {
		 return new Vector2D(length * Math.cos(angle), length * Math.sin(angle));
	}

	public boolean hitTest(Vector2D point) {
		Vector2D ab = getLineVector();
		Vector2D ac = position.sub(ab.scale(0.5)).sub(point);
		double pointDistance = Math.abs(ab.crossProductIntensity(ac) / ab.length());
		if(position.sub(point).length() < length / 2 && pointDistance < Constants.MIRROR_DIST_EPS) {
			return true;
		}
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		Vector2D line = getLineVector();
		return new Rectangle((int) (position.getdX() - line.getdX() / 2), (int) (position.getdY() - line.getdY() / 2), (int) line.getdX(),
				(int) line.getdY());
	}

	@Override
	public boolean shouldCull() {
		return false;
	}
}
