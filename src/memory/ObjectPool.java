package memory;

import java.util.ArrayList;

import debug.PerformanceMonitor;

public abstract class ObjectPool<T extends Poolable> {
	private ArrayList<T> freeList;
	private String statisticName;
	private int size;

	public ObjectPool(int size, String statisticName) {
		this.size = size;
		this.statisticName = statisticName;
	}

	protected abstract T createEmpty();

	public void reportFree() {
		PerformanceMonitor.getInstance().recordStatistic(statisticName, freeList.size());
	}

	public void free(T o) {
		freeList.add(o);
		reportFree();
	}

	public T create() {
		if (freeList.isEmpty()) {
			System.err.println("Warning: Object pool is full, satisfying the request by creating a new object.");
			return createEmpty();
		} else {
			T obj = freeList.get(0);
			freeList.remove(0);

			reportFree();
			return obj;
		}
	}

	public void repopulate() {
		freeList = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			freeList.add(createEmpty());
		}
		
		reportFree();
	}
}
