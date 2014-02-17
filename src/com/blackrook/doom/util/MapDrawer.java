/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.blackrook.commons.math.RMath;
import com.blackrook.doom.DoomMap;
import com.blackrook.doom.enums.LineStyle;
import com.blackrook.doom.enums.PointStyle;
import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.struct.Vertex;

/**
 * An object that writes map data in a graphical format to a Graphics2D context.
 * @author Matthew Tropiano
 */
public class MapDrawer
{
	/** The area coordinates of the map that will be rendered. */
	private Rectangle bounds;
	/** The view coordinates that the map will scaled to fit in. */
	private Rectangle view;
	/** The current MapDrawerAttributes for drawing the map. */
	private MapDrawerAttributes attributes;
	
	/**
	 * Creates a new MapDrawer.
	 */
	public MapDrawer(MapDrawerAttributes attributes)
	{
		bounds = new Rectangle();
		view = new Rectangle();
		setAttributes(attributes);
	}

	/**
	 * Returns a reference to the Rectangle that contains area coordinates of the map that will be rendered.
	 */
	public Rectangle getBounds()
	{
		return bounds;
	}

	/**
	 * Returns a reference to the Rectangle that contains area coordinates of the map that will be rendered.
	 */
	public void setBounds(int x, int y, int width, int height)
	{
		bounds.setBounds(x, y, width, height);
	}

	/**
	 * Returns a reference to the Rectangle that contains the view coordinates that the map will scaled to fit in.
	 */
	public Rectangle getView()
	{
		return view;
	}

	/**
	 * Sets the values of the Rectangle that contains the view coordinates that the map will scaled to fit in.
	 */
	public void setView(int x, int y, int width, int height)
	{
		view.setBounds(x, y, width, height);
	}

	/**
	 * Sets the bounds to a map's total bounds (this involves scanning each vertex).
	 */
	public void setBoundsByMap(DoomMap dm)
	{
		float min_x = Float.MAX_VALUE; 
		float max_x = -Float.MAX_VALUE; 
		float min_y = Float.MAX_VALUE; 
		float max_y = -Float.MAX_VALUE;
		
		for (Vertex v : dm.getVertexList())
		{
			min_x = Math.min(min_x, v.getX());
			max_x = Math.max(max_x, v.getX());
			min_y = Math.min(min_y, v.getY());
			max_y = Math.max(max_y, v.getY());
		}
		
		setBounds((int)min_x, (int)min_y, (int)(max_x - min_x), (int)(max_y - min_y));
	}
	
	/**
	 * Adds a "border" of space around the current bounds of this drawer.
	 */
	public void addBoundsBorder(int amount)
	{
		bounds.x = bounds.x - amount;
		bounds.y = bounds.y - amount;
		bounds.width = bounds.width + amount * 2;
		bounds.height = bounds.height + amount * 2;
	}
	
	/**
	 * Returns the "aspect ratio" of the current bounds, which is
	 * the ratio of width over height.
	 */
	public float getBoundsAspect()
	{
		return (float)bounds.width / bounds.height;
	}
	
	/**
	 * Returns the "aspect ratio" of the current view, which is
	 * the ratio of width over height.
	 */
	public float getViewAspect()
	{
		return (float)view.width / view.height;
	}
	
	/**
	 * Corrects the view aspect of this map drawer to match
	 * the bounds by adjusting the view.
	 */
	public void correctViewAspect()
	{
		float bAspect = getBoundsAspect();
		float vAspect = getViewAspect();

		// width shorter than height
		if (bAspect < 1)
		{
			if (vAspect < 1) // view width is shorter than height
			{
				view.height = (int)(view.width / bAspect);
				view.x = 0;
				view.y = 0;
			}
			else // view width is longer than or equal to height
			{
				view.width = (int)(view.height * bAspect);
				view.x = 0;
				view.y = 0;
			}
		}
		
		// width longer than height
		else
		{
			if (vAspect >= 1) // view width is longer than or equal to height
			{
				view.width = (int)(view.height * bAspect);
				view.x = 0;
				view.y = 0;
			}
			else // view width is shorter than height
			{
				view.height = (int)(view.width / bAspect);
				view.x = 0;
				view.y = 0;
			}
		}
	}
	
	/**
	 * Corrects the view of this map drawer to match
	 * the bounds aspect and fitting it in the view of a canvas bounds.
	 */
	public void correctViewAspectForCanvas(Rectangle canvasRect)
	{
		correctViewAspect();
		
		if (view.width > canvasRect.width)
		{
			view.width = canvasRect.width;
			view.height = (int)(view.width / getBoundsAspect());
		}
		
		else if (view.height > canvasRect.height)
		{
			view.height = canvasRect.height;
			view.width = (int)(view.height * getBoundsAspect());
		}
		
		view.x = Math.abs(canvasRect.width - view.width) / 2;
		view.y = Math.abs(view.height - canvasRect.height) / 2;
	}
	
	/**
	 * Returns a reference to the MapDrawerAttributes that modify the behavior
	 * of this MapDrawer. If this is null, the MapDrawer will throw a NullPointerException.
	 */
	public MapDrawerAttributes getAttributes()
	{
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(MapDrawerAttributes attributes)
	{
		this.attributes = attributes;
	}
	
	/**
	 * Draws this map to a Graphics 2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	public void draw(Graphics g, DoomMap dm)
	{
		if (attributes.canDrawBackground())
			drawBackground(g, dm);

		if (attributes.canDrawGrid())
			drawGrid(g, dm);

		if (attributes.canDrawLinedefs())
			drawLinedefs(g, dm);
		
		if (attributes.canDrawThings())
			drawThings(g, dm);

		if (attributes.canDrawVertices())
			drawVertices(g, dm);

		if (attributes.canDrawBorder())
			drawBorder(g, dm);
			
	}
	
	/**
	 * Draws the grid to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawGrid(Graphics g, DoomMap dm)
	{
		if (attributes.canDrawFineGrid())
			drawFineGrid(g, dm);
		if (attributes.canDrawCoarseGrid())
			drawCoarseGrid(g, dm);
	}
	
	/**
	 * Draws the coarse grid to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawCoarseGrid(Graphics g, DoomMap dm)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(attributes.getLineStyleForCoarseGrid().getStroke());
		g.setColor(attributes.getColorForCoarseGrid());
		int size = attributes.getCoarseGridSize();
		for (int x = (bounds.x / size) * size; x < bounds.x + bounds.width; x += size)
		{
			int vx = correctX(x);
			g.drawLine(vx, view.y, vx, view.height + view.y - 1);
		}
		for (int y = (bounds.y / size) * size; y < bounds.y + bounds.height; y += size)
		{
			int vy = correctY(y);
			g.drawLine(view.x, vy, view.width + view.x - 1, vy);
		}
	}
	
	/**
	 * Draws the fine grid to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawFineGrid(Graphics g, DoomMap dm)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(attributes.getLineStyleForFineGrid().getStroke());
		g.setColor(attributes.getColorForFineGrid());
		int size = attributes.getFineGridSize();
		for (int x = (bounds.x / size) * size; x < bounds.x + bounds.width; x += size)
		{
			int vx = correctX(x);
			g.drawLine(vx, view.y, vx, view.height + view.y - 1);
		}
		for (int y = (bounds.y / size) * size; y < bounds.y + bounds.height; y += size)
		{
			int vy = correctY(y);
			g.drawLine(view.x, vy, view.width + view.x - 1, vy);
		}
	}
	
	/**
	 * Draws the background to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawBackground(Graphics g, DoomMap dm)
	{
		g.setColor(attributes.getColorForBackground());
		g.fillRect(view.x, view.y, view.width, view.height);
	}
	
	/**
	 * Draws the border to a Graphics2D context around the view bounds.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawBorder(Graphics g, DoomMap dm)
	{
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(attributes.getLineStyleForBorder().getStroke());
		
		g.setColor(attributes.getColorForBorder());
		g.drawRect(view.x, view.y, view.width-1, view.height-1);
	}

	/**
	 * Draws the map vertices to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawVertices(Graphics g, DoomMap dm)
	{
		Color c = g.getColor();
		for (Vertex vertex : dm.getVertexList())
		{
			if (!c.equals(attributes.getColorForVertex(vertex)))
			{
				c = attributes.getColorForVertex(vertex);
				g.setColor(c);
			}

			if (attributes.canDrawVertex(vertex))
			{
				int x = correctX(vertex.getX());
				int y = correctY(vertex.getY());
				PointStyle p = attributes.getPointStyleForVertex(vertex);
				int thickness = (int)attributes.getThicknessForVertex(vertex);
				switch (p)
				{
					case SOLID:
						g.fillOval(
								(int)(x-(thickness/2)), 
								(int)(y-(thickness/2)), 
								thickness, thickness);
						break;
					case OUTLINE:
						g.drawOval(
								(int)(x-(thickness/2)), 
								(int)(y-(thickness/2)), 
								thickness, thickness);
						break;
				}
			}
		}
	}
	
	/**
	 * Draws the map linedefs to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawLinedefs(Graphics g, DoomMap dm)
	{
		Color c = g.getColor();
		Stroke stroke = null;
		Graphics2D g2d = (Graphics2D)g;
		
		for (Linedef line : dm.getLinedefList())
		{
			if (stroke != attributes.getLineStyleForLinedef(line).getStroke())
			{
				stroke = attributes.getLineStyleForLinedef(line).getStroke();
				g2d.setStroke(stroke);
			}

			if (!c.equals(attributes.getColorForLinedef(line)))
			{
				c = attributes.getColorForLinedef(line);
				g.setColor(c);
			}

			if (attributes.canDrawLinedef(line))
			{
				Vertex v1 = dm.getStartingVertexFor(line);
				Vertex v2 = dm.getEndingVertexFor(line);
				
				int x1 = correctX(v1.getX());
				int x2 = correctX(v2.getX());
				int y1 = correctY(v1.getY());
				int y2 = correctY(v2.getY());

				g.drawLine(x1, y1, x2, y2);

				if (attributes.canDrawLinedefFaces())
				{
					g2d.setStroke(LineStyle.SOLID.getStroke());
					int mx = (x1 + x2) / 2;
					int my = (y1 + y2) / 2;

					double dx = (y1 - y2);
					double dy = (x2 - x1);
					
					double len = Math.sqrt(dy*dy + dx*dx);
					
					dx = (dx / len) * 5;
					dy = (dy / len) * 5;
					
					g.drawLine(mx, my, mx+(int)dx, my+(int)dy);
				}
				
				if (attributes.canDrawLinedefDirection())
				{
					g2d.setStroke(LineStyle.SOLID.getStroke());
					double rot1 = Math.PI/4;

					double dx = (y1 - y2);
					double dy = (x2 - x1);

					double tx = (dx * Math.cos(rot1)) - (dy * Math.sin(rot1));
					double ty = (dx * Math.sin(rot1)) + (dy * Math.cos(rot1));
					
					double tlen = Math.sqrt(ty*ty + tx*tx);
					
					tx = (tx / tlen) * 8;
					ty = (ty / tlen) * 8;

					double t2x = -ty;
					double t2y = tx;

					g.drawLine(x2, y2, x2+(int)tx, y2+(int)ty);
					g.drawLine(x2, y2, x2+(int)t2x, y2+(int)t2y);
				}

				g2d.setStroke(stroke);
			}
		}
	}

	/**
	 * Draws the map things to a Graphics2D context.
	 * @param g		the context to use.
	 * @param dm 	the map to draw.
	 */
	protected void drawThings(Graphics g, DoomMap dm)
	{
		Color c = g.getColor();
		for (Thing thing : dm.getThingList())
		{
			if (!c.equals(attributes.getColorForThing(thing)))
			{
				c = attributes.getColorForThing(thing);
				g.setColor(c);
			}

			if (attributes.canDrawThing(thing))
			{
				int x = correctX(thing.getX());
				int y = correctY(thing.getY());
				PointStyle p = attributes.getPointStyleForThing(thing);
				int thickness = correctWidth(attributes.getThicknessForThing(thing));
				switch (p)
				{
					case SOLID:
						g.fillOval(
								(int)(x-(thickness/2)), 
								(int)(y-(thickness/2)), 
								thickness, thickness);
						break;
					case OUTLINE:
						g.drawOval(
								(int)(x-(thickness/2)), 
								(int)(y-(thickness/2)), 
								thickness, thickness);
						break;
				}
				
			}
		}
	}

	/**
	 * Gets the correctly-projected X coordinate value after view projection. 
	 */
	protected int correctX(double x)
	{
		double d = 0;
		d = RMath.getInterpolationFactor(x, bounds.x, bounds.x + bounds.width);
		return (int)(d * view.width) + view.x;
	}
	
	/**
	 * Gets the correctly-projected Y coordinate value after view projection. 
	 */
	protected int correctY(double y)
	{
		double d = 0;
		d = RMath.getInterpolationFactor(y, bounds.y, bounds.y + bounds.height);
		return (int)((view.height - (d * view.height)) + view.y);
	}
	
	/**
	 * Gets the correctly-projected width after view projection. 
	 */
	protected int correctWidth(double width)
	{
		double d = 0;
		d = RMath.getInterpolationFactor(width, 0, bounds.width);
		return Math.max(1, (int)(d * view.width));
	}
	
	/**
	 * Gets the correctly-projected height after view projection. 
	 */
	protected int correctHeight(double height)
	{
		double d = 0;
		d = RMath.getInterpolationFactor(height, 0, bounds.height);
		return Math.max(1, (int)(d * view.height));
	}
	
}
