package debug;

import java.util.ArrayList;

public class NanoTimeMeasurement implements Measurement {
	private long startTime;
	private ArrayList<MeasurementListener> listeners;

	public NanoTimeMeasurement() {
		startTime = System.nanoTime();
		listeners = new ArrayList<>();
	}

	@Override
	public void done() {
		long elapsed = System.nanoTime() - startTime;
		for (MeasurementListener l : listeners) {
			l.onMeasured(elapsed);
		}
	}

	@Override
	public void addListener(MeasurementListener listener) {
		listeners.add(listener);
	}

}
