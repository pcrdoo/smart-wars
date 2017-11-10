package view.gfx;

import view.Updatable;

public abstract class TimedGfx implements Updatable {
	protected double time, duration;
	
	public TimedGfx(double duration) {
		this.time = this.duration = duration;
	}
	
	public boolean isDone() {
		return time <= 0.0;
	}
	
	@Override
	public void update(double dt) {
		time -= dt;
		if (time < 0.0) {
			time = 0.0;
			return;
		}
	}
}
