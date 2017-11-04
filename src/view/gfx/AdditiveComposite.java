package view.gfx;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public class AdditiveComposite implements Composite {
	private int intensity;
	private static AdditiveComposite[] composites;
	
	private AdditiveComposite(int intensity) {
		this.intensity = intensity;
	}
	
	public static AdditiveComposite getInstance(int intensity) {
		if (composites == null) {
			composites = new AdditiveComposite[256];
		}
		
		if (composites[intensity] == null) {
			composites[intensity] = new AdditiveComposite(intensity);
		}
		
		return composites[intensity];
	}
	
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return new AdditiveCompositeContext(intensity / 255.0);
	}
}