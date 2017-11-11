package debug;

public class MultisizeMovingAverage {
	private MovingAverage[] avgs;
	
	public MultisizeMovingAverage(int[] sizes) {
		avgs = new MovingAverage[sizes.length];
		for (int i = 0; i < avgs.length; i++) {
			avgs[i] = new MovingAverage(sizes[i]);
		}
	}
	
	public void add(double val) {
		for (int i = 0; i < avgs.length; i++) {
			avgs[i].add(val);
		}
	}
	
	public double getAverage(int sizeBucket) {
		return avgs[sizeBucket].getAverage();
	}
}
