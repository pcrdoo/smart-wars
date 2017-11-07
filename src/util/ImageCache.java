package util;

import java.awt.Graphics2D;
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
	
	private static BufferedImage convertToType(BufferedImage image, int type) {
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
	    Graphics2D graphics = newImage.createGraphics();
	    graphics.drawImage(image, 0, 0, null);
	    graphics.dispose();
	    return newImage;
	}
	
	public BufferedImage get(String filename) {
		if (cache.containsKey(filename)) {
			return cache.get(filename);
		} else {
			BufferedImage image = convertToType(Util.loadImage(filename), BufferedImage.TYPE_INT_ARGB);
			cache.put(filename, image);
			
			return image;
		}
	}
}
