package debug;

public interface Measurement {
	void done();

	void addListener(MeasurementListener listener);
}
