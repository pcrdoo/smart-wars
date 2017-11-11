package view.gfx;

import memory.Poolable;
import view.Updatable;

public abstract class TimedGfx implements Updatable, Poolable {
	protected double time, duration;
	
	public TimedGfx() {
	}
	
	public void init(double duration) {
		this.time = this.duration = duration;
	}
	
	@Override
	public void reset() {
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
