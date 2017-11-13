package debug;

import java.util.HashMap;

public class PerformanceMonitor {
	private boolean enabled;
	private HashMap<String, MultisizeMovingAverage> averages;
	private HashMap<String, Double> statistics;

	private static PerformanceMonitor instance;
	private static final int[] MOVING_AVERAGE_SIZES = new int[] { 10, 50, 100 };

	private class Listener implements MeasurementListener {
		private String name;

		public Listener(String name) {
			this.name = name;
		}

		@Override
		public void onMeasured(long nanoseconds) {
			if (!averages.containsKey(name)) {
				averages.put(name, new MultisizeMovingAverage(MOVING_AVERAGE_SIZES));
			}

			averages.get(name).add(nanoseconds / 1000000.0);
		}
	}

	private PerformanceMonitor() {
		averages = new HashMap<>();
		statistics = new HashMap<>();
	}

	public Measurement measure(String name) {
		if (!enabled) {
			return new NoopMeasurement();
		} else {
			NanoTimeMeasurement m = new NanoTimeMeasurement();
			m.addListener(new Listener(name));

			return m;
		}
	}

	public void recordStatistic(String name, double value) {
		if (!enabled) {
			return;
		}

		statistics.put(name, value);
	}

	public double getStatistic(String name) {
		if (!statistics.containsKey(name)) {
			return 0;
		}

		return statistics.get(name);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int[] getMovingAverageSizes() {
		return MOVING_AVERAGE_SIZES;
	}

	public double getAverageMeasurement(String name, int sizeBucket) {
		if (!averages.containsKey(name)) {
			return 0.0;
		}

		return averages.get(name).getAverage(sizeBucket);
	}

	public static PerformanceMonitor getInstance() {
		if (instance == null) {
			instance = new PerformanceMonitor();
		}

		return instance;
	}
}
