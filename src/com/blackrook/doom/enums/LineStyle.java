/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.enums;

import java.awt.BasicStroke;
import java.awt.Stroke;

/**
 * Enumeration of line styles for the Map Drawer.
 * @author Matthew Tropiano
 */
public enum LineStyle
{
	SOLID(new BasicStroke(1f)),
	THICK_SOLID(new BasicStroke(3f)),
	SHORT_DASH(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 2f}, 1f)),
	LONG_DASH(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, new float[]{10f, 2f}, 1f)),
	THICK_SHORT_DASH(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, new float[]{10f, 6f}, 1f)),
	THICK_LONG_DASH(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f, new float[]{20f, 6f}, 1f)),
	DOTTED(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{1f}, 1f));
	
	private Stroke stroke;
	
	private LineStyle(Stroke stroke)
	{
		this.stroke = stroke;
	}
	
	public Stroke getStroke()
	{
		return stroke;
	}
	
}
