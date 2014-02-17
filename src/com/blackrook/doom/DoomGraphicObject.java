/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.blackrook.doom.struct.ColorMap;
import com.blackrook.doom.struct.Palette;

/**
 * Interface for Doom graphic data (patch or PNG, at the moment).
 * @author Matthew Tropiano
 */
public interface DoomGraphicObject extends DoomObject
{
	
	/**
	 * Gets the offset from the center, horizontally, in pixels.
	 */
	public int getOffsetX();

	/**
	 * Gets the offset from the center, vertically, in pixels.
	 */
	public int getOffsetY();

	/**
	 * Returns the width of this graphic in pixels.
	 */
	public int getWidth();
	
	/**
	 * Returns the height of this graphic in pixels.
	 */
	public int getHeight();

	/**
	 * Sets the pixel data for this graphic using an Image and Palette.
	 * Depending on the implementing class, this may or may not auto-correct
	 * the color information to an indexed representation using the palette.
	 */
	public void setImage(BufferedImage image, Palette palette);

	/**
	 * Sets the pixel data at a location in the graphic using a particular palette and/or colormap.
	 * @param x			patch x-coordinate.
	 * @param y			patch y-coordinate.
	 * @param color		the color to set.
	 * @param palette 	the palette to use for color adjustment.
	 */
	public void setPixelColor(int x, int y, Color color, Palette palette);

	/**
	 * Gets the pixel data at a location in the graphic using a particular palette and/or colormap.
	 * Returns a Color value or the transparentColor if the pixel is transparent.
	 * @param x					patch x-coordinate.
	 * @param y					patch y-coordinate.
	 * @param colormap			the colormap to use for the index lookup, before palette lookup. if null, no map is used.
	 * @param palette 			the palette to use for color lookup.
	 * @param transparentColor	the transparency color to return if the pixel is translucent.
	 * @return the color of the requested pixel.
	 */
	public Color getPixelColor(int x, int y, Palette palette, ColorMap colormap, Color transparentColor);
}
