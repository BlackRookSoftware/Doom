/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.util;

import java.awt.Color;

import com.blackrook.doom.enums.LineStyle;
import com.blackrook.doom.enums.PointStyle;
import com.blackrook.doom.struct.Linedef;
import com.blackrook.doom.struct.Thing;
import com.blackrook.doom.struct.Vertex;

/**
 * Every MapDrawer uses this type of object to 
 * determine how to draw objects to the canvas. 
 * @author Matthew Tropiano
 */
public interface MapDrawerAttributes
{
	/**
	 * Can the map drawer draw linedefs?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawLinedefs();
	
	/**
	 * Can the map drawer draw indicators showing the facing direction of the linedef?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawLinedefFaces();
	
	/**
	 * Can the map drawer draw indicators showing the direction of the linedef?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawLinedefDirection();
	
	/**
	 * Can the map drawer draw vertices?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawVertices();
	
	/**
	 * Can the map drawer draw things?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawThings();

	/**
	 * Can the map drawer draw the alignment grid?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawGrid();
	
	/**
	 * Can the map drawer draw the coarser lines of the grid,
	 * rather, the larger unit division increments?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawCoarseGrid();
	
	/**
	 * Can the map drawer draw the finer lines of the grid,
	 * rather, the smaller unit division increments?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawFineGrid();
	
	/**
	 * Can the map drawer draw the outer border of 
	 * the map graphic (rectangle surrounding drawing area)?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawBorder();

	/**
	 * Can the map drawer draw the background of 
	 * the map graphic (rectangle surrounding drawing area)?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawBackground();

	/**
	 * Can the map drawer draw this specific linedef?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawLinedef(Linedef linedef);
	
	/**
	 * Can the map drawer draw this specific vertex?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawVertex(Vertex vertex);
	
	/**
	 * Can the map drawer draw this specific thing?
	 * @return true if so, false otherwise.
	 */
	public boolean canDrawThing(Thing thing);
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for the background.
	 */
	public Color getColorForBackground();
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for outer border of the map graphic (rectangle surrounding drawing area).
	 */
	public Color getColorForBorder();
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for a specific linedef.
	 */
	public Color getColorForLinedef(Linedef linedef);
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for a specific vertex.
	 */
	public Color getColorForVertex(Vertex vertex);
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for a specific thing.
	 */
	public Color getColorForThing(Thing thing);
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for a specific thing's direction indicator.
	 */
	public Color getColorForThingDirection(Thing thing);
	
	/**
	 * Returns the java.awt.Color object used for the color
	 * information for the coarser grid.
	 */
	public Color getColorForCoarseGrid();

	/**
	 * Returns the java.awt.Color object used for the color
	 * information for the finer grid.
	 */
	public Color getColorForFineGrid();

	/**
	 * Returns the point thickness for a particular vertex
	 * (point thickness does not change regardless of view).
	 */
	public float getThicknessForVertex(Vertex vertex);

	/**
	 * Returns the point thickness for a particular thing 
	 * (in map units - this is the only point-based element re-shaped for the view).
	 */
	public float getThicknessForThing(Thing thing);

	/**
	 * Returns the line style object used for the border.
	 */
	public LineStyle getLineStyleForBorder();
	
	/**
	 * Returns the line style object used for drawing a specific linedef.
	 */
	public LineStyle getLineStyleForLinedef(Linedef linedef);
	
	/**
	 * Returns the point style object used for drawing a specific vertex.
	 */
	public PointStyle getPointStyleForVertex(Vertex vertex);
	
	/**
	 * Returns the point style object used for drawing a specific thing.
	 */
	public PointStyle getPointStyleForThing(Thing thing);
	
	/**
	 * Returns the line style object used for drawing the coarser grid.
	 */
	public LineStyle getLineStyleForCoarseGrid();

	/**
	 * Returns the line style object used for drawing the finer grid.
	 */
	public LineStyle getLineStyleForFineGrid();

	/**
	 * Returns the size of the coarser grid.
	 */
	public int getCoarseGridSize();

	/**
	 * Returns the size of the finer grid.
	 */
	public int getFineGridSize();

}
