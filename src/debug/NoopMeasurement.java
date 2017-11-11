package debug;

public class NoopMeasurement implements Measurement {
	@Override
	public void done() {
		// No-op		
	}

	@Override
	public void addListener(MeasurementListener listener) {
		// No-op
	}
}
