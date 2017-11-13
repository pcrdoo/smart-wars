package util;

import java.nio.ByteBuffer;

public class Vector2D {

	protected double x;
	protected double y;

	public Vector2D() {
		x = y = 0.0;
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Vector2D(" + x + ", " + y + ")";
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public Vector2D add(Vector2D other) {
		Vector2D ret = new Vector2D(x + other.x, y + other.y);
		return ret;
	}

	public Vector2D sub(Vector2D other) {
		Vector2D ret = new Vector2D(x - other.x, y - other.y);
		return ret;
	}

	public Vector2D scale(double scaleFactor) {
		Vector2D ret = new Vector2D(x * scaleFactor, y * scaleFactor);
		return ret;
	}

	public Vector2D normalize() {
		Vector2D ret = new Vector2D();
		double len = length();
		if (len != 0) {
			ret = scale(1 / len);
		}
		return ret;
	}

	public double dotProduct(Vector2D other) {
		return x * other.x + y * other.y;
	}

	public double crossProductIntensity(Vector2D other) {
		return x * other.y - y * other.x;
	}

	public void clampX(double minx, double maxx) {
		x = Math.max(x, minx);
		x = Math.min(x, maxx);
	}

	public void clampY(double miny, double maxy) {
		y = Math.max(y, miny);
		y = Math.min(y, maxy);
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void serializeTo(ByteBuffer buffer) {
		buffer.putFloat((float) x);
		buffer.putFloat((float) y);
	}

	public static Vector2D deserializeFrom(ByteBuffer buffer) {
		return new Vector2D(buffer.getFloat(), buffer.getFloat());
	}

	public static int getSerializedSize() {
		return 2 * 4;
	}
}