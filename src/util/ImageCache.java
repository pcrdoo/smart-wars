package util;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import rafgfxlib.Util;

public class ImageCache {
	private HashMap<String, BufferedImage> cache;
	private static ImageCache instance;
	
	private ImageCache() {
		cache = new HashMap<>();
	}
	
	public static ImageCache getInstance() {
		if (instance == null) {
			instance = new ImageCache();
		}
		
		return instance;
	}
	
	public BufferedImage get(String filename) {
		if (cache.containsKey(filename)) {
			return cache.get(filename);
		} else {
			BufferedImage image = Util.loadImage(filename);
			cache.put(filename, image);
			
			return image;
		}
	}
}
