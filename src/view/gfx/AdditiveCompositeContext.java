package view.gfx;

import java.awt.*;
import java.awt.image.*;

public class AdditiveCompositeContext implements CompositeContext {
	double q;
	
	public AdditiveCompositeContext(double q) {
		this.q = q;
	};

	private static int addChannel(int a, int b) {
		return Math.min(a + b, 255);
	}
	
	// Don't call with weird image formats, Bad Things(tm) will happen.
	public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
		int w1 = src.getSampleModel().getWidth();
		int w2 = dstIn.getWidth();
		int wOut = dstOut.getWidth();
		int chan1 = src.getNumBands();
		int chan2 = dstIn.getNumBands();
		int chanOut = dstOut.getNumBands();

		int minCh = Math.min(Math.min(chan1, chan2), chanOut);
		
		byte[] srcBuf = ((DataBufferByte)src.getDataBuffer()).getData();
		int[] dstBuf = ((DataBufferInt)dstIn.getDataBuffer()).getData();
		int[] outBuf = ((DataBufferInt)dstOut.getDataBuffer()).getData();

		for (int y = 0; y < dstIn.getHeight(); y++) {
			for (int x = 0; x < wOut; x++) {
				/*int ra = srcBuf[(x + y * w1) * 3 + 2] & 0xFF;
				int ga = srcBuf[(x + y * w1) * 3 + 1] & 0xFF;
				int ba = srcBuf[(x + y * w1) * 3 + 0] & 0xFF;
				
				int b = dstBuf[x + y * w2];
				int rb = (b >> 16) & 0xFF;
				int gb = (b >> 8) & 0xFF;
				int bb = (b >> 0) & 0xFF;
				
				outBuf[x + y * wOut] = 0xFF000000 + 
						(addChannel((int)(ra * q), rb) << 16) +
						(addChannel((int)(ga * q), gb) << 8) +
						(addChannel((int)(ba * q), bb) << 0);*/
		        float[] pxSrc = null;
		        pxSrc = src.getPixel(x, y, pxSrc);
		        float[] pxDst = null;
		        pxDst = dstIn.getPixel(x, y, pxDst);
		        
		        for(int i = 0; i < 3 && i < minCh; i++) {
		          pxDst[i] = (float)Math.min(255, (pxSrc[i] * q) + (pxDst[i]));
		          dstOut.setPixel(x, y, pxDst);
		        }
						
			}
		}
	}

	public void dispose() {
	}
}