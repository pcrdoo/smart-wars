package util;

import java.awt.image.BufferedImage;

public class ImageConverter {
	public static BufferedImage toRGB(BufferedImage src) {
		if (src.getType() == BufferedImage.TYPE_INT_ARGB) {
			return src;
		}
		
		int[] buf = src.getData().getPixels(0, 0, src.getWidth(), src.getHeight(), (int[]) null);
		int[] newBuf = new int[src.getWidth() * src.getHeight()];

		for (int i = 0; i < newBuf.length; i++) {
			newBuf[i] = (buf[4 * i + 1] << 16) + (buf[4 * i + 2] << 8) + buf[4 * i + 3];
		}
		
		return new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
	}
}
