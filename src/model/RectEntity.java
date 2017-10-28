package model;

import util.Vector2D;

public class RectEntity extends Entity {
	
	protected Vector2D size;

	public RectEntity(Vector2D position, Vector2D velocity) {
		super(position, velocity);
		// TODO Auto-generated constructor stub
	}

	public boolean hitTest(Vector2D point) {
		return point.getdX() > this.position.getdX() && point.getdX() < this.position.getdX() + this.size.getdX() &&
				point.getdY() > this.position.getdY() && point.getdY() < this.position.getdY() + this.size.getdY();
	}

}
