package view.gfx;

import java.awt.*;
import java.awt.image.*;

public class AdditiveCompositeContext implements CompositeContext {
	double q;
	
	public AdditiveCompositeContext(double q) {
		this.q = q;
	};

	private static void addChannels(int[] a, int[] b, int[] dst) {
		for (int i = 0; i < 3; i++) {
			dst[i] = Math.min(a[i] + b[i], 255);
		}
	}
	
	public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
		if (src.getSampleModel().getDataType() != DataBuffer.TYPE_INT
				|| dstIn.getSampleModel().getDataType() != DataBuffer.TYPE_INT
				|| dstOut.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
			throw new IllegalStateException("Source and destination must store pixels as INT.");
		}

		int width = Math.min(src.getWidth(), dstIn.getWidth());
		int height = Math.min(src.getHeight(), dstIn.getHeight());

		float alpha = (float)q;

		int[] srcPixel = new int[4];
		int[] dstPixel = new int[4];
		int[] srcPixels = new int[width];
		int[] dstPixels = new int[width];

		int[] result = new int[4];
		for (int y = 0; y < height; y++) {
			src.getDataElements(0, y, width, 1, srcPixels);
			dstIn.getDataElements(0, y, width, 1, dstPixels);
			for (int x = 0; x < width; x++) {
				// pixels are stored as INT_ARGB
				// our arrays are [R, G, B, A]
				int pixel = srcPixels[x];
				srcPixel[0] = (pixel >> 16) & 0xFF;
				srcPixel[1] = (pixel >> 8) & 0xFF;
				srcPixel[2] = (pixel) & 0xFF;
				srcPixel[3] = (pixel >> 24) & 0xFF;

				pixel = dstPixels[x];
				dstPixel[0] = (pixel >> 16) & 0xFF;
				dstPixel[1] = (pixel >> 8) & 0xFF;
				dstPixel[2] = (pixel) & 0xFF;
				dstPixel[3] = (pixel >> 24) & 0xFF;

				addChannels(srcPixel, dstPixel, result);

				// mixes the result with the opacity
				dstPixels[x] = ((int) (dstPixel[3] + (result[3] - dstPixel[3]) * alpha) & 0xFF) << 24
						| ((int) (dstPixel[0] + (result[0] - dstPixel[0]) * alpha) & 0xFF) << 16
						| ((int) (dstPixel[1] + (result[1] - dstPixel[1]) * alpha) & 0xFF) << 8
						| (int) (dstPixel[2] + (result[2] - dstPixel[2]) * alpha) & 0xFF;
			}
			dstOut.setDataElements(0, y, width, 1, dstPixels);
		}
	}

	public void dispose() {
	}
}