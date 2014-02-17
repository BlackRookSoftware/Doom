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
 * MapDrawerAttributes implementation that acts as a "Get and Set" object
 * for setting individual attributes.
 * @author Matthew Tropiano
 */
public class CustomMapDrawerAttributes implements MapDrawerAttributes
{
	
	protected boolean canDrawBorder;
	protected boolean canDrawBackground;
	protected boolean canDrawCoarseGrid;
	protected boolean canDrawFineGrid;
	protected boolean canDrawGrid;
	protected boolean canDrawLinedefDirection;
	protected boolean canDrawLinedefFaces;
	protected boolean canDrawLinedefs;
	protected boolean canDrawNodes;
	protected boolean canDrawThings;
	protected boolean canDrawVertices;
	protected Color colorForBackground;
	protected Color colorForBorder;
	protected Color colorForCoarseGrid;
	protected Color colorForFineGrid;
	protected Color colorForLinedef;
	protected Color colorForThing;
	protected Color colorForThingDirection;
	protected Color colorForVertex;
	protected LineStyle lineStyleForBorder;
	protected LineStyle lineStyleForCoarseGrid;
	protected LineStyle lineStyleForFineGrid;
	protected LineStyle lineStyleForLinedef;
	protected PointStyle pointStyleForThing;
	protected PointStyle pointStyleForVertex;
	protected float thicknessForThing;
	protected float thicknessForVertex;
	protected int coarseGridSize;
	protected int fineGridSize;
	
	/**
	 * Creates a default attribute set.
	 */
	public CustomMapDrawerAttributes()
	{
		setDrawBorder(false);
		setDrawBackground(true);
		setDrawCoarseGrid(false);
		setDrawFineGrid(false);
		setDrawGrid(false);
		setDrawLinedefDirection(false);
		setDrawLinedefFaces(false);
		setDrawLinedefs(false);
		setDrawNodes(false);
		setDrawThings(false);
		setDrawVertices(false);
		setColorForBackground(Color.WHITE);
		setColorForBorder(Color.WHITE);
		setColorForCoarseGrid(Color.WHITE);
		setColorForFineGrid(Color.WHITE);
		setColorForLinedef(Color.WHITE);
		setColorForThing(Color.WHITE);
		setColorForThingDirection(Color.WHITE);
		setColorForVertex(Color.WHITE);
		setLineStyleForBorder(LineStyle.SOLID);
		setLineStyleForCoarseGrid(LineStyle.SOLID);
		setLineStyleForFineGrid(LineStyle.SOLID);
		setLineStyleForLinedef(LineStyle.SOLID);
		setPointStyleForThing(PointStyle.SOLID);
		setPointStyleForVertex(PointStyle.SOLID);
		setThicknessForThing(1);
		setThicknessForVertex(1);
		setCoarseGridSize(64);
		setFineGridSize(16);
	}
	
	public void setDrawBorder(boolean canDrawBorder)
	{
		this.canDrawBorder = canDrawBorder;
	}

	public void setDrawBackground(boolean canDrawBackground)
	{
		this.canDrawBackground = canDrawBackground;
	}

	public void setDrawCoarseGrid(boolean canDrawCoarseGrid)
	{
		this.canDrawCoarseGrid = canDrawCoarseGrid;
	}

	public void setDrawFineGrid(boolean canDrawFineGrid)
	{
		this.canDrawFineGrid = canDrawFineGrid;
	}

	public void setDrawGrid(boolean canDrawGrid)
	{
		this.canDrawGrid = canDrawGrid;
	}

	public void setDrawLinedefDirection(boolean canDrawLinedefDirection)
	{
		this.canDrawLinedefDirection = canDrawLinedefDirection;
	}

	public void setDrawLinedefFaces(boolean canDrawLinedefFaces)
	{
		this.canDrawLinedefFaces = canDrawLinedefFaces;
	}

	public void setDrawLinedefs(boolean canDrawLinedefs)
	{
		this.canDrawLinedefs = canDrawLinedefs;
	}

	public void setDrawNodes(boolean canDrawNodes)
	{
		this.canDrawNodes = canDrawNodes;
	}

	public void setDrawThings(boolean canDrawThings)
	{
		this.canDrawThings = canDrawThings;
	}

	public void setDrawVertices(boolean canDrawVertices)
	{
		this.canDrawVertices = canDrawVertices;
	}

	public void setColorForBackground(Color colorForBackground)
	{
		this.colorForBackground = colorForBackground;
	}

	public void setColorForBorder(Color colorForBorder)
	{
		this.colorForBorder = colorForBorder;
	}

	public void setColorForCoarseGrid(Color colorForCoarseGrid)
	{
		this.colorForCoarseGrid = colorForCoarseGrid;
	}

	public void setColorForFineGrid(Color colorForFineGrid)
	{
		this.colorForFineGrid = colorForFineGrid;
	}

	public void setColorForLinedef(Color colorForLinedef)
	{
		this.colorForLinedef = colorForLinedef;
	}

	public void setColorForThing(Color colorForThing)
	{
		this.colorForThing = colorForThing;
	}

	public void setColorForThingDirection(Color colorForThingDirection)
	{
		this.colorForThingDirection = colorForThingDirection;
	}

	public void setColorForVertex(Color colorForVertex)
	{
		this.colorForVertex = colorForVertex;
	}

	public void setLineStyleForBorder(LineStyle lineStyleForBorder)
	{
		this.lineStyleForBorder = lineStyleForBorder;
	}

	public void setLineStyleForCoarseGrid(LineStyle lineStyleForCoarseGrid)
	{
		this.lineStyleForCoarseGrid = lineStyleForCoarseGrid;
	}

	public void setLineStyleForFineGrid(LineStyle lineStyleForFineGrid)
	{
		this.lineStyleForFineGrid = lineStyleForFineGrid;
	}

	public void setLineStyleForLinedef(LineStyle lineStyleForLinedef)
	{
		this.lineStyleForLinedef = lineStyleForLinedef;
	}

	public void setPointStyleForThing(PointStyle pointStyleForThing)
	{
		this.pointStyleForThing = pointStyleForThing;
	}

	public void setPointStyleForVertex(PointStyle pointStyleForVertex)
	{
		this.pointStyleForVertex = pointStyleForVertex;
	}

	public void setThicknessForThing(float thicknessForThing)
	{
		this.thicknessForThing = thicknessForThing;
	}

	public void setThicknessForVertex(float thicknessForVertex)
	{
		this.thicknessForVertex = thicknessForVertex;
	}

	public void setCoarseGridSize(int coarseGridSize)
	{
		this.coarseGridSize = coarseGridSize;
	}

	public void setFineGridSize(int fineGridSize)
	{
		this.fineGridSize = fineGridSize;
	}

	@Override
	public boolean canDrawBorder()
	{
		return canDrawBorder;
	}

	@Override
	public boolean canDrawBackground()
	{
		return canDrawBackground;
	}

	@Override
	public boolean canDrawCoarseGrid()
	{
		return canDrawCoarseGrid;
	}

	@Override
	public boolean canDrawFineGrid()
	{
		return canDrawFineGrid;
	}

	@Override
	public boolean canDrawGrid()
	{
		return canDrawGrid;
	}

	@Override
	public boolean canDrawLinedef(Linedef linedef)
	{
		return canDrawLinedefs;
	}

	@Override
	public boolean canDrawLinedefDirection()
	{
		return canDrawLinedefDirection;
	}

	@Override
	public boolean canDrawLinedefFaces()
	{
		return canDrawLinedefFaces;
	}

	@Override
	public boolean canDrawLinedefs()
	{
		return canDrawLinedefs;
	}

	@Override
	public boolean canDrawThing(Thing thing)
	{
		return canDrawThings;
	}

	@Override
	public boolean canDrawThings()
	{
		return canDrawThings;
	}

	@Override
	public boolean canDrawVertex(Vertex vertex)
	{
		return canDrawVertices;
	}

	@Override
	public boolean canDrawVertices()
	{
		return canDrawVertices;
	}

	@Override
	public Color getColorForBackground()
	{
		return colorForBackground;
	}

	@Override
	public Color getColorForBorder()
	{
		return colorForBorder;
	}

	@Override
	public Color getColorForCoarseGrid()
	{
		return colorForCoarseGrid;
	}

	@Override
	public Color getColorForFineGrid()
	{
		return colorForFineGrid;
	}

	@Override
	public Color getColorForLinedef(Linedef linedef)
	{
		return colorForLinedef;
	}

	@Override
	public Color getColorForThing(Thing thing)
	{
		return colorForThing;
	}

	@Override
	public Color getColorForThingDirection(Thing thing)
	{
		return colorForThingDirection;
	}

	@Override
	public Color getColorForVertex(Vertex vertex)
	{
		return colorForVertex;
	}

	@Override
	public LineStyle getLineStyleForBorder()
	{
		return lineStyleForBorder;
	}

	@Override
	public LineStyle getLineStyleForCoarseGrid()
	{
		return lineStyleForCoarseGrid;
	}

	@Override
	public LineStyle getLineStyleForFineGrid()
	{
		return lineStyleForFineGrid;
	}

	@Override
	public LineStyle getLineStyleForLinedef(Linedef linedef)
	{
		return lineStyleForLinedef;
	}

	@Override
	public PointStyle getPointStyleForThing(Thing thing)
	{
		return pointStyleForThing;
	}

	@Override
	public PointStyle getPointStyleForVertex(Vertex vertex)
	{
		return pointStyleForVertex;
	}

	@Override
	public float getThicknessForThing(Thing thing)
	{
		return thicknessForThing;
	}

	@Override
	public float getThicknessForVertex(Vertex vertex)
	{
		return thicknessForVertex;
	}

	@Override
	public int getCoarseGridSize()
	{
		return coarseGridSize;
	}

	@Override
	public int getFineGridSize()
	{
		return fineGridSize;
	}

}
