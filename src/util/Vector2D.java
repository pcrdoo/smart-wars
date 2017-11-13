package util;

import java.lang.Math;
import java.nio.ByteBuffer;

public class Vector2D {

	protected double dX;
	protected double dY;

	// Constructor methods ....

	public Vector2D() {
		dX = dY = 0.0;
	}

	public Vector2D(double dX, double dY) {
		this.dX = dX;
		this.dY = dY;
	}

	// Convert vector to a string ...

	public String toString() {
		return "Vector2D(" + dX + ", " + dY + ")";
	}

	// Compute magnitude of vector ....

	public double length() {
		return Math.sqrt(dX * dX + dY * dY);
	}

	// Sum of two vectors ....

	public Vector2D add(Vector2D v1) {
		Vector2D v2 = new Vector2D(this.dX + v1.dX, this.dY + v1.dY);
		return v2;
	}

	// Subtract vector v1 from v .....

	public Vector2D sub(Vector2D v1) {
		Vector2D v2 = new Vector2D(this.dX - v1.dX, this.dY - v1.dY);
		return v2;
	}

	// Scale vector by a constant ...

	public Vector2D scale(double scaleFactor) {
		Vector2D v2 = new Vector2D(this.dX * scaleFactor, this.dY * scaleFactor);
		return v2;
	}

	// Normalize a vectors length....

	public Vector2D normalize() {
		Vector2D v2 = new Vector2D();
		double len = length();
		if (len != 0) {
			v2 = this.scale(1 / len);
		}
		return v2;
	}

	// Dot and cross product of two vectors .....

	public double dotProduct(Vector2D v1) {
		return this.dX * v1.dX + this.dY * v1.dY;
	}

	public double crossProductIntensity(Vector2D v1) {
		return this.dX * v1.dY - this.dY * v1.dX;
	}

	// Clamp coordinates...

	public void clampX(double mindX, double maxdX) {
		if (this.dX < mindX) {
			setX(mindX);
		}
		if (this.dX > maxdX) {
			setX(maxdX);
		}
	}

	public void clampY(double mindY, double maxdY) {
		if (this.dY < mindY) {
			setY(mindY);
		}
		if (this.dY > maxdY) {
			setY(maxdY);
		}
	}

	public void setX(double dX) {
		this.dX = dX;
	}
	
	public void setY(double dY) {
		this.dY = dY;
	}
	
	public double getX() {
		return dX;
	}

	public double getY() {
		return dY;
	}
	
	public void serializeTo(ByteBuffer buffer) {
		buffer.putFloat((float)dX);
		buffer.putFloat((float)dY);
	}
	
	public static Vector2D deserializeFrom(ByteBuffer buffer)  {
		return new Vector2D(buffer.getFloat(), buffer.getFloat());
	}
}