package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import debug.PerformanceMonitor;
import main.Constants;
import rafgfxlib.Util;

public class ImageCache {
	private HashMap<String, BufferedImage> cache;
	private static ImageCache instance;
	private int misses;
	private int hits;

	private ImageCache() {
		cache = new HashMap<>();
	}

	public static ImageCache getInstance() {
		if (instance == null) {
			instance = new ImageCache();
		}

		return instance;
	}

	private static BufferedImage convertToType(BufferedImage image, int type) {
		if (image.getType() == type) {
			return image;
		}
		
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
		Graphics2D graphics = newImage.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return newImage;
	}

	private BufferedImage loadAndReturn(String filename) {
		BufferedImage image = convertToType(Util.loadImage(filename), BufferedImage.TYPE_INT_ARGB);
		cache.put(filename, image);

		return image;
	}

	public void preload(String[] filenames) {
		for (String filename : filenames) {
			loadAndReturn(Constants.IMAGE_FILENAME_PREFIX + filename);
		}
	}

	public BufferedImage get(String filename) {
		filename = Constants.IMAGE_FILENAME_PREFIX + filename;
		if (cache.containsKey(filename)) {
			hits++;
			PerformanceMonitor.getInstance().recordStatistic("ImageCacheHits", hits);

			return cache.get(filename);
		} else {
			misses++;
			PerformanceMonitor.getInstance().recordStatistic("ImageCacheMisses", misses);

			return loadAndReturn(filename);
		}
	}
}
