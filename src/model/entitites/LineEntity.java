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
	
	public Vector2D getLineVector() {
		 return new Vector2D(length * Math.cos(angle), length * Math.sin(angle));
	}

	public boolean hitTest(Vector2D point) {
		if(position.sub(point).length() < length / 2 && Math.abs(getSignedPointDistance(point)) < Constants.MIRROR_DIST_EPS) {
			return true;
		}
		return false;
	}
	
	public boolean isPointOnBottomSide(Vector2D point) {
		return getSignedPointDistance(point) < 0;
	}

	private double getSignedPointDistance(Vector2D point) {
		Vector2D ab = getLineVector();
		Vector2D ac = point.sub(ab.scale(0.5)).sub(position);
		return ab.crossProductIntensity(ac) / ab.length();
	}
	
	@Override
	public Rectangle getBoundingBox() {
		Vector2D line = getLineVector();
		if (line.getX() < 0) {
			line = new Vector2D(-line.getX(), line.getY());
		}
		
		if (line.getY() < 0) {
			line = new Vector2D(line.getX(), -line.getY());
		}
		
		return new Rectangle((int) (position.getX() - line.getX() / 2), (int) (position.getY() - line.getY() / 2), (int) line.getX(),
				(int) line.getY());
	}

	@Override
	public boolean shouldCull() {
		return false;
	}
}