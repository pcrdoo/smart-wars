package debug;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DebugDisplay implements view.Drawable {
	private static DebugDisplay instance;
	
	private boolean enabled;
	
	private HashMap<String, ArrayList<Rectangle>> debugRectsByCategory;
	private HashMap<String, ArrayList<Rectangle>> debugOvalsByCategory;
	private HashMap<String, ArrayList<Rectangle>> debugLinesByCategory;
	private HashSet<String> visibleCategories;
	
	private HashMap<String, Color> debugCategoryColors;
	
	private DebugDisplay() {
		debugRectsByCategory = new HashMap<>();
		debugOvalsByCategory = new HashMap<>();
		debugLinesByCategory = new HashMap<>();
		
		debugCategoryColors = new HashMap<>();
		visibleCategories = new HashSet<>();
	}
	
	public static DebugDisplay getInstance() {
		if (instance == null) {
			instance = new DebugDisplay();
		}
		
		return instance;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			debugRectsByCategory.clear();
		}
	}
	
	public void setCategoryColor(String category, Color c) {
		debugCategoryColors.put(category, c);
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	private void setColorForCategory(String category, Graphics2D g) {
		Color c = Color.WHITE;
		if (debugCategoryColors.containsKey(category)) {
			c = debugCategoryColors.get(category);
		}
		g.setColor(c);
		System.out.println(c.getAlpha());
	}
	
	private void addRectByCategory(String category, Rectangle r, HashMap<String, ArrayList<Rectangle>> byCategory) {
		ArrayList<Rectangle> rects;
		if (!byCategory.containsKey(category)) {
			rects = new ArrayList<>();
			byCategory.put(category, rects);
		} else {
			rects = byCategory.get(category);
		}
		
		rects.add(r);	
	}
	
	public void addRectangle(String category, Rectangle r) {
		addRectByCategory(category, r, debugRectsByCategory);
	}
	
	public void addOval(String category, Rectangle r) {
		addRectByCategory(category, r, debugOvalsByCategory);
	}

	public void addLine(String category, Point first, Point second) {
		addRectByCategory(category, new Rectangle(first, new Dimension((int)(second.getX() - first.getX()), (int)(second.getY() - first.getY()))), debugLinesByCategory);
	}
	
	public void showCategory(String category) {
		visibleCategories.add(category);
	}
	
	public void hideCategory(String category) {
		visibleCategories.remove(category);
	}
	
	private boolean isCategoryVisible(String category) {
		return visibleCategories.contains(category);
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (!enabled) {
			return;
		}
		
		Composite old = g.getComposite();
		
		for (HashMap.Entry<String, ArrayList<Rectangle>> e : debugRectsByCategory.entrySet()) {
			if (!isCategoryVisible(e.getKey())) {
				continue;
			}
			
			setColorForCategory(e.getKey(), g);
			for (Rectangle r : e.getValue()) {
				g.drawRect((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
			}
		}
		
		for (HashMap.Entry<String, ArrayList<Rectangle>> e : debugOvalsByCategory.entrySet()) {
			if (!isCategoryVisible(e.getKey())) {
				continue;
			}
			
			setColorForCategory(e.getKey(), g);
			for (Rectangle r : e.getValue()) {
				g.drawOval((int)r.getMinX(), (int)r.getMinY(), (int)r.getWidth(), (int)r.getHeight());
			}
		}	
		
		for (HashMap.Entry<String, ArrayList<Rectangle>> e : debugLinesByCategory.entrySet()) {
			if (!isCategoryVisible(e.getKey())) {
				continue;
			}

			setColorForCategory(e.getKey(), g);
			for (Rectangle r : e.getValue()) {
				g.drawLine((int)r.getMinX(), (int)r.getMinY(), (int)r.getMaxX(), (int)r.getMaxY());
			}
		}
		
		g.setComposite(old);
		
		debugRectsByCategory.clear();
		debugOvalsByCategory.clear();
		debugLinesByCategory.clear();
	}
	
}
