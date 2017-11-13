package debug;

public class MovingAverage {
	private double samples[];
	private double sum = 0.0;
	private int size;
	private int curr = 0;

	public MovingAverage(int size) {
		samples = new double[size];
		this.size = size;

		for (int i = 0; i < size; i++) {
			samples[i] = 0.0;
		}
	}

	public void add(double val) {
		sum -= samples[curr];
		sum += val;

		samples[curr] = val;
		curr = (curr + 1) % size;
	}

	public double getAverage() {
		return sum / size;
	}
}
