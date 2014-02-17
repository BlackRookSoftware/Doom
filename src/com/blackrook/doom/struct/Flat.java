/*******************************************************************************
 * Copyright (c) 2009-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.doom.struct;

import static com.blackrook.doom.DoomObjectUtils.checkByteUnsigned;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.blackrook.commons.Common;
import com.blackrook.commons.math.RMath;
import com.blackrook.doom.DataExportException;
import com.blackrook.doom.DoomGraphicObject;
import com.blackrook.io.SuperReader;
import com.blackrook.io.SuperWriter;

/**
 * <p>
 * Doom graphic data that has no header data for its dimensions/offsets.
 * Normally, flats are the floor/ceiling textures in the Doom engine that are
 * a set size (64x64) and thus have no need for header information, but fullscreen
 * pictures like Heretic's TITLE lump are also a straight mapping of pixels with assumed
 * dimensions (in this case, 320x200). This class can read both, and its dimensions can
 * be arbitrarily set by the programmer regardless of the amount of data inside.
 * </p>
 * NOTE: The readDoomBytes() method will only read as many bytes as possible to fill the
 * current dimensions of the flat, as this information is not found in the byte data.
 * @author Matthew Tropiano
 */
public class Flat implements DoomGraphicObject
{
	public static final short PIXEL_TRANSLUCENT = 0;
	
	/** This flat's width. */
	private int width;
	/** This flat's height. */
	private int height;
	/** The pixel data. */
	private short[] pixels;
	
	/**
	 * Creates a new flat with dimensions (1, 1).
	 */
	public Flat()
	{
		this(1, 1);
	}
	
	/**
	 * Creates a new flat.
	 * @param width		the width of the flat in pixels.
	 * @param height	the height of the flat in pixels.
	 */
	public Flat(int width, int height)
	{
		if (width < 1 || height < 1)
			throw new IllegalArgumentException("Width or height cannot be less than 1.");
		setDimensions(width, height);
	}

	/**
	 * Sets the dimensions of this flat.
	 * WARNING: This will clear all of the data in the patch.
	 * @param width		the width of the flat in pixels.
	 * @param height	the height of the flat in pixels.
	 */
	public void setDimensions(int width, int height)
	{
		this.width = width;
		this.height = height;
		pixels = new short[width*height];
	}
	
	/**
	 * Returns the width of this graphic in pixels.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Returns the height of this graphic in pixels.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Sets the pixel data at a location in the flat.
	 * Valid values are in the range of 0 to 255, with
	 * 0 to 255 being palette indexes.
	 * Values outside this range are CLAMPED into the range.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 * @param value	the value to set.
	 */
	public void setPixel(int x, int y, int value)
	{
		pixels[y*width + x] = (short)RMath.clampValue(value, 0, 255);
	}
	
	@Override
	public void setImage(BufferedImage image, Palette palette)
	{
		setDimensions(image.getWidth(), image.getHeight());
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				setPixelColor(i, j, Common.argbToColor(image.getRGB(i, j)), palette);
	}

	@Override
	public void setPixelColor(int x, int y, Color color, Palette palette)
	{
		setPixel(x, y, palette.getNearestColorIndex(color));
	}

	/**
	 * Gets the pixel data at a location in the flat.
	 * Returns a palette index value from 0 to 255 or PIXEL_TRANSLUCENT
	 * if the pixel is not filled in.
	 * @param x	patch x-coordinate.
	 * @param y	patch y-coordinate.
	 */
	public short getPixel(int x, int y)
	{
		return pixels[y*width + x];
	}
	
	
	/**
	 * Gets the pixel data at a location in the patch using a particular palette and/or colormap.
	 * Returns a Color value or the transparentColor if the pixel is transparent.
	 * if the pixel is not filled in.
	 * @param x					patch x-coordinate.
	 * @param y					patch y-coordinate.
	 * @param colormap			the colormap to use for the index lookup, before palette lookup. if null, no map is used.
	 * @param pal 				the palette used for color lookup.
	 * @param transparentColor	the transparency color to return if the pixel is translucent.
	 * @return the color of the requested pixel.
	 */
	@Override
	public Color getPixelColor(int x, int y, Palette pal, ColorMap colormap, Color transparentColor)
	{
		if (colormap == null)
			return getPixel(x,y) >= 0 ? pal.getColor(getPixel(x,y)) : transparentColor;
		else
			return getPixel(x,y) >= 0 ? pal.getColor(colormap.getPaletteIndex(getPixel(x,y))) : transparentColor;
	}

	/**
	 * Clears the pixel data.
	 */
	public void clear()
	{
		setDimensions(width, height);
	}
	
	@Override
	public byte[] getDoomBytes() throws DataExportException
	{
		callDoomCompatibilityCheck();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		SuperWriter sw = new SuperWriter(bos,SuperWriter.LITTLE_ENDIAN);
		try{
			for (short pixel : pixels)
				sw.writeByte((byte)pixel);
		} catch (IOException e){}
		
		return bos.toByteArray();
	}

	@Override
	public boolean isDoomCompatible()
	{
		try {
			callDoomCompatibilityCheck();
		} catch (DataExportException e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks this data structure for data export integrity for the Doom format. 
	 * @throws DataExportException if a bad criterion is found.
	 */
	public void callDoomCompatibilityCheck() throws DataExportException
	{
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				checkByteUnsigned("Pixel ("+x+", "+y+")", pixels[y*width + x]);
	}
	
	@Override
	public void readDoomBytes(InputStream in) throws IOException
	{
		SuperReader sr = new SuperReader(in,SuperReader.LITTLE_ENDIAN);
		for (int i = 0; i < width*height; i++)
			pixels[i] = (short)(sr.readByte() & 0x00ff);
	}

	@Override
	public void writeDoomBytes(OutputStream out) throws IOException, DataExportException
	{
		out.write(getDoomBytes());
	}

	@Override
	public int getOffsetX()
	{
		return 0;
	}

	@Override
	public int getOffsetY()
	{
		return 0;
	}

}
