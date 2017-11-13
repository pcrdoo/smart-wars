package debug;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import main.Constants;

public class PerformanceOverlay implements view.Drawable {
	private final static int START_X = 20, START_Y = 20;
	private final static int LINE_SPACING = 12;

	private static final Font FONT = new Font("Monospaced", Font.PLAIN, 10);
	private static final float ALPHA = 0.7f;

	private PerformanceMonitor monitor;
	private ArrayList<String> displayedMeasurements;
	private ArrayList<String> displayedStatistics;

	public PerformanceOverlay(PerformanceMonitor monitor) {
		this.monitor = monitor;
		displayedMeasurements = new ArrayList<>();
		displayedStatistics = new ArrayList<>();
	}

	public void addDisplayedStatistic(String name) {
		displayedStatistics.add(name);
	}

	public void addDisplayedMeasurement(String name) {
		displayedMeasurements.add(name);
	}

	public void removeDisplayedStatistic(String name) {
		displayedStatistics.remove(name);
	}

	public void removeDisplayedMeasurement(String name) {
		displayedMeasurements.remove(name);
	}

	private void addMeasurementLines(ArrayList<String> dest) {
		int[] movingAverageSizes = monitor.getMovingAverageSizes();
		DecimalFormat fmt = new DecimalFormat("0.00");

		for (String measurement : displayedMeasurements) {
			StringBuilder sb = new StringBuilder();
			sb.append(measurement);
			padTo(sb, Constants.PERF_NAME_MAX_WIDTH, measurement, '.');
			sb.append(": ");

			for (int i = 0; i < movingAverageSizes.length; i++) {
				sb.append('[');
				String s = Integer.toString(movingAverageSizes[i]);
				sb.append(s);
				padTo(sb, 3, s, ' ');
				sb.append(']');

				s = fmt.format(monitor.getAverageMeasurement(measurement, i));
				sb.append(s);
				sb.append("ms ");
				padTo(sb, 6, s, ' ');

			}

			dest.add(sb.toString());
		}
	}

	private void addStatisticLines(ArrayList<String> dest) {
		DecimalFormat fmt = new DecimalFormat("0");

		for (String statistic : displayedStatistics) {
			StringBuilder sb = new StringBuilder();
			sb.append(statistic);
			padTo(sb, Constants.PERF_NAME_MAX_WIDTH, statistic, '.');

			sb.append(": ");
			sb.append(fmt.format(monitor.getStatistic(statistic)));
			dest.add(sb.toString());
		}
	}

	private void padTo(StringBuilder sb, int w, String s, char c) {
		for (int i = 0; i < w - s.length(); i++) {
			sb.append(c);
		}
	}

	private ArrayList<String> composeLines() {
		ArrayList<String> lines = new ArrayList<>();

		addMeasurementLines(lines);
		addStatisticLines(lines);

		return lines;
	}

	@Override
	public void draw(Graphics2D g) {
		ArrayList<String> linesToDraw = composeLines();

		Composite old = g.getComposite();

		int y = START_Y;
		g.setColor(Color.WHITE);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));
		g.setFont(FONT);
		for (String line : linesToDraw) {
			g.drawString(line, START_X, y);
			y += LINE_SPACING;
		}
		g.setComposite(old);
	}
}
