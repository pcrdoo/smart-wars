package model;

import util.Vector2D;

public class RectEntity extends Entity {
	protected Vector2D size;
	
	public boolean hitTest(Vector2D point) {
		return point.getdX() > this.position.getdX() && point.getdX() < this.position.getdX() + this.size.getdX() &&
				point.getdY() > this.position.getdY() && point.getdY() < this.position.getdY() + this.size.getdY();
	}

}
